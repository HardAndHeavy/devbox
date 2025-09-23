package app

import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respondBytes
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.Parameters
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.google.zxing.client.j2se.MatrixToImageWriter
import java.io.ByteArrayOutputStream
import java.util.HashMap

private const val DEFAULT_WIDTH = 300
private const val DEFAULT_HEIGHT = 300
private const val DEFAULT_MARGIN = 1
private const val MIN_QR_VERSION = 1
private const val MAX_QR_VERSION = 40
private const val MIN_MASK_PATTERN = 0
private const val MAX_MASK_PATTERN = 7

fun Application.configureRouting() {
    routing {
        configureRootRoute()
        configureQrRoute()
    }
}

private fun Routing.configureRootRoute() {
    get("/") {
        call.respondText("This is the Ktor in DevBox.")
    }
}

private fun Routing.configureQrRoute() {
    get("/qr") {
        val data = call.parameters["data"] ?: ""
        
        val qrParameters = parseQrParameters(call.parameters)
        
        if (qrParameters.width <= 0 || qrParameters.height <= 0) {
            call.respondText("Width and height must be positive integers.", status = HttpStatusCode.BadRequest)
            return@get
        }

        try {
            val qrBytes = generateQrCode(data, qrParameters)
            call.respondBytes(qrBytes, ContentType.Image.PNG)
        } catch (e: WriterException) {
            call.respondText("Error generating QR code: ${e.message}", status = HttpStatusCode.BadRequest)
        } catch (e: IllegalArgumentException) {
            call.respondText("Invalid parameters: ${e.message}", status = HttpStatusCode.BadRequest)
        }
    }
}

private data class QrParameters(
    val width: Int,
    val height: Int,
    val errorCorrection: ErrorCorrectionLevel,
    val charset: String,
    val margin: Int,
    val qrVersion: Int?,
    val maskPattern: Int?
)

private fun parseQrParameters(parameters: Parameters): QrParameters {
    val widthStr = parameters["width"]
    val heightStr = parameters["height"]
    val errorCorrectionStr = parameters["errorCorrection"] ?: "H"
    val charset = parameters["charset"] ?: "UTF-8"
    val marginStr = parameters["margin"] ?: DEFAULT_MARGIN.toString()
    val qrVersionStr = parameters["qrVersion"]
    val maskPatternStr = parameters["maskPattern"]

    val width = widthStr?.toIntOrNull() ?: DEFAULT_WIDTH
    val height = heightStr?.toIntOrNull() ?: DEFAULT_HEIGHT
    val margin = marginStr.toIntOrNull() ?: DEFAULT_MARGIN
    
    val qrVersion = qrVersionStr?.toIntOrNull()?.takeIf { it in MIN_QR_VERSION..MAX_QR_VERSION }
    val maskPattern = maskPatternStr?.toIntOrNull()?.takeIf { it in MIN_MASK_PATTERN..MAX_MASK_PATTERN }

    val errorCorrection = parseErrorCorrectionLevel(errorCorrectionStr)

    return QrParameters(width, height, errorCorrection, charset, margin, qrVersion, maskPattern)
}

private fun parseErrorCorrectionLevel(errorCorrectionStr: String): ErrorCorrectionLevel {
    return when (errorCorrectionStr.uppercase()) {
        "L" -> ErrorCorrectionLevel.L
        "M" -> ErrorCorrectionLevel.M
        "Q" -> ErrorCorrectionLevel.Q
        "H" -> ErrorCorrectionLevel.H
        else -> ErrorCorrectionLevel.H
    }
}

private fun generateQrCode(data: String, parameters: QrParameters): ByteArray {
    val hints = HashMap<EncodeHintType, Any>()
    hints[EncodeHintType.ERROR_CORRECTION] = parameters.errorCorrection
    hints[EncodeHintType.CHARACTER_SET] = parameters.charset
    hints[EncodeHintType.MARGIN] = parameters.margin
    parameters.qrVersion?.let { 
        hints[EncodeHintType.QR_VERSION] = it 
    }
    parameters.maskPattern?.let { 
        hints[EncodeHintType.QR_MASK_PATTERN] = it 
    }

    val qrWriter = QRCodeWriter()
    val bitMatrix = qrWriter.encode(data, BarcodeFormat.QR_CODE, parameters.width, parameters.height, hints)

    ByteArrayOutputStream().use { outputStream ->
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream)
        return outputStream.toByteArray()
    }
}
