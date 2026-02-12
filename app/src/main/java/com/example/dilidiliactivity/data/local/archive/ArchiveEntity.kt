package com.example.dilidiliactivity.data.local.archive

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson


private val gson = Gson()

@Entity(tableName = "archive_table")
data class ArchiveEntity(
    @PrimaryKey val bvid: String,
    val aid: Long = 0L,
    val videos: Int = 0,
    val tid: Int = 0,
    val tname: String = "",
    val copyright: Int = 0,
    val pic: String = "",
    val title: String = "",
    val pubdate: Long = 0L,
    val ctime: Long = 0L,
    val desc: String = "",
    val state: Int = 0,
    val duration: Int = 0,
    val mission_id: Int? = null,
    val rights: Rights = Rights(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
    val owner: Owner = Owner(0L, "", ""),
    val stat: Stat = Stat(0L, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
    val dynamic: String? = null,
    val cid: Long = 0L,
    val dimension: Dimension = Dimension(0, 0, 0),
    val short_link_v2: String = "",
    val first_frame: String = "",
    val pub_location: String? = null,
    val cover43: String? = null,
    val tidv2: Int = 0,
    val tnamev2: String = "",
    val pid_v2: Int = 0,
    val pid_name_v2: String = "",
    val season_type: Int = 0,
    val is_ogv: Boolean = false,
    val ogv_infoJson: String? = null,
    val rcmd_reason: String? = null,
    val enable_vt: Int = 0,
    val ai_rcmdJson: String? = null
)



fun ArchiveEntity.toDomain(): Archive {
    return Archive(
        aid = this.aid,
        videos = this.videos,
        tid = this.tid,
        tname = this.tname,
        copyright = this.copyright,
        pic = this.pic,
        title = this.title,
        pubdate = this.pubdate,
        ctime = this.ctime,
        desc = this.desc,
        state = this.state,
        duration = this.duration,
        mission_id = this.mission_id,
        rights = this.rights,
        owner = this.owner,
        stat = this.stat,
        dynamic = this.dynamic,
        cid = this.cid,
        dimension = this.dimension,
        short_link_v2 = this.short_link_v2,
        first_frame = this.first_frame,
        pub_location = this.pub_location,
        cover43 = this.cover43,
        tidv2 = this.tidv2,
        tnamev2 = this.tnamev2,
        pid_v2 = this.pid_v2,
        pid_name_v2 = this.pid_name_v2,
        bvid = this.bvid,
        season_type = this.season_type,
        is_ogv = this.is_ogv,
        ogv_info = this.ogv_infoJson?.let { gson.fromJson(it, Any::class.java) },
        rcmd_reason = this.rcmd_reason,
        enable_vt = this.enable_vt,
        ai_rcmd = this.ai_rcmdJson?.let { gson.fromJson(it, Any::class.java) }
    )
}

fun Archive.toEntity(): ArchiveEntity {
    return ArchiveEntity(
        bvid = this.bvid,
        aid = this.aid,
        videos = this.videos,
        tid = this.tid,
        tname = this.tname,
        copyright = this.copyright,
        pic = this.pic,
        title = this.title,
        pubdate = this.pubdate,
        ctime = this.ctime,
        desc = this.desc,
        state = this.state,
        duration = this.duration,
        mission_id = this.mission_id ?: -1, // null 用 -1 代替
        rights = this.rights,
        owner = this.owner,
        stat = this.stat,
        dynamic = this.dynamic ?: "",
        cid = this.cid,
        dimension = this.dimension,
        short_link_v2 = this.short_link_v2,
        first_frame = this.first_frame ?: "", // ✅ 判空
        pub_location = this.pub_location ?: "",
        cover43 = this.cover43 ?: "",
        tidv2 = this.tidv2,
        tnamev2 = this.tnamev2,
        pid_v2 = this.pid_v2,
        pid_name_v2 = this.pid_name_v2,
        season_type = this.season_type,
        is_ogv = this.is_ogv,
        ogv_infoJson = this.ogv_info?.let { gson.toJson(it) } ?: "{}", // ✅ 判空
        rcmd_reason = this.rcmd_reason ?: "",
        enable_vt = this.enable_vt,
        ai_rcmdJson = this.ai_rcmd?.let { gson.toJson(it) } ?: "{}" // ✅ 判空
    )
}
