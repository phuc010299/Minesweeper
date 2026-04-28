package com.minesweeper.dto;

/**
 * Data Transfer Object cho một ô (Cell).
 * Chỉ chứa thông tin mà frontend cần hiển thị.
 * Không lộ vị trí mìn khi game đang chơi (bảo mật).
 */
public class CellDTO {
    private int row;
    private int col;
    private boolean revealed;
    private boolean flagged;
    private boolean mine;           // Chỉ true khi revealed && là mìn (game over)
    private int adjacentMines;      // Chỉ có giá trị khi revealed

    // === Constructors ===
    public CellDTO() {}

    public CellDTO(int row, int col, boolean revealed, boolean flagged,
                   boolean mine, int adjacentMines) {
        this.row = row;
        this.col = col;
        this.revealed = revealed;
        this.flagged = flagged;
        this.mine = mine;
        this.adjacentMines = adjacentMines;
    }

    // === Getters & Setters ===
    public int getRow() { return row; }
    public void setRow(int row) { this.row = row; }

    public int getCol() { return col; }
    public void setCol(int col) { this.col = col; }

    public boolean isRevealed() { return revealed; }
    public void setRevealed(boolean revealed) { this.revealed = revealed; }

    public boolean isFlagged() { return flagged; }
    public void setFlagged(boolean flagged) { this.flagged = flagged; }

    public boolean isMine() { return mine; }
    public void setMine(boolean mine) { this.mine = mine; }

    public int getAdjacentMines() { return adjacentMines; }
    public void setAdjacentMines(int adjacentMines) { this.adjacentMines = adjacentMines; }
}
