package com.gavinsappcreations.upcominggames.database

import android.content.Context
import androidx.paging.DataSource
import androidx.room.*
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.gavinsappcreations.upcominggames.domain.Game
import com.gavinsappcreations.upcominggames.utilities.allPlatforms

fun buildFinalQuery(
    sortDirection: String,
    startReleaseDateMillis: Long,
    endReleaseDateMillis: Long,
    platformsIndices: MutableSet<Int>
): SimpleSQLiteQuery {

    // TODO: this is preventing games with null release dates from ever showing up

    val queryBeginning = "SELECT * FROM Game " +
            // These two lines constrain the games returned to be within the date range requested.
            "WHERE Game.releaseDateInMillis > $startReleaseDateMillis " +
            "AND Game.releaseDateInMillis < $endReleaseDateMillis "

    val queryMiddle = if (platformsIndices.size == 0) {
        // If no platforms are selected, choose a WHERE clause that always returns zero rows.
        "AND Game.gameId = null "
    } else {
        // Create a LIKE clause for each platform the user has selected.
        platformsIndices.joinToString(prefix = "AND (", postfix = ")", separator = " OR ") {
            "Game.platforms LIKE '%${allPlatforms[it].abbreviation},%' "
        }
    }

    val queryEnd = "ORDER BY " +
            // This line puts any games with unknown release dates at the end of the returned list.
            "CASE WHEN Game.dateFormat IS 4 THEN 1 ELSE 0 END, " +

            // This line sets the sort direction as either ascending or descending.
            "Game.releaseDateInMillis $sortDirection"

    return SimpleSQLiteQuery(queryBeginning + queryMiddle + queryEnd)
}

@Dao
interface GameDao {

    @Query(
        "SELECT * FROM Game " +
                // This line constrains the games returned to be within the date range requested.
                "WHERE Game.releaseDateInMillis > :startReleaseDate AND Game.releaseDateInMillis < :endReleaseDate " +
                // This line constrains the games returned to the platforms specified by the user.
                "AND Game.platforms LIKE '%XSX,%'" +
                "ORDER BY " +
                // This line puts any games with unknown release dates at the end of the returned list.
                "CASE WHEN Game.dateFormat IS 4 THEN 1 ELSE 0 END," +
                // This line uses the variable sortDirection to switch between ASC and DESC order.
                "CASE WHEN :sortDirection = 'asc' THEN Game.releaseDateInMillis END ASC, CASE WHEN :sortDirection = 'desc' THEN Game.releaseDateInMillis END DESC"
    )
    fun getGames(
        sortDirection: String,
        startReleaseDate: Long,
        endReleaseDate: Long
    ): DataSource.Factory<Int, Game>


    @RawQuery(observedEntities = [Game::class])
    fun getGamesTest(query: SupportSQLiteQuery): DataSource.Factory<Int, Game>


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
