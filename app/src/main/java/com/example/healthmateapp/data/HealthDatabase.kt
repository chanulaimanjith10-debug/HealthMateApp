package com.example.healthmateapp.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class HealthDatabase(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_NAME (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                date TEXT NOT NULL,
                steps INTEGER NOT NULL,
                calories INTEGER NOT NULL,
                water INTEGER NOT NULL
            )
        """.trimIndent()

        db.execSQL(createTable)
    }

    fun insertRecord(record: HealthRecord): Long {
        val values = ContentValues().apply {
            put("date", record.date)
            put("steps", record.steps)
            put("calories", record.calories)
            put("water", record.water)
        }

        return writableDatabase.insert(TABLE_NAME, null, values)
    }

    fun getAllRecords(): List<HealthRecord> {
        val records = mutableListOf<HealthRecord>()

        val cursor = readableDatabase.query(
            TABLE_NAME,
            arrayOf("id", "date", "steps", "calories", "water"),
            null,
            null,
            null,
            null,
            "date DESC, id DESC"
        )

        cursor.use {
            while (it.moveToNext()) {
                records.add(
                    HealthRecord(
                        id = it.getInt(it.getColumnIndexOrThrow("id")),
                        date = it.getString(it.getColumnIndexOrThrow("date")),
                        steps = it.getInt(it.getColumnIndexOrThrow("steps")),
                        calories = it.getInt(it.getColumnIndexOrThrow("calories")),
                        water = it.getInt(it.getColumnIndexOrThrow("water"))
                    )
                )
            }
        }

        return records
    }

    fun getRecordByDate(date: String): HealthRecord? {
        val cursor = readableDatabase.query(
            TABLE_NAME,
            arrayOf("id", "date", "steps", "calories", "water"),
            "date = ?",
            arrayOf(date),
            null,
            null,
            "id DESC",
            "1"
        )

        cursor.use {
            if (it.moveToFirst()) {
                return HealthRecord(
                    id = it.getInt(it.getColumnIndexOrThrow("id")),
                    date = it.getString(it.getColumnIndexOrThrow("date")),
                    steps = it.getInt(it.getColumnIndexOrThrow("steps")),
                    calories = it.getInt(it.getColumnIndexOrThrow("calories")),
                    water = it.getInt(it.getColumnIndexOrThrow("water"))
                )
            }
        }

        return null
    }

    fun updateRecord(record: HealthRecord): Int {
        val values = ContentValues().apply {
            put("date", record.date)
            put("steps", record.steps)
            put("calories", record.calories)
            put("water", record.water)
        }

        return writableDatabase.update(
            TABLE_NAME,
            values,
            "id = ?",
            arrayOf(record.id.toString())
        )
    }

    fun deleteRecord(id: Int): Int {
        return writableDatabase.delete(
            TABLE_NAME,
            "id = ?",
            arrayOf(id.toString())
        )
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        // Add migrations if the table changes.
    }

    companion object {
        private const val DATABASE_NAME = "healthmate.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "health_records"
    }
}