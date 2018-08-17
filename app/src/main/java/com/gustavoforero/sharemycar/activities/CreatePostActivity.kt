package com.gustavoforero.sharemycar.activities

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.gustavoforero.sharemycar.R
import com.gustavoforero.sharemycar.domain.City
import com.gustavoforero.sharemycar.domain.Trip
import com.gustavoforero.sharemycar.util.FirestoreConstants
import com.gustavoforero.sharemycar.util.FirestoreConstants.Companion.KEY_COLLECTION_TRIPS
import kotlinx.android.synthetic.main.activity_create_post.*
import org.jetbrains.anko.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class CreatePostActivity : AppCompatActivity(), OnCompleteListener<QuerySnapshot> {

    var mCitiesList = ArrayList<City>()
    var mDestinationId: String = ""
    var mOriginID: String = ""


    override fun onComplete(task: Task<QuerySnapshot>) {
        if (task.isSuccessful) {
            for (document in task.result) {
                mCitiesList.add(City(document.id, document.data["name"].toString()))
            }
        } else
            toast("Ups ha ocurrido un problema :(")
    }

    fun getCities(): List<String> {
        val list = ArrayList<String>()
        for (city in mCitiesList) {
            list.add(city.cityName)
        }
        return list
    }

    fun getCityId(name: String): String {
        for (city in mCitiesList) {
            if (city.cityName == name)
                return city.cityId
        }
        return ""
    }

    fun showCitySelector(title: String, isOrigin: Boolean, editText: EditText) {
        if (mCitiesList.isNotEmpty()) {
            val citiesList = getCities()
            selector(title, citiesList
            ) { dialogInterface, i ->
                if (isOrigin) {
                    if (mDestinationId.isNotEmpty()) {
                        if (getCityId(citiesList[i]) == mDestinationId) {
                            toast(getString(R.string.msg_equals_cities))
                            return@selector
                        }
                    }
                } else {
                    if (mOriginID.isNotEmpty()) {
                        if (getCityId(citiesList[i]) == mOriginID) {
                            toast(getString(R.string.msg_equals_cities))
                            return@selector
                        }
                    }
                }
                if (isOrigin)
                    mOriginID = getCityId(citiesList[i])
                else
                    mDestinationId = getCityId(citiesList[i])
                editText.setText(citiesList[i])
            }
        }
    }

    lateinit var mFirebaseDb: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setDisplayShowHomeEnabled(true)
        }
        mFirebaseDb = FirebaseFirestore.getInstance()
        initViews()
        loadDefaultHourAndDate()
    }


    private fun validate(fields: Array<View>): Boolean {
        for (i in fields.indices) {
            val currentField = fields[i]
            if (currentField is EditText) {
                if (currentField.text.toString().isEmpty()) {
                    currentField.error = ""
                    return false
                }
            } else if (currentField is TextView) {
                if (currentField.text.toString().isEmpty()) {
                    currentField.error = ""
                    return false
                }
            }
        }
        return true
    }


    fun savePost(trip: Trip) {
        val dialog = progressDialog(message = "Estamos publicando la información", title = "Creando viaje")
        dialog.show()
        mFirebaseDb.collection(KEY_COLLECTION_TRIPS).document(trip.origen.toString()).set(trip)
                .addOnSuccessListener { documentReference ->
                    dialog.dismiss()
                    alert("Hemos compartido tu viaje ahora está visible para quienes estén en busca!!", "Viaje publicado") {
                        yesButton { finish() }
                    }.show()
                }.addOnFailureListener { e ->
                    println(e)
                    dialog.dismiss()
                }

    }


    fun getTripData(): Trip {
        val trip = Trip()
        trip.origen = txt_origin.text.toString()
        trip.fecha = lab_date.text.toString()
        trip.hora = lab_hour.text.toString()
        trip.destino = txt_destination.text.toString()
        trip.cupos = quantity.quantity
        trip.name = txt_name.text.toString()
        trip.phone = txt_phone.text.toString()
        trip.precio = txt_price.text.toString()
        return trip
    }

    fun setHour(time: String) {
        try {
            val sdf = SimpleDateFormat("H:mm", Locale.getDefault())
            val dateObj = sdf.parse(time)
            lab_hour.text = SimpleDateFormat("K:mm", Locale.getDefault()).format(dateObj)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

    }

    private fun loadDefaultHourAndDate() {
        val c = Calendar.getInstance()
        val hour = getString(R.string.hour_concat, c.get(Calendar.HOUR_OF_DAY).toString(), c.get(Calendar.MINUTE).toString())
        setHour(hour).toString()
        lab_date.text = lab_date.context.getString(R.string.date_concat, c.get(Calendar.DAY_OF_MONTH).toString(), (c.get(Calendar.MONTH) + 1).toString(), c.get(Calendar.YEAR).toString())
    }

    private fun initViews() {
        panel_date.setOnClickListener {
            val c = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(this,
                    DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

                        lab_date.text = lab_date.context.getString(R.string.date_concat, dayOfMonth.toString(), (monthOfYear + 1).toString(), year.toString())
                    }
                    , c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))
            datePickerDialog.show()
        }
        panel_hour.setOnClickListener {
            val c = Calendar.getInstance()
            val timePicker = TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { view, hour, minute ->
                setHour(getString(R.string.hour_concat, hour.toString(), minute.toString()))
            }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), false)
            timePicker.show()
        }

        txt_origin.setOnClickListener {
            showCitySelector(getString(R.string.title_choose_from), true, txt_origin)
        }
        txt_destination.setOnClickListener {
            showCitySelector(getString(R.string.title_choose_to), false, txt_destination)
        }
        mFirebaseDb.collection(FirestoreConstants.KEY_COLLECTION_CITIES).get().addOnCompleteListener(this)
        lab_share.setOnClickListener {
            onShareClick()
        }
    }

    fun onShareClick() {
        val fieldsOK = validate(arrayOf(txt_origin, txt_destination, lab_date, lab_hour, txt_price, txt_name, txt_phone))

        if (fieldsOK) {

            savePost(getTripData())
        }
    }
}

