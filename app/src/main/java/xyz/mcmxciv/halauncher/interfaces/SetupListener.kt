package xyz.mcmxciv.halauncher.interfaces

interface SetupListener {
    fun onDiscoveryModeSelected()
    fun onManualModeSelected()
    fun onServiceSelected(serviceUrl: String)
    fun onAuthenticated()
    fun onDeviceRegistered()
}