package com.psut.smartpv.data

data class DeviceReading(val imei:String,val lon:Double,val lat:Double,val temp:Double,val hum:Double,val energy:Double,val expected:Double,val status:Boolean) {
}