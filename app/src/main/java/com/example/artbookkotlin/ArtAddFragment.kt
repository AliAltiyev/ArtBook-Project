package com.example.artbookkotlin

import android.Manifest
import android.app.Activity
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.artbookkotlin.databinding.FragmentArtAddBinding
import com.google.android.material.snackbar.Snackbar
import java.io.ByteArrayOutputStream
import java.io.IOException

const val DATABASE_NAME = "Art"
const val DATABASE_TABLE_NAME = "art"
const val DATABASE_ART_NAME_COLUMN_NAME = "art name"
const val DATABASE_ARTIST_NAME_COLUMN_NAME = "artist name"
const val DATABASE_YEAR_COLUMN_NAME = "year"

class ArtAddFragment : Fragment() {
    private lateinit var binding: FragmentArtAddBinding
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private var selectedBitmap: Bitmap? = null
    private val action = ArtAddFragmentDirections.actionArtAddFragmentToSplashFragment()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentArtAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)


        registerLauncher()
        binding.selectImageFromGallery.setOnClickListener {

            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        requireActivity(),
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                ) {
                    Snackbar.make(view, "Permission needed for gallery", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Give Permission",
                            View.OnClickListener {
                                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                            }).show()
                } else {
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            } else {
                val intentToGallery =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }
        }

        binding.saveArtistButton.setOnClickListener {


            val artName = binding.artNameEditText.text.toString()
            val artistName = binding.artistNameEditText.text.toString()
            val year = binding.yearEditText.text.toString()


            if (selectedBitmap != null) {
                val smallBitmap = makeSmallerBitmap(selectedBitmap!!, 300)

                val outPutStream = ByteArrayOutputStream()
                smallBitmap.compress(Bitmap.CompressFormat.PNG, 50, outPutStream)
                val byteArrayBitmap = outPutStream.toByteArray()


                try {
                    val database =
                        requireActivity().openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null)
                    database.execSQL(
                        "CREATE TABLE IF NOT EXISTS $DATABASE_TABLE_NAME(id INTEGER PRIMARY KEY,art_name VARCHAR,artist_name VARCHAR,year INT,image BLOB)"
                    )
                    val stringSQL =
                        "INSERT INTO $DATABASE_TABLE_NAME(art_name,artist_name,year,image) VALUES (?,?,?,?)"


                    val statement = database.compileStatement(stringSQL)
                    statement.bindString(1, artName)
                    statement.bindString(2, artistName)
                    statement.bindString(3, year)
                    statement.bindBlob(4, byteArrayBitmap)
                    statement.execute()
                } catch (e: Exception) {

                    e.printStackTrace()

                }
            }

            Navigation.findNavController(it).navigate(action)
            Toast.makeText(context, "Saved", Toast.LENGTH_LONG).show()
        }


    }


    private fun makeSmallerBitmap(image: Bitmap, maximumSize: Int): Bitmap {
        var width = image.width
        var height = image.height

        val bitmapRatio: Double = width.toDouble() / height.toDouble()
        if (bitmapRatio > 1) {
            width = maximumSize
            val scaledHeight = width / bitmapRatio
            height = scaledHeight.toInt()
        } else {
            height = maximumSize
            val scaledWidth = height * bitmapRatio
            width = scaledWidth.toInt()
        }
        return Bitmap.createScaledBitmap(image, width, height, true)
    }


    private fun registerLauncher() {
        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val intentFromResult = result.data
                    if (intentFromResult != null) {
                        val imageData = intentFromResult.data
                        try {
                            if (Build.VERSION.SDK_INT >= 28) {
                                val source = imageData?.let {
                                    ImageDecoder.createSource(
                                        requireActivity().contentResolver,
                                        it
                                    )
                                }
                                selectedBitmap = source?.let { ImageDecoder.decodeBitmap(it) }
                                binding.selectImageFromGallery.setImageBitmap(selectedBitmap)
                            } else {
                                selectedBitmap = MediaStore.Images.Media.getBitmap(
                                    requireActivity().contentResolver,
                                    imageData
                                )
                                binding.selectImageFromGallery.setImageBitmap(selectedBitmap)
                            }
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
                if (result) {
                    //permission granted
                    val intentToGallery =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    activityResultLauncher.launch(intentToGallery)
                } else {
                    //permission denied
                    Toast.makeText(requireContext(), "Permisson needed!", Toast.LENGTH_LONG)
                        .show()
                }
            }


    }


}
