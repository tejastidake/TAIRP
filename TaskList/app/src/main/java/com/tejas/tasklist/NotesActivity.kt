package com.tejas.tasklist

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.tejas.tasklist.DB.Task
import com.tejas.tasklist.DB.TaskDatabase
import com.tejas.tasklist.databinding.ActivityNotesBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class NotesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNotesBinding.inflate(layoutInflater)

        setContentView(binding.root)


        supportActionBar?.hide()


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = Color.TRANSPARENT
        }


        binding.titleEditText.requestFocus()

        val inputMethodManager: InputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(binding.titleEditText, InputMethodManager.SHOW_IMPLICIT)



        binding.saveFabBtn.setOnClickListener {
            if (binding.titleEditText.text.toString() == "") {
                binding.titleEditText.apply {
                    error = "Required"
                    hint = "Title Required"

                    requestFocus()

                    val inputMethodManager: InputMethodManager =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.showSoftInput(
                        binding.titleEditText,
                        InputMethodManager.SHOW_IMPLICIT
                    )

                }
            } else if (binding.descEditText.text.toString() == "") {
                binding.descEditText.apply {
                    error = "Required"
                    hint = "Description Required"

                    requestFocus()

                    val inputMethodManager: InputMethodManager =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.showSoftInput(
                        binding.descEditText,
                        InputMethodManager.SHOW_IMPLICIT
                    )

                }
            } else {
                saveTask()
            }
        }


        binding.backBtn.setOnClickListener {
            finish()
        }

    }


    private fun saveTask() {

        val currentDateTime = Calendar.getInstance().time

        val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(currentDateTime)
        val date = SimpleDateFormat("yy/MM/dd", Locale.getDefault()).format(currentDateTime)


        lifecycleScope.launch {
            TaskDatabase(applicationContext).getTaskDao().insertTask(
                Task(
                    0,
                    "${binding.titleEditText.text.trim()}",
                    "${binding.descEditText.text.trim()}",
                    "$date, $time"
                )
            )
            Toast.makeText(applicationContext, "Saved", Toast.LENGTH_SHORT).show()
            finish()
        }

    }
}