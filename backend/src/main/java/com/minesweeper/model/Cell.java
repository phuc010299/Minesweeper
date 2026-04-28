package com.minesweeper.model;

/**
 * Lớp đại diện cho một ô (cell) trên bảng Minesweeper.
 * Mỗi ô có các trạng thái: ẩn/hiện, có cờ hay không, có mìn hay không,
 * và số mìn lân cận (adjacentMines).
 */
public class Cell {

    // === Thuộc tính ===
    private boolean mine;           // true nếu ô này chứa mìn
    private boolean revealed;       // true nếu ô đã được mở
    private boolean flagged;        // true nếu ô đã được cắm cờ
    private int adjacentMines;      // Số mìn ở 8 ô xung quanh
    private final int row;          // Vị trí hàng
    private final int col;          // Vị trí cột

    /**
     * Constructor - tạo ô mới tại vị trí (row, col).
     * Mặc định: không mìn, chưa mở, chưa cắm cờ.
     */
    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
        this.mine = false;
        this.revealed = false;
        this.flagged = false;
        this.adjacentMines = 0;
    }

    // === Getters ===

    public boolean isMine() {
        return mine;
    }

    public boolean isRevealed() {
        return revealed;
    }

    public boolean isFlagged() {
        return flagged;
    }

    public int getAdjacentMines() {
        return adjacentMines;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    // === Setters ===

    public void setMine(boolean mine) {
        this.mine = mine;
    }

    public void setAdjacentMines(int count) {
        this.adjacentMines = count;
    }

    // === Actions ===

    /**
     * Mở ô này. Chỉ mở được nếu chưa mở và chưa cắm cờ.
     * @return true nếu mở thành công, false nếu không thể mở
     */
    public boolean reveal() {
        if (revealed || flagged) {
            return false; // Không thể mở ô đã revealed hoặc đã flagged
        }
        revealed = true;
        return true;
    }

    /**
     * Bật/tắt cờ trên ô. Chỉ toggle được nếu ô chưa được mở.
     * @return true nếu toggle thành công
     */
    public boolean toggleFlag() {
        if (revealed) {
            return false; // Không thể flag ô đã revealed
        }
        flagged = !flagged;
        return true;
    }

    /**
     * Reset ô về trạng thái ban đầu (cho game mới).
     */
    public void reset() {
        mine = false;
        revealed = false;
        flagged = false;
        adjacentMines = 0;
    }

    /**
     * Kiểm tra ô có phải là ô trống (không mìn, không số) hay không.
     * Ô trống sẽ trigger flood fill khi được mở.
     */
    public boolean isEmpty() {
        return !mine && adjacentMines == 0;
    }

    @Override
    public String toString() {
        if (flagged) return "🚩";
        if (!revealed) return "■";
        if (mine) return "💣";
        if (adjacentMines == 0) return " ";
        return String.valueOf(adjacentMines);
    }
}
