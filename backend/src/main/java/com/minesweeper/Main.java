package com.minesweeper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main - Entry point cho ứng dụng Minesweeper.
 * 
 * Sử dụng Spring Boot để:
 * - Khởi tạo REST API server
 * - Phục vụ React frontend (static files)
 * - Quản lý dependency injection
 */
@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
        System.out.println("🎮 Minesweeper Backend started on http://localhost:8080");
        System.out.println("📡 API: http://localhost:8080/api/game");
    }
}
