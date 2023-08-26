package com.tejas.tasklist

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.tejas.tasklist.DB.Task
import com.tejas.tasklist.DB.TaskDatabase
import com.tejas.tasklist.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setContentView(binding.root)
        } else {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Compatibility Issue")
                .setMessage("This app requires at least Android API level 26.")
                .setPositiveButton("Exit") { _, _ -> finish() }
                .setCancelable(false)
                .create()
                .show()
        }


        supportActionBar?.hide()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.apply {
                decorView.systemUiVisibility =
                    (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)

                statusBarColor = Color.TRANSPARENT
            }
        }



        binding.searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {

                binding.taskRecyclerView.layoutManager =
                    StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

                binding.taskRecyclerView.adapter = TaskAdapter(this@MainActivity).apply {
                    lifecycleScope.launch {
                        setData(TaskDatabase(applicationContext).getTaskDao().searchTasks("%$s%"))
                    }
                }
            }

        })

        binding.addTask.setOnClickListener {
            startActivity(Intent(this, NotesActivity::class.java))
        }


        swipeToDelete(binding.taskRecyclerView)

    }

    override fun onResume() {
        super.onResume()


        binding.taskRecyclerView.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        binding.taskRecyclerView.adapter = TaskAdapter(this).apply {
            lifecycleScope.launch {
                val allTasks = TaskDatabase(applicationContext).getTaskDao().showAllTask()
                setData(allTasks)

            }
        }

    }

    private fun swipeToDelete(taskRecyclerView: RecyclerView) {
        val swipeToDeleteCallback = object : SwipeToDelete() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                val deletedTaskPosition = viewHolder.adapterPosition
                val deletedTask =
                    (taskRecyclerView.adapter as TaskAdapter).list[deletedTaskPosition]

                (taskRecyclerView.adapter as TaskAdapter).list.removeAt(deletedTaskPosition)
                taskRecyclerView.adapter?.notifyDataSetChanged()

                deleteTask(deletedTask)

                val snackBar = Snackbar.make(taskRecyclerView, "Note Deleted", Snackbar.LENGTH_LONG)
                    .addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                            super.onDismissed(transientBottomBar, event)
                        }

                        override fun onShown(transientBottomBar: Snackbar?) {

                            transientBottomBar?.setAction("Undo") {
                                lifecycleScope.launch {
                                    TaskDatabase(applicationContext).getTaskDao()
                                        .insertTask(deletedTask)
                                    onResume()
                                }

                            }

                            super.onShown(transientBottomBar)

                        }
                    }).apply {
                        animationMode = Snackbar.ANIMATION_MODE_FADE
                        anchorView = binding.addTask
                    }
                snackBar.show()

            }

        }

        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(taskRecyclerView)

    }


    fun deleteTask(task: Task) {
        lifecycleScope.launch {
            TaskDatabase(applicationContext).getTaskDao().deleteTask(task)
        }
    }

    fun emptyList() {
        TaskAdapter(this).apply {
            lifecycleScope.launch {
                val allTasks = TaskDatabase(applicationContext).getTaskDao().showAllTask()

                if (allTasks.isEmpty()) {
                    binding.emptyListAnimation.visibility = View.VISIBLE
                    binding.emptyListAnimation.playAnimation()
                } else {
                    binding.emptyListAnimation.visibility = View.GONE
                }
            }
        }
    }

}