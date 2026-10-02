package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.exception.BusinessException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.model.Group;
import com.example.demo.model.Student;
import com.example.demo.repository.GroupDao;
import com.example.demo.repository.StudentDao;

@Service
public class StudentServiceImpl implements StudentService {

    private final StudentDao studentDao;
    private final GroupDao groupDao;

    public StudentServiceImpl(StudentDao studentDao, GroupDao groupDao) {
        this.studentDao = studentDao;
        this.groupDao = groupDao;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Student> getAllStudents() {
        return studentDao.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Student> search(String groupName, String surname, int page, int size) {
        if (page < 0 || size < 1 || size > 100) {
            throw new BusinessException("page має бути >= 0, а size — від 1 до 100");
        }
        return studentDao.findAll(groupName, surname, page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public Student get(long id) {
        return studentDao.findById(id)
                .orElseThrow(() -> new NotFoundException("Студента з id=" + id + " не знайдено"));
    }

    @Override
    @Transactional
    public Student create(Student s) {
        requireFilled(s);
        Group group = findGroup(s.getGroupName());
        long id = studentDao.create(s.getSurname(), s.getName(), group.getId());
        return get(id);
    }

    @Override
    @Transactional
    public Student update(long id, Student s) {
        get(id); // 404, якщо студента немає
        requireFilled(s);
        Group group = findGroup(s.getGroupName());
        studentDao.update(id, s.getSurname(), s.getName(), group.getId());
        return get(id);
    }

    @Override
    @Transactional
    public Student patch(long id, Student p) {
        Student current = get(id);
        String surname = p.getSurname() != null ? p.getSurname() : current.getSurname();
        String name = p.getName() != null ? p.getName() : current.getName();
        String groupName = p.getGroupName() != null ? p.getGroupName() : current.getGroupName();
        Group group = findGroup(groupName);
        studentDao.update(id, surname, name, group.getId());
        return get(id);
    }

    @Override
    @Transactional
    public void delete(long id) {
        if (!studentDao.delete(id)) {
            throw new NotFoundException("Студента з id=" + id + " не знайдено");
        }
    }

    private Group findGroup(String groupName) {
        return groupDao.findByName(groupName)
                .orElseThrow(() -> new BusinessException("Групи '" + groupName + "' не існує"));
    }

    private static void requireFilled(Student s) {
        if (isBlank(s.getSurname()) || isBlank(s.getName()) || isBlank(s.getGroupName())) {
            throw new BusinessException("Поля surname, name та groupName обов'язкові");
        }
    }

    private static boolean isBlank(String v) {
        return v == null || v.isBlank();
    }
}
