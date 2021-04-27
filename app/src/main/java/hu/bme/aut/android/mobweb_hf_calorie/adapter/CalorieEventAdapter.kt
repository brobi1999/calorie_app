package hu.bme.aut.android.mobweb_hf_calorie.adapter

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.android.mobweb_hf_calorie.MyDate
import hu.bme.aut.android.mobweb_hf_calorie.MyTime
import hu.bme.aut.android.mobweb_hf_calorie.R
import hu.bme.aut.android.mobweb_hf_calorie.data.CalorieEvent
import kotlinx.android.synthetic.main.item_calorie_event.view.*
import org.w3c.dom.Text

enum class OrderBy {
    DateTime,
    Name;
}

class CalorieEventAdapter internal constructor(private val listener: OnCalorieEventSelectedListener?) :
    RecyclerView.Adapter<CalorieEventAdapter.CalorieEventHolder>() {

    var order: OrderBy = OrderBy.DateTime
    private var calorieEvents: MutableList<CalorieEvent>

    fun addToCalorieEvents(calEv: CalorieEvent){
        calorieEvents.add(calEv)
        var sorted = emptyList<CalorieEvent>()
        when (order) {
            OrderBy.DateTime -> {
                sorted = calorieEvents.sortedWith(compareBy({ it.date.year }, { it.date.month },{ it.date.day },{ it.time.hour},{ it.time.minute })) as List<CalorieEvent>
            }
            OrderBy.Name ->{
                sorted = calorieEvents.sortedWith(compareBy({ it.name })) as List<CalorieEvent>
            }
            else -> {

            }
        }


        calorieEvents.clear()
        for( calEv in sorted){
            calorieEvents.add(calEv)
        }

        notifyDataSetChanged()
    }

    fun setOrderBy(ob: OrderBy){
        order = ob
    }

    interface OnCalorieEventSelectedListener {
        fun onItemChanged(calEvent: CalorieEvent?)
        fun onLongClick(calEvent: CalorieEvent, itemView: View)
        fun onItemSelected(calEvent: CalorieEvent?, calorieEvents: MutableList<CalorieEvent>)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalorieEventHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_calorie_event, parent, false)
        return CalorieEventHolder(view)
    }

    override fun onBindViewHolder(holder: CalorieEventHolder, position: Int) {
        holder.itemView.setOnClickListener{
            listener?.onItemSelected(holder.item, calorieEvents)
        }

        val item = calorieEvents[position]
        holder.calorieEventName.text = item.name
        holder.CalorieCount.text = item.calorieCount.toString()
        holder.eventType.text = item.type.name
        holder.date.text = item.date.displayDate

        holder.time.text = item.time.displayTime
        when(holder.eventType.text.toString()){
            "Workout"->holder.myImg.setBackgroundResource(R.drawable.baseline_fitness_center_24)
            else->holder.myImg.setBackgroundResource(R.drawable.baseline_restaurant_24)
        }

        holder.item = item

    }

    override fun getItemCount(): Int {
        return calorieEvents.size
    }

    fun update(calEvs: List<CalorieEvent>) {
        calorieEvents.clear()
        for( calEv in calEvs){
            addToCalorieEvents(calEv)
        }
        notifyDataSetChanged()
    }

    fun addCalorieEvent(calEvent: CalorieEvent) {
        addToCalorieEvents(calEvent)
        notifyItemInserted(calorieEvents.size-1)
    }

    fun removeCalorieEvent(calEvent: CalorieEvent?) {
        var idx = calorieEvents.indexOf(calEvent)
        calorieEvents.removeAt(idx)
        notifyItemRemoved(idx)

    }

    fun deleteAll(){
        calorieEvents.clear()
        notifyDataSetChanged()
    }



    inner class CalorieEventHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var itemView_: View

        val myImg : ImageView
        val calorieEventName : TextView
        val CalorieCount : TextView
        val eventType : TextView
        val date: TextView
        val time: TextView
        var item: CalorieEvent? = null

        init {
            this.itemView_ = itemView

            myImg = itemView.my_img
            calorieEventName = itemView.calorieEventName
            CalorieCount = itemView.CalorieCount
            eventType = itemView.eventType
            date = itemView.date
            time = itemView.time
            itemView.setOnLongClickListener {
                listener?.onLongClick(item as CalorieEvent, itemView)
                true
            }

        }
    }

    init {
        calorieEvents = ArrayList()
    }
}