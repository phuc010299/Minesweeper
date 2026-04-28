# Đặc tả Design Patterns cho Biểu đồ UML - Dự án Minesweeper

Tài liệu này mô tả chi tiết **4 Design Patterns** được áp dụng trong hệ thống game Minesweeper (Backend Spring Boot + Frontend React). Mục đích: cung cấp đủ thông tin để vẽ chính xác các **biểu đồ lớp UML (Class Diagram)** cho từng pattern.

**Quy ước ký hiệu:**
- `+` = public, `-` = private, `#` = protected, `~` = package-private
- `<<interface>>` = Java Interface
- Mũi tên nét đứt đầu tam giác rỗng = implements (realization)
- Mũi tên nét liền đầu tam giác rỗng = extends (generalization)
- Mũi tên nét liền đầu mũi tên thường = association / dependency
- Mũi tên nét liền đầu hình thoi đặc = composition
- Mũi tên nét liền đầu hình thoi rỗng = aggregation

---

## TỔNG QUAN HỆ THỐNG

Hệ thống gồm các package chính:
- `com.minesweeper.controller` — REST API endpoints
- `com.minesweeper.service` — Business logic
- `com.minesweeper.model` — Domain entities (Board, Cell)
- `com.minesweeper.model.config` — Cấu hình độ khó
- `com.minesweeper.dto` — Data Transfer Objects
- `com.minesweeper.pattern.state` — State Pattern
- `com.minesweeper.pattern.strategy` — Strategy Pattern
- `com.minesweeper.pattern.factory` — Factory Method Pattern
- `com.minesweeper.utils` — Tiện ích (GameTimer)

---

## 1. STATE PATTERN (Behavioral)

**Mục đích:** Quản lý vòng đời ván game. Hành vi của các thao tác (click mở ô, cắm cờ, chord) thay đổi tuỳ theo trạng thái hiện tại của game (Sẵn sàng / Đang chơi / Thắng / Thua).

### Các lớp tham gia:

#### 1.1. `<<interface>> GameState` (State)
- Package: `com.minesweeper.pattern.state`
- Vai trò trong pattern: **State** (giao diện trạng thái trừu tượng)
- Methods:
  - `+ revealCell(service: GameService, row: int, col: int): boolean`
  - `+ toggleFlag(service: GameService, row: int, col: int): boolean`
  - `+ chordCell(service: GameService, row: int, col: int): boolean`
  - `+ getName(): String`

#### 1.2. `ReadyState` (Concrete State)
- Package: `com.minesweeper.pattern.state`
- Vai trò: **Concrete State** — trạng thái ban đầu, chưa click lần nào
- Implements: `GameState`
- Logic đặc biệt: Khi `revealCell()` được gọi, nó sẽ:
  1. Gọi `service.initFirstClick(row, col)` để sinh mìn (đảm bảo ô đầu tiên an toàn)
  2. Chuyển trạng thái sang `PlayingState` bằng `service.changeState(new PlayingState())`
  3. Gọi `service.doRevealCell(row, col)` để mở ô
- `toggleFlag()`: cho phép cắm cờ (gọi `service.doToggleFlag()`)
- `chordCell()`: trả về `false` (chưa có ô nào mở nên không chord được)
- `getName()`: trả về `"READY"`

#### 1.3. `PlayingState` (Concrete State)
- Package: `com.minesweeper.pattern.state`
- Implements: `GameState`
- Vai trò: **Concrete State** — trạng thái đang chơi
- Tất cả methods đều delegate trực tiếp cho `service.doRevealCell()`, `service.doToggleFlag()`, `service.doChordCell()`
- `getName()`: trả về `"PLAYING"`

#### 1.4. `WonState` (Concrete State)
- Package: `com.minesweeper.pattern.state`
- Implements: `GameState`
- Vai trò: **Concrete State** — trạng thái thắng
- Tất cả `revealCell`, `toggleFlag`, `chordCell` đều trả về `false` (không cho thao tác)
- `getName()`: trả về `"WON"`

#### 1.5. `LostState` (Concrete State)
- Package: `com.minesweeper.pattern.state`
- Implements: `GameState`
- Vai trò: **Concrete State** — trạng thái thua
- Tất cả methods đều trả về `false`
- `getName()`: trả về `"LOST"`

#### 1.6. `GameService` (Context)
- Package: `com.minesweeper.service`
- Vai trò: **Context** — lưu giữ trạng thái hiện tại và uỷ quyền hành vi
- Attributes liên quan:
  - `- gameState: GameState` ← đây là tham chiếu đến State hiện tại
- Methods liên quan:
  - `+ changeState(newState: GameState): void` — chuyển trạng thái
  - `+ processRevealCell(row: int, col: int): boolean` — delegate cho `gameState.revealCell(this, row, col)`
  - `+ processToggleFlag(row: int, col: int): boolean` — delegate cho `gameState.toggleFlag(this, row, col)`
  - `+ processChordCell(row: int, col: int): boolean` — delegate cho `gameState.chordCell(this, row, col)`

