package com.example.a09uscamera

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.MediaController
import android.widget.VideoView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {
    lateinit var vidView: VideoView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        vidView = findViewById(R.id.videoView)
        val mediaCollection = MediaController(this)
        mediaCollection.setAnchorView(vidView)
        vidView.setMediaController(mediaCollection)

        val takeVideo = findViewById<Button>(R.id.TakeVideo)
        takeVideo.setOnClickListener()
        {
            //startForResult.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE),)
            //Fent servir File Provider ara haurem de gestionar millor el retorn de l'Intent de la Càmera
            val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE).also{
                it.resolveActivity(packageManager).also{component->
                    //File pot ser un fitxer emmagatzemat a la memòria, no cal que estigui al magatzem del dispositiu
                    //val photoFile:File

                    //Crearem un métode que guardi el File que necessitem

                    createVideoFile()

                    //Uri sí que queda emmagatzemat a una ruta del magatzem del dispositiu
                    val videoUri: Uri = FileProvider.getUriForFile(this,"com.example.fileprovider.fileprovider", file)

                    it.putExtra(MediaStore.EXTRA_OUTPUT, videoUri)
                    //Hem reanomenat l'iterador per defecte a component per poder continuar tinguen accés a l'iterador it que fa referència a l'intent. Sinó no ens deixaria
                }
            }
            //Ara cridarem el launch passant el l'intent modificat
            startForResult.launch(intent)
            //also vol dir que sobre aquest intent també farem més coses(also)
        }
    }

    //Creem una variable global perquè file el necessitarem a més d'un lloc.

    private lateinit var file: File
    private fun createVideoFile() {
        //Necessitem accedir a un directori extern
        //Enviroment.DIRECTORY_PICTURES retorna la ruta on es guarden les images al dispositiu
        val dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        //Crearem un fitxer temporal
        //El nom del fitxer serà "IMG_" seguit del temps actual en milisegons acabat en _. Ho indiquem al prefix:
        //L'extensió l'indicarem al "sufix" i serà -jpg

        file = File.createTempFile("Denise_${System.currentTimeMillis()}_",".mp4", dir)
        //val fileOutputOptions = FileOutputOptions.Builder(file).build()
    }

    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
    {
            result: ActivityResult ->
        if(result.resultCode == Activity.RESULT_OK)
        {
            val intent = result.data
            //Ara el bitmap es troba enregistrat al File
            //val imageBitmap = intent?.extras?.get("data") as Bitmap
            //val imageBitmap = BitmapFactory.decodeFile(file.toString())
            val vidUri = file.toUri()
            vidView.setVideoURI(vidUri)
            vidView.start()
        }
    }
}