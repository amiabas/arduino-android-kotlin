package com.baray.zohor.arduinocontroller.main.fragments.connection.adapter

import android.bluetooth.BluetoothDevice
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.baray.zohor.arduinocontroller.R

class CustomAdapter(private val dataSet: List<BluetoothDevice>, private val customOnClickListener: OnClickListener) : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {



    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
          val txtDeviceName : TextView
          val txtMacAddress : TextView
          val btnConnect : Button

        init {
            txtDeviceName = view.findViewById(R.id.txt_device_name)
            txtMacAddress = view.findViewById(R.id.txt_device_mac_address)
            btnConnect = view.findViewById(R.id.btn_connect)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.bluetooth_device_card, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txtDeviceName.text = dataSet[position].name
        holder.txtMacAddress.text = dataSet[position].address
        holder.btnConnect.setOnClickListener {
            customOnClickListener.onClick(dataSet[position])
        }
    }

    override fun getItemCount() = dataSet.size


    public class OnClickListener (val clickListener: (device :BluetoothDevice) -> Unit){
        fun onClick(device: BluetoothDevice) = clickListener(device)
    }
}