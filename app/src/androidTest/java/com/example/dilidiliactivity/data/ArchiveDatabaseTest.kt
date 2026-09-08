package com.example.dilidiliactivity.data

import androidx.room.Room
import androidx.room.withTransaction
import androidx.test.core.app.ApplicationProvider
import com.example.dilidiliactivity.data.local.archive.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ArchiveDatabaseTest {
    private lateinit var db: AppDatabase
    private lateinit var dao: ArchiveDao
    @Before fun setUp() {
        db = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), AppDatabase::class.java).build()
        dao = db.archiveDao()
    }
    @After fun tearDown() = db.close()

    @Test fun duplicateItemsArePersistedOnceInStableOrder() = runBlocking {
        dao.replaceRegion(1, listOf(ArchiveEntity("a"), ArchiveEntity("a"), ArchiveEntity("b")))
        assertEquals(listOf("a", "b"), dao.observeRegion(1).first().map { it.bvid })
    }
    @Test fun refreshUpdatesDetailsAndDoesNotAffectAnotherRegion() = runBlocking {
        dao.replaceRegion(1, listOf(ArchiveEntity("a", title = "old")))
        dao.replaceRegion(2, listOf(ArchiveEntity("b")))
        dao.replaceRegion(1, listOf(ArchiveEntity("a", title = "new")))
        assertEquals("new", dao.getArchive("a")!!.title)
        assertEquals(listOf("b"), dao.getRegion(2).map { it.bvid })
    }
    @Test fun failedTransactionRetainsPreviousRowsAndOrder() = runBlocking {
        dao.replaceRegion(1, listOf(ArchiveEntity("a", title = "old")))
        try {
            db.withTransaction {
                dao.replaceRegion(1, listOf(ArchiveEntity("b")))
                throw IllegalStateException("injected write failure")
            }
        } catch (_: IllegalStateException) { }
        assertEquals(listOf("a"), dao.getRegion(1).map { it.bvid })
        assertNull(dao.getArchive("b"))
    }
    @Test fun successfulEmptySnapshotClearsOnlyItsFeed() = runBlocking {
        dao.replaceRegion(1, listOf(ArchiveEntity("a")))
        dao.replaceRegion(1, emptyList())
        assertTrue(dao.observeRegion(1).first().isEmpty())
        assertNotNull(dao.getArchive("a"))
    }
    @Test fun versionTwoMigrationPreservesStoredArchive() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val name = "migration-test.db"
        context.deleteDatabase(name)
        try {
            val database = Room.databaseBuilder(context, AppDatabase::class.java, name).build()
            try {
                database.archiveDao().insertArchive(ArchiveEntity("preserved"))
                database.openHelper.writableDatabase.execSQL("DROP TABLE archive_feed_entries")
                database.openHelper.writableDatabase.execSQL("PRAGMA user_version = 2")
            } finally { database.close() }
            val migrated = Room.databaseBuilder(context, AppDatabase::class.java, name)
                .addMigrations(AppDatabase.MIGRATION_2_3).build()
            try {
                assertEquals("preserved", migrated.archiveDao().getArchive("preserved")!!.bvid)
                assertTrue(migrated.archiveDao().getRegion(1).isEmpty())
            } finally { migrated.close() }
        } finally { context.deleteDatabase(name) }
    }
}
