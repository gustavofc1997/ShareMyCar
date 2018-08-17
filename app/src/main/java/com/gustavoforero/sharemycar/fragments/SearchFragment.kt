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
import com.google.firebase.firestore.QuerySnapshot
import com.gustavoforero.sharemycar.R
import com.gustavoforero.sharemycar.adapter.SearchTripsAdapter
import com.gustavoforero.sharemycar.domain.City
import com.gustavoforero.sharemycar.domain.Trip
import com.gustavoforero.sharemycar.util.FirestoreConstants
import kotlinx.android.synthetic.main.fragmet_search.*
import org.jetbrains.anko.support.v4.selector
import org.jetbrains.anko.support.v4.toast


class SearchFragment : Fragment(), OnCompleteListener<QuerySnapshot> {


    override fun onComplete(task: Task<QuerySnapshot>) {
        panel_progress.visibility=View.GONE

        if (task.isSuccessful) {

            if (task.result.isEmpty) {
                rv_trips.visibility = View.GONE
                panel_no_items.visibility = View.VISIBLE
            } else {
                for (document in task.result) {
                    mTripList.add(Trip(
                            document.data["origen"].toString(),
                            document.data["destino"].toString(),
                            document.data["fecha"].toString(),
                            document.data["hora"].toString(),
                            (document.data["cupos"] as Long).toInt(),
                            document.data.get(key = "precio").toString(),
                            document.data["phone"].toString(),
                            document.data["name"].toString()))
                }
                adapter.setItems(mTripList)
            }
        } else
            toast("Ups ha ocurrido un problema :(")

    }

    companion object {
        const val PATH_TRIPS = "Trips"

        fun newInstance(): SearchFragment {
            return SearchFragment()
        }
    }

    lateinit var mFirebaseDb: FirebaseFirestore
    lateinit var adapter: SearchTripsAdapter
    var mTripList = ArrayList<Trip>()
    var mCitiesList = java.util.ArrayList<City>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mFirebaseDb = FirebaseFirestore.getInstance()
        adapter = SearchTripsAdapter(this.requireContext())
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


    fun queryTripsFromOrigin(city: String) {
        panel_progress.visibility=View.VISIBLE

        val trips = mFirebaseDb.collection(FirestoreConstants.KEY_COLLECTION_TRIPS)
        val query = trips.whereEqualTo("origen", city)
        val querySnapshot = query.get().addOnCompleteListener { task ->
            panel_progress.visibility=View.GONE

            if (task.result.isEmpty) {
                rv_trips.visibility = View.GONE
                panel_no_items.visibility = View.VISIBLE

            } else {
                rv_trips.visibility = View.VISIBLE
                panel_no_items.visibility = View.GONE
                val list = ArrayList<Trip>()
                for (queryDocumentSnapshot in task.result) {
                    list.add(Trip(
                            queryDocumentSnapshot.data["origen"].toString(),
                            queryDocumentSnapshot.data["destino"].toString(),
                            queryDocumentSnapshot.data["fecha"].toString(),
                            queryDocumentSnapshot.data["hora"].toString(),
                            (queryDocumentSnapshot.data["cupos"] as Long).toInt(),
                            queryDocumentSnapshot.data.get(key = "precio").toString(),
                            queryDocumentSnapshot.data["phone"].toString(),
                            queryDocumentSnapshot.data["name"].toString()))
                }
                adapter.setItems(list)
            }


        }

    }

    fun showCitySelector() {
        var city: String
        if (mCitiesList.isNotEmpty()) {
            val citiesList = getCities()
            selector(getString(R.string.title_choose_from_search), citiesList
            ) { dialogInterface, i ->
                city = citiesList[i]
                txt_city_from.setText(city)
                queryTripsFromOrigin(city)
            }
        }

    }

    fun initViews() {
        panel_progress.visibility=View.VISIBLE
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
        mFirebaseDb.collection(FirestoreConstants.KEY_COLLECTION_TRIPS).get().addOnCompleteListener(this)
        val mLayoutManager = LinearLayoutManager(context)
        rv_trips.layoutManager = mLayoutManager
        rv_trips.adapter = adapter


    }

}