package com.example.fundup

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CEOActivity : AppCompatActivity() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val questionPages = listOf(
        R.layout.activity_ceo_question1,
        R.layout.activity_ceo_question2,
        R.layout.activity_ceo_question3,
        R.layout.activity_ceo_question4,
        R.layout.activity_ceo_question5
    )

    private lateinit var previousButton: ImageButton
    private lateinit var nextButton: Button

    private var currentQuestionIndex = 0

    private lateinit var sharedPreferences: SharedPreferences

    // Initialize Firestore
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(questionPages[currentQuestionIndex]) // Set the initial layout file

        previousButton = findViewById(R.id.previousButton)
        nextButton = findViewById(R.id.nextButton)

        previousButton.setOnClickListener {
            handlePreviousButtonClick()
        }

        nextButton.setOnClickListener {
            handleNextButtonClick()
        }

        sharedPreferences = getSharedPreferences("CEOPreferences", Context.MODE_PRIVATE)

        currentQuestionIndex = savedInstanceState?.getInt("currentQuestionIndex") ?: 0
        loadQuestionPage()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("currentQuestionIndex", currentQuestionIndex)
    }

    override fun onBackPressed() {
        handlePreviousButtonClick()
    }

    private fun loadQuestionPage() {
        if (currentQuestionIndex < questionPages.size) {
            setContentView(questionPages[currentQuestionIndex]) // Change the layout file

            previousButton = findViewById(R.id.previousButton)
            nextButton = findViewById(R.id.nextButton)

            previousButton.setOnClickListener {
                handlePreviousButtonClick()
            }

            nextButton.setOnClickListener {
                handleNextButtonClick()
            }

            if (currentQuestionIndex == 0) {
                previousButton.visibility = View.GONE
            } else {
                previousButton.visibility = View.VISIBLE
            }

            if (currentQuestionIndex == questionPages.size - 1) {
                nextButton.text = "Finish"
            } else {
                nextButton.text = "Next"
            }

            loadEnteredValues() // Load entered values into the fields
        }
    }

    private fun handlePreviousButtonClick() {
        if (currentQuestionIndex > 0) {
            currentQuestionIndex--
            loadQuestionPage()
        } else {
            val intent = Intent(this, RoleSelectionActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
        }
    }

    private fun handleNextButtonClick() {
        // Save entered values to SharedPreferences
        saveEnteredValues()

        if (currentQuestionIndex < questionPages.size - 1) {
            // If there are more question pages remaining, increment the index and load the next page
            currentQuestionIndex++
            loadQuestionPage()
        } else {
            // Retrieve the user ID from FirebaseAuth
            val userID = auth.currentUser?.uid

            if (userID != null) {
                // Create a HashMap with the field names and their corresponding values
                val data = hashMapOf<String, Any?>()

                // Retrieve the values from SharedPreferences using the correct keys
                for (i in 0 until questionPages.size) {
                    val ceoNameValue = sharedPreferences.getString("ceoName$i", null)
                    val ceoNikValue = sharedPreferences.getString("ceoNik$i", null)
                    val ceoEmailValue = sharedPreferences.getString("ceoEmail$i", null)
                    val startupNameValue = sharedPreferences.getString("startupName$i", null)
                    val startupWebsiteValue = sharedPreferences.getString("startupWebsite$i", null)
                    val targetValue = sharedPreferences.getString("target$i", null)
                    val developmentalLevelValue = sharedPreferences.getString("developmentalLevel$i", null)
                    val industryValue = sharedPreferences.getString("industry$i", null)

                    // Add non-null values to the data HashMap
                    ceoNameValue?.let { data["ceoName"] = it }
                    ceoNikValue?.let { data["ceoNik"] = it }
                    ceoEmailValue?.let { data["ceoEmail"] = it }
                    startupNameValue?.let { data["startupName"] = it }
                    startupWebsiteValue?.let { data["startupWebsite"] = it }
                    targetValue?.let { data["target"] = it }
                    developmentalLevelValue?.let { data["developmentalLevel"] = it }
                    industryValue?.let { data["industry"] = it }

                    // Print the input data in the logcat
                    Log.d("InputData", "CEO Name: $ceoNameValue")
                    Log.d("InputData", "CEO NIK: $ceoNikValue")
                    Log.d("InputData", "CEO Email: $ceoEmailValue")
                    Log.d("InputData", "Startup Name: $startupNameValue")
                    Log.d("InputData", "Startup Website: $startupWebsiteValue")
                    Log.d("InputData", "Target: $targetValue")
                    Log.d("InputData", "Developmental Level: $developmentalLevelValue")
                    Log.d("InputData", "Industry: $industryValue")
                }

                // Save the entered values to Firestore with the user ID as the document ID
                db.collection("ceoData")
                    .document(userID)
                    .set(data)
                    .addOnSuccessListener {
                        Toast.makeText(
                            this,
                            "Data saved successfully! Document ID: $userID",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(
                            this,
                            "Error saving data. Please try again. Error: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                val intent = Intent(this, RoleSelectionActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                finish()
            } else {
                // User is not logged in or user ID is null
                // Handle the case accordingly
            }
        }
    }

    private fun saveEnteredValues() {
        // Get the references to the views
        val ceoNameEditText = findViewById<EditText>(R.id.ceoNameEditText)
        val ceoNikEditText = findViewById<EditText>(R.id.ceoNikEditText)
        val ceoEmailEditText = findViewById<EditText>(R.id.ceoEmailEditText)
        val startupNameEditText = findViewById<EditText>(R.id.startupNameEditText)
        val startupWebsiteEditText = findViewById<EditText>(R.id.startupWebsiteEditText)
        val targetSpinner = findViewById<Spinner>(R.id.spinnerTarget)
        val developmentalSpinner = findViewById<Spinner>(R.id.spinnerDevelopmental)
        val industrySpinner = findViewById<Spinner>(R.id.spinnerIndustry)

        // Get the entered values from the views
        val ceoNameValue = ceoNameEditText?.text?.toString()
        val ceoNikValue = ceoNikEditText?.text?.toString()
        val ceoEmailValue = ceoEmailEditText?.text?.toString()
        val startupNameValue = startupNameEditText?.text?.toString()
        val startupWebsiteValue = startupWebsiteEditText?.text?.toString()
        val targetValue = targetSpinner?.selectedItem?.toString()
        val developmentalLevelValue = developmentalSpinner?.selectedItem?.toString()
        val industryValue = industrySpinner?.selectedItem?.toString()

        // Save the entered values to SharedPreferences with the correct keys using dynamic index
        val editor = sharedPreferences.edit()
        editor.putString("ceoName$currentQuestionIndex", ceoNameValue)
        editor.putString("ceoNik$currentQuestionIndex", ceoNikValue)
        editor.putString("ceoEmail$currentQuestionIndex", ceoEmailValue)
        editor.putString("startupName$currentQuestionIndex", startupNameValue)
        editor.putString("startupWebsite$currentQuestionIndex", startupWebsiteValue)
        editor.putString("target$currentQuestionIndex", targetValue)
        editor.putString("developmentalLevel$currentQuestionIndex", developmentalLevelValue)
        editor.putString("industry$currentQuestionIndex", industryValue)
        editor.apply()
    }

    private fun loadEnteredValues() {
        // Get the references to the views
        val ceoNameEditText = findViewById<EditText>(R.id.ceoNameEditText)
        val ceoNikEditText = findViewById<EditText>(R.id.ceoNikEditText)
        val ceoEmailEditText = findViewById<EditText>(R.id.ceoEmailEditText)
        val startupNameEditText = findViewById<EditText>(R.id.startupNameEditText)
        val startupWebsiteEditText = findViewById<EditText>(R.id.startupWebsiteEditText)
        val targetSpinner = findViewById<Spinner>(R.id.spinnerTarget)
        val developmentalSpinner = findViewById<Spinner>(R.id.spinnerDevelopmental)
        val industrySpinner = findViewById<Spinner>(R.id.spinnerIndustry)

        // Load the entered values from SharedPreferences
        val ceoNameValue = sharedPreferences.getString("ceoName$currentQuestionIndex", "")
        val ceoNikValue = sharedPreferences.getString("ceoNik$currentQuestionIndex", "")
        val ceoEmailValue = sharedPreferences.getString("ceoEmail$currentQuestionIndex", "")
        val startupNameValue = sharedPreferences.getString("startupName$currentQuestionIndex", "")
        val startupWebsiteValue =
            sharedPreferences.getString("startupWebsite$currentQuestionIndex", "")
        val targetValue = sharedPreferences.getString("target$currentQuestionIndex", "")
        val developmentalLevelValue =
            sharedPreferences.getString("developmentalLevel$currentQuestionIndex", "")
        val industryValue = sharedPreferences.getString("industry$currentQuestionIndex", "")

        // Set the entered values to the views
        ceoNameEditText?.setText(ceoNameValue)
        ceoNikEditText?.setText(ceoNikValue)
        ceoEmailEditText?.setText(ceoEmailValue)
        startupNameEditText?.setText(startupNameValue)
        startupWebsiteEditText?.setText(startupWebsiteValue)
        targetSpinner?.setSelection(getSpinnerIndex(targetSpinner, targetValue))
        developmentalSpinner?.setSelection(
            getSpinnerIndex(
                developmentalSpinner,
                developmentalLevelValue
            )
        )
        industrySpinner?.setSelection(getSpinnerIndex(industrySpinner, industryValue))
    }

    private fun getSpinnerIndex(spinner: Spinner, value: String?): Int {
        val adapter = spinner.adapter
        for (i in 0 until adapter.count) {
            if (adapter.getItem(i).toString() == value) {
                return i
            }
        }
        return 0
    }
}