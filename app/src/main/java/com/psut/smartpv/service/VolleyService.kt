package com.psut.smartpv.service

import android.content.Context
import android.util.Log
import com.android.volley.*
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.psut.smartpv.constant.UrlConstant
import org.json.JSONArray
import org.json.JSONObject


class VolleyService private constructor() {


    companion object {
        private var volleyService = VolleyService()

        fun getInstance(): VolleyService {
            return volleyService
        }
    }

    fun postJSONObject(
        url: String,
        params: MutableMap<String, String>,
        context: Context,
        completionHandler: (response: JSONObject?) -> Unit,
        errorHandler: (error: VolleyError?) -> Unit
    ) {
        val volleyRequestQueue = Volley.newRequestQueue(context)
        val jsonObject = JSONObject(params as Map<*, *>)
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, url, jsonObject, { response ->
                Log.i("", "post request OK! Response: $response")
                completionHandler(response)
            }, { error ->
                VolleyLog.e("error in volley: $error")
                errorHandler(error)

            }
        )
        volleyRequestQueue.add(jsonObjectRequest)


    }

    fun getJSONObject(
        url: String,
        id: String,
        context: Context,
        completionHandler: (response: JSONObject?) -> Unit,
        errorHandler: (error: VolleyError?) -> Unit
    ) {
        val volleyRequestQueue = Volley.newRequestQueue(context)

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, "${UrlConstant.BASE_URL + url}/$id", null, { response ->
                Log.i("", "post request OK! Response: $response")
                completionHandler(response)
            }, { error ->
                VolleyLog.e("error in volley: $error")
                errorHandler(error)

            }
        )
        jsonObjectRequest.retryPolicy =
            DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
        volleyRequestQueue.add(jsonObjectRequest)


    }

    fun postJSONArray(
        url: String,
        obj: List<Any>,
        context: Context,
        completionHandler: (response: JSONArray?) -> Unit,
        errorHandler: (error: VolleyError?) -> Unit
    ) {
        val volleyRequestQueue = Volley.newRequestQueue(context)
        val jsonObject = JSONArray(obj)
        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.POST, url, jsonObject, { response ->
                Log.i("", "post request OK! Response: $response")
                completionHandler(response)
            }, { error ->
                VolleyLog.e("error in volley: $error")
                errorHandler(error)

            }
        )
        volleyRequestQueue.add(jsonArrayRequest)


    }

    fun getJSONArray(
        url: String,

        context: Context,
        completionHandler: (response: JSONArray?) -> Unit,
        errorHandler: (error: VolleyError?) -> Unit
    ) {
        val volleyRequestQueue = Volley.newRequestQueue(context)

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, UrlConstant.BASE_URL + url, null, { response ->
                Log.i("", "get request OK! Response: $response")
                completionHandler(response)
            }, { error ->
                VolleyLog.e("error in volley: $error")
                errorHandler(error)

            }
        )
        volleyRequestQueue.add(jsonArrayRequest)


    }

    fun postStringRequest(
        url: String,
        params: MutableMap<String, String>,
        context: Context,
        completionHandler: (response: String?) -> Unit,
        errorHandler: (error: VolleyError?) -> Unit
    ) {
        val volleyRequestQueue = Volley.newRequestQueue(context)
        val jsonObjectRequest =
            object : StringRequest(Request.Method.POST, UrlConstant.BASE_URL + url, { response ->
                completionHandler(response)

            }, { error ->
                VolleyLog.e("error in volley: ${error.message}")
                errorHandler(error)


            }) {
                override fun getParams(): MutableMap<String, String> {
                    return params
                }
            }


        volleyRequestQueue.add(jsonObjectRequest)


    }

    fun getJSONArray(
        url: String,
        id: String,
        context: Context,
        completionHandler: (response: JSONArray?) -> Unit,
        errorHandler: (error: VolleyError?) -> Unit
    ) {
        val volleyRequestQueue = Volley.newRequestQueue(context)

        val jsonObjectRequest = JsonArrayRequest(
            Request.Method.GET, "${UrlConstant.BASE_URL + url}/$id", null, { response ->
                Log.i("", "get request OK! Response: $response")
                completionHandler(response)
            }, { error ->
                VolleyLog.e("error in volley: $error")
                errorHandler(error)

            }
        )
        jsonObjectRequest.retryPolicy =
            DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
        volleyRequestQueue.add(jsonObjectRequest)


    }
}