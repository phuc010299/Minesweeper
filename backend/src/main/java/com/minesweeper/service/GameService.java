package com.minesweeper.service;

import com.minesweeper.dto.CellDTO;
import com.minesweeper.dto.GameDTO;
import com.minesweeper.pattern.factory.*;
import com.minesweeper.model.Board;
import com.minesweeper.model.Cell;
import com.minesweeper.model.config.DifficultyConfig;
import com.minesweeper.pattern.state.*;
import com.minesweeper.pattern.strategy.MineGenerationStrategy;
import com.minesweeper.pattern.strategy.SafeFirstClickStrategy;
import com.minesweeper.utils.GameTimer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * GameService - Tầng Service chứa toàn bộ business logic.
 * Trực tiếp uỷ quyền thao tác cho GameState mà không cần thông qua Command.
 */
@Service
public class GameService {

    private Board board;
    private DifficultyConfig currentConfig;
    private GameState gameState;
    private MineGenerationStrategy mineStrategy;
    private final GameTimer timer;

    // Lưu vị trí mìn bị click (để highlight đỏ trên UI)
    private int hitMineRow = -1;
    private int hitMineCol = -1;

    private static final int[][] DIRECTIONS = {
        {-1, -1}, {-1, 0}, {-1, 1},
        { 0, -1},          { 0, 1},
        { 1, -1}, { 1, 0}, { 1, 1}
    };

    @Autowired
    public GameService(GameTimer timer) {
        this.timer = timer;
        this.mineStrategy = new SafeFirstClickStrategy();
        this.currentConfig = new BeginnerDifficultyFactory().createDifficulty();
        newGame();
    }

    public void newGame() {
        this.board = new Board(currentConfig);
        this.gameState = new ReadyState();
        this.timer.reset();
        
        this.hitMineRow = -1;
        this.hitMineCol = -1;
    }

    public void changeDifficulty(String difficulty) {
        DifficultyFactory factory = switch (difficulty.toLowerCase()) {
            case "beginner" -> new BeginnerDifficultyFactory();
            case "intermediate" -> new IntermediateDifficultyFactory();
            case "expert" -> new ExpertDifficultyFactory();
            default -> throw new IllegalArgumentException("Unknown difficulty: " + difficulty);
        };
        this.currentConfig = factory.createDifficulty();
        newGame();
    }

    // =============================================
    // === XỬ LÝ SỰ KIỆN TỪ CONTROLLER ===
    // =============================================

    public void revealOrChordCell(int row, int col) {
        Cell cell = board.getCell(row, col);
        GameState stateBefore = this.gameState;

        if (cell.isRevealed()) {
            processChordCell(row, col);
        } else if (!cell.isFlagged()) {
            if (isFirstClick()) {
                timer.start();
            }
            processRevealCell(row, col);
        }

        if (gameState instanceof WonState || gameState instanceof LostState) {
            timer.stop();
        }

        if (!(stateBefore instanceof LostState) && this.gameState instanceof LostState) {
            this.hitMineRow = row;
            this.hitMineCol = col;
        }
    }

    public void toggleFlag(int row, int col) {
        processToggleFlag(row, col);
    }

    // =============================================
    // === DTO BUILDING ===
    // =============================================