### Quan hệ UML:
- `GameService` ——(association)——> `GameState` (thuộc tính `gameState`, multiplicity 1)
- `ReadyState` - - -▷ `GameState` (implements)
- `PlayingState` - - -▷ `GameState` (implements)
- `WonState` - - -▷ `GameState` (implements)
- `LostState` - - -▷ `GameState` (implements)
- Tất cả Concrete State đều nhận `GameService` làm tham số trong mỗi method (dependency ngược về Context)

### Sơ đồ chuyển trạng thái:
```
[ReadyState] ---(revealCell)--> [PlayingState]
[PlayingState] ---(mở trúng mìn)--> [LostState]
[PlayingState] ---(mở hết ô an toàn)--> [WonState]
[WonState] --- (final state, không chuyển tiếp)
[LostState] --- (final state, không chuyển tiếp)
Bất kỳ trạng thái nào ---(newGame())--> [ReadyState]
```

---

## 2. STRATEGY PATTERN (Behavioral)

**Mục đích:** Cho phép thay đổi thuật toán rải mìn lên bảng mà không ảnh hưởng đến logic game. Hiện có 2 thuật toán: rải ngẫu nhiên thuần tuý và rải có vùng an toàn quanh click đầu tiên.

### Các lớp tham gia:

#### 2.1. `<<interface>> MineGenerationStrategy` (Strategy)
- Package: `com.minesweeper.pattern.strategy`
- Vai trò: **Strategy** (giao diện chiến lược)
- Methods:
  - `+ generateMines(grid: Cell[][], mineCount: int, excludeRow: int, excludeCol: int): void`

#### 2.2. `RandomMineStrategy` (Concrete Strategy)
- Package: `com.minesweeper.pattern.strategy`
- Implements: `MineGenerationStrategy`
- Vai trò: **Concrete Strategy** — rải mìn ngẫu nhiên, chỉ tránh đúng 1 ô click đầu
- Logic: Tạo danh sách tất cả vị trí trừ ô (excludeRow, excludeCol), shuffle rồi chọn `mineCount` vị trí đầu tiên

#### 2.3. `SafeFirstClickStrategy` (Concrete Strategy)
- Package: `com.minesweeper.pattern.strategy`
- Implements: `MineGenerationStrategy`
- Vai trò: **Concrete Strategy** — rải mìn nhưng đảm bảo vùng 3×3 quanh click đầu tiên không có mìn
- Methods riêng:
  - `- isSafeZone(row: int, col: int, excludeRow: int, excludeCol: int): boolean`

#### 2.4. `GameService` (Context)
- Vai trò: **Context** — chọn và sử dụng strategy
- Attributes liên quan:
  - `- mineStrategy: MineGenerationStrategy` ← tham chiếu đến strategy hiện tại
- Methods liên quan:
  - `+ initFirstClick(row: int, col: int): void` — gọi `mineStrategy.generateMines(...)`

### Quan hệ UML:
- `GameService` ——(association)——> `MineGenerationStrategy` (thuộc tính `mineStrategy`, multiplicity 1)
- `RandomMineStrategy` - - -▷ `MineGenerationStrategy` (implements)
- `SafeFirstClickStrategy` - - -▷ `MineGenerationStrategy` (implements)
- Cả 2 Concrete Strategy đều có dependency đến `Cell` (thao tác trên `Cell[][]`)

---

## 3. FACTORY METHOD PATTERN (Creational)

**Mục đích:** Tạo các đối tượng cấu hình độ khó (số hàng, số cột, số mìn) mà không cần dùng `if-else` dài dòng. Mỗi mức độ khó có một Factory riêng tạo ra Product tương ứng.

### Các lớp tham gia:

#### 3.1. `<<interface>> DifficultyFactory` (Creator)
- Package: `com.minesweeper.pattern.factory`
- Vai trò: **Creator** (nhà máy trừu tượng)
- Methods:
  - `+ createDifficulty(): DifficultyConfig` ← **Factory Method**

#### 3.2. `BeginnerDifficultyFactory` (Concrete Creator)
- Package: `com.minesweeper.pattern.factory`
- Implements: `DifficultyFactory`
- `createDifficulty()` trả về `new BeginnerConfig()`

#### 3.3. `IntermediateDifficultyFactory` (Concrete Creator)
- Package: `com.minesweeper.pattern.factory`
- Implements: `DifficultyFactory`
- `createDifficulty()` trả về `new IntermediateConfig()`

#### 3.4. `ExpertDifficultyFactory` (Concrete Creator)
- Package: `com.minesweeper.pattern.factory`
- Implements: `DifficultyFactory`
- `createDifficulty()` trả về `new ExpertConfig()`

#### 3.5. `<<interface>> DifficultyConfig` (Product)
- Package: `com.minesweeper.model.config`
- Vai trò: **Product** (sản phẩm trừu tượng)
- Methods:
  - `+ getName(): String`
  - `+ getRows(): int`
  - `+ getCols(): int`
  - `+ getMineCount(): int`

