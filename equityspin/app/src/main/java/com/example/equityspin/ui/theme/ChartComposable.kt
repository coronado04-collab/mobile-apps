package com.example.equityspin.ui

import android.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun StockLineChart(
    entries: List<Entry>,
    modifier: Modifier = Modifier
) {
    AndroidView(
        factory = { context ->
            LineChart(context).apply {
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                axisRight.isEnabled = false
                description.isEnabled = false
                legend.isEnabled = false
                setTouchEnabled(false)
                xAxis.valueFormatter = DateValueFormatter()
            }
        },
        update = { chart ->
            val dataSet = LineDataSet(entries, "Price").apply {
                setDrawCircles(false)
                lineWidth = 2f
                color = Color.BLUE
            }
            chart.data = LineData(dataSet)
            chart.invalidate()
        },
        modifier = modifier
    )
}

class DateValueFormatter : ValueFormatter() {

    private val dateFormat = SimpleDateFormat("MMM yyyy", Locale.getDefault())

    override fun getFormattedValue(value: Float): String {
        val date = Date(value.toLong() * 1000) // Convert seconds to milliseconds
        return dateFormat.format(date)
    }
}