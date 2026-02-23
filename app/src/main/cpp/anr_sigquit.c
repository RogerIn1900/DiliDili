/**
 * 可选：SIGQUIT 捕获。在系统因 ANR 向进程发送 SIGQUIT 时，写一条记录到文件。
 * Handler 内仅使用 async-signal-safe：write / sigaction / _exit。
 */
#include <jni.h>
#include <signal.h>
#include <stdio.h>
#include <unistd.h>
#include <fcntl.h>
#include <string.h>
#include <android/log.h>

#define LOG_TAG "AnrSigquit"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

static const char SIGQUIT_MARKER[] = "SIGQUIT\n";
static char s_path[256];
static int s_fd = -1;
static struct sigaction s_old;

static void sigquit_handler(int sig) {
    (void) sig;
    if (s_fd >= 0) {
        write(s_fd, SIGQUIT_MARKER, sizeof(SIGQUIT_MARKER) - 1);
    }
    sigaction(SIGQUIT, &s_old, NULL);
    raise(SIGQUIT);
}

JNIEXPORT jint JNI_OnLoad(JavaVM* vm, void* reserved) {
    (void) vm;
    (void) reserved;
    return JNI_VERSION_1_6;
}

/* 由 Java 调用：传入目录路径（如 getFilesDir().getAbsolutePath()），在该目录下创建 anr_sigquit_received.txt 并注册 SIGQUIT */
JNIEXPORT jboolean JNICALL
Java_com_example_dilidiliactivity_anr_AnrSigquitNative_installNative(JNIEnv *env, jclass clazz, jstring files_dir) {
    (void) clazz;
    const char *dir = (*env)->GetStringUTFChars(env, files_dir, NULL);
    if (!dir) return JNI_FALSE;
    snprintf(s_path, sizeof(s_path), "%s/anr_sigquit_received.txt", dir);
    (*env)->ReleaseStringUTFChars(env, files_dir, dir);

    s_fd = open(s_path, O_WRONLY | O_CREAT | O_TRUNC, 0640);
    if (s_fd < 0) {
        LOGI("open %s failed", s_path);
        return JNI_FALSE;
    }

    struct sigaction sa;
    memset(&sa, 0, sizeof(sa));
    sa.sa_handler = sigquit_handler;
    sigemptyset(&sa.sa_mask);
    sa.sa_flags = 0;
    if (sigaction(SIGQUIT, &sa, &s_old) != 0) {
        close(s_fd);
        s_fd = -1;
        return JNI_FALSE;
    }
    LOGI("SIGQUIT handler installed, path=%s", s_path);
    return JNI_TRUE;
}
