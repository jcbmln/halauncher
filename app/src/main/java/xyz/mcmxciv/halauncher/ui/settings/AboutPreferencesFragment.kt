package xyz.mcmxciv.halauncher.ui.settings

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
import xyz.mcmxciv.halauncher.ui.main.MainActivity
import xyz.mcmxciv.halauncher.ui.LauncherPreferenceFragment
import xyz.mcmxciv.halauncher.ui.createViewModel

class AboutPreferencesFragment : LauncherPreferenceFragment(), InstallStateUpdatedListener {
    private lateinit var viewModel: SettingsViewModel
    private lateinit var appUpdateManager: AppUpdateManager

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        viewModel = createViewModel { component.settingsViewModel() }
        addPreferencesFromResource(R.xml.about_preferences)

        addClickListener(
            findPreference(PRIVACY_POLICY_KEY),
            AboutPreferencesFragmentDirections
                .actionAboutPreferencesFragmentToWebviewPreferenceFragment(
                    BuildConfig.PRIVACY_POLICY_URL
                )
        )

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
                        MainActivity.UPDATE_REQUEST_CODE
                    )
                }
            }

            return@setOnPreferenceClickListener true
        }
    }

    override fun onStateUpdate(state: InstallState) {
        popupSnackbarForCompleteUpdate()
    }

    private fun popupSnackbarForCompleteUpdate() {
        Snackbar.make(requireView(),
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
