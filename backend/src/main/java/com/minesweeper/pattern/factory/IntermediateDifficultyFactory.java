package com.minesweeper.pattern.factory;

import com.minesweeper.model.config.DifficultyConfig;

/**
 * Concrete Creator cho mức độ Intermediate.
 */
public class IntermediateDifficultyFactory implements DifficultyFactory {
    @Override
    public DifficultyConfig createDifficulty() {
        return new com.minesweeper.model.config.IntermediateConfig();
    }
}
