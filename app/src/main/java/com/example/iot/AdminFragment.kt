package com.example.iot

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import java.util.concurrent.TimeUnit

class AdminFragment : Fragment() {
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var imageTree: ImageView
    private lateinit var imageWitheredTree: ImageView
    private lateinit var humidityTextView: TextView
    private lateinit var temperatureTextView: TextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imageTree = view.findViewById(R.id.tree)
        imageWitheredTree = view.findViewById(R.id.withered_tree)
        humidityTextView = view.findViewById(R.id.humidityTextView)
        temperatureTextView = view.findViewById(R.id.temperatureTextView)
        val detailBtn: Button = view.findViewById(R.id.detail)
        val onSwitch: Switch = view.findViewById(R.id.on)
        val autoSwitch: Switch = view.findViewById(R.id.auto)

        updateTreeStatus()

        // Bắt đầu định kỳ gọi hàm cập nhật trạng thái cây (mỗi 5 giây)
        handler.postDelayed(object : Runnable {
            override fun run() {
                updateTreeStatus()
                // Lặp lại sau mỗi khoảng thời gian
                handler.postDelayed(this, TimeUnit.SECONDS.toMillis(1))
            }
        }, TimeUnit.SECONDS.toMillis(1))

        ApiClient.getPumpAuto(requireContext(), 1) { pumpStatus, autoStatus ->
            // Cập nhật trạng thái của Switch dựa trên dữ liệu từ API
            onSwitch.isChecked = pumpStatus
            autoSwitch.isChecked = autoStatus

            // Đặt text tương ứng cho Switch
//            onSwitch.text = if (pumpStatus) "ON" else "OFF"
//            autoSwitch.text = if (autoStatus) "AUTO" else "MANUAL"
        }

        onSwitch.setOnCheckedChangeListener { _, isChecked ->
//            onSwitch.text = if (isChecked) "ON" else "OFF"
            // Gọi API để cập nhật trạng thái của Pump
            ApiClient.updatePump(requireContext(), isChecked) { isSuccess ->
                if (isSuccess == true) {
                    // Xử lý khi cập nhật thành công
                    Log.d("TAG", "Pump updated successfully")
                } else {
                    // Xử lý khi có lỗi
                    Log.e("TAG", "Error updating Pump")
                }
            }
        }

        autoSwitch.setOnCheckedChangeListener { _, isChecked ->
//            autoSwitch.text = if (isChecked) "AUTO" else "MANUAL"
            ApiClient.updateAuto(requireContext(), isChecked) { isSuccess ->
                if (isSuccess == true) {
                    // Xử lý khi cập nhật thành công
                    Log.d("TAG", "Auto updated successfully")
                } else {
                    // Xử lý khi có lỗi
                    Log.e("TAG", "Error updating Auto")
                }
            }
        }

        detailBtn.setOnClickListener {
            findNavController().navigate(R.id.action_admin_fragment_to_chartFragment)
        }

    }

    private fun updateTreeStatus() {
        ApiClient.getLatestTreeStatus(requireContext()) { treeStatusResponse ->
            if (treeStatusResponse != null) {
                // Cập nhật giao diện với dữ liệu mới
                humidityTextView.text = "${treeStatusResponse.humidity}"
                temperatureTextView.text = "${treeStatusResponse.temperature}"
                var humidity: Int = treeStatusResponse.humidity
                if (humidity >= 20) {
                    imageTree.visibility = View.VISIBLE
                    imageWitheredTree.visibility = View.GONE
                } else {
                    imageTree.visibility = View.GONE
                    imageWitheredTree.visibility = View.VISIBLE
                }
            } else {
                // Xử lý trường hợp có lỗi khi gọi API
                Log.e("TAG", "Error fetching latest tree status")
                Toast.makeText(
                    requireContext(),
                    "Error fetching latest tree status",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Dừng việc định kỳ gọi hàm khi Fragment bị hủy
        handler.removeCallbacksAndMessages(null)
    }

}