package com.example.demo.model;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Нова група разом зі списком студентів (створюється однією операцією). "
        + "У студентів використовуються лише поля surname та name.")
public record GroupWithStudentsRequest(
        @Schema(example = "ІК-26") String name,
        @Schema(example = "3") Integer capacity,
        List<Student> students) {
}
