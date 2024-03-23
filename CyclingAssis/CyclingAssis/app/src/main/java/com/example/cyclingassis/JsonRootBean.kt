package com.example.cyclingassis




class JsonRootBean {
    var status: String? = null
    var count: String? = null
    var info: String? = null
    var infocode: String? = null
    var forecasts: List<Forecasts>? = null
}

class Forecasts {

     var city: String? = null
     var adcode: String? = null
     var province: String? = null
     var reporttime: String? = null
     var casts: List<Casts>? = null

}

class Casts {
     var date: String? = null
     var week: String? = null
     var dayweather: String? = null
     var nightweather: String? = null
     var daytemp: String? = null
     var nighttemp: String? = null
     var daywind: String? = null
     var nightwind: String? = null
     var daypower: String? = null
     var nightpower: String? = null
     var daytemp_float: String? = null
     var nighttemp_float: String? = null
}
