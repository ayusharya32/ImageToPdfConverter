package com.easycodingg.pickfiles


import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.pdf.PdfDocument
import android.net.Uri

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    companion object {
        private const val INTENT_REQUEST_CODE = 0
    }

    private lateinit var myAdapter: MyAdapter
    private var imagesUriList: MutableList<Uri> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupRecyclerView()

        btnConvertToPdf.setOnClickListener {
            createAndSavePdf()
        }
    }

    private fun setupRecyclerView() {
        myAdapter = MyAdapter(listOf())

        recyclerView.apply {
            layoutManager = GridLayoutManager(this@MainActivity, 3)
            adapter = myAdapter
        }
    }

    private fun createAndSavePdf() {

        if(imagesUriList.size > 0){

            Toast.makeText(this, "Creating PDF..", Toast.LENGTH_SHORT).show()

            CoroutineScope(Dispatchers.IO).launch {
                val pdfDocument = PdfDocument()

                for(imageUri in imagesUriList){
                    val inputStream = contentResolver.openInputStream(imageUri)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 500, 800, false)

                    val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
                    val page = pdfDocument.startPage(pageInfo)
                    val canvas = page.canvas

                    canvas.drawBitmap(scaledBitmap, 40F, 20F, null)

                    pdfDocument.finishPage(page)
                }

                val fileName = "PDF" + SimpleDateFormat("ddMMyyyyhhmmss", Locale.UK).format(Calendar.getInstance().time)
                val filePath = getExternalFilesDir(null)?.absolutePath + "/$fileName.pdf"

                val file = File(filePath)
                if(!file.exists()){
                    file.createNewFile()
                }

                pdfDocument.writeTo(FileOutputStream(file))
                pdfDocument.close()

                withContext(Dispatchers.Main){
                    Toast.makeText(this@MainActivity, "PDF saved at $filePath", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.miSelectImages -> {
                Intent(Intent.ACTION_PICK).also {
                    it.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                    it.type = "image/*"
                    startActivityForResult(it, INTENT_REQUEST_CODE)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)

        if(resultCode == Activity.RESULT_OK && requestCode == INTENT_REQUEST_CODE){
            imagesUriList.clear()
            intent.let {
                for(i in 0 until it?.clipData?.itemCount!!){
                    imagesUriList.add(it.clipData!!.getItemAt(i).uri)
                }

                Log.d("Pathy", imagesUriList.size.toString())

                myAdapter.list = imagesUriList
                myAdapter.notifyDataSetChanged()
            }
        }
    }
}