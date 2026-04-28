package com.minesweeper.pattern.factory;

import com.minesweeper.model.config.DifficultyConfig;

/**
 * Factory Method Pattern - Giao diện Creator để tạo cấu hình độ khó.
 * Các lớp cụ thể (Concrete Creators) sẽ quyết định tham số instantiate cụ thể.
 */
public interface DifficultyFactory {
    /**
     * Factory Method
     * @return Cấu hình độ khó mới
     */
    DifficultyConfig createDifficulty();
}
