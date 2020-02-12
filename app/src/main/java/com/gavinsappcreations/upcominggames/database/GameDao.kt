package com.gavinsappcreations.upcominggames.database

import android.content.Context
import androidx.paging.DataSource
import androidx.room.*
import com.gavinsappcreations.upcominggames.domain.Game

@Dao
interface GameDao {

    @Query("SELECT * FROM Game WHERE releaseDateInMillis IS NOT NULL ORDER BY Game.releaseDateInMillis")
    fun getGames(): DataSource.Factory<Int, Game>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(games: List<Game>)
}

@Database(entities = [Game::class], version = 1)
@TypeConverters(Converters::class)
abstract class GamesDatabase : RoomDatabase() {
    abstract val gameDao: GameDao
}

private lateinit var INSTANCE: GamesDatabase

fun getDatabase(context: Context): GamesDatabase {
    synchronized(GamesDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                GamesDatabase::class.java,
                "games"
            ).build()
        }
    }
    return INSTANCE
}
