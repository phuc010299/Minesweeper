# Minesweeper Game - Architecture Document

## 1. Tổng Quan (Overview)

Game Minesweeper được xây dựng theo kiến trúc **Client-Server** với Backend là **Spring Boot (Java)** cung cấp RESTful API và Frontend là **React (TypeScript/TailwindCSS)**. 
Hệ thống Backend tuân thủ chặt chẽ kiến trúc **3-Tier Architecture** (Controller - Service - Model) kết hợp với các **GoF Design Patterns** cốt lõi để đảm bảo tính mở rộng, bảo trì và tái sử dụng mã nguồn, đồng thời tránh việc Over-engineering.

## 2. Kiến Trúc Tổng Thể (High-Level Architecture)

```
┌─────────────────────────────────────────────────────────────┐
│                       FRONTEND (React)                       │
│  ┌──────────────┐  ┌──────────────┐  ┌────────────────────┐ │
│  │  BoardGrid   │  │  GameHeader  │  │   ControlPanel     │ │
│  │  (Cells)     │  │  (Timer,     │  │   (Difficulty)     │ │
│  │              │  │   Mine count)│  │                    │ │
│  └──────────────┘  └──────────────┘  └────────────────────┘ │
└──────────────────────┬──────────────────────────────────────┘
                       │ HTTP REST (GET / POST) & JSON DTO
                       ▼
┌─────────────────────────────────────────────────────────────┐
│                    BACKEND (Spring Boot)                     │
│                                                             │
│  ┌──────────────────────────────────────────────────────┐   │
│  │               GameRestController (Controller)         │   │
│  │  - GET  /api/game                                     │   │
│  │  - POST /api/game/new?difficulty=...                  │   │
│  │  - POST /api/game/click?row=X&col=Y                   │   │
│  │  - POST /api/game/flag?row=X&col=Y                    │   │
│  └──────────────────────────┬───────────────────────────┘   │
│                             │ Giao việc (Delegate)          │
│                             ▼                               │
│  ┌──────────────────────────────────────────────────────┐   │
│  │                  GameService (Service)                │   │
│  │  - Chứa toàn bộ Business Logic                        │   │
│  │  - Quản lý Timer, GameState, MineStrategy             │   │
│  │  - Chuyển đổi Model -> GameDTO                        │   │
│  └──────────────────────────┬───────────────────────────┘   │
│                             │ Cập nhật Dữ liệu              │
│                             ▼                               │
│  ┌──────────────────────────────────────────────────────┐   │
│  │                     Board (Model)                     │   │
│  │  - Anemic Domain Model (Chứa data Cell[][])           │   │
│  │  - Lưu trữ cấu hình DifficultyConfig                  │   │
│  └──────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
```

## 3. Design Patterns Sử Dụng

*(Chi tiết xem thêm tại file `DESIGN_PATTERNS.md`)*

### 3.1. 3-Tier Layered Architecture
- **Controller**: `GameRestController` — Mỏng nhất có thể, chỉ nhận HTTP Request, gọi Service và trả về JSON Response.
- **Service**: `GameService` — "Trái tim" của ứng dụng, chứa toàn bộ thuật toán game, xử lý luật chơi, tính toán thắng thua.
- **Model**: `Board`, `Cell` — Lưu trữ trạng thái thô của ván cờ.

### 3.2. State Pattern (Behavioral)
- **Context**: `GameService`
- **States**: `GameState` interface với các class `ReadyState`, `PlayingState`, `WON`, `LOST`.
- Hành vi click/flag thay đổi linh hoạt tùy theo việc game đang ở trạng thái nào.

### 3.3. Strategy Pattern (Behavioral)
- **Interface**: `MineGenerationStrategy`
- **Concrete Strategies**: `RandomMineStrategy`, `SafeFirstClickStrategy` (mặc định, click đầu không nổ mìn).
- Dễ dàng thay đổi thuật toán rải mìn.

### 3.4. Factory Method Pattern (Creational)
- **Factory**: `DifficultyFactory`
- **Products**: `DifficultyConfig` (Beginner, Intermediate, Expert).
- Quản lý khởi tạo thông số bảng game (số dòng, số cột, lượng mìn).

### 3.5. Singleton Pattern (Creational qua Spring IoC)
- **`GameService`** và **`GameTimer`** được Spring khởi tạo 1 lần duy nhất (`@Service`, `@Component`), giúp lưu trữ chung một phiên bản của trò chơi trong toàn hệ thống.

*(Ghi chú: Command Pattern và Observer Pattern đã được loại bỏ để phù hợp với đặc thù Request-Response của REST API, giúp hệ thống không bị Over-engineering).*

## 4. Cấu Trúc Thư Mục (Package Structure)

```
backend/src/main/java/com/minesweeper/
├── controller/
│   └── GameRestController.java        # API Endpoints
├── service/
│   └── GameService.java               # Xử lý Core Logic & Mapping DTO
├── dto/
│   ├── GameDTO.java                   # Dữ liệu trả về cho Frontend
│   └── CellDTO.java                   # Dữ liệu từng ô vuông
├── model/
│   ├── Cell.java                      # Thực thể Ô
│   ├── Board.java                     # Thực thể Bảng
│   └── config/                        # Các cấu hình độ khó
│       ├── DifficultyConfig.java
│       ├── BeginnerConfig.java
│       └── ...
├── pattern/                           # Các Design Pattern
│   ├── factory/                       # Factory Method Pattern
│   ├── state/                         # State Pattern
│   └── strategy/                      # Strategy Pattern
└── utils/
    └── GameTimer.java                 # Đồng hồ đếm giờ
```

## 5. Flow Hoạt Động Cốt Lõi (API Flows)

### 5.1. Bắt đầu Game Mới (New Game Flow)
```
Frontend gửi POST /api/game/new?difficulty=expert
  → GameRestController nhận Request
  → GameService.changeDifficulty()
      → Dùng Factory tạo ExpertConfig
      → Khởi tạo Board mới, reset Timer
      → Trạng thái game = ReadyState
  → GameService xây dựng GameDTO
  → Trả về JSON cho Frontend vẽ bảng
```

### 5.2. Mở Ô / Chord (Reveal Flow)
```
Frontend gửi POST /api/game/click?row=X&col=Y
  → GameRestController
  → GameService.revealOrChordCell()
      → Kiểm tra nếu ô đã mở -> thực hiện Chord (mở nhanh xung quanh)
      → Nếu chưa mở -> uỷ quyền (delegate) cho GameState xử lý Reveal
        → Nếu ReadyState: Sinh mìn (SafeFirstClickStrategy), bắt đầu Timer, chuyển sang PlayingState
        → Mở ô, nếu là 0 (rỗng) -> Đệ quy Flood Fill mở các ô lân cận
        → Cập nhật trạng thái thắng/thua nếu trúng mìn
  → Xây dựng lại GameDTO & trả về Frontend
```

### 5.3. Cắm cờ (Flag Flow)
```
Frontend gửi POST /api/game/flag?row=X&col=Y
  → GameRestController
  → GameService.toggleFlag()
      → Uỷ quyền cho GameState.toggleFlag()
      → Cell.toggleFlag() -> Cập nhật số đếm mìn
  → Trả về GameDTO
```

## 6. Yêu Cầu Kỹ Thuật

- **Backend:** Java 17+, Spring Boot 3.x, Maven
- **Frontend:** React, TypeScript, TailwindCSS, Vite
- **Testing:** JUnit 5 (Backend)
- **Deployment:** Chạy độc lập (Standalone) với `mvn spring-boot:run` và `npm run dev`.