#### 3.6. `BeginnerConfig` (Concrete Product)
- Package: `com.minesweeper.model.config`
- Implements: `DifficultyConfig`
- Giá trị: name="Beginner", rows=9, cols=9, mineCount=10

#### 3.7. `IntermediateConfig` (Concrete Product)
- Package: `com.minesweeper.model.config`
- Implements: `DifficultyConfig`
- Giá trị: name="Intermediate", rows=16, cols=16, mineCount=40

#### 3.8. `ExpertConfig` (Concrete Product)
- Package: `com.minesweeper.model.config`
- Implements: `DifficultyConfig`
- Giá trị: name="Expert", rows=16, cols=30, mineCount=99

### Quan hệ UML:
- `BeginnerDifficultyFactory` - - -▷ `DifficultyFactory` (implements)
- `IntermediateDifficultyFactory` - - -▷ `DifficultyFactory` (implements)
- `ExpertDifficultyFactory` - - -▷ `DifficultyFactory` (implements)
- `BeginnerConfig` - - -▷ `DifficultyConfig` (implements)
- `IntermediateConfig` - - -▷ `DifficultyConfig` (implements)
- `ExpertConfig` - - -▷ `DifficultyConfig` (implements)
- Mỗi Concrete Creator ——(dependency/creates)——> Concrete Product tương ứng (BeginnerDifficultyFactory creates BeginnerConfig, v.v.)
- `DifficultyFactory` ——(dependency)——> `DifficultyConfig` (return type của factory method)
- `GameService` sử dụng `DifficultyFactory` trong method `changeDifficulty()` (dependency)

---

## 4. SINGLETON PATTERN (Creational — qua Spring IoC)

**Mục đích:** Đảm bảo chỉ có duy nhất 1 instance của `GameService` và `GameTimer` trong toàn bộ vòng đời ứng dụng.

### Các lớp tham gia:

#### 4.1. `GameService`
- Annotation: `@Service` (Spring tự đảm bảo singleton)
- Được inject vào `GameRestController` qua constructor `@Autowired`

#### 4.2. `GameTimer`
- Package: `com.minesweeper.utils`
- Annotation: `@Component` (Spring tự đảm bảo singleton)
- Được inject vào `GameService` qua constructor `@Autowired`

### Ghi chú cho biểu đồ:
- Trong biểu đồ lớp, có thể đánh dấu stereotype `<<singleton>>` lên `GameService` và `GameTimer`
- Không cần vẽ `private static instance` vì Spring IoC Container quản lý

---

## 5. BIỂU ĐỒ LỚP TỔNG THỂ (Class Diagram — Tất cả lớp)

Dưới đây mô tả ĐẦY ĐỦ thuộc tính và phương thức của tất cả các lớp trong hệ thống:

### 5.1. `GameRestController` (package: controller)
- Stereotype: `<<RestController>>`
- Attributes:
  - `- gameService: GameService`
- Methods:
  - `+ getGameState(): ResponseEntity<GameDTO>`
  - `+ newGame(difficulty: String): ResponseEntity<GameDTO>`
  - `+ handleClick(row: int, col: int): ResponseEntity<GameDTO>`
  - `+ handleFlag(row: int, col: int): ResponseEntity<GameDTO>`
- Quan hệ: association đến `GameService` (multiplicity 1), dependency đến `GameDTO`

### 5.2. `GameService` (package: service)
- Stereotype: `<<Service>>` `<<singleton>>`
- Attributes:
  - `- board: Board`
  - `- currentConfig: DifficultyConfig`
  - `- gameState: GameState`
  - `- mineStrategy: MineGenerationStrategy`
  - `- timer: GameTimer`
  - `- hitMineRow: int`
  - `- hitMineCol: int`
  - `- DIRECTIONS: int[][]` (static final)
- Methods (public):
  - `+ newGame(): void`
  - `+ changeDifficulty(difficulty: String): void`
  - `+ revealOrChordCell(row: int, col: int): void`
  - `+ toggleFlag(row: int, col: int): void`
  - `+ getGameDTO(): GameDTO`
  - `+ processRevealCell(row: int, col: int): boolean`
  - `+ processToggleFlag(row: int, col: int): boolean`
  - `+ processChordCell(row: int, col: int): boolean`
  - `+ changeState(newState: GameState): void`
  - `+ initFirstClick(row: int, col: int): void`
  - `+ doRevealCell(row: int, col: int): boolean`
  - `+ doToggleFlag(row: int, col: int): boolean`
  - `+ doChordCell(row: int, col: int): boolean`
  - `+ isValidPosition(row: int, col: int): boolean`
  - `+ getBoard(): Board`
  - `+ getGameState(): GameState`
  - `+ isFirstClick(): boolean`
  - `+ getRemainingMines(): int`
- Methods (private):
  - `- floodFill(row: int, col: int): void`
  - `- calculateAdjacentMines(): void`
  - `- countAdjacentFlags(row: int, col: int): int`
  - `- revealAllMines(): void`
  - `- autoFlagAllMines(): void`
