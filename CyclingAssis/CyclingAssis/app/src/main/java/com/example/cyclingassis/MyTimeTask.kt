package com.example.cyclingassis

import java.util.*

class MyTimeTask(task:TimerTask) {
    private var timer: Timer?
    private val task: TimerTask?

    init {
        this.task = task
        timer = Timer()
    }

//From Amap SDK
    /**
     * .每隔time毫秒启动一次
     *
     * @param time 间隔时间.单位毫秒
     */
    fun startTask(time: Long) {
        timer!!.schedule(task!!, 0, time)
    }

    /**
     * .取消定时任务
     */
    fun stopTask() {
        if (timer != null) {
            timer!!.cancel()
            task?.cancel()
        }
    }
}