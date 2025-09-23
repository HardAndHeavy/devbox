package app

import com.google.zxing.BinaryBitmap
import com.google.zxing.client.j2se.BufferedImageLuminanceSource
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.qrcode.QRCodeReader
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsBytes
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import java.io.ByteArrayInputStream
import javax.imageio.ImageIO

class QrCodeTest : FunSpec({

    test("QR endpoint should generate QR code with data") {
        testApplication {
            application {
                module()
            }
            
            val testData = "Hello, QR Code!"
            client.get("/qr?data=$testData").apply {
                status shouldBe HttpStatusCode.OK
                val contentTypeHeader = headers["Content-Type"]
                contentTypeHeader shouldNotBe null
                contentTypeHeader shouldContain "image/png"
                
                val bytes = bodyAsBytes()
                bytes.isNotEmpty() shouldBe true
                
                val decodedText = decodeQrCode(bytes)
                decodedText shouldBe testData
            }
        }
    }

    test("QR endpoint should handle empty data") {
        testApplication {
            application {
                module()
            }
            
            client.get("/qr").apply {
                status shouldBe HttpStatusCode.OK
                val bytes = bodyAsBytes()
                bytes.isNotEmpty() shouldBe true
                
                val decodedText = decodeQrCode(bytes)
                decodedText shouldBe ""
            }
        }
    }

    test("QR endpoint should accept custom dimensions") {
        testApplication {
            application {
                module()
            }
            
            val testData = "Test with dimensions"
            client.get("/qr?data=$testData&width=500&height=500").apply {
                status shouldBe HttpStatusCode.OK
                
                val bytes = bodyAsBytes()
                val image = ImageIO.read(ByteArrayInputStream(bytes))
                image.width shouldBe 500
                image.height shouldBe 500
            }
        }
    }

    test("QR endpoint should handle invalid dimensions") {
        testApplication {
            application {
                module()
            }
            
            client.get("/qr?data=test&width=-100&height=200").apply {
                status shouldBe HttpStatusCode.BadRequest
                bodyAsText() shouldContain "Width and height must be positive"
            }
        }
    }

    test("QR endpoint should accept error correction levels") {
        testApplication {
            application {
                module()
            }
            
            val testData = "Error correction test"
            val errorLevels = listOf("L", "M", "Q", "H")
            
            for (level in errorLevels) {
                client.get("/qr?data=$testData&errorCorrection=$level").apply {
                    status shouldBe HttpStatusCode.OK
                    val decodedText = decodeQrCode(bodyAsBytes())
                    decodedText shouldBe testData
                }
            }
        }
    }

    test("QR endpoint should handle invalid error correction level") {
        testApplication {
            application {
                module()
            }
            
            client.get("/qr?data=test&errorCorrection=INVALID").apply {
                status shouldBe HttpStatusCode.OK
            }
        }
    }

    test("QR endpoint should accept custom margin") {
        testApplication {
            application {
                module()
            }
            
            val testData = "Margin test"
            client.get("/qr?data=$testData&margin=5").apply {
                status shouldBe HttpStatusCode.OK
                val decodedText = decodeQrCode(bodyAsBytes())
                decodedText shouldBe testData
            }
        }
    }

    test("QR endpoint should handle QR version parameter") {
        testApplication {
            application {
                module()
            }
            
            val testData = "Version test"
            client.get("/qr?data=$testData&qrVersion=10").apply {
                status shouldBe HttpStatusCode.OK
                val decodedText = decodeQrCode(bodyAsBytes())
                decodedText shouldBe testData
            }
        }
    }

    test("QR endpoint should ignore invalid QR version") {
        testApplication {
            application {
                module()
            }
            
            client.get("/qr?data=test&qrVersion=50").apply {
                status shouldBe HttpStatusCode.OK
            }
        }
    }

    test("QR endpoint should handle mask pattern parameter") {
        testApplication {
            application {
                module()
            }
            
            val testData = "Mask pattern test"
            client.get("/qr?data=$testData&maskPattern=3").apply {
                status shouldBe HttpStatusCode.OK
                val decodedText = decodeQrCode(bodyAsBytes())
                decodedText shouldBe testData
            }
        }
    }

    test("QR endpoint should handle charset parameter") {
        testApplication {
            application {
                module()
            }
            
            val testData = "Тест кириллицы"
            val encodedData = java.net.URLEncoder.encode(testData, "UTF-8")
            client.get("/qr?data=$encodedData&charset=UTF-8").apply {
                status shouldBe HttpStatusCode.OK
                val decodedText = decodeQrCode(bodyAsBytes())
                decodedText shouldBe testData
            }
        }
    }

    test("QR endpoint should handle special characters in data") {
        testApplication {
            application {
                module()
            }
            
            val specialChars = "!@#$%^&*()_+-=[]{}|;:,.<>?"
            val encodedData = java.net.URLEncoder.encode(specialChars, "UTF-8")
            client.get("/qr?data=$encodedData").apply {
                status shouldBe HttpStatusCode.OK
                val decodedText = decodeQrCode(bodyAsBytes())
                decodedText shouldBe specialChars
            }
        }
    }

    test("QR endpoint should handle long text data") {
        testApplication {
            application {
                module()
            }
            
            val longText = "A".repeat(1000)
            val encodedData = java.net.URLEncoder.encode(longText, "UTF-8")
            client.get("/qr?data=$encodedData").apply {
                status shouldBe HttpStatusCode.OK
                val decodedText = decodeQrCode(bodyAsBytes())
                decodedText shouldBe longText
            }
        }
    }

    test("QR endpoint should handle URL as data") {
        testApplication {
            application {
                module()
            }
            
            val url = "https://example.com/path?param=value&another=test"
            val encodedUrl = java.net.URLEncoder.encode(url, "UTF-8")
            client.get("/qr?data=$encodedUrl").apply {
                status shouldBe HttpStatusCode.OK
                val decodedText = decodeQrCode(bodyAsBytes())
                decodedText shouldBe url
            }
        }
    }

    test("QR endpoint should handle all parameters together") {
        testApplication {
            application {
                module()
            }
            
            val testData = "Full parameters test"
            val params = listOf(
                "data=$testData",
                "width=400",
                "height=400",
                "errorCorrection=M",
                "charset=UTF-8",
                "margin=2",
                "qrVersion=5",
                "maskPattern=2"
            ).joinToString("&")
            
            client.get("/qr?$params").apply {
                status shouldBe HttpStatusCode.OK
                val bytes = bodyAsBytes()
                val image = ImageIO.read(ByteArrayInputStream(bytes))
                image.width shouldBe 400
                image.height shouldBe 400
                
                val decodedText = decodeQrCode(bytes)
                decodedText shouldBe testData
            }
        }
    }
})

private fun decodeQrCode(imageBytes: ByteArray): String {
    val bufferedImage = ImageIO.read(ByteArrayInputStream(imageBytes))
    val source = BufferedImageLuminanceSource(bufferedImage)
    val bitmap = BinaryBitmap(HybridBinarizer(source))
    val reader = QRCodeReader()
    val result = reader.decode(bitmap)
    return result.text
}
