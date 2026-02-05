package hexlet.code.controller;

import hexlet.code.dto.BuildUrlPage;
import hexlet.code.dto.UrlCheckService;
import hexlet.code.dto.UrlPage;
import hexlet.code.dto.UrlsPage;
import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlCheckRepository;
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
import java.util.List;
import java.util.Optional;

import static hexlet.code.repository.UrlCheckRepository.saveUrlCheck;
import static io.javalin.rendering.template.TemplateUtil.model;

@Slf4j
public class UrlsController {
    private static final Integer STATUS_CODE_404 = 404;
    private static final Integer STATUS_CODE_500 = 500;

    public static void show(Context ctx) throws SQLException {
        var id = ctx.pathParamAsClass("id", Long.class).get();
        var url = UrlRepository.findById(id)
                .orElseThrow(() -> new NotFoundResponse("Entity with id = " + id + " not found"));

        List<UrlCheck> checksList = UrlCheckRepository.findAllChecksByUrlId(id);
        UrlPage urlPage = new UrlPage(url, checksList);

        ctx.render("urls/show.jte", model("page", urlPage));
    }

    public static void index(Context ctx) {
        try {
            var urls = UrlRepository.getEntities();
            for (Url url : urls) {
                Optional<UrlCheck> lastCheck = UrlCheckRepository.findLastCheckByUrlId(url.getId());
                lastCheck.ifPresent(url::setLastCheck);
            }
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
                ctx.sessionAttribute("flash", "Сайт уже существует");
                ctx.redirect(NamedRoutes.rootPath());
                return;
            } else {
                Url urlEntity = new Url(baseUrl);
                UrlRepository.save(urlEntity);
                ctx.sessionAttribute("flash", "Сайт успешно добавлена");
            }
        } catch (URISyntaxException | MalformedURLException e) {
            ctx.sessionAttribute("flash", "Некорректный URL");
            ctx.redirect(NamedRoutes.rootPath());
            return;
        }

        ctx.redirect(NamedRoutes.urlsPath());
    }

    public static void createCheck(Context ctx) {
        Long id = Long.parseLong(ctx.pathParam("id"));

        Optional<Url> optionalUrl;
        try {
            optionalUrl = UrlRepository.findById(id);
        } catch (SQLException e) {
            e.printStackTrace();
            ctx.status(STATUS_CODE_500).result("Error in database");
            return;
        }

        if (optionalUrl.isEmpty()) {
            ctx.status(STATUS_CODE_404).result("URL is not found");
            return;
        }

        Url url = optionalUrl.get();
        UrlCheckService urlCheckService = new UrlCheckService();

        try {
            UrlCheck newCheck = urlCheckService.performCheck(url.getName());
            newCheck.setUrlId(id);
            saveUrlCheck(newCheck);
            ctx.redirect("/urls/" + id);
        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(STATUS_CODE_500).result("Error during verification URL");
        }
    }
}
