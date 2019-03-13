package se.dennispettersson.webauthy.AuthMessaging

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import se.dennispettersson.webauthy.AuthMessaging.Content.AuthMessagingNotification
import se.dennispettersson.webauthy.AuthMessaging.Content.AuthMessagingNotificationContent
import se.dennispettersson.webauthy.R

class AuthMessagingNotificationFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_main, container, false)

        Log.d(TAG, "onCreateView")

        if (view is RecyclerView) {
            view.apply {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                adapter = AuthMessagingNotificationRecyclerViewAdapter(
                    AuthMessagingNotificationContent.ITEMS,
                    listener
                )
            }
        }

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        Log.d(TAG, "onAttach")

        if (context is OnListFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("${context} must implement OnListFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()

        Log.d(TAG, "onDetach")

        listener = null
    }

    companion object {
        private val TAG = "AMNF"
        private var columnCount = 1
        private var listener: OnListFragmentInteractionListener? = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson
     * [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onListFragmentInteraction(item: AuthMessagingNotification?)
    }

}
