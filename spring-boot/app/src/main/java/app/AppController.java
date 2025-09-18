package app;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

@RestController
public class AppController {

    @GetMapping("/")
    public String hello() {
        return "This is the Spring Boot in DevBox.";
    }

    @GetMapping("/qr")
    public ResponseEntity<?> generateQRCode(
            @RequestParam(value = "data", defaultValue = "") String data,
            @RequestParam(value = "width", required = false) Integer width,
            @RequestParam(value = "height", required = false) Integer height,
            @RequestParam(value = "errorCorrection", defaultValue = "H") String errorCorrectionStr,
            @RequestParam(value = "charset", defaultValue = "UTF-8") String charset,
            @RequestParam(value = "margin", defaultValue = "1") Integer margin,
            @RequestParam(value = "qrVersion", required = false) Integer qrVersion,
            @RequestParam(value = "maskPattern", required = false) Integer maskPattern) {

        int finalWidth = (width != null && width > 0) ? width : 300;
        int finalHeight = (height != null && height > 0) ? height : 300;
        int finalMargin = (margin != null && margin >= 0) ? margin : 1;

        if (finalWidth <= 0 || finalHeight <= 0) {
            return ResponseEntity.badRequest().body("Width and height must be positive integers.");
        }

        ErrorCorrectionLevel errorCorrection;
        switch (errorCorrectionStr.toUpperCase()) {
            case "L":
                errorCorrection = ErrorCorrectionLevel.L;
                break;
            case "M":
                errorCorrection = ErrorCorrectionLevel.M;
                break;
            case "Q":
                errorCorrection = ErrorCorrectionLevel.Q;
                break;
            case "H":
                errorCorrection = ErrorCorrectionLevel.H;
                break;
            default:
                errorCorrection = ErrorCorrectionLevel.H;
        }

        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, errorCorrection);
        hints.put(EncodeHintType.CHARACTER_SET, charset);
        hints.put(EncodeHintType.MARGIN, finalMargin);

        if (qrVersion != null && qrVersion >= 1 && qrVersion <= 40) {
            hints.put(EncodeHintType.QR_VERSION, qrVersion);
        }

        if (maskPattern != null && maskPattern >= 0 && maskPattern <= 7) {
            hints.put(EncodeHintType.QR_MASK_PATTERN, maskPattern);
        }

        try {
            QRCodeWriter qrWriter = new QRCodeWriter();
            var bitMatrix = qrWriter.encode(data, BarcodeFormat.QR_CODE, finalWidth, finalHeight, hints);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            byte[] qrBytes = outputStream.toByteArray();

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(qrBytes);

        } catch (WriterException e) {
            return ResponseEntity.badRequest()
                    .body("Error generating QR code: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected error: " + e.getMessage());
        }
    }
}
