package xyz.mcmxciv.halauncher.activities.setup

import androidx.fragment.app.Fragment
import xyz.mcmxciv.halauncher.interfaces.ServiceSelectedListener

abstract class SetupFragment : Fragment() {
    lateinit var serviceSelectedListener: ServiceSelectedListener
}