package com.example.demo.repository;

import com.example.demo.model.Student;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class StudentRepository {

    private final List<Student> students = new ArrayList<>();

    public StudentRepository() {
        students.add(new Student(1L, "Шевченко", "Олена", "ІК-21"));
        students.add(new Student(2L, "Коваленко", "Андрій", "ІК-22"));
        students.add(new Student(3L, "Бондаренко", "Марія", "ІК-21"));
    }

    public List<Student> findAll() {
        return students;
    }

    public void add(Student student) {
        students.add(student);
    }

    public void delete(Long id) {
        students.removeIf(student -> student.getId().equals(id));
    }

    public List<Student> findBySurname(String surname) {
        return students.stream()
                .filter(student ->
                        student.getSurname().equalsIgnoreCase(surname))
                .toList();
    }
}