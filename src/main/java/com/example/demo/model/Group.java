package com.example.demo.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Академічна група")
public class Group {

    @Schema(description = "Ідентифікатор (IDENTITY)", example = "3", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Назва групи", example = "ІК-23")
    private String name;

    @Schema(description = "Максимальна кількість студентів (за замовчуванням 30)", example = "25")
    private Integer capacity;

    public Group() {
    }

    public Group(Long id, String name, Integer capacity) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }
}
