package app;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

/** Tests for Application main class. */
@SpringBootTest
@DisplayName("Application Context Tests")
class ApplicationTest {

  @Autowired private ApplicationContext context;

  @Test
  @DisplayName("Should load application context")
  void contextLoads() {
    assertThat(context).isNotNull();
  }

  @Test
  @DisplayName("Should have AppController bean")
  void shouldHaveControllerBean() {
    assertThat(context.getBean(AppController.class)).isNotNull();
  }

  @Test
  @DisplayName("Should start application")
  void main() {
    // This test ensures the main method doesn't throw exceptions
    Application.main(new String[] {});
    assertThat(context).isNotNull();
  }
}
