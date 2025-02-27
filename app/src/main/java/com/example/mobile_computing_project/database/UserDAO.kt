package com.example.mobile_computing_project.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

//@Dao
//interface UserDao {
//    @Query("SELECT * FROM users")
//    fun getAll(): List<User>
//
//    @Query("SELECT * FROM users WHERE uid IN (:userIds)")
//    fun loadAllByIds(userIds: IntArray): List<User>
//
////    @Query("SELECT * FROM user WHERE first_name LIKE :first AND " +
////            "last_name LIKE :last LIMIT 1")
////    fun findByName(first: String, last: String): User
//
//    @Query("SELECT profile_picture_path FROM users WHERE uid LIKE (:userId)")
//    fun loadProfilePicturePath(userId: Int): String?
//
//    @Insert
//    suspend fun insertAll(vararg users: User)
//
//    @Delete
//    suspend fun delete(user: User)
//}
