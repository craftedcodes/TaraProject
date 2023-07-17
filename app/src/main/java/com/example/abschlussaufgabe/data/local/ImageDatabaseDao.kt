// Package declaration
package com.example.abschlussaufgabe.data.local

// Import statements
import android.media.Image
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

// Annotation to indicate that this is a DAO (Data Access Object) interface
@Dao
interface ImageDatabaseDao {
	
	// Annotation to indicate that this is an insert method
	// The onConflict strategy is set to REPLACE, which means that if an image with the same primary key already exists, it will be replaced
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	
	// Function to insert an image into the database
	// The function is marked with 'suspend' keyword to indicate that it is a suspending function and should be called from a coroutine or another suspending function
	suspend fun insertImage(image: Image)
	
	// Annotation to indicate that this is an update method
	@Update
	
	// Function to update an image in the database
	// The function is marked with 'suspend' keyword to indicate that it is a suspending function and should be called from a coroutine or another suspending function
	suspend fun updateImage(image: Image)
	
	// Annotation to indicate that this is a delete method
	@Delete
	
	// Function to delete an image from the database
	// The function is marked with 'suspend' keyword to indicate that it is a suspending function and should be called from a coroutine or another suspending function
	suspend fun deleteImage(image: Image)
	
	// Annotation to indicate that this is a query method
	// The SQL query is to select an image by its id from the 'image_result' table
	@Query("SELECT * FROM image_result WHERE id = :id")
	
	// Function to get an image by its id from the database
	// The function returns a LiveData object which can be observed for changes
	fun getImageById(id: Long): LiveData<Image?>
	
	// Annotation to indicate that this is a query method
	// The SQL query is to select all images from the 'image_result' table
	@Query("SELECT * FROM image_result")
	
	// Function to get all images from the database
	// The function returns a LiveData object which can be observed for changes
	fun getAllImages(): LiveData<List<Image>>
	
	// Annotation to indicate that this is a query method
	// The SQL query is to delete all images from the 'image_result' table
	@Query("DELETE FROM image_result")
	
	// Function to delete all images from the database
	// The function is marked with 'suspend' keyword to indicate that it is a suspending function and should be called from a coroutine or another suspending function
	suspend fun deleteAllImages()
	
	// Annotation to indicate that this is a query method
	// The SQL query is to delete an image by its id from the 'image_result' table
	@Query("DELETE FROM image_result WHERE id = :id")
	
	// Function to delete an image by its id from the database
	// The function is marked with 'suspend' keyword to indicate that it is a suspending function and should be called from a coroutine or another suspending function
	suspend fun deleteImageById(id: Long)
}
