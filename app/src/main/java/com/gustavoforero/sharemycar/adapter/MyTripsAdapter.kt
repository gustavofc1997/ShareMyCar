package com.gustavoforero.sharemycar.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gustavoforero.sharemycar.R
import com.gustavoforero.sharemycar.domain.Trip
import kotlinx.android.synthetic.main.item_my_post.view.*
import kotlinx.android.synthetic.main.item_post.view.*

class MyTripsAdapter(val mContext: Context,val listener:DeleteTrip) : RecyclerView.Adapter<MyTripsAdapter.ItemViewHolder>() {


    var trips: ArrayList<Trip> = ArrayList()


    fun setItems(list: ArrayList<Trip>) {
        this.trips = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ItemViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.item_my_post, p0, false)
        return MyTripsAdapter.ItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return trips.size
    }

    override fun onBindViewHolder(p0: ItemViewHolder, p1: Int) {
        p0.loadItem(trips[p1],listener)
    }


    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        fun loadItem(trip: Trip,listener:DeleteTrip) {
            itemView.lab_finish.setOnClickListener {
                trip.origen?.let { it1 -> listener.onDeleteTrip(it1) }
            }
            itemView.lab_cities_my_trip.text = "${trip.origen} - ${trip.destino}"
            itemView.lab_trip_date.text = com.gustavoforero.sharemycar.util.Util.getDate(trip.fecha)
        }
    }
    public interface DeleteTrip{
        fun onDeleteTrip(string: String)
    }
}