package com.dnake.application.ui.xfsetup

import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.dnake.application.R


/**
 * [RecyclerView.Adapter] that can display a [DummyItem].
 * TODO: Replace the implementation with code for your data type.
 */
@Suppress("DEPRECATION")
open class MyItemRecyclerViewAdapter(
    private val values: List<DummyContent.DummyItem>
) : RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder>() {
    var colorC:Int=0;

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_item_xfsetup, parent, false)
        colorC=view.resources.getColor(R.color.colorC )

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        if(position%7== DummyContent.dw -1 ) {
            holder.idView.setTextColor(Color.RED)
            holder.contentView.setTextColor(Color.RED)
            holder.buttonA.setTextColor(Color.RED)
            holder.buttonD.setTextColor(Color.RED)

        }
        else{
            holder.idView.setTextColor(Color.YELLOW)
            holder.contentView.setTextColor(Color.YELLOW)
            holder.buttonA.setTextColor(Color.YELLOW)
            holder.buttonD.setTextColor(Color.YELLOW)
        }
        holder.idView.text = item.id
        holder.contentView.text = (item.content*10).toString()
        holder.pos=position;
        if(position%2==0)
        holder.mView.setBackgroundColor(colorC)
        else
        holder.mView.setBackgroundColor(0x00000000)
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mView=view
        val idView: TextView = view.findViewById(R.id.item_number)
        val contentView: TextView = view.findViewById(R.id.content)
        val buttonA:Button=view.findViewById(R.id.buttonA)
        val buttonD=view.findViewById<Button>(R.id.buttonD)
        var pos:Int= 0;
        init {
            buttonA.setOnClickListener(){
                values[pos].content= if(values[pos].content+5<=255) (values[pos].content+5)/5*5 else 255
                notifyDataSetChanged()
            }
            buttonD.setOnClickListener(){
                values[pos].content= if((values[pos].content-5)/5*5 >=55) (values[pos].content-5)/5*5 else 55
                notifyDataSetChanged()
            }
        }
        override fun toString(): String {
            return super.toString() + " '" + contentView.text + "'"
        }
    }
}