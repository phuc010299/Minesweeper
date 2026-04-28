package com.minesweeper.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests cho lớp Cell.
 */
class CellTest {

    private Cell cell;

    @BeforeEach
    void setUp() {
        cell = new Cell(0, 0);
    }

    @Test
    @DisplayName("Cell mới tạo phải ở trạng thái mặc định")
    void testDefaultState() {
        assertFalse(cell.isMine(), "Mặc định không phải mìn");
        assertFalse(cell.isRevealed(), "Mặc định chưa mở");
        assertFalse(cell.isFlagged(), "Mặc định chưa cắm cờ");
        assertEquals(0, cell.getAdjacentMines(), "Mặc định 0 mìn lân cận");
    }

    @Test
    @DisplayName("Reveal ô thành công")
    void testReveal() {
        assertTrue(cell.reveal(), "Reveal phải thành công");
        assertTrue(cell.isRevealed(), "Ô phải ở trạng thái revealed");
    }

    @Test
    @DisplayName("Không thể reveal ô đã revealed")
    void testRevealAlreadyRevealed() {
        cell.reveal();
        assertFalse(cell.reveal(), "Không thể reveal ô đã mở");
    }

    @Test
    @DisplayName("Không thể reveal ô đã flagged")
    void testRevealFlaggedCell() {
        cell.toggleFlag();
        assertFalse(cell.reveal(), "Không thể reveal ô đã cắm cờ");
    }

    @Test
    @DisplayName("Toggle flag thành công")
    void testToggleFlag() {
        assertTrue(cell.toggleFlag(), "Toggle flag phải thành công");
        assertTrue(cell.isFlagged(), "Ô phải có cờ");

        assertTrue(cell.toggleFlag(), "Toggle lần 2 phải thành công");
        assertFalse(cell.isFlagged(), "Ô phải bỏ cờ");
    }

    @Test
    @DisplayName("Không thể flag ô đã revealed")
    void testFlagRevealedCell() {
        cell.reveal();
        assertFalse(cell.toggleFlag(), "Không thể flag ô đã mở");
    }

    @Test
    @DisplayName("Set mine hoạt động đúng")
    void testSetMine() {
        cell.setMine(true);
        assertTrue(cell.isMine());

        cell.setMine(false);
        assertFalse(cell.isMine());
    }

    @Test
    @DisplayName("isEmpty trả về true khi không mìn và adjacentMines = 0")
    void testIsEmpty() {
        assertTrue(cell.isEmpty(), "Ô không mìn, 0 adjacent → empty");

        cell.setAdjacentMines(3);
        assertFalse(cell.isEmpty(), "Ô có adjacent > 0 → không empty");

        cell.setAdjacentMines(0);
        cell.setMine(true);
        assertFalse(cell.isEmpty(), "Ô mìn → không empty");
    }

    @Test
    @DisplayName("Reset đưa ô về trạng thái ban đầu")
    void testReset() {
        cell.setMine(true);
        cell.reveal();
        cell.setAdjacentMines(5);

        cell.reset();

        assertFalse(cell.isMine());
        assertFalse(cell.isRevealed());
        assertFalse(cell.isFlagged());
        assertEquals(0, cell.getAdjacentMines());
    }

    @Test
    @DisplayName("Vị trí row/col đúng")
    void testPosition() {
        Cell c = new Cell(3, 7);
        assertEquals(3, c.getRow());
        assertEquals(7, c.getCol());
    }
}
