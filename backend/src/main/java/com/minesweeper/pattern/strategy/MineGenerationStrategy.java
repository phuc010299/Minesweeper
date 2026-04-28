package com.minesweeper.pattern.strategy;

import com.minesweeper.model.Cell;

/**
 * Strategy Pattern - Interface cho thuật toán đặt mìn.
 * Cho phép thay đổi cách đặt mìn mà không ảnh hưởng đến code khác.
 * 
 * Các implementation:
 * - RandomMineStrategy: đặt mìn ngẫu nhiên
 * - SafeFirstClickStrategy: đảm bảo click đầu tiên an toàn
 */
public interface MineGenerationStrategy {

    /**
     * Đặt mìn lên bảng game.
     * @param grid mảng 2D các ô
     * @param mineCount số mìn cần đặt
     * @param excludeRow hàng cần tránh (vị trí first click), -1 nếu không cần
     * @param excludeCol cột cần tránh (vị trí first click), -1 nếu không cần
     */
    void generateMines(Cell[][] grid, int mineCount, int excludeRow, int excludeCol);
}
