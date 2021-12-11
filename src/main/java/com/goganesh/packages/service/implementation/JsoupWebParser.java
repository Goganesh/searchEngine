package com.goganesh.packages.service.implementation;

import com.goganesh.packages.domain.Page;
import com.goganesh.packages.exception.BadBaseUrlException;
import com.goganesh.packages.service.WebParser;
import lombok.SneakyThrows;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class JsoupWebParser implements WebParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsoupWebParser.class);

    @Value("${search-bot.delay}")
    private int delay;

    @Value("${search-bot.user-agent}")
    private String userAgent;

    @Value("${search-bot.referer}")
    private String referer;

    @Override
    public String getTextByPageAndField(Page page, String tag) {
        String content = page.getContent();
        Document document = Jsoup.parse(content);
        Elements elements = document.getElementsByTag(tag);

        return elements.text();
    }

    @SneakyThrows
    @Override
    public Page parsePageByUrl(String url) {
        Thread.sleep(delay);

        Connection connection;
        try {
            connection = Jsoup.connect(url)
                    .userAgent(userAgent)
                    .referrer(referer)
                    .get()
                    .connection();
        } catch (IOException e) {
            throw new BadBaseUrlException("Bad URL parsing - " + url);
        }

        String content;
        try {
            content = connection.get().html();
        } catch (IOException e) {
            content = connection.response().statusMessage();
            LOGGER.debug("While parsing " + url + " , status code " + connection.response().statusCode());
        }

        return Page.builder()
                .path(url)
                .code(connection.response().statusCode())
                .content(content)
                .build();
    }

    @Override
    public Set<String> getDomainUrlsByPage(Page page) {
        return Jsoup.parse(page.getContent())
                .getElementsByTag("a")
                .stream()
                .map(element -> element.attr("href"))
                .map(url -> getAbsoluteUrl(page.getPath(), url))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private String getAbsoluteUrl(String baseUrl, String link) {
        String absoluteUrl = null;
        URL url = null;
        try {
            url = new URL(baseUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        if (link.startsWith("/") && link.length() > 2) {
            absoluteUrl = url.getProtocol() + "://" + url.getHost() + link;
        } else if (link.startsWith(url.getProtocol() + "://" + url.getHost())) {
            absoluteUrl = link;
        } else {
            LOGGER.debug("While parsing " + baseUrl + " ,skip - " + link);
        }

        return absoluteUrl;
    }
}
