package com.example.demo.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Результат переведення студентів між групами")
public record TransferResult(
        @Schema(description = "Скільки студентів переведено") int moved,
        @Schema(description = "Скільки студентів тепер у цільовій групі") int totalInTarget) {
}
