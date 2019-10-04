package xyz.mcmxciv.halauncher.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.utilities.UserSettings

class SetupActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup)

        val setupButtonEnter = findViewById<Button>(R.id.setup_button_enter)
        setupButtonEnter.setOnClickListener {
            val setupEditUrl = findViewById<TextView>(R.id.setup_edit_url)
            val url = setupEditUrl.text.toString()
            UserSettings.url = url

            returnToMainActivity(url)
        }
    }

    private fun returnToMainActivity(url: String) {
        val intent = Intent()
        intent.putExtra("url", url)
        setResult(RESULT_OK, intent)
        finish()
    }
}
