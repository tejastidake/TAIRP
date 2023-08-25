package com.tejas.tasklist.DB

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TaskDao {

    @Insert
    suspend fun insertTask(task: Task)

    @Query("SELECT * FROM task ORDER BY time DESC")
    suspend fun showAllTask(): List<Task>

    @Query("SELECT * FROM task WHERE title LIKE :searchText OR desc LIKE :searchText ORDER BY time DESC")
    suspend fun searchTasks(searchText: String): List<Task>

}