package com.goganesh.packages.service;

import com.goganesh.packages.domain.Page;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class JsoupWebParser implements WebParser{

    private static final Logger LOGGER = LoggerFactory.getLogger(JsoupWebParser.class);

    private static final String USER_AGENT = "HeliontSearchBot";
    private static final String REFERER = "http://www.google.com";

    @Override
    public Page parsePageByUrl(String url) throws IOException {
        Connection connection = Jsoup.connect(url)
                .userAgent(USER_AGENT)
                .referrer(REFERER)
                .get()
                .connection();

        return Page.builder()
                .path(url)
                .code(connection.response().statusCode())
                .content(connection.get().html())
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

        if (link.startsWith("/") && link.endsWith("/") && link.length() > 2) {
            absoluteUrl = url.getProtocol() + "://" + url.getHost() + link;
        } else if (link.startsWith(url.getProtocol() + "://" + url.getHost()) && link.endsWith("/")) {
            absoluteUrl = link;
        } else {
            LOGGER.info("While parsing " + baseUrl + " ,skip - " + link);
        }
        return absoluteUrl;
    }
}
