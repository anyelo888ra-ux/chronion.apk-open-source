package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarks")
data class Bookmark(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val url: String,
    val folder: String = "General",
    val tags: String = "",
    val faviconUrl: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val isSynced: Boolean = true
)

@Entity(tableName = "history")
data class HistoryItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val url: String,
    val timestamp: Long = System.currentTimeMillis(),
    val visitCount: Int = 1,
    val isIncognito: Boolean = false
)

@Entity(tableName = "userscripts")
data class UserScript(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String,
    val author: String = "Chronioñ Community",
    val version: String = "1.0",
    val matchUrlPattern: String = "*", // * for all websites or specific domains
    val jsCode: String = "",
    val cssCode: String = "",
    val isEnabled: Boolean = true,
    val isBuiltIn: Boolean = false
)
