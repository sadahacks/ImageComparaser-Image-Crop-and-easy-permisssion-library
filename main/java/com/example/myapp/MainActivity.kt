package com.example.myapp

import android.Manifest
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.myapp.image_picker.DialogImageAttachment
import com.example.myapp.image_picker.ImageAttachentCallback
import id.zelory.compressor.Compressor
import kotlinx.coroutines.launch
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.security.Permission

class MainActivity : AppCompatActivity() , EasyPermissions.PermissionCallbacks,
    EasyPermissions.RationaleCallbacks {

    var dialogImageAttachment: DialogImageAttachment? = null

    private val PERMISSIONS =
        arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

    companion object {
        const val PERMISSION_REQUEST_CODE = 124
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val img: ImageView = findViewById(R.id.imageView)

        img.setOnClickListener {
            selectImage(img)
        }


        locationAndContactsTask()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        dialogImageAttachment?.onActivityResult(requestCode, resultCode, data)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }





    private fun selectImage(img:ImageView) {
        dialogImageAttachment =
            DialogImageAttachment.make(this)
                .setType(DialogImageAttachment.Profile)
                .setCallback(object : ImageAttachentCallback {

                    override fun onSuccess(file: File?) {
                        if (file != null) {
//                            userProfile = Compressor.compress(requireContext(), file)

                            lifecycleScope.launch {
                                img.setImageBitmap(
                                    BitmapFactory.decodeFile(
                                        Compressor.compress(
                                            this@MainActivity!!,
                                            file
                                        ).absolutePath
                                    )
                                )
                            }

                        }
                    }

                    override fun onFailure(error: String?) {
                        Toast.makeText(this@MainActivity, error, Toast.LENGTH_SHORT).show()
                    }

                    override fun onClick(intent: Intent?, id: Int) {
                        startActivityForResult(intent,id)
                    }
                })
        dialogImageAttachment?.show()
    }


    @AfterPermissionGranted(PERMISSION_REQUEST_CODE)
    fun locationAndContactsTask() {
        if (EasyPermissions.hasPermissions(this, *PERMISSIONS)) {
        } else {
            // Ask for both permissions
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.rationale_permissions),
                PERMISSION_REQUEST_CODE,
                *PERMISSIONS
            )
        }
    }



    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this@MainActivity).build().show()
        } else {
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.rationale_permissions),
                PERMISSION_REQUEST_CODE,
                *PERMISSIONS
            )
        }
    }


    override fun onRationaleAccepted(requestCode: Int) {

    }

    override fun onRationaleDenied(requestCode: Int) {
        finish()
    }
}