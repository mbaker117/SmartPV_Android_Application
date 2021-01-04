package com.psut.smartpv.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartView
import com.google.gson.GsonBuilder
import com.psut.smartpv.R
import com.psut.smartpv.constant.DataConstant
import com.psut.smartpv.constant.UrlConstant
import com.psut.smartpv.data.HistoryChart
import com.psut.smartpv.handler.CustomProgressDialogHandler
import com.psut.smartpv.service.ChartService
import com.psut.smartpv.service.VolleyService
import com.psut.smartpv.util.SharedPreferencesUtil

class HistoryChartFragment : Fragment() {
    private val progressDialog = CustomProgressDialogHandler()
    private lateinit var chart: AAChartView
    private lateinit var itemsSwipeToRefresh: SwipeRefreshLayout
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.history_chart, container, false)
        progressDialog.show(context!!)
        chart = view.findViewById<AAChartView>(R.id.aa_chart_view)
        val imei = SharedPreferencesUtil.getString(activity!!.applicationContext, DataConstant.IMEI).toString()
        getData(imei, context!!)
        itemsSwipeToRefresh = view.findViewById<SwipeRefreshLayout>(R.id.itemsswipetorefresh)
        itemsSwipeToRefresh.setProgressBackgroundColorSchemeColor(
            ContextCompat.getColor(
                context!!,
                R.color.white
            )
        )
        itemsSwipeToRefresh.setColorSchemeColors(
            ContextCompat.getColor(
                context!!,
                R.color.mainColor
            )
        )

        itemsSwipeToRefresh.setOnRefreshListener {
            getData(imei, context!!)
        }
        return view
    }

    private fun getData(imei: String, context: Context) {
        VolleyService.getInstance()
            .getJSONObject(
                UrlConstant.HISTORY_CHART_PATH, imei, context, { response ->

                    if (response != null) {
                        val gson = GsonBuilder().create()
                        val historyData =
                            gson.fromJson(response.toString(), HistoryChart::class.java)
                        val model = ChartService.getInstance().getAAModel(
                            historyData.energyData,
                            historyData.expectedData,
                            historyData.date,
                            getString(R.string.chart_title),
                            getString(R.string.chart_sub_title),
                            getString(R.string.energy_data_name)
                            , getString(R.string.expected_data_name)
                            ,
                            context
                        )
                        chart.aa_drawChartWithChartModel(model)
                    }else
                        Toast.makeText(context, context.getString(R.string.no_data), Toast.LENGTH_LONG)
                            .show()

                    progressDialog.dialog.dismiss()
                    itemsSwipeToRefresh.isRefreshing = false


                },
                {
                    Toast.makeText(context, context.getString(R.string.error), Toast.LENGTH_LONG)
                        .show()
                    progressDialog.dialog.dismiss()
                    itemsSwipeToRefresh.isRefreshing = false
                })
    }


}