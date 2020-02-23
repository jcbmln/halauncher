package xyz.mcmxciv.halauncher.data.api

class HomeAssistantApiHolder {
    private var _homeAssistantApi: HomeAssistantApi? = null

    var homeAssistantApi: HomeAssistantApi?
        get() = _homeAssistantApi
        set(value) { _homeAssistantApi = value }
}