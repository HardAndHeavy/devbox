package app

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.*
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.google.zxing.client.j2se.MatrixToImageWriter
import java.io.ByteArrayOutputStream

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("This is the Ktor in DevBox.")
        }

        get("/qr") {
            val data = call.parameters["data"] ?: ""

            val widthStr = call.parameters["width"]
            val heightStr = call.parameters["height"]
            val errorCorrectionStr = call.parameters["errorCorrection"] ?: "H"
            val charset = call.parameters["charset"] ?: "UTF-8"
            val marginStr = call.parameters["margin"] ?: "1"
            val qrVersionStr = call.parameters["qrVersion"]
            val maskPatternStr = call.parameters["maskPattern"]

            val width = widthStr?.toIntOrNull() ?: 300
            val height = heightStr?.toIntOrNull() ?: 300
            val margin = marginStr.toIntOrNull() ?: 1
            val qrVersion = qrVersionStr?.toIntOrNull()
            val maskPattern = maskPatternStr?.toIntOrNull()

            if (width <= 0 || height <= 0) {
                call.respond(HttpStatusCode.BadRequest, "Width and height must be positive integers.")
                return@get
            }

            val errorCorrection = when (errorCorrectionStr.uppercase()) {
                "L" -> ErrorCorrectionLevel.L
                "M" -> ErrorCorrectionLevel.M
                "Q" -> ErrorCorrectionLevel.Q
                "H" -> ErrorCorrectionLevel.H
                else -> {
                    ErrorCorrectionLevel.H
                }
            }

            val hints = mutableMapOf<EncodeHintType, Any>()
            hints[EncodeHintType.ERROR_CORRECTION] = errorCorrection
            hints[EncodeHintType.CHARACTER_SET] = charset
            hints[EncodeHintType.MARGIN] = margin
            if (qrVersion != null && qrVersion in 1..40) {
                hints[EncodeHintType.QR_VERSION] = qrVersion
            }
            if (maskPattern != null && maskPattern in 0..7) {
                hints[EncodeHintType.QR_MASK_PATTERN] = maskPattern
            }

            try {
                val qrWriter = QRCodeWriter()
                val bitMatrix = qrWriter.encode(data, BarcodeFormat.QR_CODE, width, height, hints)

                val outputStream = ByteArrayOutputStream()
                MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream)
                val qrBytes = outputStream.toByteArray()

                call.respondBytes(qrBytes, ContentType.Image.PNG)
            } catch (e: WriterException) {
                call.respond(HttpStatusCode.BadRequest, "Error generating QR code: ${e.message}")
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Unexpected error: ${e.message}")
            }
        }
    }
}
