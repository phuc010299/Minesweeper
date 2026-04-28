import os
import shutil

src_base = "/Users/mac/workspace/Study /khoa-hoc-may-tinh/ky-02/phan-tich-thiet-ke-huong-doi-tuong/Cuoi-ky/Minesweeper/backend/src/main/java/com/minesweeper"

moves = {
    "model/state": "pattern/state",
    "strategy": "pattern/strategy",
    "command": "pattern/command",
    "factory": "pattern/factory",
    "observer": "pattern/observer",
    "timer": "utils"
}

for old, new in moves.items():
    old_path = os.path.join(src_base, old)
    new_path = os.path.join(src_base, new)
    if os.path.exists(old_path):
        os.makedirs(os.path.dirname(new_path), exist_ok=True)
        shutil.move(old_path, new_path)
        print(f"Moved {old_path} to {new_path}")

config_files = ["DifficultyConfig.java", "BeginnerConfig.java", "IntermediateConfig.java", "ExpertConfig.java"]
os.makedirs(os.path.join(src_base, "model/config"), exist_ok=True)
for f in config_files:
    old_path = os.path.join(src_base, "model", f)
    new_path = os.path.join(src_base, "model/config", f)
    if os.path.exists(old_path):
        shutil.move(old_path, new_path)
        print(f"Moved {old_path} to {new_path}")

