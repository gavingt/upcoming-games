package com.gavinsappcreations.upcominggames.database

import android.content.Context
import androidx.paging.DataSource
import androidx.room.*
import com.gavinsappcreations.upcominggames.domain.Game
import com.gavinsappcreations.upcominggames.utilities.SortDirection

@Dao
interface GameDao {

    @Query("SELECT * FROM Game " +
            // This line constrains the games returned to be within the date range requested.
            "WHERE Game.releaseDateInMillis > :startReleaseDate AND Game.releaseDateInMillis < :endReleaseDate " +
            "ORDER BY " +
            // This line puts any games with unknown release dates at the end of the returned list.
            "CASE WHEN Game.dateFormat IS 4 THEN 1 ELSE 0 END," +
            // This line uses the variable sortDirection to switch between ASC and DESC order.
            "CASE WHEN :sortDirection = 'asc' THEN Game.releaseDateInMillis END ASC, CASE WHEN :sortDirection = 'desc' THEN Game.releaseDateInMillis END DESC")
    fun getGames(sortDirection: String, startReleaseDate: Long, endReleaseDate: Long): DataSource.Factory<Int, Game>

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
            ).createFromAsset("database/games.db").build()
        }
    }
    return INSTANCE
}
