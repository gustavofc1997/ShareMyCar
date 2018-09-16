package com.gustavoforero.sharemycar.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.gustavoforero.sharemycar.R
import com.gustavoforero.sharemycar.adapter.SearchTripsAdapter
import com.gustavoforero.sharemycar.domain.City
import com.gustavoforero.sharemycar.domain.Trip
import com.gustavoforero.sharemycar.util.FirestoreConstants
import kotlinx.android.synthetic.main.fragmet_search.*
import org.jetbrains.anko.support.v4.selector
import org.jetbrains.anko.support.v4.toast
import com.gustavoforero.sharemycar.util.BottomOffsetDecoration
import android.content.pm.PackageManager
import android.widget.Toast
import android.content.Intent
import android.telephony.PhoneNumberUtils
import android.content.ComponentName
import android.net.Uri


class SearchFragment : Fragment(), OnCompleteListener<QuerySnapshot> ,SearchTripsAdapter.OnPhonePressed{
    override fun onPhonePressedListener(trip: Trip) {
        val countries = listOf("Llamar", "Enviar mensaje en Whatsapp")
        selector("Selecciona una acción", countries) { _, i ->
            if (countries[i].equals("Llamar")){
                val intent = Intent(Intent.ACTION_DIAL)
                // Send phone number to intent as data
                intent.data = Uri.parse("tel:${trip.phone}")
                // Start the dialer app activity with number
                startActivity(intent)
            }else{
                openWhatsApp(trip.phone)
            }

        }
    }

    private fun openWhatsApp(num:String?) {
        val isWhatsappInstalled = whatsappInstalledOrNot("com.whatsapp")
        if (isWhatsappInstalled) {

            val sendIntent = Intent("android.intent.action.MAIN")
            sendIntent.component = ComponentName("com.whatsapp", "com.whatsapp.Conversation")
            sendIntent.putExtra("jid", PhoneNumberUtils.stripSeparators("57$num") + "@s.whatsapp.net")//phone number without "+" prefix

            startActivity(sendIntent)
        } else {
            val uri = Uri.parse("market://details?id=com.whatsapp")
            val goToMarket = Intent(Intent.ACTION_VIEW, uri)
            toast("WhatsApp not Installed")
            startActivity(goToMarket)
        }
    }

    private fun whatsappInstalledOrNot(uri: String): Boolean {
        var app_installed = false

        activity?.run {
            val pm =packageManager
            try {
                pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES)
                app_installed = true
            } catch (e: PackageManager.NameNotFoundException) {
                app_installed = false
            }

        }
        return app_installed
    }


    override fun onComplete(task: Task<QuerySnapshot>) {
        if (panel_progress.visibility == View.VISIBLE)
            panel_progress.visibility = View.GONE
        if (task.isSuccessful) {

            if (task.result.isEmpty) {
                rv_trips.visibility = View.GONE
                panel_no_items.visibility = View.VISIBLE
            } else {
                mTripList.clear()
                for (document in task.result) {
                    mTripList.add(createTrip(document))
                }
                adapter.setItems(mTripList)
            }
        } else
            toast("Ups ha ocurrido un problema :(")

    }

    companion object {

        fun newInstance(): SearchFragment {
            return SearchFragment()
        }
    }

    lateinit var mFirebaseDb: FirebaseFirestore
    lateinit var adapter: SearchTripsAdapter
    var mTripList = ArrayList<Trip>()
    var mCitiesList = java.util.ArrayList<City>()
    var citySelected = "All"



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mFirebaseDb = FirebaseFirestore.getInstance()
        adapter = SearchTripsAdapter(this.requireContext(),this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragmet_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }


    fun getCities(): List<String> {
        val list = java.util.ArrayList<String>()
        for (city in mCitiesList) {
            list.add(city.cityName)
        }
        return list
    }


    override fun onResume() {
        super.onResume()
        queryTripsFromOrigin(citySelected)
    }


    fun createTrip(queryDocumentSnapshot: QueryDocumentSnapshot): Trip {
        return Trip(
                queryDocumentSnapshot.data["origen"].toString(),
                queryDocumentSnapshot.data["destino"].toString(),
                queryDocumentSnapshot.data["fecha"].toString(),
                queryDocumentSnapshot.data["hora"].toString(),
                (queryDocumentSnapshot.data["cupos"] as Long).toInt(),
                queryDocumentSnapshot.data.get(key = "precio").toString(),
                queryDocumentSnapshot.data["phone"].toString(),
                queryDocumentSnapshot.data["name"].toString())

    }

    fun queryTripsFromOrigin(city: String) {
        val trips = mFirebaseDb.collection(FirestoreConstants.KEY_COLLECTION_TRIPS)
        if (city != "All") {
            val query = trips.whereEqualTo("origen", city)
            val querySnapshot = query.get().addOnCompleteListener { task ->
                if (panel_progress.visibility == View.VISIBLE)
                    panel_progress.visibility = View.GONE
                if (task.result.isEmpty) {
                    rv_trips.visibility = View.GONE
                    panel_no_items.visibility = View.VISIBLE
                } else {
                    rv_trips.visibility = View.VISIBLE
                    panel_no_items.visibility = View.GONE
                    val list = ArrayList<Trip>()
                    for (queryDocumentSnapshot in task.result) {
                        list.add(createTrip(queryDocumentSnapshot))
                    }
                    adapter.setItems(list)
                }

            }
        } else {
            mFirebaseDb.collection(FirestoreConstants.KEY_COLLECTION_TRIPS).get().addOnCompleteListener(this)

        }

    }

    fun showCitySelector() {
        if (mCitiesList.isNotEmpty()) {
            val citiesList = getCities()
            selector(getString(R.string.title_choose_from_search), citiesList
            ) { dialogInterface, i ->
                citySelected = citiesList[i]
                txt_city_from.setText(citySelected)
                panel_progress.visibility = View.VISIBLE
                queryTripsFromOrigin(citySelected)
            }
        }

    }

    private fun initViews() {
        panel_progress.visibility = View.VISIBLE
        mFirebaseDb.collection(FirestoreConstants.KEY_COLLECTION_CITIES).get().addOnCompleteListener { task: Task<QuerySnapshot> ->
            if (task.isSuccessful) {
                for (document in task.result) {
                    mCitiesList.add(City(document.id, document.data["name"].toString()))
                }
            } else
                toast("Ups ha ocurrido un problema :(")
        }

        txt_city_from.setOnClickListener {
            showCitySelector()
        }
        queryTripsFromOrigin(citySelected)
        val mLayoutManager = LinearLayoutManager(context)
        rv_trips.layoutManager = mLayoutManager
        rv_trips.adapter = adapter
        val offsetPx = resources.getDimension(R.dimen.offset)
        val bottomOffsetDecoration = BottomOffsetDecoration(offsetPx.toInt())
        rv_trips.addItemDecoration(bottomOffsetDecoration)


    }

}