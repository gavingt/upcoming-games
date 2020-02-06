package com.gavinsappcreations.upcominggames.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.TypeConverters

@Dao
interface GameDao {
    @Query("SELECT * FROM DatabaseGame ORDER BY DatabaseGame.releaseDateString")
    fun getGames(): LiveData<List<DatabaseGame>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(games: List<DatabaseGame>)
}

@Database(entities = [DatabaseGame::class], version = 1)
@TypeConverters(Converters::class)
abstract class GamesDatabase : RoomDatabase() {
    abstract val gameDao: GameDao
}

private lateinit var INSTANCE: GamesDatabase

fun getDatabase(context: Context): GamesDatabase {
    synchronized(GamesDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(context.applicationContext,
                    GamesDatabase::class.java,
                    "games").build()
        }
    }
    return INSTANCE
}