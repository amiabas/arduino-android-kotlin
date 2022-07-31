package com.baray.zohor.arduinocontroller.main.fragments.home

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.baray.zohor.arduinocontroller.R
import com.baray.zohor.arduinocontroller.databinding.FragmentHomeBinding
import com.baray.zohor.arduinocontroller.main.BluetoothRepository
import com.baray.zohor.arduinocontroller.main.MainActivity
import com.baray.zohor.arduinocontroller.main.fragments.connection.ConnectionFragmentDirections
import java.nio.charset.Charset

class HomeFragment : Fragment(R.layout.fragment_home), View.OnClickListener {

    private var bluetoothRepository: BluetoothRepository? = null


    private var _binding : FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val args: HomeFragmentArgs by navArgs()

    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        bluetoothRepository = BluetoothRepository((activity as MainActivity).applicationContext)
        val device = args.device


        connectToBluetoothDevice(device)
        binding.btnHomeSend.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.btn_home_send -> {
                        val data  = binding.eTxtHomeCode.text.toString()
                        val byteArr = data.toByteArray(Charset.defaultCharset())
                    bluetoothRepository?.sendByteData(byteArr)
                    binding.eTxtHomeCode.text = null
                }
            }
        }

    private fun connectToBluetoothDevice(device: BluetoothDevice){
        bluetoothRepository?.connectToTargetedDevice(device)
        bluetoothRepository?.connected?.observe(viewLifecycleOwner){
                isConnect -> if(isConnect == true){
                    Toast.makeText((activity as MainActivity).applicationContext, "دستگاه متصل شد.", Toast.LENGTH_SHORT).show()
                    binding.txtHomeName.text = device.name
                    binding.txtHomeMacAddress.text = device.address
                 } else if(isConnect == false){
                    Toast.makeText((activity as MainActivity).applicationContext, "اتصال نا موفق", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_homeFragment_to_connectionFragment)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        bluetoothRepository?.disconnect()
    }
}