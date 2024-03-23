package com.example.cyclingassis

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import java.util.ArrayList

class HistoryDao(context: Context) {
    var db: SQLiteDatabase? = null
    var sqLite: DatabaseHelper? = null

    init {
        sqLite = DatabaseHelper(context)
    }

    fun insert(history: History): Int {
        var flag = -1
        db = sqLite?.writableDatabase
        val values = ContentValues()
        // values.put("userno",history.getId());
        values.put("startTime", history.startTime)
        values.put("endTime", history.endTime)
        values.put("sportTime", history.sportTime)
        values.put("kilometre", history.kilometre)
        flag = db?.insert(DatabaseHelper.tb_name, null, values)!!.toInt()
        db?.close()
        return flag
    }

    //Delete data
    fun deleteUser(id: String): Int {
        var flag = -1
        db = sqLite?.writableDatabase
        flag = db!!.delete(DatabaseHelper.tb_name, "id = ?", arrayOf(id + ""))
        db?.close()
        return flag
    }

    //Updated data
    fun updateUser(history: History): Int? {
        db = sqLite?.writableDatabase
        val values = ContentValues()
        val number = db?.update(
            DatabaseHelper.tb_name, values, "id = ?",
            arrayOf(history.id.toString())
        )
        db?.close()
        return number
    }

    //Query data
    fun queryAll(): ArrayList<History> {
        var list: ArrayList<History>
        db = sqLite?.writableDatabase
        val cursor = db?.query(DatabaseHelper.tb_name, null, null, null, null, null, null)
        list = convertFromCursor(cursor)
        return list
    }

    //Organising query data into collections
    private fun convertFromCursor(cursor: Cursor?): ArrayList<History> {
        val list = ArrayList<History>()
        if (cursor != null && cursor.moveToFirst()) {
            //Iterate through the entire query result set with a cursor
            do {
                val id = cursor.getInt(cursor.getColumnIndex("id"))
                val startTime = cursor.getString(cursor.getColumnIndex("startTime"))
                val endTime = cursor.getString(cursor.getColumnIndex("endTime"))
                val sportTime = cursor.getString(cursor.getColumnIndex("sportTime"))
                val kilometre = cursor.getString(cursor.getColumnIndex("kilometre"))
                val history = History()
                history.startTime = startTime
                history.endTime = endTime
                history.sportTime = sportTime
                history.kilometre = kilometre
                history.id = id
                list.add(history)
            } while (cursor.moveToNext())
        }
        return list
    }
}
