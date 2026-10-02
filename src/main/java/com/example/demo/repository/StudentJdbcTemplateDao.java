package com.example.demo.repository;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Student;

/** Реалізація DAO на основі JdbcTemplate (активна при app.dao.type=template). */
@Repository
@ConditionalOnProperty(name = "app.dao.type", havingValue = "template")
public class StudentJdbcTemplateDao implements StudentDao {

    private static final String SELECT = """
            SELECT s.id, s.surname, s.name, g.name AS group_name
            FROM student s JOIN student_group g ON g.id = s.group_id
            """;

    // Student — звичайний bean (конструктор без параметрів + setter-и), тому підходить BeanPropertyRowMapper:
    // колонка group_name автоматично мапиться на властивість groupName
    private static final RowMapper<Student> MAPPER = new BeanPropertyRowMapper<>(Student.class);

    private final JdbcTemplate jdbc;

    public StudentJdbcTemplateDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public long create(String surname, String name, long groupId) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(con -> {
            // Вказуємо колонку "id", щоб драйвер повернув згенерований IDENTITY-ключ
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO student (surname, name, group_id) VALUES (?, ?, ?)",
                    new String[] { "id" });
            ps.setString(1, surname);
            ps.setString(2, name);
            ps.setLong(3, groupId);
            return ps;
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }

    @Override
    public Optional<Student> findById(long id) {
        return jdbc.query(SELECT + " WHERE s.id = ?", MAPPER, id).stream().findFirst();
    }

    @Override
    public List<Student> findAll(String groupName, String surname, int page, int size) {
        StringBuilder sql = new StringBuilder(SELECT).append(" WHERE 1 = 1");
        List<Object> args = new ArrayList<>();
        if (groupName != null && !groupName.isBlank()) {
            sql.append(" AND LOWER(g.name) = LOWER(?)");
            args.add(groupName);
        }
        if (surname != null && !surname.isBlank()) {
            sql.append(" AND LOWER(s.surname) = LOWER(?)");
            args.add(surname);
        }
        // Пагінація в SQL Server: OFFSET ... FETCH (потребує ORDER BY)
        sql.append(" ORDER BY s.id OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        args.add(page * size);
        args.add(size);
        return jdbc.query(sql.toString(), MAPPER, args.toArray());
    }

    @Override
    public List<Student> findAll() {
        return jdbc.query(SELECT + " ORDER BY s.id", MAPPER);
    }

    @Override
    public List<Student> findByGroupId(long groupId) {
        return jdbc.query(SELECT + " WHERE s.group_id = ? ORDER BY s.id", MAPPER, groupId);
    }

    @Override
    public int countByGroupId(long groupId) {
        Integer n = jdbc.queryForObject("SELECT COUNT(*) FROM student WHERE group_id = ?", Integer.class, groupId);
        return n == null ? 0 : n;
    }

    @Override
    public boolean update(long id, String surname, String name, long groupId) {
        return jdbc.update(
                "UPDATE student SET surname = ?, name = ?, group_id = ? WHERE id = ?",
                surname, name, groupId, id) > 0;
    }

    @Override
    public boolean delete(long id) {
        return jdbc.update("DELETE FROM student WHERE id = ?", id) > 0;
    }

    @Override
    public int moveAll(long fromGroupId, long toGroupId) {
        return jdbc.update("UPDATE student SET group_id = ? WHERE group_id = ?", toGroupId, fromGroupId);
    }
}
