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
    startReleaseDateMillis: Long?,
    endReleaseDateMillis: Long?,
    platformsIndices: MutableSet<Int>
): SimpleSQLiteQuery {

    val queryBeginning = "SELECT * FROM Game WHERE " +
            if (startReleaseDateMillis == null) {
                // If release date type is "unknown", select only games with null release dates.
                "Game.releaseDateInMillis IS NULL "
            } else {
                // These two lines constrain the games returned to be within the date range requested.
                "Game.releaseDateInMillis > $startReleaseDateMillis " +
                        "AND Game.releaseDateInMillis < $endReleaseDateMillis "
            }

    val queryMiddle = if (platformsIndices.size == 0) {
        // If no platforms are selected, choose a WHERE clause that always returns zero rows so no games appear.
        "AND 1 = 2 "
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

    @RawQuery(observedEntities = [Game::class])
    fun getGameList(query: SupportSQLiteQuery): DataSource.Factory<Int, Game>

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
