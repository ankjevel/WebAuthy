package se.dennispettersson.webauthy.AuthMessaging

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_authmessagingnotification.view.*
import se.dennispettersson.webauthy.AuthMessaging.Content.AuthMessagingNotification
import se.dennispettersson.webauthy.R

class AuthMessagingNotificationRecyclerViewAdapter(
    private val mValues: MutableSet<AuthMessagingNotification>,
    private val mListener: OnListFragmentInteractionListener?
) : RecyclerView.Adapter<AuthMessagingNotificationRecyclerViewAdapter.ViewHolder>() {
    private val mOnClickListener: View.OnClickListener

    init {
        mOnClickListener = View.OnClickListener { view ->
            val item = view.tag as AuthMessagingNotification

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
        val item = mValues.reversed().get(position)

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

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mIdView: TextView = mView.item_number
        val mContentView: TextView = mView.content

        override fun toString(): String = "${super.toString()} '${mContentView.text}'"
    }
}