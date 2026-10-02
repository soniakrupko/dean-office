package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import com.example.demo.model.Student;

public interface StudentDao {

    /** Створює студента і повертає згенерований БД ключ. */
    long create(String surname, String name, long groupId);

    Optional<Student> findById(long id);

    /** Пошук за назвою групи та/або прізвищем (без урахування регістру) з пагінацією (page з 0). */
    List<Student> findAll(String groupName, String surname, int page, int size);

    List<Student> findAll();

    List<Student> findByGroupId(long groupId);

    int countByGroupId(long groupId);

    boolean update(long id, String surname, String name, long groupId);

    boolean delete(long id);

    /** Переносить усіх студентів з однієї групи в іншу, повертає кількість змінених рядків. */
    int moveAll(long fromGroupId, long toGroupId);
}
