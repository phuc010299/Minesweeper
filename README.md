# 💣 Minesweeper Game

Game dò mìn (Minesweeper) — Đồ án cuối kỳ môn **Phân tích & Thiết kế Hướng đối tượng**.

**Kiến trúc**: Spring Boot (Java 17) + React (Vite)

## 📋 Yêu cầu hệ thống

- **Java**: JDK 17 trở lên
- **Maven**: 3.8+
- **Node.js**: 18+ (kèm npm)

## 🚀 Hướng dẫn chạy

### 1. Backend (Spring Boot — port 8080)

```bash
cd backend
mvn clean install        # Tải dependencies & build
mvn spring-boot:run      # Chạy server
```

> Server sẽ chạy tại: http://localhost:8080

### 2. Frontend (React/Vite — port 5173)

```bash
cd frontend
npm install              # Tải dependencies (tạo node_modules/)
npm run dev              # Chạy dev server
```

> Mở trình duyệt tại: http://localhost:5173

### ⚠️ Lưu ý
- Chạy **Backend trước**, sau đó mới chạy Frontend.
- Thư mục `node_modules/` và `target/` **không có trong repo** (đã gitignore). Chạy lệnh `npm install` và `mvn clean install` để tạo lại.

## 🎮 Cách chơi

| Thao tác | Hành động |
|----------|-----------|
| Click trái | Mở ô |
| Click phải | Cắm/gỡ cờ 🚩 |
| Click trái vào ô đã mở (có số) | Chord — mở nhanh ô xung quanh |

**Độ khó**: Beginner (9×9, 10 mìn) · Intermediate (16×16, 40 mìn) · Expert (16×30, 99 mìn)

## 🏗️ Design Patterns

| Pattern | Vai trò |
|---------|---------|
| **State** | Quản lý vòng đời game (Ready → Playing → Won/Lost) |
| **Strategy** | Thuật toán rải mìn (Random / SafeFirstClick) |
| **Factory Method** | Tạo cấu hình độ khó (Beginner / Intermediate / Expert) |
| **Singleton** | GameService, GameTimer — 1 instance duy nhất (Spring IoC) |

## 📁 Cấu trúc dự án

```
Minesweeper/
├── backend/                    # Spring Boot (Java)
│   └── src/main/java/com/minesweeper/
│       ├── controller/         # REST API
│       ├── service/            # Business Logic
│       ├── model/              # Board, Cell
│       ├── dto/                # GameDTO, CellDTO
│       ├── pattern/            # state/ strategy/ factory/
│       └── utils/              # GameTimer
├── frontend/                   # React (Vite)
│   └── src/
│       ├── App.jsx             # Main component
│       └── index.css           # Styles
├── ARCHITECTURE.md             # Tài liệu kiến trúc
├── DESIGN_PATTERNS.md          # Tài liệu design patterns
└── UML_DESIGN_PATTERNS_SPEC.md # Đặc tả UML chi tiết
```
