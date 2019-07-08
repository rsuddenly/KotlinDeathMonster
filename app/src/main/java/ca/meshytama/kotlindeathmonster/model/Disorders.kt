package ca.meshytama.kotlindeathmonster.model

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query

@Entity(tableName = "disorders",
        foreignKeys = [ForeignKey(
                entity = Expansion::class,
                parentColumns = arrayOf("name"),
                childColumns = arrayOf("expansion"))]
)
data class Disorder(
        @PrimaryKey val name: String,
        val description: String,
        val expansion: String
)

@Dao
interface DisordersDao {
    @Query("SELECT * from disorders")
    suspend fun getAll(): List<Disorder>

    @Query("SELECT * from disorders ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandom(): Disorder

    @Insert
    suspend fun insert(disorder:Disorder)
}