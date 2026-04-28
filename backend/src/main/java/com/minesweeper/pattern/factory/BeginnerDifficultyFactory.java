package com.minesweeper.pattern.factory;

import com.minesweeper.model.config.DifficultyConfig;

/**
 * Concrete Creator cho mức độ Beginner.
 */
public class BeginnerDifficultyFactory implements DifficultyFactory {
    @Override
    public DifficultyConfig createDifficulty() {
        return new com.minesweeper.model.config.BeginnerConfig();
    }
}
