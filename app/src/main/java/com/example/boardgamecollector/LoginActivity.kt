package com.example.boardgamecollector

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText

class LoginActivity : AppCompatActivity() {
    private val dbHandler = MyDBHandler(this, null, null, 1)
    lateinit var editText: EditText
    private lateinit var btn: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        editText = findViewById(R.id.login)
        btn = findViewById(R.id.button)
        editText.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).apply {
                    hideSoftInputFromWindow(currentFocus?.windowToken, 0)
                }
                clickStart()
                editText.clearFocus()
                editText.isCursorVisible = false
                return@OnKeyListener true
            }
            false
        })
        btn.setOnClickListener{clickStart()}
        val username = dbHandler.getUserData()
        if (username != null){
            toMainActivity()
        }
    }

    fun clickStart(){
        val inputUsername = editText.text.toString().filterNot { it.isWhitespace() }
        val existingUsername = dbHandler.getUserData()
        if (existingUsername!=inputUsername)
        {
            dbHandler.saveUser(inputUsername)
        }
        toMainActivity()
    }

    private fun toMainActivity(){
        val intent = Intent(this, UserSettingsActivity::class.java)
        startActivity(intent)
        finish()
    }
}