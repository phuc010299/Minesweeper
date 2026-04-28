package com.minesweeper.service;

import com.minesweeper.pattern.factory.BeginnerDifficultyFactory;
import com.minesweeper.model.config.DifficultyConfig;
import com.minesweeper.pattern.state.*;
import com.minesweeper.utils.GameTimer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests cho lớp GameService.
 * Test các chức năng chính: reveal, flag, chord, flood fill, win/lose.
 */
class GameServiceTest {

    private GameService service;
    private DifficultyConfig beginnerConfig;

    @BeforeEach
    void setUp() {
        GameTimer timer = new GameTimer();
        service = new GameService(timer);
        beginnerConfig = new BeginnerDifficultyFactory().createDifficulty();
    }

    @Test
    @DisplayName("GameService khởi tạo đúng kích thước")
    void testBoardInitialization() {
        assertEquals(9, service.getBoard().getConfig().getRows());
        assertEquals(9, service.getBoard().getConfig().getCols());
        assertTrue(service.getGameState() instanceof ReadyState);
        assertTrue(service.isFirstClick());
    }

    @Test
    @DisplayName("First click luôn an toàn (không trúng mìn)")
    void testFirstClickSafe() {
        service.revealOrChordCell(0, 0);

        assertFalse(service.getGameState() instanceof LostState, "First click phải an toàn!");
        assertFalse(service.isFirstClick());
    }

    @Test
    @DisplayName("Reveal cell thay đổi trạng thái ô")
    void testRevealCell() {
        service.revealOrChordCell(4, 4);
        assertTrue(service.getBoard().getCell(4, 4).isRevealed());
    }

    @Test
    @DisplayName("Toggle flag hoạt động đúng")
    void testToggleFlag() {
        service.toggleFlag(0, 0);
        assertTrue(service.getBoard().getCell(0, 0).isFlagged());
        assertEquals(1, service.getBoard().getFlagCount());

        service.toggleFlag(0, 0);
        assertFalse(service.getBoard().getCell(0, 0).isFlagged());
        assertEquals(0, service.getBoard().getFlagCount());
    }

    @Test
    @DisplayName("Không thể flag ô đã revealed")
    void testCannotFlagRevealedCell() {
        service.revealOrChordCell(4, 4);
        service.toggleFlag(4, 4);
        assertFalse(service.getBoard().getCell(4, 4).isFlagged(), "Không thể flag ô đã mở");
    }

    @Test
    @DisplayName("getRemainingMines tính đúng")
    void testRemainingMines() {
        assertEquals(10, service.getRemainingMines());

        service.toggleFlag(0, 0);
        assertEquals(9, service.getRemainingMines());

        service.toggleFlag(0, 1);
        assertEquals(8, service.getRemainingMines());

        service.toggleFlag(0, 0);
        assertEquals(9, service.getRemainingMines());
    }

    @Test
    @DisplayName("Vị trí không hợp lệ bị reject")
    void testInvalidPosition() {
        assertFalse(service.isValidPosition(-1, 0));
        assertFalse(service.isValidPosition(0, -1));
        assertFalse(service.isValidPosition(100, 0));
        assertFalse(service.isValidPosition(0, 100));
    }
}
