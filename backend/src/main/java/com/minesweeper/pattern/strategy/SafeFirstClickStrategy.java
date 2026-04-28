package com.minesweeper.pattern.strategy;

import com.minesweeper.model.Cell;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Strategy Pattern - Đặt mìn an toàn cho first click.
 * Đảm bảo ô first click VÀ 8 ô xung quanh đều không có mìn.
 * Điều này giúp người chơi luôn có một khởi đầu tốt.
 */
public class SafeFirstClickStrategy implements MineGenerationStrategy {

    @Override
    public void generateMines(Cell[][] grid, int mineCount, int excludeRow, int excludeCol) {
        int rows = grid.length;
        int cols = grid[0].length;

        // Tạo danh sách vị trí hợp lệ, loại trừ vùng an toàn 3x3 quanh first click
        List<int[]> positions = new ArrayList<>();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                // Kiểm tra xem ô có nằm trong vùng an toàn 3x3 không
                if (isSafeZone(r, c, excludeRow, excludeCol)) {
                    continue; // Bỏ qua ô trong vùng an toàn
                }
                positions.add(new int[]{r, c});
            }
        }

        // Xáo trộn và chọn vị trí đặt mìn
        Collections.shuffle(positions);

        // Nếu không đủ vị trí (bảng quá nhỏ), đặt ít mìn hơn
        int actualMines = Math.min(mineCount, positions.size());
        for (int i = 0; i < actualMines; i++) {
            int r = positions.get(i)[0];
            int c = positions.get(i)[1];
            grid[r][c].setMine(true);
        }
    }

    /**
     * Kiểm tra một ô có nằm trong vùng an toàn 3x3 quanh first click hay không.
     * Vùng an toàn bao gồm ô first click và 8 ô lân cận.
     */
    private boolean isSafeZone(int row, int col, int excludeRow, int excludeCol) {
        // Nếu không có vị trí excluded (first click), không cần vùng an toàn
        if (excludeRow < 0 || excludeCol < 0) {
            return false;
        }
        return Math.abs(row - excludeRow) <= 1 && Math.abs(col - excludeCol) <= 1;
    }
}
