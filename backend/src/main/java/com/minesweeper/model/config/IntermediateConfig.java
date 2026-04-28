package com.minesweeper.model.config;

/**
 * Cấu hình độ khó Trung Bình (Intermediate).
 * Concrete Product trong mô hình Factory Method.
 */
public class IntermediateConfig implements DifficultyConfig {
    @Override
    public String getName() { return "Intermediate"; }
    
    @Override
    public int getRows() { return 16; }
    
    @Override
    public int getCols() { return 16; }
    
    @Override
    public int getMineCount() { return 40; }
}
