package com.minesweeper.model.config;

/**
 * Giao diện cấu hình độ khó cho game Minesweeper.
 * Product Interface trong mô hình Factory Method Pattern.
 */
public interface DifficultyConfig {
    String getName();
    int getRows();
    int getCols();
    int getMineCount();
}
