package com.dinhngoctranduy.controller;

import com.dinhngoctranduy.util.error.IdInvalidException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
    @GetMapping("/")
    public ResponseEntity<String> home() throws IdInvalidException {
        if (true) {
            throw new IdInvalidException("check");
        }
        return ResponseEntity.ok("HOME PAGE");
    }
}
