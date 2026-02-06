package hexlet.code.dto;

import hexlet.code.model.UrlCheck;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class UrlCheckServiceTest {
    private static final int STATUS_CODE_OK = 200;
    private static final int STATUS_CODE_NOT_FOUND = 404;
    private static MockWebServer mockServer;
    private static UrlCheckService service;

    @BeforeAll
    static void beforeAll() throws IOException {
        mockServer = new MockWebServer();
        service = new UrlCheckService();
        mockServer.start();
    }

    @AfterAll
    static void afterAll() throws IOException {
        mockServer.shutdown();
    }

    @Test
    void testPerformCheckSuccess() {
        String html = "<!DOCTYPE html>"
                + "<html lang='en'>"
                + "<head>"
                + "<meta name='keywords' content='test'>"
                + "<meta name='description' content='description'>"
                + "<title>Test Page</title>"
                + "</head>"
                + "<body>"
                + "<h1>Test Header</h1>"
                + "</body>"
                + "</html>";

        mockServer.enqueue(new MockResponse().setBody(html).setResponseCode(STATUS_CODE_OK));

        String url = mockServer.url("/").toString();
        UrlCheck check = service.performCheck(url);

        assertThat(check.getStatusCode()).isEqualTo(STATUS_CODE_OK);
        assertThat(check.getTitle()).isEqualTo("Test Page");
        assertThat(check.getH1()).isEqualTo("Test Header");
        assertThat(check.getDescription()).isEqualTo("description");
    }

    @Test
    void testPerformCheck404() {
        mockServer.enqueue(new MockResponse().setResponseCode(STATUS_CODE_NOT_FOUND));

        String url = mockServer.url("/").toString();
        UrlCheck check = service.performCheck(url);

        assertThat(check.getStatusCode()).isEqualTo(STATUS_CODE_NOT_FOUND);
        assertThat(check.getTitle()).isEqualTo("No title");
        assertThat(check.getH1()).isEqualTo("No h1");
        assertThat(check.getDescription()).isEqualTo("No description");
    }

    @Test
    void testPerformCheckWithMissingMetaTags() {
        String html = "<!DOCTYPE html>"
                + "<html>"
                + "<head><title>Title Only</title></head>"
                + "<body><h1>H1 Only</h1></body>"
                + "</html>";

        mockServer.enqueue(new MockResponse().setBody(html).setResponseCode(STATUS_CODE_OK));

        String url = mockServer.url("/").toString();
        UrlCheck check = service.performCheck(url);

        assertThat(check.getStatusCode()).isEqualTo(STATUS_CODE_OK);
        assertThat(check.getTitle()).isEqualTo("Title Only");
        assertThat(check.getH1()).isEqualTo("H1 Only");
        assertThat(check.getDescription()).isEqualTo("No description");
    }

    @Test
    void testPerformCheckWithMissingH1() {
        String html = "<!DOCTYPE html>"
                + "<html>"
                + "<head>"
                + "<meta name='description' content='desc'>"
                + "<title>Title</title>"
                + "</head>"
                + "<body></body>"
                + "</html>";

        mockServer.enqueue(new MockResponse().setBody(html).setResponseCode(STATUS_CODE_OK));

        String url = mockServer.url("/").toString();
        UrlCheck check = service.performCheck(url);

        assertThat(check.getStatusCode()).isEqualTo(STATUS_CODE_OK);
        assertThat(check.getTitle()).isEqualTo("Title");
        assertThat(check.getH1()).isEqualTo("No h1");
        assertThat(check.getDescription()).isEqualTo("desc");
    }

    @Test
    void testPerformCheckEmptyPage() {
        mockServer.enqueue(new MockResponse().setBody("").setResponseCode(STATUS_CODE_OK));

        String url = mockServer.url("/").toString();
        UrlCheck check = service.performCheck(url);

        assertThat(check.getStatusCode()).isEqualTo(STATUS_CODE_OK);
        assertThat(check.getTitle()).isEqualTo("No title");
        assertThat(check.getH1()).isEqualTo("No h1");
        assertThat(check.getDescription()).isEqualTo("No description");
    }

    @Test
    void testPerformCheckWithMultipleH1Tags() {
        String html = "<!DOCTYPE html>"
                + "<html>"
                + "<head><title>Title</title></head>"
                + "<body>"
                + "<h1>First H1</h1>"
                + "<h1>Second H1</h1>"
                + "</body>"
                + "</html>";

        mockServer.enqueue(new MockResponse().setBody(html).setResponseCode(STATUS_CODE_OK));

        String url = mockServer.url("/").toString();
        UrlCheck check = service.performCheck(url);

        assertThat(check.getStatusCode()).isEqualTo(STATUS_CODE_OK);
        assertThat(check.getTitle()).isEqualTo("Title");
        assertThat(check.getH1()).isEqualTo("First H1 Second H1");
    }
}
