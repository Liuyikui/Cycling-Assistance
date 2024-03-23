package com.example.cyclingassis

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context)//Construct the database and pass in the database name and version number.
    : SQLiteOpenHelper(context, dbname, null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        //id，startTime，endTime，kilometre
        db.execSQL(
            ("create table if not exists "+tb_name+
                    "(id integer primary key  AUTOINCREMENT," +
                    "startTime varchar(100)," +
                    "endTime varchar(100)," +
                    "sportTime varchar(100)," +
                    "kilometre varchar(200))")
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

    }

    companion object {
        val dbname = "mydb"//Database name
        val tb_name = "history"//table name

    }
}

