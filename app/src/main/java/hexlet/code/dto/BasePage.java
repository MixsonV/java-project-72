package hexlet.code.dto;

public class BasePage {
    private String flash;

    public BasePage() { }

    public BasePage(String newFlash) {
        this.setFlash(newFlash);
    }

    /**
     * @param newFlash
     */
    public void setFlash(String newFlash) {
        this.flash = newFlash;
    }

    /**

     * @return String
     */
    public String getFlash() {
        return this.flash;
    }
}
