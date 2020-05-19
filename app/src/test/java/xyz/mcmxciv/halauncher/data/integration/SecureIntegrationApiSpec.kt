package xyz.mcmxciv.halauncher.data.integration

import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.RecordedRequest
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import xyz.mcmxciv.halauncher.data.HomeAssistantMockWebServer
import xyz.mcmxciv.halauncher.domain.models.DeviceInfo
import xyz.mcmxciv.halauncher.domain.models.WebhookInfo

object SecureIntegrationApiSpec : Spek({
    val mockWebServer by memoized { HomeAssistantMockWebServer(SecureIntegrationApi::class.java) }

    describe("#${SecureIntegrationApi::registerDevice.name}") {
        context("success") {
            lateinit var request: RecordedRequest
            lateinit var webhookInfo: Any

            beforeEachTest {
                val deviceInfo = DeviceInfo(
                    "appId",
                    "appName",
                    "appVersion",
                    "deviceName",
                    "manufacturer",
                    "model",
                    "osName",
                    "osVersion"
                )
                mockWebServer.enqueue(
                    200,
                    """{
                      "webhook_id": "12345"
                    }"""
                )
                webhookInfo = runBlocking {
                    mockWebServer.secureApi.registerDevice(deviceInfo)
                }
                request = mockWebServer.takeRequest()
            }

            it("should create a POST request") {
                assertThat(request.method).isEqualTo("POST")
                assertThat(request.path).isEqualTo("/api/mobile_app/registrations")
                assertThat(request.headers).contains(Pair("Authorization", "Bearer ABCD"))
                assertThat(request.body.readUtf8()).contains(""""app_id":"appId"""")
            }

            it("should return a WebhookInfo object") {
                assertThat(webhookInfo is WebhookInfo).isTrue()
            }
        }
    }
})
