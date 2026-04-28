package com.minesweeper.controller;

import com.minesweeper.dto.GameDTO;
import com.minesweeper.service.GameService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/game")
@CrossOrigin(origins = "*") // Cho phép React dev server truy cập
public class GameRestController {

    private final GameService gameService;

    @Autowired
    public GameRestController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping
    public ResponseEntity<GameDTO> getGameState() {
        return ResponseEntity.ok(gameService.getGameDTO());
    }

    @PostMapping("/new")
    public ResponseEntity<GameDTO> newGame(
            @RequestParam(defaultValue = "beginner") String difficulty) {
        gameService.changeDifficulty(difficulty);
        return ResponseEntity.ok(gameService.getGameDTO());
    }

    @PostMapping("/click")
    public ResponseEntity<GameDTO> handleClick(
            @RequestParam int row, @RequestParam int col) {
        gameService.revealOrChordCell(row, col);
        return ResponseEntity.ok(gameService.getGameDTO());
    }

    @PostMapping("/flag")
    public ResponseEntity<GameDTO> handleFlag(
            @RequestParam int row, @RequestParam int col) {
        gameService.toggleFlag(row, col);
        return ResponseEntity.ok(gameService.getGameDTO());
    }
}
