package com.sachin.app.pydemo

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(this))
        }
        val result = findViewById<TextView>(R.id.result)
        val arrView = findViewById<TextView>(R.id.arr_view)
        val arr = arrayOf(1, 2, 3, 4, 5)
        arrView.text = "[" + arr.joinToString() + "]"

        try {
            val py = Python.getInstance()
            val npy = py.getModule("demo")
            val s = npy.callAttr("getArrSize", arr).toJava(Int::class.java)
            result.text = "size: $s"
        } catch (e: Exception) {
            e.printStackTrace()
            result.text = e.toString()
        }
    }
}