- Methods (protected):
  - `# checkWinCondition(): void`

### 5.3. `Board` (package: model)
- Attributes:
  - `- grid: Cell[][]` (final)
  - `- config: DifficultyConfig` (final)
  - `- flagCount: int`
  - `- revealedCount: int`
- Methods:
  - `+ Board(config: DifficultyConfig)` — constructor
  - `+ getCell(row: int, col: int): Cell`
  - `+ getGrid(): Cell[][]`
  - `+ getConfig(): DifficultyConfig`
  - `+ getFlagCount(): int`
  - `+ incrementFlagCount(): void`
  - `+ decrementFlagCount(): void`
  - `+ getRevealedCount(): int`
  - `+ incrementRevealedCount(): void`
- Quan hệ: **composition** đến `Cell` (chứa Cell[][], multiplicity *), association đến `DifficultyConfig`

### 5.4. `Cell` (package: model)
- Attributes:
  - `- mine: boolean`
  - `- revealed: boolean`
  - `- flagged: boolean`
  - `- adjacentMines: int`
  - `- row: int` (final)
  - `- col: int` (final)
- Methods:
  - `+ Cell(row: int, col: int)` — constructor
  - `+ isMine(): boolean`
  - `+ isRevealed(): boolean`
  - `+ isFlagged(): boolean`
  - `+ getAdjacentMines(): int`
  - `+ getRow(): int`
  - `+ getCol(): int`
  - `+ setMine(mine: boolean): void`
  - `+ setAdjacentMines(count: int): void`
  - `+ reveal(): boolean`
  - `+ toggleFlag(): boolean`
  - `+ reset(): void`
  - `+ isEmpty(): boolean`
  - `+ toString(): String`

### 5.5. `GameDTO` (package: dto)
- Attributes:
  - `- rows: int`
  - `- cols: int`
  - `- gameState: String`
  - `- remainingMines: int`
  - `- elapsedSeconds: int`
  - `- difficulty: String`
  - `- cells: CellDTO[][]`
  - `- hitMineRow: int`
  - `- hitMineCol: int`
- Methods: getters/setters cho tất cả attributes
- Quan hệ: **composition** đến `CellDTO` (chứa CellDTO[][], multiplicity *)

### 5.6. `CellDTO` (package: dto)
- Attributes:
  - `- row: int`
  - `- col: int`
  - `- revealed: boolean`
  - `- flagged: boolean`
  - `- mine: boolean`
  - `- adjacentMines: int`
- Methods: getters/setters cho tất cả attributes

### 5.7. `GameTimer` (package: utils)
- Stereotype: `<<Component>>` `<<singleton>>`
- Attributes:
  - `- startTimeMillis: long`
  - `- frozenSeconds: int`
  - `- running: boolean`
  - `- MAX_TIME: int = 999` (static final)
- Methods:
  - `+ start(): void`
  - `+ stop(): void`
  - `+ reset(): void`
  - `+ getElapsedSeconds(): int`
  - `+ isRunning(): boolean`

### Quan hệ tổng thể giữa các lớp:
```
GameRestController ——> GameService (association, 1)
GameService ——> Board (association, 1)
GameService ——> GameState (association, 1) [State Pattern]
GameService ——> MineGenerationStrategy (association, 1) [Strategy Pattern]
GameService ——> GameTimer (association, 1)
GameService ··> DifficultyFactory (dependency) [Factory Pattern]
GameService ··> GameDTO (dependency, creates)
Board ◆——> Cell (composition, *)
Board ——> DifficultyConfig (association, 1)
GameDTO ◆——> CellDTO (composition, *)
```

---

## 6. BIỂU ĐỒ CA SỬ DỤNG (Use Case Diagram)

### Actors (Tác nhân):
- **Người chơi (Player)** — Tác nhân chính, tương tác với game qua giao diện web

### Use Cases:

| # | Tên Use Case | Mô tả |
|---|---|---|
| UC01 | Bắt đầu ván mới (New Game) | Người chơi nhấn nút New Game để reset bảng và chọn lại độ khó |
| UC02 | Chọn độ khó (Select Difficulty) | Người chơi chọn 1 trong 3 mức: Beginner (9×9, 10 mìn), Intermediate (16×16, 40 mìn), Expert (16×30, 99 mìn) |
| UC03 | Mở ô (Reveal Cell) | Người chơi click trái vào ô chưa mở để lật ô đó, có thể kích hoạt flood-fill nếu ô trống |
| UC04 | Cắm/Gỡ cờ (Toggle Flag) | Người chơi click phải vào ô chưa mở để đánh dấu nghi ngờ mìn, hoặc gỡ cờ |
| UC05 | Chord (Mở nhanh) | Khi ô đã mở có số = số cờ xung quanh, click trái vào ô đó sẽ tự động mở tất cả ô lân cận chưa cắm cờ |
| UC06 | Xem trạng thái game | Người chơi xem bộ đếm mìn còn lại, đồng hồ thời gian, trạng thái thắng/thua |

