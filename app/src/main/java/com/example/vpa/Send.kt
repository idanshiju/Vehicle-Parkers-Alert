package com.example.vpa
import android.content.Context
import android.os.StrictMode
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap
import java.util.Map

class Send {
    companion object {
        private const val BASE_URL = "https://fcm.googleapis.com/fcm/send"
        private const val SERVER_KEY = "key=AAAAf9Ocy9E:APA91bGctxnY9WM15VYYnf5-E8ytkDek4bdj0xgDDGUnAjBk4CULImbYaJsHXbMdK0xtQLvGz8fSypQjYkup6Mt5KRM5id5oEavltk4HGa2q0Ld493xhqm4TsnJnjPqZnmxlgUwCBpNL"

        fun pushNotification(context: Context, token: String, title: String, message: String, callback: ((Boolean, String?) -> Unit)? = null) {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)

            val queue: RequestQueue = Volley.newRequestQueue(context)

            try {
                val json = JSONObject()
                json.put("to", token)
                val notification = JSONObject()
                notification.put("title", title)
                notification.put("body", message)
                json.put("notification", notification)

                val jsonObjectRequest = object : JsonObjectRequest(
                    Request.Method.POST,
                    BASE_URL,
                    json,
                    Response.Listener { response ->
                        println("FCM $response")
                    },
                    Response.ErrorListener { error: VolleyError ->
                        error.printStackTrace()
                    }) {
                    @Throws(AuthFailureError::class)
                    override fun getHeaders(): MutableMap<String, String>? {
                        val params: MutableMap<String, String> = HashMap()
                        params["Content-Type"] = "application/json"
                        params["Authorization"] = SERVER_KEY
                        return params
                    }
                }

                queue.add(jsonObjectRequest)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            val notificationSentSuccessfully = true
            val resultMessage = "Notification sent successfully."
            callback?.invoke(notificationSentSuccessfully, resultMessage)
        }
    }
}
