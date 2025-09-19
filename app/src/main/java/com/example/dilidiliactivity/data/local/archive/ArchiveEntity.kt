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
    val rightsJson: String = "",     // Rights 序列化 JSON
    val ownerJson: String = "",      // Owner 序列化 JSON
    val statJson: String = "",       // Stat 序列化 JSON
    val dynamic: String? = null,
    val cid: Long = 0L,
    val dimensionJson: String = "",  // Dimension 序列化 JSON
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
        rights = gson.fromJson(this.rightsJson, Rights::class.java),
        owner = gson.fromJson(this.ownerJson, Owner::class.java),
        stat = gson.fromJson(this.statJson, Stat::class.java),
        dynamic = this.dynamic,
        cid = this.cid,
        dimension = gson.fromJson(this.dimensionJson, Dimension::class.java),
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



//
//fun Archive.toEntity(): ArchiveEntity {
//    return ArchiveEntity(
//        bvid = this.bvid,
//        aid = this.aid,
//        videos = this.videos,
//        tid = this.tid,
//        tname = this.tname,
//        copyright = this.copyright,
//        pic = this.pic,
//        title = this.title,
//        pubdate = this.pubdate,
//        ctime = this.ctime,
//        desc = this.desc,
//        state = this.state,
//        duration = this.duration,
//        mission_id = this.mission_id,
//        rightsJson = gson.toJson(this.rights),
//        ownerJson = gson.toJson(this.owner),
//        statJson = gson.toJson(this.stat),
//        dynamic = this.dynamic,
//        cid = this.cid,
//        dimensionJson = gson.toJson(this.dimension),
//        short_link_v2 = this.short_link_v2,
//        first_frame = this.first_frame,
//        pub_location = this.pub_location,
//        cover43 = this.cover43,
//        tidv2 = this.tidv2,
//        tnamev2 = this.tnamev2,
//        pid_v2 = this.pid_v2,
//        pid_name_v2 = this.pid_name_v2,
//        season_type = this.season_type,
//        is_ogv = this.is_ogv,
//        ogv_infoJson = this.ogv_info?.let { gson.toJson(it) },
//        rcmd_reason = this.rcmd_reason,
//        enable_vt = this.enable_vt,
//        ai_rcmdJson = this.ai_rcmd?.let { gson.toJson(it) }
//    )
//}
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
        rightsJson = gson.toJson(this.rights),
        ownerJson = gson.toJson(this.owner),
        statJson = gson.toJson(this.stat),
        dynamic = this.dynamic ?: "",
        cid = this.cid,
        dimensionJson = gson.toJson(this.dimension),
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


fun ArchiveEntity.toArchive(): Archive {
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
        rights = gson.fromJson(this.rightsJson, Rights::class.java),
        owner = gson.fromJson(this.ownerJson, Owner::class.java),
        stat = gson.fromJson(this.statJson, Stat::class.java),
        dynamic = this.dynamic,
        cid = this.cid,
        dimension = gson.fromJson(this.dimensionJson, Dimension::class.java),
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

