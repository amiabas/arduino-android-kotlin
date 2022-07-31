package com.baray.zohor.arduinocontroller.main

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import java.io.InputStream
import java.io.OutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

import com.baray.zohor.arduinocontroller.main.fragments.connection.util.*
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.util.*

class BluetoothRepository(private val context: Context) {

    var connected: MutableLiveData<Boolean?> = MutableLiveData(null)
    var progressState: MutableLiveData<String> = MutableLiveData("")
    val putTxt: MutableLiveData<String> = MutableLiveData("")

    val inProgress = MutableLiveData<Event<Boolean>>()
    val connectError = MutableLiveData<Event<Boolean>>()

    var mBluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private var mBluetoothStateReceiver: BroadcastReceiver? = null
    var targetDevice: BluetoothDevice? = null
    var socket: BluetoothSocket? = null
    var mOutputStream: OutputStream? = null
    var mInputStream: InputStream? = null

    var foundDevice:Boolean = false

    private lateinit var sendByte:ByteArray
    var discovery_error = false

    fun isBluetoothSupport():Boolean{
        return if(mBluetoothAdapter==null) {
            Toast.makeText(context, "Bluetooth Not Support!", Toast.LENGTH_SHORT).show()
            false
        }else{
            true
        }
    }

    fun isBluetoothEnabled():Boolean{
        return if (!mBluetoothAdapter!!.isEnabled) {
            // 블루투스를 지원하지만 비활성 상태인 경우
            // 블루투스를 활성 상태로 바꾸기 위해 사용자 동의 요청
            Toast.makeText(context, "Bluetooth is Enable", Toast.LENGTH_SHORT).show()
            false
        }else{
            true
        }
    }

