package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Студент. Бік «багато» у зв'язку «один-до-багатьох»: кожен студент належить одній групі.
 */
@Entity
@Table(name = "student")
public class StudentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String surname;

    @Column(nullable = false, length = 50)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_id", nullable = false)
    private GroupEntity studentGroup;

    protected StudentEntity() {
        // потрібен JPA
    }

    public StudentEntity(String surname, String name, GroupEntity studentGroup) {
        this.surname = surname;
        this.name = name;
        this.studentGroup = studentGroup;
    }

    public Long getId() {
        return id;
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

    public GroupEntity getStudentGroup() {
        return studentGroup;
    }

    public void setStudentGroup(GroupEntity studentGroup) {
        this.studentGroup = studentGroup;
    }
}
