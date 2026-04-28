package com.minesweeper.model.config;

/**
 * Cấu hình độ khó Mới Bắt Đầu (Beginner).
 * Concrete Product trong mô hình Factory Method.
 */
public class BeginnerConfig implements DifficultyConfig {
    @Override
    public String getName() { return "Beginner"; }
    
    @Override
    public int getRows() { return 9; }
    
    @Override
    public int getCols() { return 9; }
    
    @Override
    public int getMineCount() { return 10; }
}
