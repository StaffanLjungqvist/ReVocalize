package se.agara.revocalize.adapters

import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.RecyclerView
import se.agara.revocalize.MainActivity
import se.agara.revocalize.R
import se.agara.revocalize.models.Slize


class MyRecyclerAdapter(val context : Context, var slizes : List<Slize>, var audioHelper : AudioAdapter) : RecyclerView.Adapter<MyRecyclerAdapter.MyViewHolder>() {

    var blinknumber = -1



    inner class MyViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        var btnSlice = view.findViewById<Button>(R.id.btnbutton)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.slize_item, parent, false))
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val slice = slizes[position]

        if (position == blinknumber) {
            holder.itemView.setBackgroundColor(Color.WHITE)
        } else {
            holder.itemView.setBackgroundColor(Color.parseColor(slice.color))
        }

        holder.itemView.setOnClickListener {

   //    audioHelper.playAudio(slice)

    //        println("playing audio from slice ${slice}")

        }



        holder.itemView.setOnTouchListener { view, event ->

            /*
if(event.actionMasked == MotionEvent.ACTION_UP) {
    audioHelper.playAudio(slice)

    println("playing audio from slice ${slice}")
}
*/



            if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                /*
                2. When we detect touch-down event, we call the
                startDragging(...) method we prepared above
                */
                (context as MainActivity).startDragging(holder)

                audioHelper.playAudio(slice)

                println("playing audio from slice ${slice}")

            }
            return@setOnTouchListener true
        }

    }

    override fun getItemCount(): Int {
        return slizes.size
    }


    fun runLight(slizes : List<Slize>) {
        val mainHandler = Handler(Looper.getMainLooper())
        blinknumber = 0
        mainHandler.post(object : Runnable {

            override fun run() {

                Log.d("kolla", "blinknummer : ${blinknumber}")

                if (blinknumber < (slizes.size )) {
                    notifyItemChanged(blinknumber)
                    blinknumber += 1

                    mainHandler.postDelayed(this, slizes[0].length)
                } else {
                    blinknumber = -1
                    Log.d("kolla", "blinknummer : ${blinknumber}")

                }
            }

        })

    }




}