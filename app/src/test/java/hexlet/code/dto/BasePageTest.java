package hexlet.code.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BasePageTest {

    @Test
    void testBasePageConstructorWithFlash() {
        String flashMessage = "Test Flash";
        BasePage page = new BasePage(flashMessage);

        assertThat(page.getFlash()).isEqualTo(flashMessage);
    }

    @Test
    void testBasePageSettersAndGetters() {
        BasePage page = new BasePage();
        String flashMessage = "Another Flash";

        page.setFlash(flashMessage);

        assertThat(page.getFlash()).isEqualTo(flashMessage);
    }
}
