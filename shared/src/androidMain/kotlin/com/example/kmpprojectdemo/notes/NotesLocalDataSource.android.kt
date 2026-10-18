package com.example.kmpprojectdemo.notes

import android.content.Context
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.example.kmpprojectdemo.data.AndroidAppContext
import com.example.kmpprojectdemo.db.AppDatabase
import com.example.kmpprojectdemo.db.SqlDelightNotesLocalDataSource

actual fun createNotesLocalDataSource(): NotesLocalDataSource {
    val context = (AndroidAppContext.context as? Context)
    return if (context != null) {
        val driver = AndroidSqliteDriver(AppDatabase.Schema, context, "smartnotes.db")
        SqlDelightNotesLocalDataSource(driver)
    } else {
        InMemoryNotesLocalDataSource()
    }
}
