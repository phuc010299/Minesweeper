package com.minesweeper.model.config;

/**
 * Cấu hình độ khó Chuyên Gia (Expert).
 * Concrete Product trong mô hình Factory Method.
 */
public class ExpertConfig implements DifficultyConfig {
    @Override
    public String getName() { return "Expert"; }
    
    @Override
    public int getRows() { return 16; }
    
    @Override
    public int getCols() { return 30; }
    
    @Override
    public int getMineCount() { return 99; }
}
