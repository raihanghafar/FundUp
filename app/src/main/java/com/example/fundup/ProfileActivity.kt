package com.example.fundup

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class ProfileActivity : AppCompatActivity() {

    private lateinit var logoutButton: Button
    private lateinit var profileImage: ImageView
    private lateinit var emailTextView: TextView
    private lateinit var usernameTextView: TextView
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        logoutButton = findViewById(R.id.logoutButton)
        profileImage = findViewById(R.id.profileImage)
        emailTextView = findViewById(R.id.emailTextView)
        usernameTextView = findViewById(R.id.usernameTextView)

        val serverClientId = getString(R.string.default_web_client_id)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(serverClientId)
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            displayUserInfo(user)

            logoutButton.setOnClickListener {
                signOut()
            }
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }

    private fun signOut() {
        FirebaseAuth.getInstance().signOut()

        googleSignInClient.signOut().addOnCompleteListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()

            Toast.makeText(this, "Log-out successful", Toast.LENGTH_SHORT).show()
        }
    }

    private fun displayUserInfo(user: FirebaseUser) {
        Glide.with(this)
            .load(user.photoUrl)
            .circleCrop()
            .into(profileImage)

        emailTextView.text = user.email
        usernameTextView.text = user.displayName
    }
}