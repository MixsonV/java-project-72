package hexlet.code.controller;

import hexlet.code.dto.BuildUrlPage;
import hexlet.code.dto.UrlPage;
import hexlet.code.dto.UrlsPage;
import hexlet.code.model.Url;
import hexlet.code.repository.UrlRepository;
import hexlet.code.util.NamedRoutes;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import lombok.extern.slf4j.Slf4j;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.SQLException;

import static io.javalin.rendering.template.TemplateUtil.model;

@Slf4j
public class UrlsController {
    private static final Integer STATUS_CODE_500 = 500;

    public static void show(Context ctx) throws SQLException {
        var id = ctx.pathParamAsClass("id", Long.class).get();
        var url = UrlRepository.find(id)
                .orElseThrow(() -> new NotFoundResponse("Entity with id = " + id + " not found"));
        var page = new UrlPage(url);
        ctx.render("urls/show.jte", model("page", page));
    }

    public static void index(Context ctx) {
        try {
            var urls = UrlRepository.getEntities();
            var page = new UrlsPage(urls);
            page.setFlash(ctx.consumeSessionAttribute("flash"));
            ctx.render("urls/index.jte", model("page", page));
        } catch (SQLException e) {
            ctx.status(STATUS_CODE_500).result("Server error occurred");
            e.printStackTrace();
        }
    }

    public static void build(Context ctx) {
        var page = new BuildUrlPage();
        page.setFlash(ctx.consumeSessionAttribute("flash"));
        ctx.render("urls/build.jte", model("page", page));
    }

    public static void create(Context ctx) throws SQLException {
        String name = ctx.formParam("url");

        if (!name.startsWith("http://") && !name.startsWith("https://")) {
            ctx.sessionAttribute("flash", "Некорректный URL");
            ctx.redirect(NamedRoutes.rootPath());
            return;
        }

        try {
            URI uri = new URI(name);
            URL url = uri.toURL();

            String baseUrl = url.getProtocol() + "://" + url.getHost();
            if (url.getPort() != -1) {
                baseUrl += ":" + url.getPort();
            }

            System.out.println("Base URL: " + baseUrl);

            if (UrlRepository.existsByName(baseUrl)) {
                ctx.sessionAttribute("flash", "Страница уже существует");
            } else {
                Url urlEntity = new Url(baseUrl);
                UrlRepository.save(urlEntity);
                ctx.sessionAttribute("flash", "Страница успешно добавлена");
            }
        } catch (URISyntaxException | MalformedURLException e) {
            ctx.sessionAttribute("flash", "Некорректный URL");
        }

        ctx.redirect(NamedRoutes.rootPath());
    }
}
