package com.psut.smartpv.activity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Geocoder
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.google.gson.GsonBuilder
import com.psut.smartpv.KTLoadingButton
import com.psut.smartpv.R
import com.psut.smartpv.constant.DataConstant
import com.psut.smartpv.constant.UrlConstant
import com.psut.smartpv.data.DeviceReading
import com.psut.smartpv.data.DeviceReadingList
import com.psut.smartpv.databinding.ActivityHomeBinding
import com.psut.smartpv.handler.CustomProgressDialogHandler
import com.psut.smartpv.service.VolleyService
import com.psut.smartpv.util.SharedPreferencesUtil


import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.system.exitProcess

class Home : AppCompatActivity() {
    private var doubleBackToExitPressedOnce = false
    private lateinit var binding: ActivityHomeBinding
    private val progressDialog = CustomProgressDialogHandler()
    companion object{
        const val OK_RESPONSE = "OK"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        progressDialog.show(this);
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.devicesRV.layoutManager = LinearLayoutManager(this)
        getDevices()

        binding.itemsswipetorefresh.setProgressBackgroundColorSchemeColor(
            ContextCompat.getColor(
                this,
                R.color.white
            )
        )
        binding.itemsswipetorefresh.setColorSchemeColors(
            ContextCompat.getColor(
                this,
                R.color.mainColor
            )
        )

        binding.itemsswipetorefresh.setOnRefreshListener {
            getDevices()
        }
        binding.addDeviceBtn.setOnClickListener {
            val view = layoutInflater.inflate(R.layout.input_dialog_layout, null)
            val customDialog = AlertDialog.Builder(this)
                .setView(view)
                .show()
            customDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val addBtn = view.findViewById<KTLoadingButton>(R.id.button)
            val imeiEdTxt = view.findViewById<EditText>(R.id.imeiEdTxt)
            addBtn.setOnClickListener {
                addBtn.startLoading()
                addDevice(imeiEdTxt.text.toString(), addBtn, imeiEdTxt)
            }
        }

        binding.logoutBtn.setOnClickListener {
            logout()
        }
    }
    private fun logout() {
        SharedPreferencesUtil.remove(this)
        val intent = Intent(this, Login::class.java)
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finish()
            exitProcess(-1)

        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, getString(R.string.exit_msg), Toast.LENGTH_SHORT).show()
        Handler().postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000)
    }


    private fun getDevices() {
        val email: String = SharedPreferencesUtil.getString(this, DataConstant.EMAIL).toString()

        VolleyService.getInstance()
            .getJSONObject(UrlConstant.DEVICES_PATH, email, this, { response ->
                if (response != null) {
                    val gson = GsonBuilder().create()
                    val deviceReadings =
                        gson.fromJson(response.toString(), DeviceReadingList::class.java)
                    val adapter =
                        CustomAdapter(deviceReadings.readings as ArrayList<DeviceReading>, this)
                    binding.devicesRV.adapter = adapter
                }else
                    Toast.makeText(this, getString(R.string.no_devices), Toast.LENGTH_LONG)
                        .show()
                progressDialog.dialog.dismiss();
                binding.itemsswipetorefresh.isRefreshing = false
            },
                {
                    progressDialog.dialog.dismiss();
                    binding.itemsswipetorefresh.isRefreshing = false
                })


    }

    private fun addDevice(imei: String, button: KTLoadingButton, editText: EditText) {

        val params = HashMap<String, String>();
        params[DataConstant.IMEI] = imei
        params[DataConstant.EMAIL] = SharedPreferencesUtil.getString(this, DataConstant.EMAIL).toString()
        VolleyService.getInstance()
            .postStringRequest(UrlConstant.ADD_DEVICE_PATH, params, this, { response ->
                if (response == OK_RESPONSE) {
                    button.doResult(true)
                    Toast.makeText(this, getString(R.string.device_added), Toast.LENGTH_LONG).show()
                    getDevices()
                    editText.error = null

                } else {
                    button.doResult(false)
                    Toast.makeText(this, response, Toast.LENGTH_LONG).show()
                    editText.error = response
                }
            }, { error ->
                button.doResult(false)
                Toast.makeText(this, getString(R.string.error), Toast.LENGTH_LONG).show()
            })
    }

    private class CustomAdapter(
        private var dataSet: ArrayList<DeviceReading>,
        private val context: Context
    ) :
        RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val temp: TextView
            val hum: TextView
            val energy: TextView
            val expected: TextView
            val imei: TextView
            val location: TextView
            val status: TextView
            val historyBtn: Button
            val l1: LinearLayout
            val l2: TableLayout
            val l3: LinearLayout


            init {
                // Define click listener for the ViewHolder's View.
                temp = view.findViewById(R.id.tempValue)
                hum = view.findViewById(R.id.humValue)
                energy = view.findViewById(R.id.energyValue)
                expected = view.findViewById(R.id.expectedValue)
                imei = view.findViewById(R.id.imeiValue)
                location = view.findViewById(R.id.locationValue)
                status = view.findViewById(R.id.statusValue)
                historyBtn = view.findViewById(R.id.historyBtn)
                l1 = view.findViewById(R.id.l1)
                l2 = view.findViewById(R.id.l2)
                l3 = view.findViewById(R.id.l3)
            }
        }

        // Create new views (invoked by the layout manager)
        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
            // Create a new view, which defines the UI of the list item
            val view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.devices_item_layout, viewGroup, false)

            return ViewHolder(view)
        }

        // Replace the contents of a view (invoked by the layout manager)
        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            val decimal = BigDecimal(dataSet[position].temp).setScale(1, RoundingMode.HALF_EVEN)
            viewHolder.imei.text = dataSet[position].imei
            viewHolder.temp.text = context.getString(R.string.temp_symbol, decimal.toString())
            viewHolder.hum.text =
                context.getString(R.string.hum_symbol, dataSet[position].hum.toInt(), "%")
            viewHolder.energy.text =
                context.getString(R.string.energy_symbol, BigDecimal(dataSet[position].energy).setScale(1, RoundingMode.HALF_EVEN).toString())
            viewHolder.expected.text =
                context.getString(R.string.energy_symbol,BigDecimal(dataSet[position].expected).setScale(1, RoundingMode.HALF_EVEN).toString())
            if (dataSet[position].status)
            {   viewHolder.status.setTextColor(context.getColor(R.color.mainColor))
                viewHolder.status.text = context.getString(R.string.device_item_status_ok)
            }
            else
            {
                viewHolder.status.setTextColor(context.getColor(R.color.failColor))
                viewHolder.status.text = context.getString(R.string.device_item_status_fail)
            }

            viewHolder.imei.setOnClickListener {
                if (viewHolder.l1.visibility == View.GONE) {
                    viewHolder.l1.visibility = View.VISIBLE
                    viewHolder.l2.visibility = View.VISIBLE
                    viewHolder.l3.visibility = View.VISIBLE
                    viewHolder.historyBtn.visibility = View.VISIBLE
                } else {
                    viewHolder.l1.visibility = View.GONE
                    viewHolder.l2.visibility = View.GONE
                    viewHolder.l3.visibility = View.GONE
                    viewHolder.historyBtn.visibility = View.GONE
                }
            }

            val fromLocation = Geocoder(context, Locale.getDefault()).getFromLocation(
                dataSet[position].lat,
                dataSet[position].lon,
                1
            )
            if(fromLocation.size>0)
            {

                viewHolder.location.text = fromLocation[0].locality.toUpperCase()
            }else{
                viewHolder.location.text = "Jordan".toUpperCase()
            }

            viewHolder.historyBtn.setOnClickListener {

                val intent = Intent(context,History::class.java)
                SharedPreferencesUtil.addStringToSharedPreferences(context,DataConstant.IMEI,dataSet[position].imei)
                startActivity(context,intent,null)


            }

        }

        // Return the size of your dataset (invoked by the layout manager)
        override fun getItemCount() = dataSet.size

    }
}