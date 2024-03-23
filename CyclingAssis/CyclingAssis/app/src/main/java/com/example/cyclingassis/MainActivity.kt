package com.example.cyclingassis


import android.app.Activity
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import com.amap.api.maps.model.MyLocationStyle
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.location.AMapLocationListener
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import android.util.Log
import java.util.*
import android.widget.TextView
import java.text.SimpleDateFormat
import kotlin.math.*


class MainActivity : AppCompatActivity() {

    private var cityCode: String = "110000"
    private var distance: Double = 0.0//kilometre
    private var mLastLat: Double = 0.0
    private var mLastLgt: Double = 0.0
    private var mActivity: Activity? = null
    private var speed: Float? = null
    private var aMap: AMap? = null
    private var address: String? = null
    private var isStart = false
    private var myTimeTask: MyTimeTask? = null//timers
    private var second: Long = 0
    private var city: String = ""
    private var history:History? = null//Current Exercise Record
    private fun startTask() {
        myTimeTask = MyTimeTask(object : TimerTask() {
            override fun run() {
                second++
                runOnUiThread {
                    if (mActivity != null && !mActivity!!.isFinishing) {
                        if (count_tv != null) {
                            count_tv.text = getTimeString(second)
                        }
                        if (average_velocity_tv != null) {
                            updateTimeAndSpeed(second, distance, average_velocity_tv)
                        }
                    }
                }
                Log.e("TAG", second.toString())
            }
        })
        myTimeTask?.startTask(1000)
    }
    private var historyDao: HistoryDao? = null

