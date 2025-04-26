package com.innovativesoftware.domsommelier_backend.news_management.controller;

import com.innovativesoftware.domsommelier_backend.news_management.model.NewsWithFileListDTO;
import com.innovativesoftware.domsommelier_backend.news_management.service.NewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/news")
public class NewsController {

    @Autowired
    private NewService newsService;

    @GetMapping("")
    public List<NewsWithFileListDTO> findAllEvents() { return newsService.findAllNews();}
}