    public GameDTO getGameDTO() {
        GameDTO dto = new GameDTO();
        dto.setRows(board.getConfig().getRows());
        dto.setCols(board.getConfig().getCols());
        dto.setGameState(gameState.getName());
        dto.setRemainingMines(getRemainingMines());
        dto.setElapsedSeconds(timer.getElapsedSeconds());
        dto.setDifficulty(board.getConfig().getName());
        dto.setHitMineRow(hitMineRow);
        dto.setHitMineCol(hitMineCol);

        int rows = board.getConfig().getRows();
        int cols = board.getConfig().getCols();
        CellDTO[][] cellDTOs = new CellDTO[rows][cols];

        boolean isGameOver = gameState instanceof WonState || gameState instanceof LostState;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Cell cell = board.getCell(r, c);
                CellDTO cellDto = new CellDTO();
                cellDto.setRow(r);
                cellDto.setCol(c);
                cellDto.setRevealed(cell.isRevealed());
                cellDto.setFlagged(cell.isFlagged());

                if (cell.isRevealed() || isGameOver) {
                    cellDto.setMine(cell.isMine());
                    cellDto.setAdjacentMines(cell.getAdjacentMines());
                } else {
                    cellDto.setMine(false);
                    cellDto.setAdjacentMines(0);
                }

                cellDTOs[r][c] = cellDto;
            }
        }

        dto.setCells(cellDTOs);
        return dto;
    }

    // =============================================
    // === DELEGATE TO STATE ===
    // =============================================

    public boolean processRevealCell(int row, int col) {
        return gameState.revealCell(this, row, col);
    }

    public boolean processToggleFlag(int row, int col) {
        return gameState.toggleFlag(this, row, col);
    }

    public boolean processChordCell(int row, int col) {
        return gameState.chordCell(this, row, col);
    }

    // =============================================
    // === BUSINESS LOGIC THỰC SỰ ===
    // =============================================

    public void changeState(GameState newState) {
        this.gameState = newState;
    }

    public void initFirstClick(int row, int col) {
        mineStrategy.generateMines(board.getGrid(), currentConfig.getMineCount(), row, col);
        calculateAdjacentMines();
    }

    public boolean doRevealCell(int row, int col) {
        if (!isValidPosition(row, col)) return false;
        Cell cell = board.getCell(row, col);
        if (cell.isFlagged() || cell.isRevealed()) return false;

        cell.reveal();
        board.incrementRevealedCount();

        if (cell.isMine()) {
            changeState(new LostState());
            revealAllMines();
            return true;
        }

        if (cell.isEmpty()) {
            floodFill(row, col);
        }

        checkWinCondition();
        return true;
    }

    public boolean doToggleFlag(int row, int col) {
        if (!isValidPosition(row, col)) return false;
        Cell cell = board.getCell(row, col);
        if (cell.isRevealed()) return false;

        boolean wasFlagged = cell.isFlagged();
        if (cell.toggleFlag()) {
            if (wasFlagged) {
                board.decrementFlagCount();
            } else {
                board.incrementFlagCount();
            }
            return true;
        }
        return false;
    }

    public boolean doChordCell(int row, int col) {
        if (!isValidPosition(row, col)) return false;
        Cell cell = board.getCell(row, col);

        if (!cell.isRevealed() || cell.isMine() || cell.getAdjacentMines() == 0) {
            return false;
        }

        int adjacentFlags = countAdjacentFlags(row, col);
        if (adjacentFlags != cell.getAdjacentMines()) {
            return false;
        }

        boolean anyRevealed = false;
        for (int[] dir : DIRECTIONS) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];
            if (isValidPosition(newRow, newCol)) {
                Cell neighbor = board.getCell(newRow, newCol);
                if (!neighbor.isRevealed() && !neighbor.isFlagged()) {
                    processRevealCell(newRow, newCol);
                    anyRevealed = true;
                }
            }
        }
        return anyRevealed;
    }

    private void floodFill(int row, int col) {
        for (int[] dir : DIRECTIONS) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];

            if (!isValidPosition(newRow, newCol)) continue;

            Cell neighbor = board.getCell(newRow, newCol);
            if (!neighbor.isRevealed() && !neighbor.isFlagged() && !neighbor.isMine()) {
                neighbor.reveal();
                board.incrementRevealedCount();

                if (neighbor.isEmpty()) {
                    floodFill(newRow, newCol);
                }
            }
        }
    }

    private void calculateAdjacentMines() {
        int rows = currentConfig.getRows();
        int cols = currentConfig.getCols();

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (!board.getCell(r, c).isMine()) {
                    int count = 0;
                    for (int[] dir : DIRECTIONS) {
                        int nr = r + dir[0];
                        int nc = c + dir[1];
                        if (isValidPosition(nr, nc) && board.getCell(nr, nc).isMine()) {
                            count++;
                        }
                    }
                    board.getCell(r, c).setAdjacentMines(count);
                }
            }
        }
    }

    private int countAdjacentFlags(int row, int col) {
        int count = 0;
        for (int[] dir : DIRECTIONS) {
            int nr = row + dir[0];
            int nc = col + dir[1];
            if (isValidPosition(nr, nc) && board.getCell(nr, nc).isFlagged()) {
                count++;
            }
        }
        return count;
    }

    protected void checkWinCondition() {
        int totalCells = currentConfig.getRows() * currentConfig.getCols();
        int safeCells = totalCells - currentConfig.getMineCount();

        if (board.getRevealedCount() == safeCells && !(gameState instanceof WonState)) {
            changeState(new WonState());
            autoFlagAllMines();
        }
    }

    private void revealAllMines() {
        for (int r = 0; r < currentConfig.getRows(); r++) {
            for (int c = 0; c < currentConfig.getCols(); c++) {
                Cell cell = board.getCell(r, c);
                if (cell.isMine() && !cell.isRevealed()) {
                    cell.reveal();
                }
            }
        }
    }

    private void autoFlagAllMines() {
        for (int r = 0; r < currentConfig.getRows(); r++) {
            for (int c = 0; c < currentConfig.getCols(); c++) {
                Cell cell = board.getCell(r, c);
                if (cell.isMine() && !cell.isFlagged()) {
                    cell.toggleFlag();
                    board.incrementFlagCount();
                }
            }
        }
    }

    public boolean isValidPosition(int row, int col) {
        return row >= 0 && row < currentConfig.getRows() && col >= 0 && col < currentConfig.getCols();
    }

    public Board getBoard() {
        return board;
    }

    public GameState getGameState() {
        return gameState;
    }

    public boolean isFirstClick() {
        return gameState instanceof ReadyState;
    }

    public int getRemainingMines() {
        return currentConfig.getMineCount() - board.getFlagCount();
    }
}
