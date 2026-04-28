import os
import glob

# Mapping for package replacements
replacements = {
    # Package declarations
    "package com.minesweeper.state;": "package com.minesweeper.pattern.state;",
    "package com.minesweeper.strategy;": "package com.minesweeper.pattern.strategy;",
    "package com.minesweeper.command;": "package com.minesweeper.pattern.command;",
    "package com.minesweeper.factory;": "package com.minesweeper.pattern.factory;",
    "package com.minesweeper.observer;": "package com.minesweeper.pattern.observer;",
    "package com.minesweeper.timer;": "package com.minesweeper.utils;",

    # Imports
    "import com.minesweeper.state.": "import com.minesweeper.pattern.state.",
    "import com.minesweeper.strategy.": "import com.minesweeper.pattern.strategy.",
    "import com.minesweeper.command.": "import com.minesweeper.pattern.command.",
    "import com.minesweeper.factory.": "import com.minesweeper.pattern.factory.",
    "import com.minesweeper.observer.": "import com.minesweeper.pattern.observer.",
    "import com.minesweeper.timer.": "import com.minesweeper.utils.",

    "import com.minesweeper.model.DifficultyConfig;": "import com.minesweeper.model.config.DifficultyConfig;",
    "import com.minesweeper.model.BeginnerConfig;": "import com.minesweeper.model.config.BeginnerConfig;",
    "import com.minesweeper.model.IntermediateConfig;": "import com.minesweeper.model.config.IntermediateConfig;",
    "import com.minesweeper.model.ExpertConfig;": "import com.minesweeper.model.config.ExpertConfig;"
}

config_files = ["DifficultyConfig.java", "BeginnerConfig.java", "IntermediateConfig.java", "ExpertConfig.java"]

for filepath in glob.glob("/Users/mac/workspace/Study /khoa-hoc-may-tinh/ky-02/phan-tich-thiet-ke-huong-doi-tuong/Cuoi-ky/Minesweeper/backend/src/**/*.java", recursive=True):
    with open(filepath, 'r') as file:
        content = file.read()
        
    original_content = content
    
    for old, new in replacements.items():
        content = content.replace(old, new)
        
    filename = os.path.basename(filepath)
    if filename in config_files:
        content = content.replace("package com.minesweeper.model;", "package com.minesweeper.model.config;")
        
    if content != original_content:
        with open(filepath, 'w') as file:
            file.write(content)
        print(f"Updated: {filepath}")

