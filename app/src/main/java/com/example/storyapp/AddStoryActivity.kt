package com.example.storyapp

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.databinding.ActivityAddStoryBinding
import com.example.storyapp.viewmodel.AddStoryViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class AddStoryActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var descriptionEditText: EditText
    private lateinit var uploadButton: Button
    private lateinit var preference: PreferenceManager
    private lateinit var addStoryViewModel: AddStoryViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        addStoryViewModel = ViewModelProvider(this)[AddStoryViewModel::class.java]
        preference = PreferenceManager(this)
        addStoryViewModel.setPreference(preference)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        imageView = binding.imageView
        descriptionEditText = binding.edAddDescription
        uploadButton = binding.btnUpload

        val galleryButton = binding.btnGallery
        galleryButton.setOnClickListener {
            openGallery()
        }

        val cameraButton = binding.btnCamera
        cameraButton.setOnClickListener {
            askCameraPermission()
        }
        uploadButton.setOnClickListener {
            uploadStory()
        }

        addStoryViewModel.addStoryResult.observe(this, Observer { result ->
            when (result) {
                is Result.Success -> {
                    Toast.makeText(this@AddStoryActivity, "Story uploaded successfully", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@AddStoryActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                is Result.Error -> {
                    Toast.makeText(this@AddStoryActivity, "Failed upload", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun openGallery() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                PERMISSION_GALLERY_CODE
            )
        } else {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            val mimeTypes = arrayOf("image/jpeg", "image/jpg", "image/png")
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            startActivityForResult(intent, CODE_REQUEST_GALLERY)
        }
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CODE_REQUEST_CAMERA)
    }

    private fun uploadStory() {
        if (selectedImageUri == null) {
            Toast.makeText(this, R.string.select_image_first, Toast.LENGTH_SHORT).show()
            return
        }

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener(this, OnSuccessListener { location ->
                if (location != null) {
                    val latitude = location.latitude
                    val longitude = location.longitude

                    // Upload cerita dengan lokasi
                    val file = getFileFromUri(selectedImageUri!!)
                    if (file == null) {
                        Toast.makeText(this, R.string.failed_retrieve_img, Toast.LENGTH_SHORT).show()
                        return@OnSuccessListener
                    }

                    val requestBody: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
                    val photoPart: MultipartBody.Part = MultipartBody.Part.createFormData("photo", file.name, requestBody)
                    val description = descriptionEditText.text.toString()
                    val descriptionPart: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), description)
                    val latitudePart: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), latitude.toString())
                    val longitudePart: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), longitude.toString())

                    addStoryViewModel.uploadStory(descriptionPart, photoPart, latitudePart, longitudePart)
                }
            })
    }


    private fun askCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.CAMERA),
                PERMISSION_CAMERA_CODE
            )
        } else {
            openCamera()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_CAMERA_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera()
                } else {
                    Toast.makeText(
                        this,
                        "Camera Permission is Required to Use the Camera",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            PERMISSION_GALLERY_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openGallery()
                } else {
                    Toast.makeText(
                        this,
                        "Gallery Permission is Required to Access the Gallery",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CODE_REQUEST_GALLERY -> {
                    data?.data?.let { uri ->
                        selectedImageUri = uri
                        imageView.setImageURI(uri)
                    }
                }
                CODE_REQUEST_CAMERA -> {
                    data?.extras?.get("data")?.let { image ->
                        val bitmap = image as Bitmap
                        val uri = saveImage(bitmap)
                        selectedImageUri = uri
                        imageView.setImageURI(uri)
                    }
                }
            }
        }
    }

    private fun getFileFromUri(uri: Uri): File? {
        val inputStream = contentResolver.openInputStream(uri)
        val file = File(cacheDir, "temp_image.jpg")
        val outputStream = FileOutputStream(file)
        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()
        return file
    }


    private fun saveImage(image: Bitmap): Uri? {
        val savedImageUri: Uri?
        val imageFileName = "IMG_${System.currentTimeMillis()}.jpg"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imageFile = File(storageDir, imageFileName)
        savedImageUri = try {
            val stream = FileOutputStream(imageFile)
            image.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
            Uri.fromFile(imageFile)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
        return savedImageUri
    }

    companion object {
        private const val CODE_REQUEST_GALLERY = 1
        private const val CODE_REQUEST_CAMERA = 2
        private val PERMISSION_CAMERA_CODE = 100
        private val PERMISSION_GALLERY_CODE = 101
    }
}