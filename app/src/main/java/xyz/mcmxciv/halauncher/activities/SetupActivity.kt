package xyz.mcmxciv.halauncher.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.utilities.UserPreferences

class SetupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup)

        val setupButtonEnter = findViewById<Button>(R.id.setup_button_enter)
        setupButtonEnter.setOnClickListener {
            val setupEditUrl = findViewById<TextView>(R.id.setup_edit_url)
            val url = setupEditUrl.text.toString()
            UserPreferences.url = url

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
