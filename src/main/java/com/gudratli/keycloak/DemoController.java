package com.gudratli.keycloak;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : Dunay Gudratli
 * @mail : d.qudretli@gmail.com
 * @since : 3/13/2024
 **/
@RestController
@RequestMapping("/api/v1/demo")
public class DemoController {
    @GetMapping("/hello")
    @PreAuthorize("hasRole('client_user')")
    public String hello() {
        return "Hello, this is a test api for keycloak";
    }

    @GetMapping("/hello-2")
    @PreAuthorize("hasRole('client_admin')")
    public String hello2() {
        return "Hello, this is a test api for keycloak - ADMIN";
    }
}
