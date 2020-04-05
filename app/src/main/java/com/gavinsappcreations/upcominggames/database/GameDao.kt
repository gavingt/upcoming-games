package com.gavinsappcreations.upcominggames.database

import android.content.Context
import androidx.paging.DataSource
import androidx.room.*
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.gavinsappcreations.upcominggames.domain.Game
import com.gavinsappcreations.upcominggames.utilities.allKnownPlatforms

@Dao
interface GameDao {

    // Gets games for ListFragment, according to filter options specified by user.
    @RawQuery(observedEntities = [Game::class])
    fun getGameList(query: SupportSQLiteQuery): DataSource.Factory<Int, Game>

    // Inserts all games retrieved from the API when updating the database.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(games: List<Game>)

    // Gets games for SearchFragment, based on the search query typed by the user.
    @Query("SELECT * FROM Game WHERE Game.gameName LIKE :query ORDER BY Game.releaseDateInMillis DESC LIMIT 200")
    fun searchGameList(query: String): List<Game>

    // Gets games for FavoriteFragment, based on games favorited by the user.
    @Query("SELECT * FROM Game WHERE Game.isFavorite = 1 ORDER BY Game.releaseDateInMillis ASC")
    fun getFavoriteList(): DataSource.Factory<Int, Game>

    // Gets the isFavorite property of a Game, so we can display the correct star drawable in DetailFragment.
    @Query("SELECT isFavorite FROM Game WHERE guid = :gameDetailGuid")
    fun getIsFavorite(gameDetailGuid: String): Boolean

    // Updates the isFavorite property of a Game when user clicks the star drawable in DetailFragment.
    @Query("UPDATE Game SET isFavorite = :isFavorite WHERE guid = :gameDetailGuid")
    fun updateFavorite(isFavorite: Boolean, gameDetailGuid: String): Int
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


/**
 * Build the game list raw query. We need a raw query here because we need to include an
 * arbitrary number of LIKE clauses for matching different platforms.
 * @param sortDirection direction of sort by release date, can be either ASC or DESC.
 * @param startReleaseDateMillis timestamp of starting release date for which to fetch games.
 * @param endReleaseDateMillis timestamp of ending release date for which to fetch games.
 * @param platformsIndices indices of platforms in [allKnownPlatforms] we're filtering for.
 * @return The SQLite query we'll use for fetching game list.
 */
fun buildGameListQuery(
    sortDirection: String,
    startReleaseDateMillis: Long?,
    endReleaseDateMillis: Long?,
    platformsIndices: Set<Int>
): SimpleSQLiteQuery {
    val queryBeginning = "SELECT * FROM Game WHERE " +
            if (startReleaseDateMillis == null) {
                // If release date type is "any", don't restrict the release dates at all.
                ""
            } else {
                // These two lines constrain the games returned to be within the date range requested.
                "Game.releaseDateInMillis >= $startReleaseDateMillis " +
                        "AND Game.releaseDateInMillis <= $endReleaseDateMillis AND "
            }

    val queryMiddle = if (platformsIndices.isEmpty()) {
        // If no platforms are selected, choose a WHERE clause that always returns zero rows so no games appear.
        "1 = 2 "
    } else {
        // Create a LIKE clause for each platform the user has selected.
        platformsIndices.joinToString(prefix = "(", postfix = ")", separator = " OR ") {
            "Game.platforms LIKE '%,${allKnownPlatforms[it].abbreviation},%' "
        }
    }

    val queryEnd = "ORDER BY " +
            // This line puts any games with unknown release dates at the end of the returned list.
            "CASE WHEN Game.dateFormat IS 4 THEN 1 ELSE 0 END, " +

            // This line sets the sort direction as either ascending or descending.
            "Game.releaseDateInMillis $sortDirection"

    return SimpleSQLiteQuery(queryBeginning + queryMiddle + queryEnd)
}