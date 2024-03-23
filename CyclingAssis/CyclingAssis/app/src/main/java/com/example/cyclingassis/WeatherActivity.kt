package com.example.cyclingassis

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_weather.*
import okhttp3.*
import java.io.IOException

//Some of the code on this page is from the Amap Weather API.
class WeatherActivity : AppCompatActivity() {
    private val key: String = "0fc036bc1f28d8815beffaa0309733a7"
    private var adcode: String = "110000"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)
        title_tv.text = intent.getStringExtra("city")
        val code = intent.getStringExtra("cityCode")
        if (code!!.isNotEmpty()) {
            adcode = code
        }
        back_img.setOnClickListener {
            finish()
        }

        getWeatherData()


        search_img.setOnClickListener {
            val builder = AlertDialog.Builder(this@WeatherActivity)
            val view =
                LayoutInflater.from(this@WeatherActivity).inflate(R.layout.dialog_choosepage, null)
            val cancel = view.findViewById(R.id.choosepage_cancel) as TextView
            val sure = view.findViewById(R.id.choosepage_sure) as TextView
            val edittext = view.findViewById(R.id.choosepage_edittext) as EditText
            val codeEdittext = view.findViewById(R.id.code_edittext) as EditText
            val dialog = builder.create()
            dialog.show()
            dialog.window?.setContentView(view)
            //使editext可以唤起软键盘
            dialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
            cancel.setOnClickListener(View.OnClickListener {

                dialog.dismiss()
            })
            sure.setOnClickListener(View.OnClickListener {
                val codeStr = codeEdittext.text.toString()
                val cityStr = edittext.text.toString()
                if (cityStr.isNotEmpty() && codeStr.isNotEmpty()){
                    title_tv.text = cityStr
                    adcode = codeStr
                    getWeatherData()
                }
                dialog.dismiss()
            })


        }
    }

    private fun getWeatherData() {
        //Creating an instance of OkHttpClient：
        val client = OkHttpClient()
        //Creating a Request object：
        val request = Request.Builder()
            .url(
                "https://restapi.amap.com/v3/weather/weatherInfo?city=" + adcode
                        + "&key=" + key + "&extensions=all"
            )
            .build()

        //Create a Gson object to parse JSON
        val gson = Gson()
        Thread {
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    // Handling of failure conditions, such as displaying error messages or logging
                    Log.e("TAg", call.toString() + "  " + e.printStackTrace())

                }

                override fun onResponse(call: Call, response: Response) {
                    if (!response.isSuccessful) {
                        // Handling non-successful response situations, such as displaying an error message or log entry
                        return
                    }
                    val responseBody = response.body?.string() ?: return
                    Log.e("TAg", responseBody)
                    val data = gson.fromJson(
                        responseBody,
                        JsonRootBean::class.java
                    )
                    if (data != null) {
                        if (data.forecasts != null && data.forecasts!!.isNotEmpty()) {
                            runOnUiThread {
                                val mForecasts = data.forecasts!![0]
                                if (mForecasts.casts != null) {
                                    if (mForecasts.casts!!.isNotEmpty()) {
                                        temperature_tv.text = mForecasts.casts!![0].daytemp + "℃"
                                        if (mForecasts.casts!![0].dayweather!!.contains("雨")) {
                                            img.setImageResource(R.drawable.rain)
                                        }
                                        if (mForecasts.casts!![0].dayweather!!.contains("阴") ||
                                            mForecasts.casts!![0].dayweather!!.contains("多云")
                                        ) {
                                            img.setImageResource(R.drawable.cloudy)
                                        }
                                        if (mForecasts.casts!![0].dayweather!!.contains("晴")) {
                                            img.setImageResource(R.drawable.ic_sun)
                                        }
                                    }
                                    if (mForecasts.casts!!.size > 1) {
                                        temperature_tv1.text =
                                            mForecasts.casts!![1].daytemp + "℃~" + mForecasts.casts!![1].nighttemp + "℃"
                                        weather_tv1.text = "明天 " + mForecasts.casts!![1].dayweather
                                        if (mForecasts.casts!![1].dayweather!!.contains("雨")) {
                                            img1.setImageResource(R.drawable.rain)
                                        }
                                        if (mForecasts.casts!![1].dayweather!!.contains("阴") ||
                                            mForecasts.casts!![1].dayweather!!.contains("多云")
                                        ) {
                                            img1.setImageResource(R.drawable.cloudy)
                                        }
                                        if (mForecasts.casts!![1].dayweather!!.contains("晴")) {
                                            img1.setImageResource(R.drawable.ic_sun)
                                        }
                                    }
                                    if (mForecasts.casts!!.size > 2) {
                                        temperature_tv2.text =
                                            mForecasts.casts!![2].daytemp + "℃~" + mForecasts.casts!![2].nighttemp + "℃"
                                        weather_tv2.text = "后天 " + mForecasts.casts!![2].dayweather

                                        if (mForecasts.casts!![2].dayweather!!.contains("雨")) {
                                            img2.setImageResource(R.drawable.rain)
                                        }
                                        if (mForecasts.casts!![2].dayweather!!.contains("阴") ||
                                            mForecasts.casts!![2].dayweather!!.contains("多云")
                                        ) {
                                            img2.setImageResource(R.drawable.cloudy)
                                        }
                                        if (mForecasts.casts!![2].dayweather!!.contains("晴")) {
                                            img2.setImageResource(R.drawable.ic_sun)
                                        }
                                    }
                                    if (mForecasts.casts!!.size > 3) {
                                        temperature_tv3.text =
                                            mForecasts.casts!![3].daytemp + "℃~" + mForecasts.casts!![3].nighttemp + "℃"
                                        weather_tv3.text =
                                            mForecasts.casts!![3].date + " " + mForecasts.casts!![3].dayweather
                                        if (mForecasts.casts!![3].dayweather!!.contains("雨")) {
                                            img3.setImageResource(R.drawable.rain)
                                        }
                                        if (mForecasts.casts!![3].dayweather!!.contains("阴") ||
                                            mForecasts.casts!![3].dayweather!!.contains("多云")
                                        ) {
                                            img3.setImageResource(R.drawable.cloudy)
                                        }
                                        if (mForecasts.casts!![3].dayweather!!.contains("晴")) {
                                            img3.setImageResource(R.drawable.ic_sun)
                                        }
                                    }
                                }
                            }

                        }
                    }
                }
            })
        }.start()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
