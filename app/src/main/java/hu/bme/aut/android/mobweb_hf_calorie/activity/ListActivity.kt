package hu.bme.aut.android.mobweb_hf_calorie.activity

import android.Manifest
import androidx.appcompat.app.AlertDialog.Builder
import android.Manifest.permission.READ_CALENDAR
import android.Manifest.permission.WRITE_CALENDAR
import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.os.Bundle
import android.provider.CalendarContract
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.internal.ViewUtils.getContentView

import hu.bme.aut.android.mobweb_hf_calorie.R
import hu.bme.aut.android.mobweb_hf_calorie.adapter.CalorieEventAdapter
import hu.bme.aut.android.mobweb_hf_calorie.adapter.OrderBy
import hu.bme.aut.android.mobweb_hf_calorie.data.CalorieEvent
import hu.bme.aut.android.mobweb_hf_calorie.data.CalorieEventDatabase
import hu.bme.aut.android.mobweb_hf_calorie.details.DetailsActivity
import hu.bme.aut.android.mobweb_hf_calorie.fragments.EditCalorieEventDialogFragment
import hu.bme.aut.android.mobweb_hf_calorie.fragments.NewCalorieEventDialogFragment
import kotlinx.android.synthetic.main.activity_list.*
import kotlinx.android.synthetic.main.content_main.*
import permissions.dispatcher.*
import java.util.*

import kotlin.concurrent.thread

class ListActivity : AppCompatActivity(), CalorieEventAdapter.OnCalorieEventSelectedListener, NewCalorieEventDialogFragment.NewCalorieEventDialogListener,
    EditCalorieEventDialogFragment.EditCalorieEventDialogListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CalorieEventAdapter
    private lateinit var database: CalorieEventDatabase
    private lateinit var selectedEvent: CalorieEvent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        fab.setOnClickListener{
            NewCalorieEventDialogFragment().show(
                supportFragmentManager,
                NewCalorieEventDialogFragment.TAG
            )
        }

        database = Room.databaseBuilder(
            applicationContext,
            CalorieEventDatabase::class.java,
            getString(R.string.cal_events)
        ).build()
        initRecyclerView()

    }

    private fun initRecyclerView() {
        recyclerView = rv_calorie
        adapter = CalorieEventAdapter(this)
        loadItemsInBackground()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter


    }



    private fun loadItemsInBackground() {
        thread {
            val items = database.calorieEventDao().getAll()
            runOnUiThread {
                adapter.update(items)
            }
        }
    }

    override fun onItemChanged(calEvent: CalorieEvent?) {
        thread {
            database.calorieEventDao().update(calEvent)

        }
    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete_all -> {
                val alertbox: AlertDialog.Builder = Builder(this)
                alertbox.setMessage(getString(R.string.areyousure))
                alertbox.setPositiveButton(R.string.ok,
                    DialogInterface.OnClickListener { arg0, arg1 ->
                        thread {
                            database.calorieEventDao().deleteAll()
                            runOnUiThread {
                                adapter.deleteAll()
                            }
                        }
                    })
                alertbox.setNegativeButton(getString(R.string.cancel),null)
                alertbox.show()


                true
            }
            R.id.ob_date_time -> {
                adapter.setOrderBy(OrderBy.DateTime)
                loadItemsInBackground()

                true
            }
            R.id.ob_name -> {
                adapter.setOrderBy(OrderBy.Name)
                loadItemsInBackground()

                true
            }


            else -> super.onOptionsItemSelected(item)
        }
    }
    override fun onCalorieEventEdited(updatedItem: CalorieEvent) {
        thread {
            database.calorieEventDao().update(updatedItem)
            runOnUiThread {
                adapter.notifyDataSetChanged()

            }
        }
    }


    override fun onLongClick(calEvent: CalorieEvent, itemView: View) {
        selectedEvent = calEvent
        val popup = PopupMenu(this, itemView)
        popup.inflate(R.menu.menu_longclick)
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.delete -> {
                    thread {
                        database.calorieEventDao().deleteItem(calEvent)
                        runOnUiThread {
                            adapter.removeCalorieEvent(calEvent)
                        }
                    }

                    return@setOnMenuItemClickListener true
                }
                R.id.insertToCal -> {
                    insertoCalendarWithPermissionHandling(calEvent)

                }
                R.id.editCalEvent -> {
                    EditCalorieEventDialogFragment(calEvent).show(
                        supportFragmentManager,
                        EditCalorieEventDialogFragment.TAG
                    )

                }


            }
            false
        }
        popup.show()

    }



    override fun onItemSelected(calEvent: CalorieEvent?, calorieEvents: MutableList<CalorieEvent>) {
        var bundle = Bundle()
        bundle.putParcelableArray("KEY_EVENT_LIST", calorieEvents.toTypedArray())
        bundle.putParcelable("KEY_EVENT", calEvent)
        val myIntent: Intent = Intent()
        myIntent.setClass(this, DetailsActivity::class.java)
        myIntent.putExtra("MY_BUNDLE", bundle)



        startActivity(myIntent)
    }



    override fun onCalorieEventCreated(newItem: CalorieEvent) {
        thread {
            val newId = database.calorieEventDao().insert(newItem)
            val newCalorieEvent = newItem.copy(
                id = newId
            )
            runOnUiThread {
                adapter.addCalorieEvent(newCalorieEvent)
            }
        }
    }


//futásidejű engedélykérések

    private fun showRationaleDialog(
        @StringRes title: Int = R.string.rationale_dialog_title,
        @StringRes explanation: Int,
        onPositiveButton: () -> Unit,
        onNegativeButton: () -> Unit = this::finish
    ) {
        val alertDialog = AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(explanation)
            .setCancelable(false)
            .setPositiveButton(R.string.proceed) { dialog, id ->
                dialog.cancel()
                onPositiveButton()
            }
            .setNegativeButton(R.string.exit) { dialog, id -> onNegativeButton() }
            .create()
        alertDialog.show()
    }

    private fun insertoCalendarWithPermissionHandling(calEvent: CalorieEvent) {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_CALENDAR)) {
                showRationaleDialog(

                    explanation = R.string.contacts_permission_explanation,
                    onPositiveButton = this::requestContactsPermission
                )

            } else {
                requestContactsPermission()
            }
        } else {
            insertIntoCalendar(calEvent)
        }
    }

    private fun insertIntoCalendar(calEvent: CalorieEvent) {
        val startMillis: Long = Calendar.getInstance().run {
            this.set(
                calEvent.date.year,
                calEvent.date.month - 1,
                calEvent.date.day,
                calEvent.time.hour,
                calEvent.time.minute
            )
            this.timeInMillis
        }

        val values = ContentValues()
        values.put(CalendarContract.Events.DTSTART, startMillis)
        values.put(CalendarContract.Events.DTEND, startMillis + 3600000)
        values.put(CalendarContract.Events.TITLE, calEvent.name)
        values.put(CalendarContract.Events.DESCRIPTION, calEvent.description)
        values.put(CalendarContract.Events.CALENDAR_ID, 3)
        values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID())
        val uri = contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)

        playTone()
    }

    private fun playTone() {
        val uriNotif = RingtoneManager.getDefaultUri(
            RingtoneManager.TYPE_NOTIFICATION
        )
        val r = RingtoneManager.getRingtone(
            applicationContext, uriNotif
        )
        r.play()
    }

    private fun requestContactsPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(WRITE_CALENDAR),
            100
        )
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            100 -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    insertIntoCalendar(selectedEvent)
                } else {
                    // permission denied! Disable the
                    // functionality that depends on this permission.
                }
                return
            }
        }
    }




}


