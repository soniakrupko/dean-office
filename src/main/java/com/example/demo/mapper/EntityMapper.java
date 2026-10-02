package com.example.demo.mapper;

import com.example.demo.entity.GroupEntity;
import com.example.demo.entity.StudentEntity;
import com.example.demo.model.Group;
import com.example.demo.model.Student;

/** Перетворення @Entity-об'єктів на DTO (класи з пакета model), які віддає REST API. */
public final class EntityMapper {

    private EntityMapper() {
    }

    public static Student toDto(StudentEntity e) {
        return new Student(e.getId(), e.getSurname(), e.getName(), e.getStudentGroup().getName());
    }

    public static Group toDto(GroupEntity e) {
        return new Group(e.getId(), e.getName(), e.getCapacity());
    }
}
