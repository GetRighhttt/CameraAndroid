package com.example.cameraforandroid

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.hardware.Camera
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import androidx.core.app.ActivityCompat
import com.example.cameraforandroid.camera.CameraPreview
import com.example.cameraforandroid.databinding.ActivityMainBinding

/*
All of the camera code besides a few methods below have came straight from
the Android documentation, so it's straight from the source!
 */

@Suppress("Deprecation")
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var mCamera: Camera? = null
    private var mPreview: CameraPreview? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /*
        Sets the window of the screen to fullscreen.
         */
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        /*
        Keeps the screen on for demo purposes.
         */
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        /*
        Another portion of using the camera. This below just gives the camera access.
         */
        mCamera = getCameraInstance()

        mPreview = mCamera?.let {
            // Create our Preview view
            CameraPreview(this, it)
        }

        binding.apply {
            btnCamera.setOnClickListener {
                // Set the Preview view as the content of our activity.
                mPreview?.also {
                    val preview = frameLayout
                    preview.addView(it)
                }
                frameLayout.alpha = 0f
            }
        }

        /*
        Method call to request the permission when the activity starts.
         */
        requestPermissions()
    }


    /** Check if this device has a camera */
    private fun checkCameraHardware(context: Context): Boolean {
        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
    }

    /** A safe way to get an instance of the Camera object. */
    private fun getCameraInstance(): Camera? {
        return try {
            Camera.open() // attempt to get a Camera instance
        } catch (e: Exception) {
            // Camera is not available (in use or does not exist)
            null // returns null if camera is unavailable
        }
    }

    /*
    Method to release the camera.
     */
    private fun releaseCamera() {
        mCamera?.release() // release the camera for other applications
        mCamera = null
    }

    override fun onPause() {
        super.onPause()
        releaseCamera() // release the camera immediately on pause event
    }

    /*
    Permissions approach I typically take.
    Usually this approach is used when there are multiple permission requests.
    It's a good approach to practice.
     */
    private fun hasCameraPermission() =
        ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

    private fun requestPermissions() {
        val permissionsToRequest = mutableListOf<String>()

        if (!hasCameraPermission()) {
            permissionsToRequest.add(Manifest.permission.CAMERA)
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                0
            )
        }
    }

    /*
    Method called when the permissions are requested from the user.
     */
    @SuppressLint("LongLogTag")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 0 && grantResults.isNotEmpty()) {
            // can loop through grant results array.
            for (i in grantResults.indices) {// indices = size -1
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(
                        "MainActivity Permissions Request.",
                        "${permissions[i]} granted."
                    ) // print the permissions granted
                }
            }
        }
    }

}