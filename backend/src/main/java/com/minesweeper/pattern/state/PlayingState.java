package com.minesweeper.pattern.state;

import com.minesweeper.service.GameService;

public class PlayingState implements GameState {
    @Override
    public boolean revealCell(GameService service, int row, int col) {
        return service.doRevealCell(row, col);
    }

    @Override
    public boolean toggleFlag(GameService service, int row, int col) {
        return service.doToggleFlag(row, col);
    }

    @Override
    public boolean chordCell(GameService service, int row, int col) {
        return service.doChordCell(row, col);
    }

    @Override
    public String getName() {
        return "PLAYING";
    }
}