    private fun stopTask() {
        second = 0
        if (myTimeTask != null) {
            myTimeTask?.stopTask()
            myTimeTask = null
        }
    }
//The following is the Amap SDK section, I chose to keep all the original text because Amap is Chinese software.
    //声明AMapLocationClient类对象
    var mLocationClient: AMapLocationClient? = null
    //声明定位回调监听器
    var mLocationListener = AMapLocationListener { amapLocation ->
        if (amapLocation != null) {
            if (amapLocation.errorCode == 0) {
                //可在其中解析amapLocation获取相应内容。
                city = amapLocation.city
                cityCode = amapLocation.adCode
                address =
                    amapLocation.address//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                speed = amapLocation.speed
                Log.e(
                    "amapLocation", "address:"
                            + address + " speed:" + speed
                            + " city:" + city
                )

                val kilometersPerHour = speed!! * 36 / 1000 // 根据公式进行计算
                if (isStart) {
                    perhour_tv.text = String.format("%.2f", kilometersPerHour)
                    if (mLastLat != 0.0 && mLastLgt != 0.0) {
                        distance += calculateDistance(
                            mLastLat,
                            mLastLgt,
                            amapLocation.latitude,
                            amapLocation.longitude
                        )
                    } else {
                        mLastLat = amapLocation.latitude
                        mLastLgt = amapLocation.longitude
                    }
                    val formattedNumber = String.format("%.2f", distance)
                    distance_tv.text = formattedNumber
                }
            } else {
                //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                Log.e(
                    "AmapError", "location Error, ErrCode:"
                            + amapLocation.errorCode + ", errInfo:"
                            + amapLocation.errorInfo
                )
            }
        }
    }
    //声明AMapLocationClientOption对象
    var mLocationOption: AMapLocationClientOption? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        map_view.onCreate(savedInstanceState)
        mActivity = this

        aMap = map_view.map
        //初始化数据库
        historyDao = HistoryDao(this)
        val myLocationStyle =
            MyLocationStyle()//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.showMyLocation(true)



        myLocationStyle.interval(5000) //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        aMap?.myLocationStyle = myLocationStyle//设置定位蓝点的Style
        //aMap.getUiSettings().setMyLocationButtonEnabled(true);设置默认定位按钮是否显示，非必需设置。
        aMap?.isMyLocationEnabled = true// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        aMap?.moveCamera(CameraUpdateFactory.zoomTo(java.lang.Float.valueOf(18f)))
        initLoaction()
        start_tv.setOnClickListener {
            isStart = !isStart
            if (isStart) {
                start_tv.text = "stop"
                startTask()
                //Create a new record
                history = History()
                history?.startTime = getCurrentDateString()
            } else {
                if (history != null){
                    //Setting the recorded data
                    history?.endTime = getCurrentDateString()
                    history?.sportTime = count_tv.text.toString()
                    history?.kilometre = distance_tv.text.toString()
                    if (historyDao?.insert(history!!) as Int > 0){

                    }
                }
                stopTask()
                start_tv.text = "start"
                count_tv.text = getTimeString(second)
                average_velocity_tv.text = "0.00"
                distance_tv.text = "0.00"
                perhour_tv.text = "0.00"
                distance = 0.0

            }
        }

        weather_tv.setOnClickListener {
            startActivity(Intent(this,WeatherActivity::class.java)
                .putExtra("city",city)
                .putExtra("cityCode",cityCode))
        }
        history_tv.setOnClickListener {
            startActivity(Intent(this,HistoryActivity::class.java))
        }

    }

    private fun initLoaction() {
        //初始化定位
        mLocationClient = AMapLocationClient(applicationContext)
        //设置定位回调监听
        mLocationClient?.setLocationListener(mLocationListener)
        //初始化AMapLocationClientOption对象
        mLocationOption = AMapLocationClientOption()
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption?.locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption?.isNeedAddress = true
        //单位是毫秒，默认30000毫秒，建议超时时间不要低于8000毫秒。
        mLocationOption?.httpTimeOut = 20000
        mLocationOption?.isOnceLocation = false
        mLocationOption?.interval = 5000
        mLocationOption?.isWifiScan = true

        /**
         * 设置定位场景，目前支持三种场景（签到、出行、运动，默认无场景）
         */
        mLocationOption?.locationPurpose = AMapLocationClientOption.AMapLocationPurpose.Sport
        if (null != mLocationClient) {
            mLocationClient?.setLocationOption(mLocationOption)
            //设置场景模式后最好调用一次stop，再调用start以保证场景模式生效
            mLocationClient?.stopLocation()
            mLocationClient?.startLocation()
        }
    }


    /**
     * 获取当前日期字符串
     *
     */
    fun getCurrentDateString(): String {
        val df = SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA)
        return df.format(Date())
    }

    override fun onDestroy() {
        super.onDestroy()
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        map_view.onDestroy()
        stopTask()
    }

    override fun onResume() {
        super.onResume()
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        map_view.onResume()
    }

    override fun onPause() {
        super.onPause()
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        map_view.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        map_view.onSaveInstanceState(outState)
    }


    /**
     * 更新时间显示和计算平均速度
     */
    private fun updateTimeAndSpeed(
        totalSeconds: Long,
        totalDistance: Double,
        speedTextView: TextView
    ) {
        if (totalSeconds > 0 && totalDistance > 0) {
            val averageSpeed = totalDistance / totalSeconds // 单位：米/秒
            speedTextView.text = String.format("%.2f", averageSpeed) // 保留两位小数
        } else {
            speedTextView.text = "0.00"
        }
    }

    /**
     * 把时间转换为：时分秒
     * @param millisecond
     * @return
     */
    fun getTimeString(millisecond: Long): String {
        var string = ""
        val hours = millisecond / 3600
        val minutes = millisecond % 3600 / 60
        val seconds = millisecond % 60
        var hoursStr = ""
        var minutesStr = ""
        var secondsStr = ""
        hoursStr = if (hours > 9) {
            hours.toString()
        } else {
            "0$hours"
        }
        minutesStr = if (minutes > 9) {
            minutes.toString()
        } else {
            "0$minutes"
        }
        secondsStr = if (seconds > 9) {
            seconds.toString()
        } else {
            "0$seconds"
        }
        string = if (hours == 0L) {
            "$minutesStr:$secondsStr"
        } else {
            "$hoursStr:$minutesStr:$secondsStr"
        }
        return string
    }


    fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        var lat1 = lat1
        var lat2 = lat2
        val EARTH_RADIUS = 6371 // Radius of the Earth in kilometres
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        lat1 = Math.toRadians(lat1)
        lat2 = Math.toRadians(lat2)
        val a = sin(dLat / 2).pow(2.0) + cos(lat1) * cos(lat2) * sin(dLon / 2).pow(2.0)
        var c = 2 * atan2(sqrt(a), sqrt(1 - a))
        c /= 10
        return EARTH_RADIUS * c
    }

}
