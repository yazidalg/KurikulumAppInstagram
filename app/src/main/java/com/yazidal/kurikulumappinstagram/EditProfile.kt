package com.yazidal.kurikulumappinstagram

import User
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_edit_profile.*
import java.io.File

class EditProfile : AppCompatActivity() {

    private lateinit var firebaseUser: FirebaseUser
    private var cekInfoProfile = ""
    private var myUrl = ""
    private var imageUri: Uri? = null
    private var storageProfilePicture: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        storageProfilePicture = FirebaseStorage.getInstance().reference.child("Profile Picture")

        btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            val move = Intent(this@EditProfile, Login::class.java)
            startActivity(move)
            finish()
        }

        edit_photo_profile.setOnClickListener {
            cekInfoProfile = "Clicked"

            CropImage.activity()
                .setAspectRatio(1, 1)
                .start(this@EditProfile)

        }


        btnSaveProf.setOnClickListener {
            if (cekInfoProfile == "Clicked") {
                uploadImageProfAndUpdateImageProf()
            } else {
                updateInfoUser()
            }

        }
        userInfo()

    }

    private fun uploadImageProfAndUpdateImageProf() {
        when {
            imageUri == null -> Toast.makeText(this, "Pilih Foto", Toast.LENGTH_SHORT).show()

            TextUtils.isEmpty(edtNamaProfilEdProf.text.toString()) -> {
                Toast.makeText(this, "Gaboleh kosong syg :) ", Toast.LENGTH_SHORT).show()
            }
            TextUtils.isEmpty(edtUserNameEdProf.text.toString()) -> {
                Toast.makeText(this, "Gaboleh kosong syg :) ", Toast.LENGTH_SHORT).show()
            }
            TextUtils.isEmpty(edtBioEdProf.text.toString()) -> {
                Toast.makeText(this, "Gaboleh kosong syg :) ", Toast.LENGTH_SHORT).show()
            }
            else -> {
                val progDialog = ProgressDialog(this@EditProfile)
                progDialog.setTitle("Profile di perbarui")
                progDialog.setMessage("Bentar ya syg :)")
                progDialog.show()

                val fileRef = storageProfilePicture!!.child(firebaseUser.uid + ".jpg")

                var uploadTask: StorageTask<*>
                uploadTask = fileRef.putFile(imageUri!!)
                uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                    if (!task.isSuccessful) {
                        task.exception.let {
                            throw it!!
                        }
                    }
                    return@Continuation fileRef.downloadUrl
                }).addOnCompleteListener(OnCompleteListener<Uri> { task ->
                        if (task.isSuccessful) {
                            val downloadUrl = task.result
                            myUrl = downloadUrl.toString()

                            val userRef = FirebaseDatabase.getInstance().reference.child("Users")
                            val userMap = HashMap<String, Any>()

                            userMap["fullname"] = edtNamaProfilEdProf.text.toString()
                            userMap["username"] = edtUserNameEdProf.text.toString()
                            userMap["bio"] = edtBioEdProf.text.toString()
                            userMap["image"] = myUrl

                            userRef.child(firebaseUser.uid).updateChildren(userMap)
                            Toast.makeText(this, "Info telah di update", Toast.LENGTH_LONG).show()
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                            progDialog.dismiss()
                        } else {
                            progDialog.dismiss()
                        }
                })
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK
            && data != null){
            val result = CropImage.getActivityResult(data)
            imageUri = result.uri
            setProfileImage.setImageURI(imageUri)

        }
    }

    private fun updateInfoUser() {
        when {
            TextUtils.isEmpty(edtNamaProfilEdProf.text.toString()) -> {
                Toast.makeText(this, "Gaboleh kosong syg :) ", Toast.LENGTH_SHORT).show()
            }
            TextUtils.isEmpty(edtUserNameEdProf.text.toString()) -> {
                Toast.makeText(this, "Gaboleh kosong syg :) ", Toast.LENGTH_SHORT).show()
            }
            TextUtils.isEmpty(edtBioEdProf.text.toString()) -> {
                Toast.makeText(this, "Gaboleh kosong syg :) ", Toast.LENGTH_SHORT).show()
            }

            else -> {
                val userRef = FirebaseDatabase.getInstance().reference
                    .child("Users")

                val userMap = HashMap<String, Any>()
                userMap["fullname"] = edtNamaProfilEdProf.text.toString()
                userMap["username"] = edtUserNameEdProf.text.toString()
                userMap["bio"] = edtBioEdProf.text.toString()

                userRef.child(firebaseUser.uid).updateChildren(userMap)

                Toast.makeText(this, "Di Perbarui", Toast.LENGTH_SHORT).show()

                val pegi = Intent(this, MainActivity::class.java)
                startActivity(pegi)
                finish()

            }

        }

    }

    private fun userInfo() {
        val user = FirebaseDatabase.getInstance().getReference()
            .child("Users").child(firebaseUser.uid)

        user.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue<User>(User::class.java)

                    edtNamaProfilEdProf.setText(user?.getFullname())
                    edtUserNameEdProf.setText(user?.getUsername())
                    edtBioEdProf.setText(user?.getBio())

                }
            }
        })
    }
}