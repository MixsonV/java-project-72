package hexlet.code.service;

import hexlet.code.model.UrlCheck;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

@Slf4j
public final class UrlCheckService {
    private static final String DESCRIPTION = "meta[name=description]";

    static {
        Unirest.config()
                .connectTimeout(3000)
                .socketTimeout(3000)
                .verifySsl(false);
    }

    public UrlCheck performCheck(String url) {
        HttpResponse<String> response = Unirest.get(url).asString();
        int statusCode = response.getStatus();
        Document doc = Jsoup.parse(response.getBody());

        @SuppressWarnings("java:S106")
        String title = !doc.title().isEmpty() ? doc.title() : "No title";
        @SuppressWarnings("java:S106")
        String h1 = doc.select("h1").isEmpty() ? "No h1" : doc.select("h1").text();
        @SuppressWarnings("java:S106")
        String description = doc.select(DESCRIPTION).isEmpty()
                ? "No description" : doc.select(DESCRIPTION).attr("content");

        UrlCheck urlCheck = new UrlCheck();
        urlCheck.setStatusCode(statusCode);
        urlCheck.setTitle(title);
        urlCheck.setH1(h1);
        urlCheck.setDescription(description);

        return urlCheck;
    }

}
