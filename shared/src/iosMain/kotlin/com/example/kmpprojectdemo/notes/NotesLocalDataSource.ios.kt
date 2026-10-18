package com.example.kmpprojectdemo.notes

import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.example.kmpprojectdemo.db.AppDatabase
import com.example.kmpprojectdemo.db.SqlDelightNotesLocalDataSource

actual fun createNotesLocalDataSource(): NotesLocalDataSource {
    val driver = NativeSqliteDriver(AppDatabase.Schema, "smartnotes.db")
    return SqlDelightNotesLocalDataSource(driver)
}
