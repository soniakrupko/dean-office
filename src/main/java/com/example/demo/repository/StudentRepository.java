package com.example.demo.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.example.demo.entity.StudentEntity;

/**
 * CrudRepository дає CRUD, PagingAndSortingRepository — пагінацію та сортування.
 * @EntityGraph підтягує групу тим самим SQL-запитом, щоб не виникала проблема N+1.
 */
public interface StudentRepository
        extends CrudRepository<StudentEntity, Long>, PagingAndSortingRepository<StudentEntity, Long> {

    @Override
    @EntityGraph(attributePaths = "studentGroup")
    Page<StudentEntity> findAll(Pageable pageable);

    // 5.2. Методи пошуку, які Spring Data генерує за назвою
    @EntityGraph(attributePaths = "studentGroup")
    Page<StudentEntity> findByStudentGroupNameIgnoreCase(String groupName, Pageable pageable);

    @EntityGraph(attributePaths = "studentGroup")
    Page<StudentEntity> findBySurnameIgnoreCase(String surname, Pageable pageable);

    @EntityGraph(attributePaths = "studentGroup")
    Page<StudentEntity> findByStudentGroupNameIgnoreCaseAndSurnameIgnoreCase(
            String groupName, String surname, Pageable pageable);

    @EntityGraph(attributePaths = "studentGroup")
    List<StudentEntity> findAllByOrderByIdAsc();

    /** Обмеження кількості результатів ключовим словом Top. */
    @EntityGraph(attributePaths = "studentGroup")
    List<StudentEntity> findTop3ByOrderBySurnameAsc();

    long countByStudentGroupId(Long groupId);

    boolean existsByStudentGroupId(Long groupId);

    // 5.1.1. Запити мовою JPQL з анотацією @Query
    @Query("select s from StudentEntity s join fetch s.studentGroup g "
            + "where g.id = :groupId order by s.surname, s.name")
    List<StudentEntity> findByGroupIdOrdered(@Param("groupId") Long groupId);

    @Query("select s from StudentEntity s join fetch s.studentGroup "
            + "where lower(s.surname) like lower(concat('%', :part, '%')) order by s.surname")
    List<StudentEntity> searchBySurnamePart(@Param("part") String part);

    /** Масове переведення студентів з однієї групи в іншу (використовується в транзакції). */
    @Modifying(clearAutomatically = true)
    @Query("update StudentEntity s set s.studentGroup = :to where s.studentGroup = :from")
    int moveAll(@Param("from") com.example.demo.entity.GroupEntity from,
                @Param("to") com.example.demo.entity.GroupEntity to);
}
