package bustracker.sukhajata.com.bustracker

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView


import bustracker.sukhajata.com.bustracker.JourneyFragment.OnJourneyFragmentInteractionListener
import bustracker.sukhajata.com.bustracker.model.Journey

import kotlinx.android.synthetic.main.fragment_journey.view.*

/**
 * [RecyclerView.Adapter] that can display a [DummyItem] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 */
class MyJourneyRecyclerViewAdapter(
        private val mValues: List<Journey>,
        private val mListener: OnJourneyFragmentInteractionListener?)
    : RecyclerView.Adapter<MyJourneyRecyclerViewAdapter.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as Journey
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            mListener?.onJourneyFragmentInteraction(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_journey, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]

        holder.mFromView.text = item.from;
        holder.mToView.text = item.to

        with(holder.mView) {
            tag = item
            setOnClickListener(mOnClickListener)
        }
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mCodeView: TextView = mView.journey_code
        val mFromView: TextView = mView.journey_from
        val mToView: TextView = mView.journey_to

        override fun toString(): String {
            return super.toString() + " '" + mFromView.text + " " + mToView.text + "'"
        }
    }
}
