package com.example.sincronizacionservicio_compose

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase

@Entity(tableName = "edificios")
data class Edificio(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val sincronizado: Boolean = false
)

@Dao
interface EdificioDao {
    @Query("SELECT * FROM edificios WHERE sincronizado = 0")
    fun getPendientes(): List<Edificio>

    @Query("UPDATE edificios SET sincronizado = 1 WHERE id IN (:ids)")
    fun marcarComoSincronizados(ids: List<Int>): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(edificios: List<Edificio>)

    @Query("SELECT COUNT(*) FROM edificios")
    fun contarTotal(): Int
}

@Database(entities = [Edificio::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun edificioDao(): EdificioDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "edificios_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}