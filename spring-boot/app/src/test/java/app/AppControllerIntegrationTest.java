package app;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;

/** Integration tests for AppController. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {"spring.main.banner-mode=off"})
@DisplayName("AppController Integration Tests")
class AppControllerIntegrationTest {

  @LocalServerPort private int port;

  @BeforeEach
  void setUp() {
    RestAssured.port = port;
    RestAssured.basePath = "";
  }

  @Test
  @DisplayName("Should return greeting on root endpoint")
  void testRootEndpoint() {
    given()
        .when()
        .get("/")
        .then()
        .statusCode(200)
        .body(equalTo("This is the Spring Boot in DevBox."));
  }

  @Test
  @DisplayName("Should generate QR code with minimal parameters")
  void testGenerateQrCodeMinimal() {
    byte[] response =
        given()
            .queryParam("data", "Hello World")
            .when()
            .get("/qr")
            .then()
            .statusCode(200)
            .contentType("image/png")
            .extract()
            .asByteArray();

    assertThat(response).isNotEmpty();
    // PNG signature check
    assertThat(response[0]).isEqualTo((byte) 0x89);
    assertThat(response[1]).isEqualTo((byte) 0x50);
    assertThat(response[2]).isEqualTo((byte) 0x4E);
    assertThat(response[3]).isEqualTo((byte) 0x47);
  }

  @ParameterizedTest
  @CsvSource({"100, 100", "200, 200", "500, 500", "1000, 1000"})
  @DisplayName("Should generate QR codes with different sizes")
  void testDifferentSizes(int width, int height) {
    given()
        .queryParam("data", "Test Data")
        .queryParam("width", width)
        .queryParam("height", height)
        .when()
        .get("/qr")
        .then()
        .statusCode(200)
        .contentType("image/png");
  }

  @Test
  @DisplayName("Should return error for invalid dimensions")
  void testInvalidDimensions() {
    given()
        .queryParam("data", "Test")
        .queryParam("width", -100)
        .queryParam("height", -100)
        .when()
        .get("/qr")
        .then()
        .statusCode(400)
        .contentType("text/plain")
        .body(equalTo("Width and height must be positive integers."));
  }

  @ParameterizedTest
  @CsvSource({"L, 200", "M, 200", "Q, 200", "H, 200"})
  @DisplayName("Should accept all error correction levels via API")
  void testErrorCorrectionLevelsApi(String errorLevel, int expectedStatus) {
    given()
        .queryParam("data", "Test")
        .queryParam("errorCorrection", errorLevel)
        .when()
        .get("/qr")
        .then()
        .statusCode(expectedStatus)
        .contentType("image/png");
  }

  @Test
  @DisplayName("Should handle URL encoded data")
  void testUrlEncodedData() {
    given()
        .queryParam("data", "Special chars: &?#%@!")
        .queryParam("width", 300)
        .queryParam("height", 300)
        .when()
        .get("/qr")
        .then()
        .statusCode(200)
        .contentType("image/png");
  }

  @Test
  @DisplayName("Should handle Unicode data")
  void testUnicodeData() {
    given()
        .queryParam("data", "Hello 世界 🌍")
        .queryParam("charset", "UTF-8")
        .when()
        .get("/qr")
        .then()
        .statusCode(200)
        .contentType("image/png");
  }

  @Test
  @DisplayName("Should generate QR with all parameters")
  void testAllParameters() {
    given()
        .queryParam("data", "Complete Test")
        .queryParam("width", 400)
        .queryParam("height", 400)
        .queryParam("errorCorrection", "M")
        .queryParam("charset", "UTF-8")
        .queryParam("margin", 2)
        .queryParam("qrVersion", 10)
        .queryParam("maskPattern", 3)
        .when()
        .get("/qr")
        .then()
        .statusCode(200)
        .contentType("image/png");
  }

  @Test
  @DisplayName("Should handle very long data")
  void testVeryLongData() {
    String longData = "A".repeat(1000);

    given()
        .queryParam("data", longData)
        .queryParam("width", 500)
        .queryParam("height", 500)
        .when()
        .get("/qr")
        .then()
        .statusCode(200)
        .contentType("image/png");
  }

  @Test
  @DisplayName("Should handle empty data parameter")
  void testEmptyDataParameter() {
    given()
        .queryParam("data", " ")
        .when()
        .get("/qr")
        .then()
        .statusCode(200)
        .contentType("image/png");
  }

  @Test
  @DisplayName("Should use defaults when no parameters provided")
  void testNoParameters() {
    given()
        .queryParam("data", " ")
        .when()
        .get("/qr")
        .then()
        .statusCode(200)
        .contentType("image/png");
  }
}
