package com.example.demo.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Студент")
public class Student {

    @Schema(description = "Ідентифікатор (генерується послідовністю БД)", example = "101", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Прізвище", example = "Крупко")
    private String surname;

    @Schema(description = "Ім'я", example = "Софія")
    private String name;

    @Schema(description = "Назва існуючої групи", example = "ІК-23")
    private String groupName;

    public Student() {
    }

    public Student(Long id, String surname, String name, String groupName) {
        this.id = id;
        this.surname = surname;
        this.name = name;
        this.groupName = groupName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
