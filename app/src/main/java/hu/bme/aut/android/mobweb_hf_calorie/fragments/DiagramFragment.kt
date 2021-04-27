package hu.bme.aut.android.mobweb_hf_calorie.fragments

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import hu.bme.aut.android.mobweb_hf_calorie.MyDate
import hu.bme.aut.android.mobweb_hf_calorie.R
import hu.bme.aut.android.mobweb_hf_calorie.data.CalorieEvent
import kotlinx.android.synthetic.main.fragment_diagram.*
import java.time.LocalDate
import java.time.temporal.WeekFields
import java.util.*
import kotlin.math.absoluteValue


private const val CALEV = "param1"
private const val CALEVLIST = "param2"


class DiagramFragment : Fragment() {
    lateinit var calorieEvent: CalorieEvent
    lateinit var calorieEventList: MutableList<CalorieEvent>


    override fun onResume() {
        super.onResume()
        calorieEvent = arguments?.get(CALEV) as CalorieEvent

        var arrayOfA = (arguments?.get(CALEVLIST) as Array<Parcelable>).map { it as CalorieEvent }
            .toTypedArray()
        calorieEventList = arrayOfA.toMutableList()


        loadBarChart()

    }

    fun getWeekofYear(date: MyDate): Int{

        var d = LocalDate.of(date.year,date.month,date.day)
        val weekFields = WeekFields.of(Locale("hu_HUN"))
        val weekNumber: Int = d.get(weekFields.weekOfWeekBasedYear())
       return weekNumber

    }




    private fun loadBarChart() {
        val weekOfYear: Int = getWeekofYear(calorieEvent.date)

        var sameWeekEvents = mutableListOf<CalorieEvent>()

        for(c in calorieEventList){
            var week= getWeekofYear(c.date)
            if(week == weekOfYear){
                sameWeekEvents.add(c)
            }
        }


        var map = mutableMapOf<Int, Int>(1 to 0, 2 to 0, 3 to 0, 4 to 0, 5 to 0, 6 to 0, 7 to 0)

        for(swe in sameWeekEvents){
            var d = LocalDate.of(swe.date.year,swe.date.month,swe.date.day)

            var kulcs = d.dayOfWeek.value
            var beforeValue = map.get(kulcs) as Int

            var toAdd: Int = swe.calorieCount.absoluteValue
            if(swe.type.name.toString().equals("Workout"))
                toAdd *= -1;
            var afterValue = beforeValue + toAdd
            map.replace(kulcs, afterValue)

        }

        val entries = listOf(
            BarEntry(1.toFloat(), (map.get(1) as Int).toFloat()),
            BarEntry(2.toFloat(), (map.get(2) as Int).toFloat()),
            BarEntry(3.toFloat(), (map.get(3) as Int).toFloat()),
            BarEntry(4.toFloat(), (map.get(4) as Int).toFloat()),
            BarEntry(5.toFloat(), (map.get(5) as Int).toFloat()),
            BarEntry(6.toFloat(), (map.get(6) as Int).toFloat()),
            BarEntry(7.toFloat(), (map.get(7) as Int).toFloat()),


            )
        val dataSet = BarDataSet(entries, getString(R.string.cal_count))
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()



        val xAxisLabels = listOf(
            "1",
            getString(R.string.monday),
            getString(R.string.tuesday),
            getString(R.string.wednesday),
            getString(R.string.thursday),
            getString(R.string.friday),
            getString(R.string.saturday),
            getString(R.string.sunday)
        )
        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(xAxisLabels)
        barChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        barChart.setDrawGridBackground(false)
        barChart.axisLeft.isEnabled = false
        barChart.axisRight.isEnabled = false
        barChart.description.isEnabled = false

        val data = BarData(dataSet)
        barChart.data = data

        barChart.invalidate()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {



        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_diagram, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DiagramFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(calev: CalorieEvent, calevlist: MutableList<CalorieEvent>) =
            DiagramFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(CALEV, calev)
                    putParcelableArray(CALEVLIST, calevlist.toTypedArray())
                }
            }
    }
}