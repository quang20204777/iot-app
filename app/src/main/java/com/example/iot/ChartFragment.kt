package com.example.iot

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter


class ChartFragment : Fragment() {
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var lineChart: LineChart
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lineChart = view.findViewById(R.id.chart)
        handler.post(apiRunnable)
        ApiClient.getTop10TreeStatus(requireContext()) { treeStatusList ->
            // Xử lý dữ liệu trả về từ API
            if (treeStatusList != null) {
                // Gọi phương thức để cập nhật dữ liệu trên biểu đồ
                updateChart(lineChart, treeStatusList)
            } else {
                // Xử lý khi có lỗi
            }
        }
    }

    private val apiRunnable = object : Runnable {
        override fun run() {
            // Gọi API để lấy dữ liệu
            ApiClient.getTop10TreeStatus(requireContext()) { treeStatusList ->
                // Xử lý dữ liệu trả về từ API
                if (treeStatusList != null) {
                    // Gọi phương thức để cập nhật dữ liệu trên biểu đồ
                    updateChart(lineChart, treeStatusList)
                } else {
                    // Xử lý khi có lỗi
                }

                // Gọi lại Runnable sau 1 giây
                handler.postDelayed(this, 1000)
            }
        }
    }

    private fun updateChart(lineChart: LineChart, treeStatusList: List<TreeStatusResponse>) {
        val humidityEntries = mutableListOf<Entry>()
        val temperatureEntries = mutableListOf<Entry>()
        val xValues = mutableListOf<String>()
        val description = Description()
        description.text = "Tree Status"
        description.setPosition(150f, 15f)
        lineChart.description = description
        lineChart.axisRight.setDrawLabels(false)

        // Duyệt qua danh sách dữ liệu và thêm vào các mảng tương ứng
        for ((index, treeStatus) in treeStatusList.withIndex()) {
            humidityEntries.add(Entry(index.toFloat(), treeStatus.humidity.toFloat()))
            temperatureEntries.add(Entry(index.toFloat(), treeStatus.temperature))
            xValues.add(treeStatus.createdAt)
        }

        // Tạo dataset cho độ ẩm và nhiệt độ
        val humidityDataSet = LineDataSet(humidityEntries, "Humidity")
        humidityDataSet.color = Color.BLUE
        humidityDataSet.valueTextColor = Color.BLACK

        val temperatureDataSet = LineDataSet(temperatureEntries, "Temperature")
        temperatureDataSet.color = Color.RED
        temperatureDataSet.valueTextColor = Color.BLACK

        // Tạo đối tượng LineData và đặt dataset
        val lineData = LineData(humidityDataSet, temperatureDataSet)

        // Cài đặt giá trị của trục X
        val xAxis: XAxis = lineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.valueFormatter = IndexAxisValueFormatter(xValues)
        xAxis.labelCount = 5
        xAxis.granularity = 1f

        val yAxis: YAxis = lineChart.axisLeft
        yAxis.axisMinimum = 0f
        yAxis.axisMaximum = 100f
        yAxis.axisLineWidth = 2f
        yAxis.axisLineColor = Color.BLACK
        yAxis.labelCount = 10

        // Cài đặt dữ liệu cho biểu đồ
        lineChart.data = lineData

        // Refresh biểu đồ
        lineChart.invalidate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Loại bỏ callback khi Fragment bị hủy
        handler.removeCallbacks(apiRunnable)
    }
}
