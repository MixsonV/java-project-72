package hexlet.code;

import hexlet.code.controller.UrlsController;
import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import hexlet.code.util.NamedRoutes;
import io.javalin.http.Context;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AppTest {
    private static final int STATUS_CODE_200 = 200;
    private static final int STATUS_CODE_404 = 404;
    private static final long ID_999L = 999L;
    private MockWebServer mockWebServer;
    private MockedStatic<UrlRepository> mockedUrlRepo;
    private MockedStatic<UrlCheckRepository> mockedUrlCheckRepo;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        mockedUrlRepo = Mockito.mockStatic(UrlRepository.class);
        mockedUrlCheckRepo = Mockito.mockStatic(UrlCheckRepository.class);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
        mockedUrlRepo.close();
        mockedUrlCheckRepo.close();
    }

    @Test
    void testIndexPage() throws SQLException {
        // Подготовка данных
        Url mockUrl = new Url(1L, "https://example.com", null);
        List<Url> urls = List.of(mockUrl);
        when(UrlRepository.getEntities()).thenReturn(urls);
        when(UrlCheckRepository.findLastCheckByUrlId(eq(1L))).thenReturn(Optional.empty());

        // Мокируем Context
        Context ctx = Mockito.mock(Context.class);

        // Вызываем обработчик
        UrlsController.index(ctx);

        // Проверяем, что render был вызван
        verify(ctx, times(1)).render(any(String.class), any());
        // Проверяем вызов репозитория
        mockedUrlRepo.verify(() -> UrlRepository.getEntities(), times(1));
    }

    @Test
    void testCreateValidUrl() throws SQLException {
        String validUrl = "https://example.com";
        when(UrlRepository.existsByName(anyString())).thenReturn(false);

        Context ctx = Mockito.mock(Context.class);
        when(ctx.formParam("url")).thenReturn(validUrl);

        UrlsController.create(ctx);

        verify(ctx, times(1)).redirect(anyString());
        mockedUrlRepo.verify(() -> UrlRepository.existsByName("https://example.com"), times(1));
        mockedUrlRepo.verify(() -> UrlRepository.save(any(Url.class)), times(1));
    }

    @Test
    void testCreateInvalidUrl() throws SQLException {
        String invalidUrl = "not-a-url";
        Context ctx = Mockito.mock(Context.class);
        when(ctx.formParam("url")).thenReturn(invalidUrl);

        UrlsController.create(ctx);

        verify(ctx, times(1)).redirect(eq(NamedRoutes.rootPath()));
        mockedUrlRepo.verify(() -> UrlRepository.save(any(Url.class)), never());
    }

    @Test
    void testCreateDuplicateUrl() throws SQLException {
        String duplicateUrl = "https://existing-site.com";
        when(UrlRepository.existsByName(eq(duplicateUrl))).thenReturn(true);

        Context ctx = Mockito.mock(Context.class);
        when(ctx.formParam("url")).thenReturn(duplicateUrl);

        UrlsController.create(ctx);

        verify(ctx, times(1)).redirect(eq(NamedRoutes.rootPath()));
        mockedUrlRepo.verify(() -> UrlRepository.existsByName(duplicateUrl), times(1));
        mockedUrlRepo.verify(() -> UrlRepository.save(any(Url.class)), never());
    }

    @Test
    void testCreateCheckSuccess() throws Exception {
        Long urlId = 1L;
        String urlName = "https://example.com";
        Url existingUrl = new Url(urlId, urlName, null);

        when(UrlRepository.findById(eq(urlId))).thenReturn(Optional.of(existingUrl));

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(STATUS_CODE_200)
                .setBody("<html><head><title>OK</title></head><body><h1>H1</h1></body></html>"));

        Context ctx = Mockito.mock(Context.class);
        when(ctx.pathParam("id")).thenReturn(urlId.toString());

        UrlsController.createCheck(ctx);

        verify(ctx, times(1)).redirect(anyString());
        mockedUrlCheckRepo.verify(() -> UrlCheckRepository.saveUrlCheck(any(UrlCheck.class)), times(1));
    }

    @Test
    void testCreateCheckUrlNotFound() throws SQLException {
        Long nonExistentUrlId = ID_999L;
        when(UrlRepository.findById(eq(nonExistentUrlId))).thenReturn(Optional.empty());

        Context ctx = Mockito.mock(Context.class);
        when(ctx.pathParam("id")).thenReturn(nonExistentUrlId.toString());

        when(ctx.status(eq(STATUS_CODE_404))).thenReturn(ctx);
        UrlsController.createCheck(ctx);

        verify(ctx, times(1)).status(eq(STATUS_CODE_404));
        verify(ctx, times(1)).result(anyString());
        mockedUrlRepo.verify(() -> UrlRepository.findById(nonExistentUrlId), times(1));
    }
}
