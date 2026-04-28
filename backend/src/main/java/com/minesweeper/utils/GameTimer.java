package com.minesweeper.utils;

import org.springframework.stereotype.Component;

/**
 * Singleton Pattern - Timer đếm thời gian chơi game.
 * Chỉ có duy nhất 1 instance trong toàn bộ ứng dụng.
 * 
 * Phiên bản này không phụ thuộc vào Swing Timer,
 * sử dụng System.currentTimeMillis() để tính thời gian.
 * Frontend (React) sẽ tự polling để cập nhật hiển thị.
 */
@Component
public class GameTimer {

    // === Thuộc tính ===
    private long startTimeMillis;       // Thời điểm bắt đầu (millis)
    private int frozenSeconds;          // Giây bị đóng băng khi dừng
    private boolean running;            // Timer đang chạy hay không

    private static final int MAX_TIME = 999; // Giới hạn hiển thị tối đa

    /**
     * Constructor.
     */
    public GameTimer() {
        reset();
    }

    /**
     * Bắt đầu đếm thời gian.
     */
    public void start() {
        if (!running) {
            startTimeMillis = System.currentTimeMillis();
            running = true;
            frozenSeconds = -1; // Xóa giá trị đóng băng
        }
    }

    /**
     * Dừng timer, đóng băng giây hiện tại.
     */
    public void stop() {
        if (running) {
            frozenSeconds = getElapsedSeconds();
            running = false;
        }
    }

    /**
     * Reset timer về 0 và dừng.
     */
    public void reset() {
        running = false;
        frozenSeconds = 0;
        startTimeMillis = 0;
    }

    /**
     * Lấy số giây đã trôi qua.
     * Nếu timer đang chạy → tính từ startTime.
     * Nếu timer đã dừng → trả về giá trị đóng băng.
     */
    public int getElapsedSeconds() {
        if (!running) {
            return Math.max(0, frozenSeconds);
        }
        long elapsed = (System.currentTimeMillis() - startTimeMillis) / 1000;
        return (int) Math.min(elapsed, MAX_TIME);
    }

    /**
     * Kiểm tra timer có đang chạy không.
     */
    public boolean isRunning() {
        return running;
    }
}
