package com.minesweeper.pattern.strategy;

import com.minesweeper.model.Cell;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests cho MineGenerationStrategy implementations.
 */
class MineStrategyTest {

    @Test
    @DisplayName("RandomMineStrategy đặt đúng số mìn")
    void testRandomMineCount() {
        Cell[][] grid = createGrid(9, 9);
        MineGenerationStrategy strategy = new RandomMineStrategy();

        strategy.generateMines(grid, 10, 4, 4);

        int mineCount = countMines(grid);
        assertEquals(10, mineCount, "Phải đặt chính xác 10 mìn");
    }

    @Test
    @DisplayName("RandomMineStrategy không đặt mìn tại excluded position")
    void testRandomMineExcluded() {
        Cell[][] grid = createGrid(9, 9);
        MineGenerationStrategy strategy = new RandomMineStrategy();

        strategy.generateMines(grid, 10, 0, 0);

        assertFalse(grid[0][0].isMine(), "Ô excluded không được có mìn");
    }

    @Test
    @DisplayName("SafeFirstClickStrategy tạo vùng an toàn 3x3")
    void testSafeFirstClickSafeZone() {
        Cell[][] grid = createGrid(9, 9);
        MineGenerationStrategy strategy = new SafeFirstClickStrategy();

        strategy.generateMines(grid, 10, 4, 4);

        // Kiểm tra vùng 3x3 quanh (4,4) không có mìn
        for (int r = 3; r <= 5; r++) {
            for (int c = 3; c <= 5; c++) {
                assertFalse(grid[r][c].isMine(),
                    String.format("Ô (%d,%d) trong vùng an toàn không được có mìn", r, c));
            }
        }
    }

    @Test
    @DisplayName("SafeFirstClickStrategy đặt đúng số mìn")
    void testSafeFirstClickMineCount() {
        Cell[][] grid = createGrid(9, 9);
        MineGenerationStrategy strategy = new SafeFirstClickStrategy();

        strategy.generateMines(grid, 10, 4, 4);

        int mineCount = countMines(grid);
        assertEquals(10, mineCount, "Phải đặt chính xác 10 mìn");
    }

    @Test
    @DisplayName("Vùng an toàn ở góc bảng vẫn hoạt động")
    void testSafeZoneAtCorner() {
        Cell[][] grid = createGrid(9, 9);
        MineGenerationStrategy strategy = new SafeFirstClickStrategy();

        // Click ở góc (0,0) - vùng an toàn chỉ có 4 ô (vì góc)
        strategy.generateMines(grid, 10, 0, 0);

        // Kiểm tra vùng an toàn
        for (int r = 0; r <= 1; r++) {
            for (int c = 0; c <= 1; c++) {
                assertFalse(grid[r][c].isMine(),
                    String.format("Ô (%d,%d) tại góc phải an toàn", r, c));
            }
        }
    }

    // === Helper methods ===

    /**
     * Tạo grid mới với kích thước cho trước.
     */
    private Cell[][] createGrid(int rows, int cols) {
        Cell[][] grid = new Cell[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                grid[r][c] = new Cell(r, c);
            }
        }
        return grid;
    }

    /**
     * Đếm tổng số mìn trên grid.
     */
    private int countMines(Cell[][] grid) {
        int count = 0;
        for (Cell[] row : grid) {
            for (Cell cell : row) {
                if (cell.isMine()) count++;
            }
        }
        return count;
    }
}
