package com.gavinsappcreations.upcominggames.database

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface GameDao {
    @Query("SELECT * FROM DatabaseGame ORDER BY DatabaseGame.releaseDateInMillis")
    fun getGames(): LiveData<List<DatabaseGame>>

/*    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(games: List<DatabaseGame>)*/

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(games: List<DatabaseGame>): List<Long>

    @Update
    fun update(games: List<DatabaseGame>)

    @Transaction
    fun insertOrUpdate(games: List<DatabaseGame>) {
        val insertResult = insert(games)
        val updateList = mutableListOf<DatabaseGame>()

        for (i in insertResult.indices) {
            if (insertResult[i] == -1L) {
                updateList.add(games[i])
            }
        }

        if (updateList.isNotEmpty()) {
            update(updateList)
        }
    }

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
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                GamesDatabase::class.java,
                "games"
            ).build()
        }
    }
    return INSTANCE
}
