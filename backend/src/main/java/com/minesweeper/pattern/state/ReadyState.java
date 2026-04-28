package com.minesweeper.pattern.state;

import com.minesweeper.service.GameService;

public class ReadyState implements GameState {
    @Override
    public boolean revealCell(GameService service, int row, int col) {
        service.initFirstClick(row, col);
        service.changeState(new PlayingState());
        return service.doRevealCell(row, col);
    }

    @Override
    public boolean toggleFlag(GameService service, int row, int col) {
        return service.doToggleFlag(row, col);
    }

    @Override
    public boolean chordCell(GameService service, int row, int col) {
        return false;
    }

    @Override
    public String getName() {
        return "READY";
    }
}
