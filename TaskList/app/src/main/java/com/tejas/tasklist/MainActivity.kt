package com.tejas.tasklist

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.tejas.tasklist.DB.TaskDatabase
import com.tejas.tasklist.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)


        supportActionBar?.hide()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.apply {
                decorView.systemUiVisibility =
                    (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)

                statusBarColor = Color.TRANSPARENT
            }
        }

        onResume()

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