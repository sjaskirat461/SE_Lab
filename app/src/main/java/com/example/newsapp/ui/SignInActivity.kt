package com.example.newsapp.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.newsapp.R
import com.example.newsapp.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : AppCompatActivity() {

    private var _binding: ActivitySignInBinding? = null
    private val binding: ActivitySignInBinding  get() = _binding!!
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("Bhosda", "In activity")
        _binding = DataBindingUtil.setContentView(this@SignInActivity, R.layout.activity_sign_in)
        Log.d("Bhosda", "Post bind")

        firebaseAuth = FirebaseAuth.getInstance()

        binding.signInActivity = this@SignInActivity
    }

    fun setViewing(views: Int){
        binding.signUp.visibility = views
        binding.signInButton.visibility = views
        binding.passEt.visibility = views
        binding.passEtUp.visibility = views
        binding.emailEt.visibility = views
        binding.emailEtUp.visibility = views
    }

    fun verifyWithFirebase(){
        val email = binding.emailEt.text.toString()
        val passwd = binding.passEt.text.toString()
        if (email.isNotEmpty() && passwd.isNotEmpty()){
            firebaseAuth.signInWithEmailAndPassword(email, passwd).addOnCompleteListener {
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

    override fun onStart() {
        super.onStart()
        if(firebaseAuth.currentUser!=null){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        } else{
            setViewing(View.VISIBLE)
        }
    }
}