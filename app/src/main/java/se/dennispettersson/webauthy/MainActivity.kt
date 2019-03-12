package se.dennispettersson.webauthy

import android.app.NotificationManager
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_main.view.*
import android.content.Context


class MainActivity : AppCompatActivity() {

    private val mNotificationTag: String by lazy {
        getString(R.string.action_tag)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        container.adapter = mSectionsPagerAdapter

        FirebaseMessaging
            .getInstance()
            .subscribeToTopic(TOPIC_NAME)
            .addOnCompleteListener { task ->
                val msg = getString(if (task.isSuccessful()) R.string.msg_subscribed else R.string.msg_subscribe_failed)
                Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
            }

        handleIntent(intent)
    }

    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            return PlaceholderFragment.newInstance(position + 1)
        }

        override fun getCount(): Int {
            return 3
        }
    }

    class PlaceholderFragment : Fragment() {

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            val rootView = inflater.inflate(R.layout.fragment_main, container, false)
            rootView.section_label.text = getString(R.string.section_format, arguments?.getInt(ARG_SECTION_NUMBER))
            return rootView
        }

        companion object {

            private val ARG_SECTION_NUMBER = "section_number"

            fun newInstance(sectionNumber: Int): PlaceholderFragment {
                val fragment = PlaceholderFragment()
                val args = Bundle()
                args.putInt(ARG_SECTION_NUMBER, sectionNumber)
                fragment.arguments = args
                return fragment
            }
        }
    }

    private fun handleIntent (intent: Intent) {
        val accepted = arrayListOf<String?>(
            getString(R.string.action_allow),
            getString(R.string.action_deny),
            null
        )

        if (intent.action !in accepted) {
            return
        }

        val ip = intent.getStringExtra("ip")
        val uuid = intent.getStringExtra("uuid")
        val base = intent.getStringExtra("base")

        Log.d(TAG, "ip: ${ip}, uuid: ${uuid}, base: ${base}, action: ${intent.action}")

        if (intent.action == null) {
            return
        }

        getSystemService(Context.NOTIFICATION_SERVICE)?.let {
            val manager = it as NotificationManager
            for (notification in manager.activeNotifications) {
                if (notification.tag != mNotificationTag) {
                    continue
                }

                manager.cancel(notification.tag, notification.id)
            }
        }
    }


    private companion object {
        const val TOPIC_NAME = "auth"
        const val TAG = "MA"
        var mSectionsPagerAdapter: SectionsPagerAdapter? = null
    }
}
