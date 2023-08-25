package com.tejas.tasklist

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tejas.tasklist.DB.Task
import java.text.SimpleDateFormat
import java.util.*


class TaskAdapter(val context: Context, var list: MutableList<Task> = mutableListOf<Task>()) :
    RecyclerView.Adapter<TaskAdapter.ExampleViewHolder>() {


    class ExampleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val title: TextView = itemView.findViewById(R.id.task_title)
        val desc: TextView = itemView.findViewById(R.id.task_desc)
        val time: TextView = itemView.findViewById(R.id.time)

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExampleViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.task_itemview, parent, false)
        return ExampleViewHolder(view)

    }


    override fun onBindViewHolder(holder: ExampleViewHolder, position: Int) {

        holder.title.text = list[position].title
        holder.desc.text = list[position].desc
        holder.time.text = convertDateFormat(list[position].time)

    }


    private fun convertDateFormat(originalFormat: String): String {

        val originalDateFormat = SimpleDateFormat("yy/MM/dd, HH:mm:ss", Locale.getDefault())
        val desiredDateFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())

        val originalDate = originalDateFormat.parse(originalFormat)
        return desiredDateFormat.format(originalDate)

    }


    override fun getItemCount(): Int {

        (context as MainActivity).emptyList()
        return list.size

    }


    fun setData(data: List<Task>) {

        list.apply {
            clear()
            addAll(data)
            notifyDataSetChanged()
        }
    }

}
