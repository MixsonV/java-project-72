package hexlet.code.dto;

import hexlet.code.model.UrlCheck;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.Unirest;

public final class UrlCheckService {

    public UrlCheck performCheck(String url) {
        HttpResponse<String> response = Unirest.get(url).asString();
        int statusCode = response.getStatus();
        Document doc = Jsoup.parse(response.getBody());

        System.out.println(doc);
        System.out.println("Title: " + doc.title());
        System.out.println("H1: " + doc.select("h1").text());
        System.out.println("Description: " + doc.select("meta[name=description]").attr("content"));

        String title = !doc.title().isEmpty() ? doc.title() : "No title";
        String h1 = doc.select("h1").isEmpty() ? "No h1" : doc.select("h1").text();
        String description = doc.select("meta[name=description]").isEmpty()
                ? "No description" : doc.select("meta[name=description]").attr("content");

        UrlCheck urlCheck = new UrlCheck();
        urlCheck.setStatusCode(statusCode);
        urlCheck.setTitle(title);
        urlCheck.setH1(h1);
        urlCheck.setDescription(description);

        return urlCheck;
    }

}
