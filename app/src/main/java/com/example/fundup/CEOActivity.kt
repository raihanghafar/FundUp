package com.example.fundup

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class CEOActivity : AppCompatActivity() {

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

        currentQuestionIndex = intent.getIntExtra("currentQuestionIndex", 0)

        loadQuestionPage()
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
                previousButton.visibility = View.VISIBLE
            } else {
                previousButton.visibility = View.VISIBLE
            }

            if (currentQuestionIndex == questionPages.size - 1) {
                nextButton.text = "Finish"
            } else {
                nextButton.text = "Next"
            }
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
        if (currentQuestionIndex < questionPages.size - 1) {
            currentQuestionIndex++
            loadQuestionPage()
        } else {
            // Handle the finish button click event
        }
    }
}