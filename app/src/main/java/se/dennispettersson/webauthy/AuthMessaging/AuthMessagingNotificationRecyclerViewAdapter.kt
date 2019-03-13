package se.dennispettersson.webauthy.AuthMessaging

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_authmessagingnotification.view.*
import se.dennispettersson.webauthy.AuthMessaging.AuthMessagingNotificationFragment.OnListFragmentInteractionListener
import se.dennispettersson.webauthy.R

class AuthMessagingNotificationRecyclerViewAdapter(
    private val mValues: List<AuthMessagingNotification>,
    private val mListener: OnListFragmentInteractionListener?
) : RecyclerView.Adapter<AuthMessagingNotificationRecyclerViewAdapter.ViewHolder>() {
    private val mOnClickListener: View.OnClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as AuthMessagingNotification

            Log.d(TAG, "click! ${item}")

            mListener?.onListFragmentInteraction(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.fragment_authmessagingnotification, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        holder.apply {
            mIdView.text = item.uuid
            mContentView.text = item.ip
            mView.apply {
                tag = item
                setOnClickListener(mOnClickListener)
            }
        }
    }

    override fun getItemCount(): Int = mValues.size

    companion object {
        private val TAG = "AMNRVA"
    }

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mIdView: TextView = mView.item_number
        val mContentView: TextView = mView.content

        override fun toString(): String = "${super.toString()} '${mContentView.text}'"
    }
}