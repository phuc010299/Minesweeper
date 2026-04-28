package com.minesweeper.model;

import com.minesweeper.model.config.DifficultyConfig;

/**
 * Board - Anemic Domain Model chứa dữ liệu bảng game.
 * Logic nghiệp vụ nằm ở GameService.
 */
public class Board {

    private final Cell[][] grid;
    private final DifficultyConfig config;
    
    private int flagCount;
    private int revealedCount;

    public Board(DifficultyConfig config) {
        this.config = config;
        this.flagCount = 0;
        this.revealedCount = 0;

        int rows = config.getRows();
        int cols = config.getCols();
        this.grid = new Cell[rows][cols];
        
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                this.grid[r][c] = new Cell(r, c);
            }
        }
    }

    public Cell getCell(int row, int col) {
        return grid[row][col];
    }

    public Cell[][] getGrid() {
        return grid;
    }

    public DifficultyConfig getConfig() {
        return config;
    }

    public int getFlagCount() {
        return flagCount;
    }

    public void incrementFlagCount() {
        this.flagCount++;
    }

    public void decrementFlagCount() {
        this.flagCount--;
    }

    public int getRevealedCount() {
        return revealedCount;
    }

    public void incrementRevealedCount() {
        this.revealedCount++;
    }
}