### Quan hệ giữa các Use Case:
- UC01 `<<include>>` UC02 (Bắt đầu ván mới bao gồm việc chọn độ khó)
- UC03 `<<extend>>` UC05 (Mở ô có thể mở rộng thành Chord nếu ô đã revealed)
- UC03, UC04, UC05 đều cần kiểm tra trạng thái game (nếu WON/LOST thì từ chối)

### Cách vẽ:
- Hình chữ nhật bao quanh = System boundary "Minesweeper Game"
- Stick figure bên trái = Player
- Mỗi Use Case là hình oval bên trong System boundary
- Đường thẳng nối Player với từng Use Case
- Mũi tên nét đứt kèm `<<include>>` từ UC01 đến UC02
- Mũi tên nét đứt kèm `<<extend>>` từ UC05 đến UC03

---

## 7. MÔ HÌNH KHÁI NIỆM (Conceptual/Domain Model)

Mô hình khái niệm mô tả các thực thể nghiệp vụ cốt lõi trong bài toán dò mìn, KHÔNG liên quan đến code implementation. Chỉ thể hiện các khái niệm và quan hệ trong thế giới thực.

### Các lớp khái niệm (Conceptual Classes):

#### Game (Ván chơi)
- difficulty: String (Beginner / Intermediate / Expert)
- state: String (Ready / Playing / Won / Lost)
- elapsedTime: int (giây)
- remainingMines: int

#### Board (Bảng)
- rows: int
- cols: int
- totalMines: int

#### Cell (Ô)
- row: int
- col: int
- hasMine: boolean
- isRevealed: boolean
- isFlagged: boolean
- adjacentMineCount: int

#### DifficultyLevel (Mức độ khó)
- name: String
- rows: int
- cols: int
- mineCount: int

#### Player (Người chơi)
- (không có thuộc tính đặc biệt trong single-player)

### Quan hệ (Associations):
```
Player ——"chơi"——> Game              (1 — 1)
Game ——"có"——> Board                 (1 — 1)
Game ——"thuộc"——> DifficultyLevel    (1 — 1)
Board ——"chứa"——> Cell               (1 — *) (composition)
```

### Cách vẽ:
- Mỗi lớp khái niệm là hình chữ nhật 2 ngăn (tên + thuộc tính, KHÔNG có phương thức)
- Đường thẳng nối các lớp, ghi tên quan hệ ở giữa, ghi multiplicity ở 2 đầu
- Board ◆——> Cell: dùng composition (hình thoi đặc) vì Cell không tồn tại nếu không có Board

---

## 8. CÁC BIỂU ĐỒ HOẠT ĐỘNG (Activity Diagrams)

### 8.1. Hoạt động: Bắt đầu ván mới (New Game)
```
(●) Start
  ↓
[Người chơi nhấn New Game]
  ↓
[Người chơi chọn độ khó: Beginner / Intermediate / Expert]
  ↓
[Controller nhận POST /api/game/new?difficulty=X]
  ↓
[GameService.changeDifficulty(X)]
  ↓
[DifficultyFactory tạo DifficultyConfig tương ứng]  ← Factory Method Pattern
  ↓
[Tạo Board mới với config (khởi tạo Cell[][] rỗng)]
  ↓
[Đặt GameState = ReadyState]  ← State Pattern
  ↓
[Reset Timer về 0]
  ↓
[Xây dựng GameDTO và trả JSON về Frontend]
  ↓
[Frontend render bảng trống]
  ↓
(◉) End
```

### 8.2. Hoạt động: Mở ô (Reveal Cell) — Chi tiết
```
(●) Start
  ↓
[Người chơi click trái vào ô (row, col)]
  ↓
[Controller nhận POST /api/game/click?row=X&col=Y]
  ↓
[GameService.revealOrChordCell(row, col)]
  ↓
◇ Decision: Ô đã được mở (revealed)?
  |—— Có → [processChordCell(row, col)] → (xem biểu đồ Chord)
  |—— Không ↓
◇ Decision: Ô đã cắm cờ (flagged)?
  |—— Có → [Không làm gì, trả về DTO hiện tại] → (◉)
  |—— Không ↓
◇ Decision: Đây là click đầu tiên? (gameState == ReadyState)
  |—— Có → [Bắt đầu Timer] → [initFirstClick: Sinh mìn bằng Strategy Pattern]
  |         → [Tính adjacentMines cho tất cả ô] → [Chuyển sang PlayingState]
  |—— Không (đã là PlayingState) ↓
  ↓ (merge)
[doRevealCell(row, col)]
  ↓
[cell.reveal() → đánh dấu ô đã mở]
[board.incrementRevealedCount()]
  ↓
◇ Decision: Ô có mìn?
  |—— Có → [changeState(LostState)] → [revealAllMines()] → [Dừng Timer]
  |         → [Lưu hitMineRow/Col] → (◉)
  |—— Không ↓
◇ Decision: Ô trống (adjacentMines == 0)?
  |—— Có → [floodFill(): đệ quy mở tất cả ô lân cận không phải mìn]
  |—— Không → (tiếp tục)
  ↓ (merge)
[checkWinCondition()]
  ↓
◇ Decision: Tất cả ô an toàn đã mở?
  |—— Có → [changeState(WonState)] → [autoFlagAllMines()] → [Dừng Timer]
  |—— Không → (tiếp tục)
  ↓ (merge)
[Xây dựng GameDTO] → [Trả JSON về Frontend]
  ↓
(◉) End
```

