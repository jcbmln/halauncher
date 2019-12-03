package xyz.mcmxciv.halauncher.interfaces

interface IntegrationListener {
    fun onIntegrationComplete()
    fun onIntegrationFailed(message: String)
}