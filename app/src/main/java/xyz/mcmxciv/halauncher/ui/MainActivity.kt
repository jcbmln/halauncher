package xyz.mcmxciv.halauncher.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.google.android.play.core.install.model.ActivityResult
import xyz.mcmxciv.halauncher.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == UPDATE_REQUEST_CODE) {
            when (resultCode) {
                ActivityResult.RESULT_IN_APP_UPDATE_FAILED -> {
                    Toast
                        .makeText(this, "Failed to update app.", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.main_navigation_host_fragment)

        if (navController.navigateUp()) {
            return true
        }

        return super.onSupportNavigateUp()
    }

    companion object {
        const val UPDATE_REQUEST_CODE = 1
    }
}
