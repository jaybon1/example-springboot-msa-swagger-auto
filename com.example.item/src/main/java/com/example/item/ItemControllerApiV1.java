package com.example.item;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/items")
public class ItemControllerApiV1 {

    @Value("${server.port}")
    private Integer serverPort;

    @GetMapping
    public String get() {
        return "items port : " + serverPort;
    }

}
