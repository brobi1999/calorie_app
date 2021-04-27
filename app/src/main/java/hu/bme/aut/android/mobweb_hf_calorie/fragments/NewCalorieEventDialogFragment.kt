package hu.bme.aut.android.mobweb_hf_calorie.fragments

import android.app.Dialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.google.android.material.snackbar.Snackbar
import hu.bme.aut.android.mobweb_hf_calorie.MyDate
import hu.bme.aut.android.mobweb_hf_calorie.MyTime
import hu.bme.aut.android.mobweb_hf_calorie.R
import hu.bme.aut.android.mobweb_hf_calorie.data.CalorieEvent
import kotlinx.android.synthetic.main.dialog_new_calorie_event.*

class NewCalorieEventDialogFragment : DialogFragment() {
    private lateinit var nameET: EditText
    private lateinit var descriptionET: EditText
    private lateinit var calorieCountET: EditText
    private lateinit var typeSpinner: Spinner
    private lateinit var datePicker: DatePicker
    private lateinit var timePicker: TimePicker


    interface NewCalorieEventDialogListener {
        fun onCalorieEventCreated(newItem: CalorieEvent)
    }

    private lateinit var listener: NewCalorieEventDialogListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? NewCalorieEventDialogListener
            ?: throw RuntimeException(getString(R.string.newcalevDL_not_impl))
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.new_calorie_item)
            .setView(getContentView())
            .setPositiveButton(R.string.ok) { dialogInterface, i ->
                if (isValid()) {
                    listener.onCalorieEventCreated(getCalorieEvent())
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .create()
    }

    private fun getContentView(): View {

        val contentView = LayoutInflater.from(context).inflate(R.layout.dialog_new_calorie_event, null)

        nameET = contentView.findViewById(R.id.nameET)
        descriptionET = contentView.findViewById(R.id.descriptionET)
        calorieCountET = contentView.findViewById(R.id.calorieCountET)
        typeSpinner = contentView.findViewById(R.id.typeSpinner)
        typeSpinner.setAdapter(
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                resources.getStringArray(R.array.activity_types)
            )
        )
        datePicker = contentView.findViewById(R.id.datePicker)
        timePicker = contentView.findViewById(R.id.timePicker)
        timePicker.setIs24HourView(true)

        return contentView
    }

    companion object {
        const val TAG = "NewCalorieEventDialogFragment"
    }

    private fun isValid(): Boolean{
        if(nameET.text.isNotEmpty() && calorieCountET.text.isNotEmpty())
            return true;
        Toast.makeText(context, getString(R.string.invalid_creation), Toast.LENGTH_LONG).show()
        return false;
    }


    private fun getCalorieEvent() = CalorieEvent(
        id = null,
        name = nameET.text.toString(),
        description = descriptionET.text.toString(),
        calorieCount = Integer.parseInt(calorieCountET.text.toString()),
        type = CalorieEvent.Type.getByOrdinal(typeSpinner.selectedItemPosition)
            ?: CalorieEvent.Type.Workout,
        date = getDate(),
        time = getTime()

    )



    private fun getTime(): MyTime {
        var hour = timePicker.hour
        var minute = timePicker.minute

        return MyTime(hour,minute)
    }

    private fun getDate(): MyDate {
        var year = datePicker.year
        var month = datePicker.month+1
        var day = datePicker.dayOfMonth
        return MyDate(year,month,day)
    }
}