package xyz.mcmxciv.halauncher.settings.fragments

import android.os.Bundle
import androidx.preference.Preference
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import xyz.mcmxciv.halauncher.BuildConfig
import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.extensions.createViewModel
import xyz.mcmxciv.halauncher.settings.SettingsActivity
import xyz.mcmxciv.halauncher.settings.SettingsViewModel
import xyz.mcmxciv.halauncher.utils.BasePreferenceFragment

@Suppress("unused")
class AboutPreferencesFragment : BasePreferenceFragment(), InstallStateUpdatedListener {
    private lateinit var viewModel: SettingsViewModel
    private lateinit var appUpdateManager: AppUpdateManager

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        viewModel = createViewModel { component.settingsViewModel() }
        setPreferencesFromResource(R.xml.about_preferences, rootKey)

        val checkUpdatesPreference = findPreference<Preference>(CHECK_UPDATES_KEY)
        checkUpdatesPreference?.summary = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
        checkUpdatesPreference?.setOnPreferenceClickListener {
            appUpdateManager = AppUpdateManagerFactory.create(context)
            val appUpdateInfoTask = appUpdateManager.appUpdateInfo
            appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        AppUpdateType.FLEXIBLE,
                        activity,
                        SettingsActivity.UPDATE_REQUEST_CODE
                    )
                }
            }

            true
        }

        val privacyPolicyPreference = findPreference<Preference>(PRIVACY_POLICY_KEY)
        privacyPolicyPreference?.extras?.putString("url", BuildConfig.PRIVACY_POLICY_URL)
    }

    override fun onStateUpdate(state: InstallState) {
        popupSnackbarForCompleteUpdate()
    }

    private fun popupSnackbarForCompleteUpdate() {
        Snackbar.make(view!!,
            "An update has just been downloaded.",
            Snackbar.LENGTH_INDEFINITE
        ).apply {
            setAction("RESTART") { appUpdateManager.completeUpdate() }
            show()
        }
    }

    companion object {
        private const val CHECK_UPDATES_KEY = "check_updates"
        private const val PRIVACY_POLICY_KEY = "privacy_policy"
    }
}
