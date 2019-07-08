package ca.meshytama.kotlindeathmonster.model

import android.content.Context
import android.util.Log
import androidx.annotation.RawRes
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import ca.meshytama.kotlindeathmonster.R
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray

@Database(entities = [Expansion::class, Disorder::class, FightingArt::class, GlossaryEntry::class], version = 1)
abstract class KdmDatabase : RoomDatabase() {
    abstract fun expansionsDao(): ExpansionsDao
    abstract fun disordersDao(): DisordersDao
    abstract fun fightingArtsDao(): FightingArtsDao
    abstract fun glossaryDao(): GlossaryDao

    object Factory {

        fun createDatabase(name: CharSequence, context: Context, initializer: Callback): KdmDatabase {
            return Room.databaseBuilder(
                    context.applicationContext,
                    KdmDatabase::class.java,
                    name.toString()
            ).addCallback(initializer).build()
        }
    }

    abstract class Initializer(private val context: Context) : RoomDatabase.Callback() {

        companion object {
            val TAG: String = Initializer::class.java.name
        }

        /**
         * TODO
         */
        abstract fun getDatabase(): KdmDatabase

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            Log.i ( TAG, "Pre-populating DB")

            // TODO use a proper scope?
            GlobalScope.launch {
                with(getDatabase()) {
                    addExpansions(expansionsDao())
                    addDisorders(disordersDao())
                    addFightingArts(fightingArtsDao())
                    addGlossary(glossaryDao())
                }

                Log.i ( TAG, "Done pre-populating DB")
            }
        }

        private suspend fun addExpansions(dao: ExpansionsDao) {

            Log.i ( TAG, "Adding expansions")

            val json = getJsonArrayFromResource(R.raw.expansions)

            for (i in 0 until json.length()) {
                with(json.getJSONObject(i)) {
                    dao.insert(Expansion(name = getString("name")))
                }
            }
        }

        private suspend fun addDisorders(dao: DisordersDao) {

            Log.i ( TAG, "Adding disorders")

            val json = getJsonArrayFromResource(R.raw.disorders)

            for (i in 0 until json.length()) {
                with(json.getJSONObject(i)) {
                    dao.insert(Disorder(
                            name = getString("name"),
                            description = getString("description"),
                            expansion = getString("expansion")))
                }
            }
        }

        private suspend fun addFightingArts(dao: FightingArtsDao) {

            Log.i ( TAG, "Adding fighting arts")

            val json = getJsonArrayFromResource(R.raw.fighting_arts)

            for (i in 0 until json.length()) {
                with(json.getJSONObject(i)) {
                    dao.insert(FightingArt(
                            name = getString("name"),
                            description = getString("description"),
                            expansion = getString("expansion")))
                }
            }
        }

        private suspend fun addGlossary(dao: GlossaryDao) {

            Log.i ( TAG, "Adding glossary")

            val json = getJsonArrayFromResource(R.raw.glossary)

            for (i in 0 until json.length()) {
                with(json.getJSONObject(i)) {
                    dao.insert(GlossaryEntry(
                            name = getString("name"),
                            description = getString("description")))
                }
            }
        }

        private fun getJsonArrayFromResource(@RawRes keywordsFile: Int): JSONArray {
            return JSONArray(context.resources.openRawResource(keywordsFile).bufferedReader().use { it.readText() })
        }
    }
}