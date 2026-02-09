package hexlet.code.dto;

import hexlet.code.model.UrlCheck;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class UrlCheckServiceTest {
    private static final int STATUS_CODE_200 = 200;
    private MockWebServer mockWebServer;
    private UrlCheckService urlCheckService;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        urlCheckService = new UrlCheckService();
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void testPerformCheckSuccess() {
        String expectedTitle = "Test Page Title";
        String expectedH1 = "Test H1 Content";
        String expectedDescription = "This is a test description.";
        int expectedStatus = STATUS_CODE_200;

        String responseBody = "<!DOCTYPE html>"
                + "<html>"
                + "<head>"
                + "<title>" + expectedTitle + "</title>"
                + "<meta name=\"description\" content=\"" + expectedDescription + "\">"
                + "</head>"
                + "<body>"
                + "<h1>" + expectedH1 + "</h1>"
                + "<p>Some content here.</p>"
                + "</body>"
                + "</html>";

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(expectedStatus)
                .setBody(responseBody)
                .addHeader("Content-Type", "text/html"));

        String urlToCheck = mockWebServer.url("/").toString();
        UrlCheck result = urlCheckService.performCheck(urlToCheck);

        assertThat(result.getStatusCode()).isEqualTo(expectedStatus);
        assertThat(result.getTitle()).isEqualTo(expectedTitle);
        assertThat(result.getH1()).isEqualTo(expectedH1);
        assertThat(result.getDescription()).isEqualTo(expectedDescription);
    }

    @Test
    void testPerformCheckMissingElements() {
        String responseBody = "<!DOCTYPE html>"
                + "<html>"
                + "<head>"
                + "<!-- No title -->"
                + "<meta name=\"keywords\" content=\"test, keywords\">"
                + "</head>"
                + "<body>"
                + "<!-- No h1 -->"
                + "<p>Content without h1.</p>"
                + "</body>"
                + "</html>";

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(STATUS_CODE_200)
                .setBody(responseBody)
                .addHeader("Content-Type", "text/html"));

        String urlToCheck = mockWebServer.url("/").toString();
        UrlCheck result = urlCheckService.performCheck(urlToCheck);

        assertThat(result.getTitle()).isEqualTo("No title");
        assertThat(result.getH1()).isEqualTo("No h1");
        assertThat(result.getDescription()).isEqualTo("No description");
    }
}
