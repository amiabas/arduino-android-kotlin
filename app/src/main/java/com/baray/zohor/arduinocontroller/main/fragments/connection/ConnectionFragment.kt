package com.baray.zohor.arduinocontroller.main.fragments.connection

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.baray.zohor.arduinocontroller.R
import com.baray.zohor.arduinocontroller.databinding.FragmentConnectionBinding
import com.baray.zohor.arduinocontroller.main.MainActivity
import com.baray.zohor.arduinocontroller.main.fragments.connection.adapter.CustomAdapter
import com.baray.zohor.arduinocontroller.main.BluetoothRepository


class ConnectionFragment : Fragment(R.layout.fragment_connection), View.OnClickListener {



    private var _binding : FragmentConnectionBinding? = null
    private val binding get() = _binding!!
    private lateinit var mView: View

    private lateinit var viewModel: ConnectionViewModel

    private var bluetoothManager: BluetoothManager? = null
    private var bluetoothAdapter: BluetoothAdapter? = null

    private var bluetoothRepository: BluetoothRepository? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConnectionBinding.inflate(inflater, container, false)

        binding.recyclerResult.isEnabled = false

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[ConnectionViewModel::class.java]
        mView = view

        bluetoothManager = (activity as MainActivity).getSystemService(BluetoothManager::class.java)
        bluetoothAdapter = bluetoothManager?.adapter

        bluetoothRepository = BluetoothRepository((activity as MainActivity).applicationContext)

        checkStatus()
        binding.btnBluetoothController.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.btn_bluetooth_controller -> {
                if (bluetoothAdapter?.isEnabled == true) {
                    viewModel.getListOfBluetoothDevice(bluetoothAdapter!!)
                        .observe(viewLifecycleOwner) { deviceList ->
                            ArrayList<BluetoothDevice>()
                            setupRecyclerview(deviceList)
                        }
                } else if (bluetoothAdapter?.isEnabled == false){
                    changeStatus(turnOnBluetooth())
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun turnOnBluetooth() : Boolean{
        if (bluetoothAdapter?.isEnabled == false) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            enableBtIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            (activity as MainActivity).startActivity(enableBtIntent)
        }

        return true
    }

    private fun changeStatus(blStatus: Boolean) {
        if (!blStatus) {
            binding.recyclerResult.isEnabled = false
            binding.btnBluetoothController.text = getString(R.string.turn_on_bluetooth)
            binding.imgBluetoothIsDisable.visibility = View.VISIBLE
        } else if (blStatus) {
            binding.recyclerResult.isEnabled = true
            binding.btnBluetoothController.text = getString(R.string.scan_bluetooth)
            binding.imgBluetoothIsDisable.visibility = View.INVISIBLE
        }
    }

    private fun checkStatus(){
        if (bluetoothAdapter?.isEnabled == false){
            binding.recyclerResult.isEnabled = false
            binding.btnBluetoothController.text = getString(R.string.turn_on_bluetooth)
            binding.imgBluetoothIsDisable.visibility = View.VISIBLE
        } else if(bluetoothAdapter?.isEnabled == true){
            binding.recyclerResult.isEnabled = true
            binding.btnBluetoothController.text = getString(R.string.scan_bluetooth)
            binding.imgBluetoothIsDisable.visibility = View.INVISIBLE
        }
    }

    private fun setupRecyclerview(devicesList: ArrayList<BluetoothDevice>){
        binding.recyclerResult.layoutManager = LinearLayoutManager((activity as MainActivity).applicationContext)
        val recAdapter = CustomAdapter(devicesList, CustomAdapter.OnClickListener {device -> connectToBluetoothDevice(device) })

        binding.recyclerResult.adapter = recAdapter
    }

    private fun connectToBluetoothDevice(device: BluetoothDevice){
            val action = ConnectionFragmentDirections.actionConnectionFragmentToHomeFragment(device)
            findNavController().navigate(action)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}