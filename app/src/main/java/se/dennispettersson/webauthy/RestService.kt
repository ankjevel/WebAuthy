package se.dennispettersson.webauthy

import android.os.StrictMode
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import se.dennispettersson.webauthy.AuthMessaging.Content.AuthMessagingNotification
import java.net.URL

class RestService {
    @ExperimentalCoroutinesApi
    private fun request(url: String): String? {
        var res: String? = null

        val thread = GlobalScope.launch(Dispatchers.Main.immediate) {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
            try {
                res = URL(url).readText()
            } catch (ex: Exception) {
                Log.d(TAG, "ex: $ex")
            }
        }

        thread.start()

        while (!thread.isCompleted) { // wait for response
            Thread.sleep(1_000)
        }

        return res
    }

    fun handleAction(item: AuthMessagingNotification?, action: String) {
        if (item == null) {
            return
        }

        val res = request(item.getURL(action))

        Log.d(TAG, "item: $item, res: $res")
    }

    companion object {
        val TAG = "RS"
    }
}
