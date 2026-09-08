"""Measure setMediaItem -> rendered first frame on a dedicated Android test device."""
import argparse
import json
import hashlib
import re
import statistics
import subprocess
import time
import xml.etree.ElementTree as ET
from pathlib import Path

APP_ID = "com.example.dilidiliactivity"
ACTIVITY = APP_ID + ".ui.playback.PlaybackDemoActivity"
UI_TIMEOUT_SECONDS = 20
POLL_INTERVAL_SECONDS = 0.5


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--adb", default="adb")
    parser.add_argument("--apk", type=Path, required=True, help="Benchmark APK to install and hash")
    parser.add_argument("--serial", required=True, help="Dedicated test device; its logcat buffer is cleared")
    parser.add_argument("--iterations", type=int, default=3)
    parser.add_argument("--output", type=Path, required=True)
    args = parser.parse_args()
    if args.iterations < 1:
        parser.error("iterations must be positive")

    def adb(*command):
        return subprocess.check_output([args.adb, "-s", args.serial, *command], text=True, timeout=30)

    adb("install", "-r", str(args.apk.resolve()))
    samples = []
    for _ in range(args.iterations):
        adb("shell", "am", "force-stop", APP_ID)
        adb("logcat", "-c")
        adb("shell", "am", "start", "-W", "-n", APP_ID + "/" + ACTIVITY)
        adb("shell", "uiautomator", "dump", "/sdcard/window.xml")
        tree = ET.fromstring(adb("shell", "cat", "/sdcard/window.xml"))
        node = next((node for node in tree.iter("node") if node.get("resource-id") == "demo_item_0"), None)
        if node is None:
            raise RuntimeError("Demo item is absent; unlock the dedicated test device")
        left, top, right, bottom = map(int, re.findall(r"\d+", node.attrib["bounds"]))
        adb("shell", "input", "tap", str((left + right) // 2), str((top + bottom) // 2))
        deadline = time.monotonic() + UI_TIMEOUT_SECONDS
        while time.monotonic() < deadline:
            match = re.search(r"first_frame_ms=(\d+)", adb("logcat", "-d", "-s", "PlaybackMetrics:I"))
            if match:
                samples.append(int(match.group(1)))
                break
            time.sleep(POLL_INTERVAL_SECONDS)
        else:
            raise TimeoutError("No rendered first frame; playback failure is not a valid sample")
    result = {
        "device": adb("shell", "getprop", "ro.product.model").strip(),
        "sdk": adb("shell", "getprop", "ro.build.version.sdk").strip(),
        "source": subprocess.check_output(["git", "describe", "--always", "--dirty"], text=True).strip(),
        "apk_sha256": hashlib.sha256(args.apk.read_bytes()).hexdigest(),
        "metric": "setMediaItem to onRenderedFirstFrame in milliseconds",
        "fixture": "playback_fixture.mp4; H264 320x180 24fps 6s, no audio",
        "samples_ms": samples,
        "median_ms": statistics.median(samples),
        "limitations": "Local fixture only; emulator results do not establish physical-device performance or a before/after improvement",
    }
    args.output.parent.mkdir(parents=True, exist_ok=True)
    args.output.write_text(json.dumps(result, ensure_ascii=False, indent=2) + "\n")
    print(json.dumps(result, ensure_ascii=False, indent=2))


if __name__ == "__main__":
    main()
