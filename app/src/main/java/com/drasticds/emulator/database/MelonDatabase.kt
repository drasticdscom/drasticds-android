package com.drasticds.emulator.database

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.AutoMigrationSpec
import androidx.sqlite.db.SupportSQLiteDatabase
import com.drasticds.emulator.database.converters.InstantConverter
import com.drasticds.emulator.database.daos.CheatDao
import com.drasticds.emulator.database.daos.CheatDatabaseDao
import com.drasticds.emulator.database.daos.CheatFolderDao
import com.drasticds.emulator.database.daos.GameDao
import com.drasticds.emulator.database.daos.RetroAchievementsDao
import com.drasticds.emulator.database.entities.CheatDatabaseEntity
import com.drasticds.emulator.database.entities.CheatEntity
import com.drasticds.emulator.database.entities.CheatFolderEntity
import com.drasticds.emulator.database.entities.GameEntity
import com.drasticds.emulator.database.entities.retroachievements.RAAchievementEntity
import com.drasticds.emulator.database.entities.retroachievements.RAAchievementSetEntity
import com.drasticds.emulator.database.entities.retroachievements.RAGameEntity
import com.drasticds.emulator.database.entities.retroachievements.RAGameHashEntity
import com.drasticds.emulator.database.entities.retroachievements.RAGameSetMetadata
import com.drasticds.emulator.database.entities.retroachievements.RALeaderboardEntity
import com.drasticds.emulator.database.entities.retroachievements.RAPendingAchievementSubmissionEntity
import com.drasticds.emulator.database.entities.retroachievements.RAUserAchievementEntity

@Database(
    version = 8,
    exportSchema = true,
    entities = [
        CheatDatabaseEntity::class,
        GameEntity::class,
        CheatFolderEntity::class,
        CheatEntity::class,
        RAGameEntity::class,
        RAAchievementSetEntity::class,
        RAAchievementEntity::class,
        RAUserAchievementEntity::class,
        RALeaderboardEntity::class,
        RAGameSetMetadata::class,
        RAGameHashEntity::class,
        RAPendingAchievementSubmissionEntity::class,
    ],
    autoMigrations = [
        AutoMigration(from = 2, to = 3, spec = MelonDatabase.Migration2to3Spec::class),
        AutoMigration(from = 3, to = 4),
        AutoMigration(from = 6, to = 7),
    ]
)
@TypeConverters(InstantConverter::class)
abstract class MelonDatabase : RoomDatabase() {
    abstract fun cheatDatabaseDao(): CheatDatabaseDao
    abstract fun gameDao(): GameDao
    abstract fun cheatFolderDao(): CheatFolderDao
    abstract fun cheatDao(): CheatDao
    abstract fun achievementsDao(): RetroAchievementsDao

    class Migration2to3Spec : AutoMigrationSpec {
        override fun onPostMigrate(db: SupportSQLiteDatabase) {
            val result = db.query("SELECT COUNT(*) FROM game")
            if (result.moveToFirst() && result.getInt(0) > 0) {
                // If there are games, insert a cheat database. Most likely the cheat database that was used was the one
                // from DeadSkullzJr, which has the name "DeadSkullzJr's NDS Cheat Database"
                val contentValues = ContentValues().apply {
                    put("name", "DeadSkullzJr's NDS Cheat Database")
                }
                val databaseId = db.insert("cheat_database", SQLiteDatabase.CONFLICT_IGNORE, contentValues)
                db.execSQL("UPDATE game SET database_id = ?", arrayOf(databaseId))
            }
        }
    }
}