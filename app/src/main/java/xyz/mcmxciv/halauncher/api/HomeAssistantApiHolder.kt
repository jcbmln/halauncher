package xyz.mcmxciv.halauncher.api

class HomeAssistantApiHolder {
    private var _homeAssistantApi: HomeAssistantApi? = null

    var homeAssistantApi: HomeAssistantApi?
        get() = _homeAssistantApi
        set(value) { _homeAssistantApi = value }

//    fun homeAssistantSecureApi(): HomeAssistantSecureApi? {
//        return homeAssistantSecureApi
//    }

//    fun setHomeAssistantSecureApi(api: HomeAssistantSecureApi?) {
//        homeAssistantSecureApi = api
//    }
}