# Tài Liệu Báo Cáo Design Patterns - Dự Án Minesweeper

Tài liệu này trình bày chi tiết về **4 Design Patterns** cốt lõi được áp dụng trong kiến trúc hệ thống Minesweeper, giúp xử lý các logic phức tạp một cách thanh lịch và tuân thủ nguyên lý SOLID.

*(Ghi chú: Các pattern không cần thiết như Command hay Observer đã được lược bỏ để tránh Over-engineering cho hệ thống REST API).*

---

## 1. State Pattern (Mẫu Trạng Thái)
*   **Loại Pattern:** Behavioral (Hành vi)
*   **Chức năng:** Quản lý vòng đời của ván game. Thay vì dùng hàng loạt lệnh `if-else` (ví dụ: `if (isGameOver) thì không cho click`), State Pattern cho phép hành vi của game thay đổi tự động dựa trên trạng thái hiện tại.
*   **Các thành phần (Components):**
    *   **Context (Lớp Ngữ Cảnh):** `GameService` - Lớp này duy trì một tham chiếu đến trạng thái hiện tại (`private GameState gameState`) và uỷ quyền (delegate) mọi thao tác của người chơi cho đối tượng trạng thái đó xử lý thông qua hàm `processRevealCell`, `processToggleFlag`, v.v.
    *   **State (Giao diện Trạng Thái):** `GameState` - Định nghĩa các hành vi mà người chơi có thể làm: `revealCell`, `toggleFlag`, `chordCell`.
    *   **Concrete States (Trạng thái Cụ thể):**
        *   `ReadyState`: Trạng thái lúc mới tạo bảng. Ở đây, cú click đầu tiên luôn an toàn (gọi hàm sinh mìn) rồi chuyển sang `PlayingState`.
        *   `PlayingState`: Trạng thái chơi bình thường, tính toán logic mở ô/cắm cờ.
        *   `WonState` và `LostState`: Trạng thái kết thúc, từ chối (return false) mọi thao tác click/flag của người chơi.

## 2. Strategy Pattern (Mẫu Chiến Lược)
*   **Loại Pattern:** Behavioral (Hành vi)
*   **Chức năng:** Tách biệt các thuật toán sinh mìn (rải mìn) ra khỏi logic game cốt lõi, giúp bạn có thể dễ dàng thay đổi cách rải mìn (ví dụ: rải ngẫu nhiên hoàn toàn, hay rải sao cho cú click đầu tiên luôn không trúng mìn) mà không phải sửa code hiện tại.
*   **Các thành phần (Components):**
    *   **Strategy (Giao diện Chiến lược):** `MineGenerationStrategy` - Có hàm `generateMines()`.
    *   **Concrete Strategies (Chiến lược Cụ thể):**
        *   `RandomMineStrategy`: Rải mìn hoàn toàn ngẫu nhiên.
        *   `SafeFirstClickStrategy`: Rải mìn nhưng chừa lại một vùng an toàn 3x3 quanh vị trí user click đầu tiên để đảm bảo user không bao giờ thua ngay click đầu.
    *   **Context (Lớp Ngữ cảnh):** `GameService` - Nơi chứa thuật toán và quyết định sử dụng thuật toán nào.

## 3. Factory Method Pattern (Mẫu Phương thức Khởi tạo)
*   **Loại Pattern:** Creational (Khởi tạo)
*   **Chức năng:** Khởi tạo các cấu hình độ khó khác nhau (Beginner, Intermediate, Expert) mà không cần dùng chuỗi lệnh `if-else` khổng lồ hay dùng toán tử `new` bừa bãi khi tạo cấu hình. Nó giao việc khởi tạo object cho các lớp con (subclasses).
*   **Các thành phần (Components):**
    *   **Creator (Nhà máy ảo):** `DifficultyFactory` - Interface định nghĩa hàm `createDifficulty()`.
    *   **Concrete Creators (Nhà máy cụ thể):** `BeginnerDifficultyFactory`, `IntermediateDifficultyFactory`, `ExpertDifficultyFactory` - Các nhà máy này quyết định sẽ trả về bản thiết kế bảng 9x9, 16x16 hay 16x30.
    *   **Product (Sản phẩm):** `DifficultyConfig` - Giao diện chung mô tả cấu hình game.
    *   **Concrete Products (Sản phẩm Cụ thể):** `BeginnerConfig`, `IntermediateConfig`, `ExpertConfig` (Nằm trong package `model.config`).

## 4. Singleton Pattern (Mẫu Độc Bản - Triển khai qua Spring IoC)
*   **Loại Pattern:** Creational (Khởi tạo)
*   **Chức năng:** Đảm bảo trong toàn bộ vòng đời của ứng dụng, chỉ tồn tại duy nhất một phiên bản (instance) của một đối tượng nhất định, giúp tiết kiệm bộ nhớ và quản lý đồng bộ.
*   **Các thành phần (Components):**
    *   **Singleton Class:** Thay vì tự code mô hình cũ kĩ `private static GameTimer instance`, chúng ta gắn annotation `@Service` cho `GameService` và `@Component` cho `GameTimer`. Nhờ Inversion of Control (IoC) Container của Spring Boot, framework sẽ đảm bảo các lớp này là **Singleton** mặc định. Mọi nơi `@Autowired` đều dùng chung 1 bộ đếm thời gian và 1 service xử lý game.

---
> **💡 Tip cho việc báo cáo (Defense):**
> 
> Khi giải thích với hội đồng chấm thi, bạn nên nhấn mạnh rằng: *"Lý do nhóm áp dụng các Design Pattern này là để đảm bảo nguyên lý **SOLID**, đặc biệt là **Single Responsibility** (Đơn trách nhiệm) và **Open/Closed** (Dễ mở rộng, khó sửa đổi). Lấy ví dụ, nếu hệ thống muốn bổ sung thêm 1 độ khó mới (Custom) hay 1 thuật toán rải mìn mới (Hardcore), nhóm chỉ cần tạo thêm class implements các interface có sẵn chứ hoàn toàn không cần đụng vào Core Logic của GameService. Nhóm cũng đã chủ động loại bỏ Command Pattern và Observer Pattern vì nhận thấy chúng không mang lại giá trị cho mô hình REST API, tránh việc Over-engineering."*
