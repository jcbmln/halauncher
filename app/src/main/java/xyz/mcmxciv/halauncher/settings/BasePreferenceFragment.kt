package xyz.mcmxciv.halauncher.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import xyz.mcmxciv.halauncher.databinding.PreferenceThemeOverlayBinding

abstract class BasePreferenceFragment : PreferenceFragmentCompat() {
    protected lateinit var binding: PreferenceThemeOverlayBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = PreferenceThemeOverlayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().also { a ->
            if (a is AppCompatActivity) {
                a.setSupportActionBar(binding.toolbar)
                a.supportActionBar?.setDisplayHomeAsUpEnabled(true)
            }
        }
    }
}
