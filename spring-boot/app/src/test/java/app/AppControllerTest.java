package app;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

/** Unit tests for AppController. */
@DisplayName("AppController Unit Tests")
class AppControllerTest {

  private AppController controller;

  @BeforeEach
  void setUp() {
    controller = new AppController();
  }

  @Test
  @DisplayName("Should return greeting message")
  void testHello() {
    String response = controller.hello();
    assertThat(response).isEqualTo("This is the Spring Boot in DevBox.");
  }

  @Test
  @DisplayName("Should generate QR code with default parameters")
  void testGenerateQrCodeWithDefaults() {
    ResponseEntity<?> response =
        controller.generateQrCode("Test Data", null, null, "H", "UTF-8", 1, null, null);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.IMAGE_PNG);
    assertThat(response.getBody()).isInstanceOf(byte[].class);

    byte[] qrBytes = (byte[]) response.getBody();
    assertThat(qrBytes).isNotEmpty();
  }

  @Test
  @DisplayName("Should generate QR code with custom dimensions")
  void testGenerateQrCodeWithCustomDimensions() {
    ResponseEntity<?> response =
        controller.generateQrCode("Custom Size", 500, 500, "H", "UTF-8", 1, null, null);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isInstanceOf(byte[].class);
  }

  @ParameterizedTest
  @CsvSource({
    "L, true", "M, true", "Q, true", "H, true", "l, true", "m, true", "q, true", "h, true"
  })
  @DisplayName("Should accept all error correction levels")
  void testErrorCorrectionLevels(String errorLevel, boolean shouldSucceed) {
    ResponseEntity<?> response =
        controller.generateQrCode("Test", 200, 200, errorLevel, "UTF-8", 1, null, null);

    if (shouldSucceed) {
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
  }

  @Test
  @DisplayName("Should handle invalid dimensions")
  void testInvalidDimensions() {
    ResponseEntity<?> response =
        controller.generateQrCode("Test", -100, -100, "H", "UTF-8", 1, null, null);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).isEqualTo("Width and height must be positive integers.");
  }

  @Test
  @DisplayName("Should handle zero dimensions")
  void testZeroDimensions() {
    ResponseEntity<?> response =
        controller.generateQrCode("Test", 0, 0, "H", "UTF-8", 1, null, null);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).isEqualTo("Width and height must be positive integers.");
  }

  @Test
  @DisplayName("Should use default error correction for invalid input")
  void testInvalidErrorCorrection() {
    ResponseEntity<?> response =
        controller.generateQrCode("Test", 200, 200, "INVALID", "UTF-8", 1, null, null);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isInstanceOf(byte[].class);
  }

  @ParameterizedTest
  @ValueSource(ints = {1, 10, 20, 30, 40})
  @DisplayName("Should accept valid QR versions")
  void testValidQrVersions(int version) {
    String testData = version == 1 ? "Test" : "Test Data with enough content to fill QR code";

    ResponseEntity<?> response =
        controller.generateQrCode(testData, 300, 300, "H", "UTF-8", 1, version, null);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @ParameterizedTest
  @ValueSource(ints = {0, 41, 100, -1})
  @DisplayName("Should ignore invalid QR versions")
  void testInvalidQrVersions(int version) {
    ResponseEntity<?> response =
        controller.generateQrCode("Test", 300, 300, "H", "UTF-8", 1, version, null);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @ParameterizedTest
  @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6, 7})
  @DisplayName("Should accept valid mask patterns")
  void testValidMaskPatterns(int maskPattern) {
    ResponseEntity<?> response =
        controller.generateQrCode("Test", 300, 300, "H", "UTF-8", 1, null, maskPattern);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  @DisplayName("Should handle empty data")
  void testEmptyData() {
    ResponseEntity<?> response =
        controller.generateQrCode(" ", 300, 300, "H", "UTF-8", 1, null, null);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isInstanceOf(byte[].class);
  }

  @Test
  @DisplayName("Should handle different charsets")
  void testDifferentCharsets() {
    ResponseEntity<?> response =
        controller.generateQrCode("Тест データ", 300, 300, "H", "UTF-16", 1, null, null);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isInstanceOf(byte[].class);
  }

  @ParameterizedTest
  @ValueSource(ints = {0, 1, 5, 10})
  @DisplayName("Should handle different margins")
  void testDifferentMargins(int margin) {
    ResponseEntity<?> response =
        controller.generateQrCode("Test", 300, 300, "H", "UTF-8", margin, null, null);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  @DisplayName("Should handle negative margin with default")
  void testNegativeMargin() {
    ResponseEntity<?> response =
        controller.generateQrCode("Test", 300, 300, "H", "UTF-8", -5, null, null);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }
}
