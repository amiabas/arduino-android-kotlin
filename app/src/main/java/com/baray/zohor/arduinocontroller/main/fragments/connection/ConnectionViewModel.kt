package com.baray.zohor.arduinocontroller.main.fragments.connection

import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ConnectionViewModel : ViewModel() {

     fun getListOfBluetoothDevice(bluetoothAdapter: BluetoothAdapter): LiveData<ArrayList<BluetoothDevice>>{
        val devices: MutableLiveData<ArrayList<BluetoothDevice>> = MutableLiveData()

        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter.bondedDevices
        val listOfDevice =  ArrayList<BluetoothDevice>()

        pairedDevices?.forEach { device ->
            listOfDevice.add(device)
        }

        devices.value = listOfDevice

        return devices
    }

}