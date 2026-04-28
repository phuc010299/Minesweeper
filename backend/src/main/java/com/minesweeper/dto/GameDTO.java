package com.minesweeper.dto;

/**
 * Data Transfer Object cho trạng thái game.
 * Chứa toàn bộ thông tin frontend cần để render bảng game.
 */
public class GameDTO {
    private int rows;
    private int cols;
    private String gameState;       // READY, PLAYING, WON, LOST
    private int remainingMines;     // Số mìn còn lại (tổng - flags)
    private int elapsedSeconds;     // Thời gian đã trôi qua
    private String difficulty;      // Tên mức độ khó
    private CellDTO[][] cells;      // Mảng 2D trạng thái các ô
    private int hitMineRow;         // Hàng mìn bị click (-1 nếu chưa thua)
    private int hitMineCol;         // Cột mìn bị click (-1 nếu chưa thua)

    // === Constructors ===
    public GameDTO() {
        this.hitMineRow = -1;
        this.hitMineCol = -1;
    }

    // === Getters & Setters ===
    public int getRows() { return rows; }
    public void setRows(int rows) { this.rows = rows; }

    public int getCols() { return cols; }
    public void setCols(int cols) { this.cols = cols; }

    public String getGameState() { return gameState; }
    public void setGameState(String gameState) { this.gameState = gameState; }

    public int getRemainingMines() { return remainingMines; }
    public void setRemainingMines(int remainingMines) { this.remainingMines = remainingMines; }

    public int getElapsedSeconds() { return elapsedSeconds; }
    public void setElapsedSeconds(int elapsedSeconds) { this.elapsedSeconds = elapsedSeconds; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public CellDTO[][] getCells() { return cells; }
    public void setCells(CellDTO[][] cells) { this.cells = cells; }

    public int getHitMineRow() { return hitMineRow; }
    public void setHitMineRow(int hitMineRow) { this.hitMineRow = hitMineRow; }

    public int getHitMineCol() { return hitMineCol; }
    public void setHitMineCol(int hitMineCol) { this.hitMineCol = hitMineCol; }
}
