package com.example.abschlussaufgabe.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.abschlussaufgabe.data.datamodels.Entry

@Dao
interface EntryDatabaseDao {
	
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertAll(entries: List<Entry>)
	
	@Update
	suspend fun update(entry: Entry)
	
	@Query("SELECT * FROM entry")
	fun getAll(): LiveData<List<Entry>>
	
	@Query("SELECT * FROM entry WHERE id = :id")
    fun getById(id: Long): LiveData<Entry>
	
	@Query("DELETE FROM entry")
	suspend fun deleteAll(): Int
	
	@Query("DELETE FROM entry WHERE id = :id")
    suspend fun delete(id: Long): Int
}
