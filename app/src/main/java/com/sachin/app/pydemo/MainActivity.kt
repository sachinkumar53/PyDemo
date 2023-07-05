package com.sachin.app.pydemo

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File


class MainActivity : AppCompatActivity() {
    private lateinit var result: TextView
    private lateinit var iv: ImageView
    private lateinit var progress: View
    private lateinit var mainView: View
    private lateinit var btn: Button

    private var uri: Uri? = null

    private val launcher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        val u = uri
        if (isSuccess && u != null) {
            lifecycleScope.launch {
                mainView.isVisible = false
                progress.isVisible = true
                contentResolver.openInputStream(u)?.use {
                    val image = BitmapFactory.decodeStream(it)
                    resizeGray(image)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(this));
        }

        progress = findViewById(R.id.prgress_view)
        iv = findViewById(R.id.iv)
        result = findViewById(R.id.result)
        mainView = findViewById(R.id.main_view)
        btn = findViewById(R.id.btn)

        btn.setOnClickListener {
            val file = File(externalCacheDir, "IMG_" + System.currentTimeMillis() + ".jpg")
            uri = FileProvider.getUriForFile(
                this,
                applicationContext.packageName + ".provider",
                file
            )
            launcher.launch(uri)
        }

    }

    private suspend fun resizeGray(bitmap: Bitmap?) = withContext(Dispatchers.IO) {
        if (bitmap == null) return@withContext
        try {
            val py = Python.getInstance()
            val module = py.getModule("demo")
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
            val i =
                module.callAttr("resize_gray", baos.toByteArray()).toJava(ByteArray::class.java)
            val bitmap = BitmapFactory.decodeByteArray(i, 0, i.size)
            runOnUiThread {
                progress.isVisible = false
                mainView.isVisible = true
                iv.setImageBitmap(bitmap)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            runOnUiThread {
                result.text = e.toString()
            }
        }
    }
}