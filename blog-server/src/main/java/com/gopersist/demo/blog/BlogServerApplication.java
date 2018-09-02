package com.gopersist.demo.blog;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class BlogServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(BlogServerApplication.class, args);
    }

    @Value("${blog.title}")
    private String blogTitle;

    @GetMapping("/blog/title")
    public String getBlogTitle() {
        return this.blogTitle;
    }
}
