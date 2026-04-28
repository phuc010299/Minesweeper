package com.minesweeper.pattern.state;

import com.minesweeper.service.GameService;

public class WonState implements GameState {
    @Override
    public boolean revealCell(GameService service, int row, int col) {
        return false;
    }

    @Override
    public boolean toggleFlag(GameService service, int row, int col) {
        return false;
    }

    @Override
    public boolean chordCell(GameService service, int row, int col) {
        return false;
    }

    @Override
    public String getName() {
        return "WON";
    }
}
