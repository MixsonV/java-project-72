package hexlet.code.dto;

import io.javalin.validation.ValidationError;

import java.util.List;
import java.util.Map;

public final class BuildUrlPage extends BasePage {
    private String name;
    private Map<String, List<ValidationError<Object>>> errors;

    public BuildUrlPage() { }

    public BuildUrlPage(Map<String, List<ValidationError<Object>>> newError) {
        this.setErrors(newError);
    }

    public void setErrors(Map<String, List<ValidationError<Object>>> newError) {
        this.errors = newError;
    }

    public Map<String, List<ValidationError<Object>>> getErrors() {
        return this.errors;
    }

}