### 8.3. Hoạt động: Cắm/Gỡ cờ (Toggle Flag)
```
(●) Start
  ↓
[Người chơi click phải vào ô (row, col)]
  ↓
[Controller nhận POST /api/game/flag?row=X&col=Y]
  ↓
[GameService.toggleFlag(row, col)]
  ↓
[processToggleFlag() → delegate cho GameState hiện tại]
  ↓
◇ Decision: GameState cho phép? (ReadyState hoặc PlayingState)
  |—— Không (WonState/LostState) → [return false] → (◉)
  |—— Có ↓
◇ Decision: Ô đã revealed?
  |—— Có → [return false] → (◉)
  |—— Không ↓
[cell.toggleFlag() — đảo trạng thái cờ]
  ↓
◇ Decision: Trước đó có cờ?
  |—— Có → [board.decrementFlagCount()] (gỡ cờ)
  |—— Không → [board.incrementFlagCount()] (cắm cờ)
  ↓ (merge)
[Xây dựng GameDTO (cập nhật remainingMines)] → [Trả JSON]
  ↓
(◉) End
```

---

## 9. BIỂU ĐỒ TRẠNG THÁI (State Diagram)

Biểu đồ trạng thái cho đối tượng **GameState** (vòng đời của một ván chơi):

```
                    newGame() / changeDifficulty()
           ┌──────────────────────────────────────────────┐
           ↓                                              |
(●) ──→ [ReadyState]                                      |
           |                                              |
           | revealCell() (click đầu tiên)                |
           | → initFirstClick() → sinh mìn                |
           | → changeState(PlayingState)                   |
           ↓                                              |
        [PlayingState] ──────────────────────────→ newGame()
           |          |
           |          | revealCell() → trúng mìn
           |          | → changeState(LostState)
           |          ↓
           |       [LostState] ──→ newGame() ──→ [ReadyState]
           |          (final state: mọi thao tác bị từ chối)
           |
           | checkWinCondition() → đủ ô an toàn đã mở
           | → changeState(WonState)
           ↓
        [WonState] ──→ newGame() ──→ [ReadyState]
           (final state: mọi thao tác bị từ chối)
```

### Bảng chuyển trạng thái:

| Trạng thái hiện tại | Sự kiện / Điều kiện | Trạng thái mới |
|---|---|---|
| ReadyState | revealCell() — click đầu tiên | PlayingState |
| PlayingState | doRevealCell() — trúng mìn | LostState |
| PlayingState | checkWinCondition() — mở hết ô an toàn | WonState |
| WonState | newGame() | ReadyState |
| LostState | newGame() | ReadyState |
| ReadyState | newGame() | ReadyState |

### Hành vi tại mỗi trạng thái:

| Trạng thái | revealCell | toggleFlag | chordCell |
|---|---|---|---|
| ReadyState | Sinh mìn → chuyển Playing → mở ô | Cho phép | Từ chối (false) |
| PlayingState | Mở ô bình thường | Cho phép | Cho phép |
| WonState | Từ chối (false) | Từ chối (false) | Từ chối (false) |
| LostState | Từ chối (false) | Từ chối (false) | Từ chối (false) |

---

## 10. BIỂU ĐỒ TƯƠNG TÁC / TUẦN TỰ (Sequence Diagrams)

### 10.1. Sequence Diagram: Mở ô lần đầu tiên (First Click)

Các đối tượng tham gia (lifelines):
`Player`, `Frontend`, `GameRestController`, `GameService`, `ReadyState`, `SafeFirstClickStrategy`, `Board`, `Cell`

```
Player → Frontend: click trái vào ô (row=4, col=4)
Frontend → GameRestController: POST /api/game/click?row=4&col=4
GameRestController → GameService: revealOrChordCell(4, 4)
  GameService → Board: getCell(4, 4)
  Board → GameService: cell (chưa revealed, chưa flagged)
  GameService → GameService: isFirstClick() → true (gameState là ReadyState)
  GameService → GameTimer: start()
  GameService → ReadyState: revealCell(this, 4, 4)
    ReadyState → GameService: initFirstClick(4, 4)
      GameService → SafeFirstClickStrategy: generateMines(grid, mineCount, 4, 4)
        SafeFirstClickStrategy → Cell: setMine(true) [lặp cho mỗi vị trí mìn]
      GameService → GameService: calculateAdjacentMines()
        GameService → Cell: setAdjacentMines(count) [lặp cho mỗi ô]
    ReadyState → GameService: changeState(new PlayingState())
    ReadyState → GameService: doRevealCell(4, 4)
      GameService → Cell: reveal()
      GameService → Board: incrementRevealedCount()
      GameService → GameService: floodFill(4, 4) [nếu ô trống]
      GameService → GameService: checkWinCondition()
GameRestController → GameService: getGameDTO()
  GameService → GameService: xây dựng GameDTO từ Board
GameRestController → Frontend: ResponseEntity<GameDTO> (JSON)
Frontend → Player: render bảng game đã cập nhật
```

