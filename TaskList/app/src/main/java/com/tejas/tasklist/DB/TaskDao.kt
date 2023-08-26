package com.tejas.tasklist.DB

import androidx.room.*

@Dao
interface TaskDao {

    @Insert
    suspend fun insertTask(task: Task)

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("SELECT * FROM task ORDER BY time DESC")
    suspend fun showAllTask(): List<Task>

    @Query("SELECT * FROM task WHERE title LIKE :searchText OR desc LIKE :searchText ORDER BY time DESC")
    suspend fun searchTasks(searchText: String): List<Task>

    @Query("SELECT * FROM task WHERE id LIKE :id")
    suspend fun deleteTaskById(id: Int): Task

}