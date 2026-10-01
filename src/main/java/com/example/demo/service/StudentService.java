package com.example.demo.service;

import com.example.demo.model.Student;
import com.example.demo.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public void addStudent(Student student) {
        studentRepository.add(student);
    }

    public void deleteStudent(Long id) {
        studentRepository.delete(id);
    }

    public List<Student> searchBySurname(String surname) {
        return studentRepository.findBySurname(surname);
    }
}