package hexlet.code.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NamedRoutesTest {
    private static final long ID_456L = 456L;
    private static final long ID_101112L = 101112L;

    @Test
    void testRootPath() {
        String path = NamedRoutes.rootPath();
        assertThat(path).isEqualTo("/");
    }

    @Test
    void testUrlsPath() {
        String path = NamedRoutes.urlsPath();
        assertThat(path).isEqualTo("/urls");
    }

    @Test
    void testUrlPathWithIdString() {
        String id = "123";
        String path = NamedRoutes.urlPath(id);
        assertThat(path).isEqualTo("/urls/" + id);
    }

    @Test
    void testUrlPathWithIdLong() {
        String path = NamedRoutes.urlPath(ID_456L);
        assertThat(path).isEqualTo("/urls/" + ID_456L);
    }

    @Test
    void testUrlsCheckPathWithIdString() {
        String id = "789";
        String path = NamedRoutes.urlsCheckPath(id);
        assertThat(path).isEqualTo("/urls/" + id + "/checks");
    }

    @Test
    void testUrlsCheckPathWithIdLong() {
        String path = NamedRoutes.urlsCheckPath(ID_101112L);
        assertThat(path).isEqualTo("/urls/" + ID_101112L + "/checks");
    }
}
