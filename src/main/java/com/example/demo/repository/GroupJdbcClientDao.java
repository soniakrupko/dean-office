package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Group;

@Repository
@ConditionalOnProperty(name = "app.dao.type", havingValue = "client", matchIfMissing = true)
public class GroupJdbcClientDao implements GroupDao {

    private final JdbcClient client;

    public GroupJdbcClientDao(JdbcClient client) {
        this.client = client;
    }

    @Override
    public long create(String name, int capacity) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        client.sql("INSERT INTO student_group (name, capacity) VALUES (:name, :capacity)")
                .param("name", name)
                .param("capacity", capacity)
                .update(keyHolder, "id");
        return keyHolder.getKey().longValue();
    }

    @Override
    public Optional<Group> findById(long id) {
        return client.sql("SELECT id, name, capacity FROM student_group WHERE id = :id")
                .param("id", id).query(Group.class).optional();
    }

    @Override
    public Optional<Group> findByName(String name) {
        return client.sql("SELECT id, name, capacity FROM student_group WHERE LOWER(name) = LOWER(:name)")
                .param("name", name).query(Group.class).optional();
    }

    @Override
    public List<Group> findAll() {
        return client.sql("SELECT id, name, capacity FROM student_group ORDER BY id")
                .query(Group.class).list();
    }

    @Override
    public boolean update(long id, String name, int capacity) {
        return client.sql("UPDATE student_group SET name = :name, capacity = :capacity WHERE id = :id")
                .param("name", name).param("capacity", capacity).param("id", id)
                .update() > 0;
    }

    @Override
    public boolean delete(long id) {
        return client.sql("DELETE FROM student_group WHERE id = :id").param("id", id).update() > 0;
    }
}
