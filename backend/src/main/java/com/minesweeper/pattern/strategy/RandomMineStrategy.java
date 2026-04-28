package com.minesweeper.pattern.strategy;

import com.minesweeper.model.Cell;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Strategy Pattern - Đặt mìn ngẫu nhiên lên bảng.
 * Tránh vị trí first click (excludeRow, excludeCol).
 */
public class RandomMineStrategy implements MineGenerationStrategy {

    @Override
    public void generateMines(Cell[][] grid, int mineCount, int excludeRow, int excludeCol) {
        int rows = grid.length;
        int cols = grid[0].length;

        // Tạo danh sách tất cả các vị trí hợp lệ (trừ vị trí excluded)
        List<int[]> positions = new ArrayList<>();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                // Bỏ qua vị trí first click
                if (r == excludeRow && c == excludeCol) {
                    continue;
                }
                positions.add(new int[]{r, c});
            }
        }

        // Xáo trộn danh sách và chọn mineCount vị trí đầu tiên
        Collections.shuffle(positions);

        // Đặt mìn tại các vị trí được chọn
        for (int i = 0; i < mineCount && i < positions.size(); i++) {
            int r = positions.get(i)[0];
            int c = positions.get(i)[1];
            grid[r][c].setMine(true);
        }
    }
}
