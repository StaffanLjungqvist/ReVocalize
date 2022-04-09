package se.staffanljungqvist.revocalize.adapters

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class OnlineAdapter {

    val url = "https://firebasestorage.googleapis.com/v0/b/revocalize-bf576.appspot.com/o/phrases.json?alt=media&token=3668417e-3ad4-4fd2-82e3-d1ffd1db073f"


    fun downloadTask(context : Context) {
        val queue = Volley.newRequestQueue(context)
        val request = StringRequest(Request.Method.GET, url,
            Response.Listener { response ->

                Log.e("Error", response.toString())


            }, Response.ErrorListener {  })
        queue.add(request)
    }

}