    fun scanDevice(){
        progressState.postValue("device 스캔 중...")

        registerBluetoothReceiver()

        val bluetoothAdapter = mBluetoothAdapter
        foundDevice = false
        bluetoothAdapter?.startDiscovery()
    }
    fun registerBluetoothReceiver(){
        //intentfilter
        val stateFilter = IntentFilter()
        stateFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED) //BluetoothAdapter.ACTION_STATE_CHANGED : 블루투스 상태변화 액션
        stateFilter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)
        stateFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED) //연결 확인
        stateFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED) //연결 끊김 확인
        stateFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
        stateFilter.addAction(BluetoothDevice.ACTION_FOUND) //기기 검색됨
        stateFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED) //기기 검색 시작
        stateFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED) //기기 검색 종료
        stateFilter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST)
        mBluetoothStateReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent) {
                val action = intent.action //입력된 action
                if (action != null) {
                    Log.d("Bluetooth action", action)
                }
                val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                var name: String? = null
                if (device != null) {
                    name = device.name //broadcast를 보낸 기기의 이름을 가져온다.
                }
                when (action) {
                    BluetoothAdapter.ACTION_STATE_CHANGED -> {
                        val state = intent.getIntExtra(
                            BluetoothAdapter.EXTRA_STATE,
                            BluetoothAdapter.ERROR
                        )
                        when (state) {
                            BluetoothAdapter.STATE_OFF -> {
                            }
                            BluetoothAdapter.STATE_TURNING_OFF -> {
                            }
                            BluetoothAdapter.STATE_ON -> {
                            }
                            BluetoothAdapter.STATE_TURNING_ON -> {
                            }
                        }
                    }
                    BluetoothDevice.ACTION_ACL_CONNECTED -> {

                    }
                    BluetoothDevice.ACTION_BOND_STATE_CHANGED -> {
                    }
                    BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
                        connected.postValue(false)
                    }
                    BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
                    }
                    BluetoothDevice.ACTION_FOUND -> {
                        /**
                        if (!foundDevice) {
                            val device_name = device!!.name
                            val device_Address = device.address

                            // It only searches for devices with the prefix "RNM" in the Bluetooth device name.
                            if (device_name != null && device_name.length > 4) {
                                if (device_name.substring(0, 3) == "HC-05") {
                                    // filter your targetDevice and use connectToTargetedDevice()
                                    targetDevice = device
                                    foundDevice = true
                                    connectToTargetedDevice(targetDevice)
                                }
                            }

                        }
                        **/
                    }
                    BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                        if (!foundDevice) {
                            Toast.makeText(context, "device found", Toast.LENGTH_SHORT).show()
                            inProgress.postValue(Event(false))
                        }
                    }

                }
            }
        }
        context.registerReceiver(
            mBluetoothStateReceiver,
            stateFilter
        )

    }


    @ExperimentalUnsignedTypes
     fun connectToTargetedDevice(targetedDevice: BluetoothDevice?) {
        progressState.postValue("${targetDevice?.name} درحال اتصال به ")

        val thread = Thread {
            val uuid = UUID.fromString(SPP_UUID)
            try {
                socket = targetedDevice?.createRfcommSocketToServiceRecord(uuid)

                socket?.connect()

                /**
                 * After Connect Device
                 */
                connected.postValue(true)
                mOutputStream = socket?.outputStream
                mInputStream = socket?.inputStream
                // 데이터 수신
                beginListenForData()

            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                connectError.postValue(Event(true))
                try {
                    socket?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }

        //연결 thread를 수행한다
        thread.start()
    }


    fun disconnect(){
        try {
            socket?.close()
            connected.postValue(false)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    fun unregisterReceiver(){
        if(mBluetoothStateReceiver!=null) {
            context.unregisterReceiver(mBluetoothStateReceiver)
            mBluetoothStateReceiver = null
        }
    }

    /**
     * 블루투스 데이터 송신
     */
    fun sendByteData(data: ByteArray) {
        Thread {
            try {
                mOutputStream?.write(data)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.run()
    }

    /**
     * Convert
     * @ByteToUint : byte[] -> uint
     * @byteArrayToHex : byte[] -> hex string
     */
    private val m_ByteBuffer: ByteBuffer = ByteBuffer.allocateDirect(8)
    // byte -> uint
    fun ByteToUint(data: ByteArray?, offset: Int, endian: ByteOrder): Long {
        synchronized(m_ByteBuffer) {
            m_ByteBuffer.clear()
            m_ByteBuffer.order(endian)
            m_ByteBuffer.limit(8)
            if (endian === ByteOrder.LITTLE_ENDIAN) {
                m_ByteBuffer.put(data, offset, 4)
                m_ByteBuffer.putInt(0)
            } else {
                m_ByteBuffer.putInt(0)
                m_ByteBuffer.put(data, offset, 4)
            }
            m_ByteBuffer.position(0)
            return m_ByteBuffer.long
        }
    }

    fun byteArrayToHex(a: ByteArray): String? {
        val sb = StringBuilder()
        for (b in a) sb.append(String.format("%02x ", b /*&0xff*/))
        return sb.toString()
    }

    /**
     * 블루투스 데이터 수신 Listener
     */
    @ExperimentalUnsignedTypes
    fun beginListenForData() {

        val mWorkerThread = Thread {
            while (!Thread.currentThread().isInterrupted) {
                try {
                    val bytesAvailable = mInputStream?.available()
                    if (bytesAvailable != null) {
                        if (bytesAvailable > 0) { //데이터가 수신된 경우
                            val packetBytes = ByteArray(bytesAvailable)
                            mInputStream?.read(packetBytes)

                            /**
                             * 한 버퍼 처리
                             */
                            val s = String(packetBytes,Charsets.UTF_8)
                            putTxt.postValue(s)

                            /**
                             * 한 바이트씩 처리
                             */
                            for (i in 0 until bytesAvailable) {
                                val b = packetBytes[i]
                                Log.d("inputData", String.format("%02x", b))
                            }
                        }
                    }
                } catch (e: UnsupportedEncodingException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        //데이터 수신 thread 시작
        mWorkerThread.start()
    }

}