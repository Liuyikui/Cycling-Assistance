package com.example.cyclingassis

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_history.*
import kotlinx.android.synthetic.main.activity_history.back_img
import kotlinx.android.synthetic.main.list_item.view.*

class HistoryActivity : AppCompatActivity() {
    private var historyList: java.util.ArrayList<History>? = null
    private var historyDao: HistoryDao? = null
    private var adapter: HistoryAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        historyDao = HistoryDao(this)
        //Query all records
        historyList = historyDao?.queryAll()
        if (historyList == null || historyList?.size == 0){
            historyList = ArrayList()
        }

        //Setting up the adapter
        adapter = HistoryAdapter(this, historyList!!)
        list_view.adapter = adapter

        list_view.setOnItemLongClickListener { parent, view, position, id ->
            val note = historyList?.get(position)
            val alertDialog = AlertDialog.Builder(this@HistoryActivity)
                .setTitle("Tips")
                .setMessage("are you sure delete?") //图标
                .setIcon(R.mipmap.ic_launcher)
                .setPositiveButton("ok") { dialog, which ->
                    var flag = historyDao?.deleteUser(note?.id.toString()) as Int
                    if (flag > 0) {
                        historyList!!.remove(note)
                        Toast.makeText(this@HistoryActivity, "success", Toast.LENGTH_SHORT).show()
                        adapter?.notifyDataSetChanged()
                    } else {
                        Toast.makeText(this@HistoryActivity, "error", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("cancle") { dialog, which -> }
                .create()
            alertDialog.show()

            true
        }

        back_img.setOnClickListener {
            finish()
        }
    }

    class HistoryAdapter(val context: Context,
                         val historyList: ArrayList<History>) : BaseAdapter() {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val li = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val itemView = convertView ?: li.inflate(R.layout.list_item,parent,false)
            val history = historyList[position]
            itemView.time_tv.text = history.startTime
            itemView.end_time_tv.text = history.sportTime+"Stop"
            itemView.kilometre_tv.text = history.kilometre+"km"
            return itemView
        }



        override fun getItem(position: Int): Any? {
            return null
        }

        override fun getItemId(position: Int): Long {
            return 0
        }

        override fun getCount(): Int {
            return historyList.size
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
