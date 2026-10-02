package com.example.demo.repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Group;

@Repository
@ConditionalOnProperty(name = "app.dao.type", havingValue = "template")
public class GroupJdbcTemplateDao implements GroupDao {

    private static final RowMapper<Group> MAPPER = new BeanPropertyRowMapper<>(Group.class);

    private final JdbcTemplate jdbc;

    public GroupJdbcTemplateDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public long create(String name, int capacity) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO student_group (name, capacity) VALUES (?, ?)", new String[] { "id" });
            ps.setString(1, name);
            ps.setInt(2, capacity);
            return ps;
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }

    @Override
    public Optional<Group> findById(long id) {
        return jdbc.query("SELECT id, name, capacity FROM student_group WHERE id = ?", MAPPER, id)
                .stream().findFirst();
    }

    @Override
    public Optional<Group> findByName(String name) {
        return jdbc.query("SELECT id, name, capacity FROM student_group WHERE LOWER(name) = LOWER(?)", MAPPER, name)
                .stream().findFirst();
    }

    @Override
    public List<Group> findAll() {
        return jdbc.query("SELECT id, name, capacity FROM student_group ORDER BY id", MAPPER);
    }

    @Override
    public boolean update(long id, String name, int capacity) {
        return jdbc.update("UPDATE student_group SET name = ?, capacity = ? WHERE id = ?", name, capacity, id) > 0;
    }

    @Override
    public boolean delete(long id) {
        return jdbc.update("DELETE FROM student_group WHERE id = ?", id) > 0;
    }
}
