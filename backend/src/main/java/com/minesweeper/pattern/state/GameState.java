package com.minesweeper.pattern.state;

import com.minesweeper.service.GameService;

/**
 * State Pattern - Interface định nghĩa các hành vi cho từng trạng thái game.
 */
public interface GameState {
    boolean revealCell(GameService service, int row, int col);
    boolean toggleFlag(GameService service, int row, int col);
    boolean chordCell(GameService service, int row, int col);
    String getName();
}
