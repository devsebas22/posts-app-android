package com.example.postsapp.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.postsapp.data.local.dao.CommentDao
import com.example.postsapp.data.local.dao.PostDao
import com.example.postsapp.data.local.entities.CommentEntity
import com.example.postsapp.data.local.entities.PostEntity

@Database(
    entities = [PostEntity::class, CommentEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun commentDao(): CommentDao
}