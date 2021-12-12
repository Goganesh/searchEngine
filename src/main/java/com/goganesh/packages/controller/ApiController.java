package com.goganesh.packages.controller;

import com.goganesh.packages.service.SiteService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class ApiController {

    private final SiteService siteService;

    @GetMapping("/startIndexing")
    public String startIndexing() {
        //todo
        //проверка блокировки: средствами базы/средствами Redis
        //блокировка процесса mutex
        //индексация/переиндексация в разных потоках
        //переиндексация удаление всех данных кроме сайта и начало процесса заново
        //разблокировка процесса
        //возврат результата dto

        return "somedata";
    }

    @GetMapping("/stopIndexing")
    public String stopIndexing() {
        //todo
        //Метод останавливает текущий процесс индексации (переиндексации).
                //Если в настоящий момент индексация или
        //переиндексация не происходит, метод возвращает соответствующее
        //сообщение об ошибке.

        return "somedata";
    }

    @PostMapping("/indexPage")
    public String indexPage() {
        //todo
        //Метод добавляет в индекс или обновляет отдельную страницу,
        //адрес которой передан в параметре.
          //      Если адрес страницы передан неверно, метод должен вернуть
        //соответствующую ошибку.
        return "somedata";
    }

    @GetMapping("/statistics")
    public String getStatistics() {
        //todo
        return "somedata";
    }

    @GetMapping("/search")
    public String search() {
        //todo
        return "somedata";
    }

}
