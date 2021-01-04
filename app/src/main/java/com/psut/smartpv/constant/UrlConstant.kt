package com.psut.smartpv.constant

interface UrlConstant {
    companion object {
        const val BASE_URL = "http://192.168.1.37:8082/"
        const val LOGIN_PATH = "users/mobile/isValid"
        const val SIGN_UP_PATH = "users"
        const val DEVICES_PATH = "/mobiles"
        const val ADD_DEVICE_PATH = "misc/addDeviceToUser"
        const val HISTORY_NUMBER_PATH = "mobiles/history/numbers/"
        const val HISTORY_CHART_PATH = "mobiles/history/chart/"


    }
}