package com.psut.smartpv.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.gson.GsonBuilder
import com.psut.smartpv.R
import com.psut.smartpv.constant.DataConstant
import com.psut.smartpv.constant.UrlConstant
import com.psut.smartpv.data.HistoryNumbers
import com.psut.smartpv.data.HistoryNumbersList
import com.psut.smartpv.handler.CustomProgressDialogHandler
import com.psut.smartpv.service.VolleyService
import com.psut.smartpv.util.SharedPreferencesUtil
import java.math.BigDecimal
import java.math.RoundingMode

class HistoryNumbersFragment : Fragment() {
    private lateinit var rcView: RecyclerView
    private val progressDialog = CustomProgressDialogHandler()

    private lateinit var itemsSwipeToRefresh:SwipeRefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.history_numbers, container, false)
        rcView = view.findViewById(R.id.rcView)
        progressDialog.show( context!!)

        rcView.layoutManager = LinearLayoutManager(context!!)
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
            getData(imei,context!!)
        }
        return view
    }

    private fun getData(imei: String, context: Context) {
        VolleyService.getInstance()
            .getJSONObject(UrlConstant.HISTORY_NUMBER_PATH, imei, context, { response ->

                if (response != null) {
                    val gson = GsonBuilder().create()
                    val historyData =
                        gson.fromJson(response.toString(), HistoryNumbersList::class.java)
                    val data =historyData.data
                    val adapter = CustomAdapter(data.reversed(), context)
                    rcView.adapter = adapter

                } else
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



    private class CustomAdapter(
        private var dataSet:List<HistoryNumbers>,
        private val context: Context
    ) :
        RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

            val energy: TextView = view.findViewById(R.id.energyTv)
            val expected: TextView = view.findViewById(R.id.expectedTv)
            val date: TextView = view.findViewById(R.id.dateTv)


        }

        // Create new views (invoked by the layout manager)
        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
            // Create a new view, which defines the UI of the list item
            val view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.history_number_item, viewGroup, false)

            return ViewHolder(view)
        }

        // Replace the contents of a view (invoked by the layout manager)
        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            viewHolder.date.text = dataSet[position].date
            viewHolder.energy.text =
                context.getString(R.string.energy_symbol,
                    BigDecimal(dataSet[position].energy).setScale(1, RoundingMode.HALF_EVEN).toString())
            viewHolder.expected.text =
                context.getString(R.string.energy_symbol, BigDecimal(dataSet[position].expected).setScale(1, RoundingMode.HALF_EVEN).toString())
        }

        // Return the size of your dataset (invoked by the layout manager)
        override fun getItemCount() = dataSet.size

    }
}