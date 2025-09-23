package app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Main Spring Boot application class. */
@SpringBootApplication
public class Application {

  /**
   * Main method to start the Spring Boot application.
   *
   * @param args command line arguments
   */
  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}
