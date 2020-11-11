package com.yazidal.kurikulumappinstagram

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btnLogin.setOnClickListener {
            loginUser()
        }
        txtRegisAccount.setOnClickListener {
            startActivity(Intent(this, Register::class.java))
        }

    }

    private fun loginUser() {
        val email = username_login.text.toString()
        val password = password_login.text.toString()

        when {
            TextUtils.isEmpty(email) -> Toast.makeText(
                this,
                "Username cannot be null",
                Toast.LENGTH_SHORT
            ).show()
            TextUtils.isEmpty(password) -> Toast.makeText(
                this,
                "Password cannot be null",
                Toast.LENGTH_SHORT
            ).show()

            else -> {
                val progressDialog = ProgressDialog(this)
                progressDialog.setTitle("Login")
                progressDialog.setMessage("Please wait a second")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()

                val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        progressDialog.dismiss()
                        val pergi = Intent(this, MainActivity::class.java)
                        startActivity(pergi)
                        finish()
                    } else {
                        val message = task.exception.toString()
                        Toast.makeText(this, "Eror: $message", Toast.LENGTH_SHORT).show()
                        FirebaseAuth.getInstance()
                        progressDialog.dismiss()
                    }
                }
            }

        }

    }

    override fun onStart() {
        super.onStart()

        if (FirebaseAuth.getInstance().currentUser != null){
            val move = Intent(this, MainActivity::class.java)
            move.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(move)
            finish()
        }
    }
}