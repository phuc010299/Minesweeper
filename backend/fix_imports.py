import os
import glob

replacements = {
    "package com.minesweeper.model.state;": "package com.minesweeper.pattern.state;",
    "import com.minesweeper.model.state.": "import com.minesweeper.pattern.state.",
    
    "import com.minesweeper.model.DifficultyConfig;": "import com.minesweeper.model.config.DifficultyConfig;",
    "import com.minesweeper.model.BeginnerConfig;": "import com.minesweeper.model.config.BeginnerConfig;",
    "import com.minesweeper.model.IntermediateConfig;": "import com.minesweeper.model.config.IntermediateConfig;",
    "import com.minesweeper.model.ExpertConfig;": "import com.minesweeper.model.config.ExpertConfig;"
}

for filepath in glob.glob("/Users/mac/workspace/Study /khoa-hoc-may-tinh/ky-02/phan-tich-thiet-ke-huong-doi-tuong/Cuoi-ky/Minesweeper/backend/src/**/*.java", recursive=True):
    with open(filepath, 'r') as file:
        content = file.read()
        
    original_content = content
    
    for old, new in replacements.items():
        content = content.replace(old, new)
        
    if content != original_content:
        with open(filepath, 'w') as file:
            file.write(content)
        print(f"Fixed: {filepath}")

