package xyz.mcmxciv.halauncher.data.integration

import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody
import okhttp3.mockwebserver.RecordedRequest
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import retrofit2.Response
import xyz.mcmxciv.halauncher.data.HomeAssistantMockWebServer
import xyz.mcmxciv.halauncher.data.models.WebhookRequest
import xyz.mcmxciv.halauncher.integration.IntegrationApi

object IntegrationApiSpec : Spek({
    val mockWebServer by memoized { HomeAssistantMockWebServer(IntegrationApi::class.java) }

    describe("#${IntegrationApi::webhookRequest.name}") {
        context("success") {
            lateinit var request: RecordedRequest
            lateinit var response: Response<ResponseBody>

            beforeEachTest {
                mockWebServer.enqueue(200)
                response = runBlocking {
                    mockWebServer.api.webhookRequest(
                        mockWebServer.url("/api/webhook/ABC"),
                        WebhookRequest("mock_webhook_request", MockWebhookRequestData())
                    )
                }
                request = mockWebServer.takeRequest()
            }

            it("should create a POST request") {
                assertThat(request.method).isEqualTo("POST")
                assertThat(request.path).isEqualTo("/api/webhook/ABC")
            }

            it("should return a successful response") {
                assertThat(response.isSuccessful).isTrue()
            }
        }
    }

    describe("#${IntegrationApi::updateSensors.name}") {
        context("success") {
            lateinit var request: RecordedRequest
            lateinit var response: Response<Map<String, Map<String, Any>>>

            beforeEachTest {
                mockWebServer.enqueue(
                    200,
                    """{
                        "sensor1": {
                            "success": true
                        }
                    }"""
                )
                response = runBlocking {
                    mockWebServer.api.updateSensors(
                        mockWebServer.url("/api/webhook/ABC"),
                        WebhookRequest("update_sensors", MockWebhookRequestData())
                    )
                }
                request = mockWebServer.takeRequest()
            }

            it("should create a POST request") {
                assertThat(request.method).isEqualTo("POST")
                assertThat(request.path).isEqualTo("/api/webhook/ABC")
            }

            it("should return a successful response") {
                assertThat(response.isSuccessful).isTrue()
                assertThat(response.body()).isNotNull()
            }
        }
    }
})
