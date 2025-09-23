package app;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** AppController handles HTTP requests for the application. */
@RestController
public class AppController {

  /**
   * Returns a simple greeting message.
   *
   * @return greeting message
   */
  @GetMapping("/")
  public String hello() {
    return "This is the Spring Boot in DevBox.";
  }

  /**
   * Generates a QR code image based on the provided parameters.
   *
   * @param data the data to encode in the QR code
   * @param width the width of the QR code image
   * @param height the height of the QR code image
   * @param errorCorrectionStr the error correction level
   * @param charset the character encoding
   * @param margin the margin around the QR code
   * @param qrVersion the QR code version
   * @param maskPattern the mask pattern to use
   * @return ResponseEntity containing the QR code image or error message
   */
  @GetMapping("/qr")
  public ResponseEntity<?> generateQrCode(
      @RequestParam(value = "data", defaultValue = " ") String data,
      @RequestParam(value = "width", required = false) Integer width,
      @RequestParam(value = "height", required = false) Integer height,
      @RequestParam(value = "errorCorrection", defaultValue = "H") String errorCorrectionStr,
      @RequestParam(value = "charset", defaultValue = "UTF-8") String charset,
      @RequestParam(value = "margin", defaultValue = "1") Integer margin,
      @RequestParam(value = "qrVersion", required = false) Integer qrVersion,
      @RequestParam(value = "maskPattern", required = false) Integer maskPattern) {

    // Проверяем негативные значения до присвоения значений по умолчанию
    if (width != null && width <= 0) {
      return ResponseEntity.badRequest()
          .contentType(MediaType.TEXT_PLAIN)
          .body("Width and height must be positive integers.");
    }

    if (height != null && height <= 0) {
      return ResponseEntity.badRequest()
          .contentType(MediaType.TEXT_PLAIN)
          .body("Width and height must be positive integers.");
    }

    // Теперь присваиваем значения по умолчанию
    int finalWidth = (width != null && width > 50) ? width : 300;
    int finalHeight = (height != null && height > 50) ? height : 300;

    String finalData = (data == null || data.trim().isEmpty()) ? " " : data;

    ErrorCorrectionLevel errorCorrection;
    String upperCaseErrorCorrection = errorCorrectionStr.toUpperCase(Locale.ROOT);
    switch (upperCaseErrorCorrection) {
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

    int finalMargin = (margin != null && margin >= 0) ? margin : 1;
    hints.put(EncodeHintType.MARGIN, finalMargin);

    if (qrVersion != null && qrVersion >= 1 && qrVersion <= 40) {
      hints.put(EncodeHintType.QR_VERSION, qrVersion);
    }

    if (maskPattern != null && maskPattern >= 0 && maskPattern <= 7) {
      hints.put(EncodeHintType.QR_MASK_PATTERN, maskPattern);
    }

    ByteArrayOutputStream outputStream = null;
    try {
      QRCodeWriter qrWriter = new QRCodeWriter();
      var bitMatrix =
          qrWriter.encode(finalData, BarcodeFormat.QR_CODE, finalWidth, finalHeight, hints);

      outputStream = new ByteArrayOutputStream();
      MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
      byte[] qrBytes = outputStream.toByteArray();

      return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(qrBytes);

    } catch (WriterException e) {
      if (e.getMessage() != null && e.getMessage().contains("Data too big")) {
        return ResponseEntity.badRequest()
            .contentType(MediaType.TEXT_PLAIN)
            .body("Data too large for QR code: " + e.getMessage());
      }
      return ResponseEntity.badRequest()
          .contentType(MediaType.TEXT_PLAIN)
          .body("Error generating QR code: " + e.getMessage());
    } catch (IOException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .contentType(MediaType.TEXT_PLAIN)
          .body("IO error: " + e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .contentType(MediaType.TEXT_PLAIN)
          .body("Unexpected error: " + e.getMessage());
    } finally {
      if (outputStream != null) {
        try {
          outputStream.close();
        } catch (IOException e) {
          // Log the error but don't throw it
          System.err.println("Error closing output stream: " + e.getMessage());
        }
      }
    }
  }
}