### 10.2. Sequence Diagram: Mở ô bình thường (Playing — trúng số)

Lifelines: `Player`, `Frontend`, `GameRestController`, `GameService`, `PlayingState`, `Board`, `Cell`

```
Player → Frontend: click trái vào ô (row=2, col=3)
Frontend → GameRestController: POST /api/game/click?row=2&col=3
GameRestController → GameService: revealOrChordCell(2, 3)
  GameService → Board: getCell(2, 3)
  Board → GameService: cell (chưa revealed)
  GameService → GameService: isFirstClick() → false
  GameService → PlayingState: revealCell(this, 2, 3)
    PlayingState → GameService: doRevealCell(2, 3)
      GameService → Cell: reveal() → true
      GameService → Board: incrementRevealedCount()
      GameService → Cell: isEmpty() → false (có số)
      GameService → GameService: checkWinCondition() → chưa thắng
GameRestController → GameService: getGameDTO()
GameRestController → Frontend: JSON response
```

### 10.3. Sequence Diagram: Mở ô trúng mìn (Game Over)

Lifelines: `Player`, `Frontend`, `GameRestController`, `GameService`, `PlayingState`, `Board`, `Cell`

```
Player → Frontend: click trái vào ô (row=1, col=5)
Frontend → GameRestController: POST /api/game/click?row=1&col=5
GameRestController → GameService: revealOrChordCell(1, 5)
  GameService → Board: getCell(1, 5)
  GameService → PlayingState: revealCell(this, 1, 5)
    PlayingState → GameService: doRevealCell(1, 5)
      GameService → Cell: reveal()
      GameService → Board: incrementRevealedCount()
      GameService → Cell: isMine() → true
      GameService → GameService: changeState(new LostState())
      GameService → GameService: revealAllMines()
        [loop] GameService → Cell: reveal() [cho mỗi ô mìn chưa mở]
  GameService → GameTimer: stop()
  GameService → GameService: hitMineRow=1, hitMineCol=5
GameRestController → GameService: getGameDTO()
GameRestController → Frontend: JSON (gameState="LOST")
Frontend → Player: hiển thị tất cả mìn, highlight ô vừa click đỏ
```

### 10.4. Sequence Diagram: Bắt đầu ván mới (New Game)

Lifelines: `Player`, `Frontend`, `GameRestController`, `GameService`, `DifficultyFactory`, `DifficultyConfig`, `Board`

```
Player → Frontend: chọn "Expert" rồi nhấn New Game
Frontend → GameRestController: POST /api/game/new?difficulty=expert
GameRestController → GameService: changeDifficulty("expert")
  GameService → ExpertDifficultyFactory: <<create>>
  GameService → ExpertDifficultyFactory: createDifficulty()
  ExpertDifficultyFactory → ExpertConfig: <<create>>
  ExpertDifficultyFactory → GameService: expertConfig (rows=16, cols=30, mines=99)
  GameService → GameService: newGame()
    GameService → Board: <<create>> Board(expertConfig)
      Board → Cell: <<create>> Cell(r, c) [lặp 16×30 = 480 lần]
    GameService → GameService: gameState = new ReadyState()
    GameService → GameTimer: reset()
GameRestController → GameService: getGameDTO()
GameRestController → Frontend: JSON (gameState="READY", rows=16, cols=30)
Frontend → Player: render bảng trống 16×30
```

### 10.5. Sequence Diagram: Cắm cờ (Flag)

Lifelines: `Player`, `Frontend`, `GameRestController`, `GameService`, `PlayingState`, `Board`, `Cell`

```
Player → Frontend: click phải vào ô (row=3, col=7)
Frontend → GameRestController: POST /api/game/flag?row=3&col=7
GameRestController → GameService: toggleFlag(3, 7)
  GameService → PlayingState: toggleFlag(this, 3, 7)
    PlayingState → GameService: doToggleFlag(3, 7)
      GameService → Board: getCell(3, 7)
      GameService → Cell: isRevealed() → false
      GameService → Cell: isFlagged() → false
      GameService → Cell: toggleFlag() → true (giờ flagged=true)
      GameService → Board: incrementFlagCount()
GameRestController → GameService: getGameDTO()
GameRestController → Frontend: JSON (remainingMines giảm 1)
Frontend → Player: hiển thị cờ 🚩 trên ô
```

