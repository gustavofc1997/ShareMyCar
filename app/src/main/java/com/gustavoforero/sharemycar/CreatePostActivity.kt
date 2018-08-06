package com.gustavoforero.sharemycar

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.gustavoforero.sharemycar.domain.City
import com.gustavoforero.sharemycar.util.FirestoreConstants
import kotlinx.android.synthetic.main.activity_create_post.*
import org.jetbrains.anko.selector
import org.jetbrains.anko.toast
import java.util.*


class CreatePostActivity : AppCompatActivity(), TextWatcher, OnCompleteListener<QuerySnapshot> {
    override fun afterTextChanged(p0: Editable?) {
        txt_price.setText("$" + txt_price.text.toString())
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

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

    fun getCities(isOrigin: Boolean): List<String> {
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
            val citiesList = getCities(isOrigin)
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


    private fun loadDefaultHourAndDate() {

        val c = Calendar.getInstance()
        lab_hour.text = lab_hour.context.getString(R.string.hour_concat, c.get(Calendar.HOUR_OF_DAY).toString(), c.get(Calendar.MINUTE).toString())
        lab_date.text = lab_date.context.getString(R.string.date_concat, c.get(Calendar.DAY_OF_MONTH).toString(), (c.get(Calendar.MONTH) + 1).toString(), c.get(Calendar.YEAR).toString())
    }

    private fun initViews() {
        txt_price.addTextChangedListener(this)
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
                lab_hour.text = lab_hour.context.getString(R.string.hour_concat, hour.toString(), minute.toString())
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
    }
}
