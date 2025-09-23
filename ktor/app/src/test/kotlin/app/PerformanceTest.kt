package app

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.shouldBe
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kotlin.system.measureTimeMillis

class PerformanceTest : FunSpec({

    test("QR generation should be fast for small data") {
        testApplication {
            application {
                module()
            }
            
            val time = measureTimeMillis {
                repeat(10) {
                    client.get("/qr?data=PerformanceTest").apply {
                        status shouldBe HttpStatusCode.OK
                    }
                }
            }
            
            (time / 10) shouldBeLessThan 100
        }
    }

    test("QR generation should handle concurrent requests") {
        testApplication {
            application {
                module()
            }
            
            val requests = (1..20).map { index ->
                client.get("/qr?data=ConcurrentTest$index")
            }
            
            requests.forEach { response ->
                response.status shouldBe HttpStatusCode.OK
            }
        }
    }
})
