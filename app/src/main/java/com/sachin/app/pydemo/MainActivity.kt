package com.sachin.app.pydemo

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(this));
        }
        val result = findViewById<TextView>(R.id.result)
        val num1 = findViewById<TextView>(R.id.num1)
        val num2 = findViewById<TextView>(R.id.num2)
        val btn = findViewById<TextView>(R.id.btn)

        btn.setOnClickListener {
            val a = num1.text.toString().toIntOrNull()
            val b = num2.text.toString().toIntOrNull()

            if (a == null || b == null) {
                Toast.makeText(this, "Enter value of a and b", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val py = Python.getInstance()
            val i = py.builtins.callAttr("sum", arrayOf(a, b)).toJava(Int::class.java)
            result.text = i.toString()
        }
    }
}