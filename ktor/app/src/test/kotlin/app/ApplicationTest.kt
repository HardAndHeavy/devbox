package app

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication

class ApplicationTest : FunSpec({

    test("Root route should return welcome message") {
        testApplication {
            application {
                module()
            }
            
            client.get("/").apply {
                status shouldBe HttpStatusCode.OK
                bodyAsText() shouldContain "This is the Ktor in DevBox."
            }
        }
    }

    test("Health check endpoint") {
        testApplication {
            application {
                module()
            }
            
            val response = client.get("/")
            response.status shouldBe HttpStatusCode.OK
        }
    }
})
