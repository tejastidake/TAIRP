package com.tejas.tasklist

import android.content.Context
import android.content.Intent
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

        val existingTask = list[position]

        holder.itemView.setOnClickListener {

            val intent = Intent(context, NotesActivity::class.java)
            intent.putExtra("clicked_task", existingTask.id)
            intent.putExtra("title", existingTask.title)
            intent.putExtra("desc", existingTask.desc)
            context.startActivity(intent)

        }


    }

    private fun convertDateFormat(originalFormat: String): String {
        val editedTag = " | Edited"
        val edited = originalFormat.endsWith(editedTag)
        val actualOriginalFormat =
            if (edited) originalFormat.dropLast(editedTag.length) else originalFormat

        val originalDateFormat = SimpleDateFormat("yy/MM/dd, HH:mm:ss", Locale.getDefault())
        val desiredDateFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())

        val originalDate = originalDateFormat.parse(actualOriginalFormat)
        val formattedDate = desiredDateFormat.format(originalDate)

        return if (edited) "$formattedDate | Edited" else formattedDate
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
