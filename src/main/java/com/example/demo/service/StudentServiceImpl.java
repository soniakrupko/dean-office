package com.example.demo.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.GroupEntity;
import com.example.demo.entity.StudentEntity;
import com.example.demo.exception.BusinessException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.EntityMapper;
import com.example.demo.model.Student;
import com.example.demo.repository.GroupRepository;
import com.example.demo.repository.StudentRepository;

@Service
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final GroupRepository groupRepository;

    public StudentServiceImpl(StudentRepository studentRepository, GroupRepository groupRepository) {
        this.studentRepository = studentRepository;
        this.groupRepository = groupRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Student> getAllStudents() {
        return studentRepository.findAllByOrderByIdAsc().stream().map(EntityMapper::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Student> search(String groupName, String surname, int page, int size) {
        if (page < 0 || size < 1 || size > 100) {
            throw new BusinessException("page має бути >= 0, а size — від 1 до 100");
        }
        PageRequest pageable = PageRequest.of(page, size, Sort.by("id"));
        boolean byGroup = !isBlank(groupName);
        boolean bySurname = !isBlank(surname);

        Page<StudentEntity> result;
        if (byGroup && bySurname) {
            result = studentRepository.findByStudentGroupNameIgnoreCaseAndSurnameIgnoreCase(groupName, surname, pageable);
        } else if (byGroup) {
            result = studentRepository.findByStudentGroupNameIgnoreCase(groupName, pageable);
        } else if (bySurname) {
            result = studentRepository.findBySurnameIgnoreCase(surname, pageable);
        } else {
            result = studentRepository.findAll(pageable);
        }
        return result.getContent().stream().map(EntityMapper::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Student> searchBySurnamePart(String part) {
        if (isBlank(part)) {
            throw new BusinessException("Параметр part обов'язковий");
        }
        return studentRepository.searchBySurnamePart(part).stream().map(EntityMapper::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Student> firstThreeBySurname() {
        return studentRepository.findTop3ByOrderBySurnameAsc().stream().map(EntityMapper::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Student get(long id) {
        return EntityMapper.toDto(find(id));
    }

    @Override
    @Transactional
    public Student create(Student s) {
        requireFilled(s);
        GroupEntity group = findGroup(s.getGroupName());
        StudentEntity saved = studentRepository.save(new StudentEntity(s.getSurname(), s.getName(), group));
        return EntityMapper.toDto(saved);
    }

    @Override
    @Transactional
    public Student update(long id, Student s) {
        StudentEntity entity = find(id);
        requireFilled(s);
        entity.setSurname(s.getSurname());
        entity.setName(s.getName());
        entity.setStudentGroup(findGroup(s.getGroupName()));
        return EntityMapper.toDto(studentRepository.save(entity));
    }

    @Override
    @Transactional
    public Student patch(long id, Student p) {
        StudentEntity entity = find(id);
        if (p.getSurname() != null) {
            entity.setSurname(p.getSurname());
        }
        if (p.getName() != null) {
            entity.setName(p.getName());
        }
        if (p.getGroupName() != null) {
            entity.setStudentGroup(findGroup(p.getGroupName()));
        }
        return EntityMapper.toDto(studentRepository.save(entity));
    }

    @Override
    @Transactional
    public void delete(long id) {
        if (!studentRepository.existsById(id)) {
            throw new NotFoundException("Студента з id=" + id + " не знайдено");
        }
        studentRepository.deleteById(id);
    }

    private StudentEntity find(long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Студента з id=" + id + " не знайдено"));
    }

    private GroupEntity findGroup(String groupName) {
        return groupRepository.findByNameIgnoreCase(groupName)
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
