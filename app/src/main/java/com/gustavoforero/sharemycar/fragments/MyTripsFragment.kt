package com.gustavoforero.sharemycar.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.gustavoforero.sharemycar.R
import com.gustavoforero.sharemycar.adapter.MyTripsAdapter
import com.gustavoforero.sharemycar.adapter.SearchTripsAdapter
import com.gustavoforero.sharemycar.domain.City
import com.gustavoforero.sharemycar.domain.Trip
import com.gustavoforero.sharemycar.util.FirestoreConstants
import kotlinx.android.synthetic.main.fragment_my_trips.*
import kotlinx.android.synthetic.main.fragmet_search.*
import org.jetbrains.anko.support.v4.toast

class MyTripsFragment : Fragment(),MyTripsAdapter.DeleteTrip, OnCompleteListener<QuerySnapshot> {
    override fun onComplete(task: Task<QuerySnapshot>) {
        panel_progress_trips.visibility=View.GONE
        if (task.isSuccessful) {
            if (task.result.isEmpty) {
                rv_my_trips.visibility = View.GONE
                panel_trips_empty.visibility = View.VISIBLE
            } else {
                mTripList.clear()
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

    override fun onDeleteTrip(toDelete :String) {

      val writeResult = mFirebaseDb.collection("Trips").document(toDelete).delete()
        queryTrips()
    }

    lateinit var mFirebaseDb: FirebaseFirestore
    lateinit var adapter: MyTripsAdapter
    var mTripList = ArrayList<Trip>()

    companion object {
        fun newInstance(): MyTripsFragment {
            return MyTripsFragment()
        }
    }


    override fun onResume() {
        super.onResume()
        queryTrips()
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_my_trips, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mFirebaseDb = FirebaseFirestore.getInstance()
        adapter = MyTripsAdapter(this.requireContext(),this)
    }

    fun initViews() {
        val mLayoutManager = LinearLayoutManager(context)
        rv_my_trips.layoutManager = mLayoutManager
        rv_my_trips.adapter = adapter

    }

    fun queryTrips(){
        panel_progress_trips.visibility=View.VISIBLE

        val currentUser = FirebaseAuth.getInstance().currentUser
        val trips = mFirebaseDb.collection(FirestoreConstants.KEY_COLLECTION_TRIPS)
        val query = trips.whereEqualTo("email", currentUser!!.email)
        query.get().addOnCompleteListener(this)

    }

}