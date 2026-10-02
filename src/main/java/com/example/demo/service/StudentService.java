package com.example.demo.service;

import java.util.List;

import com.example.demo.model.Student;

public interface StudentService {
    List<Student> getAllStudents();
    List<Student> search(String groupName, String surname, int page, int size);
    List<Student> searchBySurnamePart(String part);
    List<Student> firstThreeBySurname();
    Student get(long id);
    Student create(Student student);
    Student update(long id, Student student);
    Student patch(long id, Student patch);
    void delete(long id);
}
