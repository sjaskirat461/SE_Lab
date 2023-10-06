package com.example.newsapp.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.newsapp.R
import com.example.newsapp.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import kotlin.math.sign

class SignUpActivity : AppCompatActivity() {
    private var _binding : ActivitySignUpBinding? = null
    private val binding : ActivitySignUpBinding get() =  _binding!!
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        _binding = DataBindingUtil.setContentView(
            this@SignUpActivity,
            R.layout.activity_sign_up
        )
        firebaseAuth = FirebaseAuth.getInstance()
        binding.signUpButton.setOnClickListener {
            signUpWithFirebase()
        }
    }

    private fun signUpWithFirebase(){
        val email = binding.emailEtSgu.text.toString()
        val passwd = binding.passEtSgu.text.toString()
        if (email.isNotEmpty() && passwd.isNotEmpty()){
            firebaseAuth.createUserWithEmailAndPassword(email, passwd).addOnCompleteListener {
                if(it.isSuccessful){
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        } else{
            Toast.makeText(this, "Empty fields Not allowed", Toast.LENGTH_SHORT).show()
        }
    }

}