---

## 11. BIỂU ĐỒ GÓI (Package Diagram) — Chi tiết

### Các package và nội dung:

```
com.minesweeper
├── controller                          [Tầng Controller — API]
│   └── GameRestController
│
├── service                             [Tầng Service — Logic]
│   └── GameService
│
├── model                               [Tầng Model — Data]
│   ├── Board
│   ├── Cell
│   └── config                          [Sub-package cấu hình]
│       ├── <<interface>> DifficultyConfig
│       ├── BeginnerConfig
│       ├── IntermediateConfig
│       └── ExpertConfig
│
├── dto                                 [Data Transfer Objects]
│   ├── GameDTO
│   └── CellDTO
│
├── pattern                             [Design Patterns]
│   ├── state                           [State Pattern]
│   │   ├── <<interface>> GameState
│   │   ├── ReadyState
│   │   ├── PlayingState
│   │   ├── WonState
│   │   └── LostState
│   │
│   ├── strategy                        [Strategy Pattern]
│   │   ├── <<interface>> MineGenerationStrategy
│   │   ├── RandomMineStrategy
│   │   └── SafeFirstClickStrategy
│   │
│   └── factory                         [Factory Method Pattern]
│       ├── <<interface>> DifficultyFactory
│       ├── BeginnerDifficultyFactory
│       ├── IntermediateDifficultyFactory
│       └── ExpertDifficultyFactory
│
└── utils                               [Tiện ích]
    └── GameTimer
```

### Quan hệ phụ thuộc giữa các package:
```
[controller] ——depends on——> [service]
[controller] ——depends on——> [dto]
[service] ——depends on——> [model]
[service] ——depends on——> [model.config]
[service] ——depends on——> [dto]
[service] ——depends on——> [pattern.state]
[service] ——depends on——> [pattern.strategy]
[service] ——depends on——> [pattern.factory]
[service] ——depends on——> [utils]
[pattern.factory] ——depends on——> [model.config]
[pattern.state] ——depends on——> [service]  (dependency ngược — truyền GameService vào method)
[pattern.strategy] ——depends on——> [model]  (thao tác trên Cell)
[model] ——depends on——> [model.config]
```

Cách vẽ: Mỗi package là hình chữ nhật có tab ở góc trên trái ghi tên package. Mũi tên nét đứt = `<<depends on>>`.

---

## 12. BIỂU ĐỒ TRIỂN KHAI (Deployment Diagram)

### Các Node (Nút triển khai):

#### Node 1: `<<device>> Client Machine` (Máy người dùng)
- Bên trong chứa:
  - `<<execution environment>> Web Browser` (Chrome, Firefox, Safari...)
    - `<<artifact>> React Frontend App`
      - Framework: React + TailwindCSS
      - Build tool: Vite
      - Port: 5173 (dev mode)
      - Chức năng: Render giao diện, gửi request HTTP, hiển thị trạng thái game

#### Node 2: `<<device>> Server Machine` (Máy chủ)
- Bên trong chứa:
  - `<<execution environment>> JVM (Java 17)`
    - `<<artifact>> Spring Boot Application` (minesweeper-1.0-SNAPSHOT.jar)
      - Port: 8080
      - Components bên trong:
        - `<<component>> GameRestController` — Xử lý REST API
        - `<<component>> GameService` — Business Logic
        - `<<component>> Board + Cell` — Dữ liệu game

### Kết nối (Communication Paths):
```
[Web Browser] ←——HTTP/REST (JSON)——→ [JVM / Spring Boot]
```
- Protocol: HTTP
- Format: JSON (GameDTO)
- Endpoints:
  - `GET /api/game` — Lấy trạng thái game
  - `POST /api/game/new` — Tạo ván mới
  - `POST /api/game/click` — Mở ô
  - `POST /api/game/flag` — Cắm/gỡ cờ

Cách vẽ: 2 hình hộp 3D lồng nhau (device > execution environment > artifact). Đường nối giữa 2 node ghi `<<HTTP/REST>>`.

---

## 13. TÓM TẮT ÁNH XẠ BIỂU ĐỒ ↔ SOURCE CODE

| Biểu đồ | Ánh xạ tới source code |
|---|---|
| Use Case Diagram | Các endpoint trong `GameRestController` (newGame, click, flag) |
| Conceptual Model | `Board`, `Cell`, `DifficultyConfig` — các thực thể nghiệp vụ |
| Activity Diagram | Luồng xử lý trong `GameService` (revealOrChordCell, toggleFlag, newGame) |
| State Diagram | `GameState` interface + 4 Concrete States trong `pattern/state/` |
| Class Diagram | Tất cả class/interface trong dự án (xem Mục 1–5 ở trên) |
| Sequence Diagram | Luồng gọi hàm: Controller → Service → State → Board → Cell |
| Package Diagram | Cấu trúc thư mục `com.minesweeper.*` |
| Deployment Diagram | Frontend (React/Vite) + Backend (Spring Boot/JVM) |
