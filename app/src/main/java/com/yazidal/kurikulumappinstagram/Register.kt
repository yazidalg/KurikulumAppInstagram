package com.yazidal.kurikulumappinstagram

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*

class Register : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        btnRegister.setOnClickListener {
            createAkun()
        }

        tvSignIn.setOnClickListener {
            startActivity(Intent(this, Login::class.java))
        }

    }

    private fun createAkun() {
        val fullname = edt_fullname_regis.text.toString()
        val username = edt_usrnmRegis.text.toString()
        val email = edt_email_regis.text.toString()
        val password = edt_password_regis.text.toString()

        when {
            TextUtils.isEmpty(fullname) -> Toast.makeText(
                this,
                "Fullname must not be null",
                Toast.LENGTH_SHORT
            ).show()
            TextUtils.isEmpty(username) -> Toast.makeText(
                this,
                "Username must not be null",
                Toast.LENGTH_SHORT
            ).show()
            TextUtils.isEmpty(email) -> Toast.makeText(
                this,
                "Email must not be null",
                Toast.LENGTH_SHORT
            ).show()
            TextUtils.isEmpty(password) -> Toast.makeText(
                this,
                "Password must not be null",
                Toast.LENGTH_SHORT
            ).show()

            else -> {
                val progressDialog = ProgressDialog(this)
                progressDialog.setTitle("Register")
                progressDialog.setMessage("Please wait a second")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()

                val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

                mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            saveUserInfo(fullname, username, email, password, progressDialog)
                        }

                    }
            }
        }

    }

    private fun saveUserInfo(
        fullname: String,
        username: String,
        email: String,
        password: String,
        progressDialog: ProgressDialog
    ) {

        val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
        val userRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users")

        val userMap = HashMap<String, Any>()
        userMap["uid"] = currentUserId
        userMap["fullname"] = fullname
        userMap["username"] = username
        userMap["email"] = email
        userMap["bio"] = "Null!"
        userMap["image"] =
            "https://firebasestorage.googleapis.com/v0/b/instagram-app-797d8.appspot.com/o/image%2Ff1.jpg?alt=media&token=919ea159-f490-42ee-b3cb-001367901cc4"

        userRef.child(currentUserId).setValue(userMap)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    progressDialog.dismiss()
                    Toast.makeText(this, "Account was create", Toast.LENGTH_SHORT).show()

                    val pindah = Intent(this@Register, MainActivity::class.java)
                    pindah.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(pindah)
                    finish()
                }else{
                    val message = task.exception!!.toString()
                    Toast.makeText(this,"Eror: $message", Toast.LENGTH_SHORT).show()
                    FirebaseAuth.getInstance().signOut()
                    progressDialog.dismiss()
                }
            }
        }
    }
