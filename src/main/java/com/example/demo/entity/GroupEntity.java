package com.example.demo.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * Група. Бік «один» у зв'язку «один-до-багатьох» (одна група — багато студентів).
 */
@Entity
@Table(name = "student_group")
// Іменований JPQL-запит: групи, у яких ще є вільні місця
@NamedQuery(name = "GroupEntity.findWithFreeSeats",
        query = "select g from GroupEntity g where g.capacity > size(g.students) order by g.name")
public class GroupEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String name;

    @Column(nullable = false)
    private Integer capacity;

    @OneToMany(mappedBy = "studentGroup")
    private List<StudentEntity> students = new ArrayList<>();

    protected GroupEntity() {
        // потрібен JPA
    }

    public GroupEntity(String name, Integer capacity) {
        this.name = name;
        this.capacity = capacity;
    }

    public Long getId() {
        return id;
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

    public List<StudentEntity> getStudents() {
        return students;
    }
}
