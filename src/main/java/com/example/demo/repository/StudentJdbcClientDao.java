package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Student;

/** Реалізація DAO на основі JdbcClient (активна за замовчуванням, app.dao.type=client). */
@Repository
@ConditionalOnProperty(name = "app.dao.type", havingValue = "client", matchIfMissing = true)
public class StudentJdbcClientDao implements StudentDao {

    private static final String SELECT = """
            SELECT s.id, s.surname, s.name, g.name AS group_name
            FROM student s JOIN student_group g ON g.id = s.group_id
            """;

    private final JdbcClient client;

    public StudentJdbcClientDao(JdbcClient client) {
        this.client = client;
    }

    @Override
    public long create(String surname, String name, long groupId) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        client.sql("INSERT INTO student (surname, name, group_id) VALUES (:surname, :name, :groupId)")
                .param("surname", surname)
                .param("name", name)
                .param("groupId", groupId)
                .update(keyHolder, "id");
        return keyHolder.getKey().longValue();
    }

    @Override
    public Optional<Student> findById(long id) {
        return client.sql(SELECT + " WHERE s.id = :id")
                .param("id", id)
                .query(Student.class)   // колонка group_name мапиться на groupName автоматично
                .optional();
    }

    @Override
    public List<Student> findAll(String groupName, String surname, int page, int size) {
        // Динамічний SQL: умови додаємо лише для переданих фільтрів
        StringBuilder sql = new StringBuilder(SELECT).append(" WHERE 1 = 1");
        boolean byGroup = groupName != null && !groupName.isBlank();
        boolean bySurname = surname != null && !surname.isBlank();
        if (byGroup) {
            sql.append(" AND LOWER(g.name) = LOWER(:groupName)");
        }
        if (bySurname) {
            sql.append(" AND LOWER(s.surname) = LOWER(:surname)");
        }
        // Пагінація в SQL Server: OFFSET ... FETCH (потребує ORDER BY)
        sql.append(" ORDER BY s.id OFFSET :offset ROWS FETCH NEXT :size ROWS ONLY");

        JdbcClient.StatementSpec spec = client.sql(sql.toString());
        if (byGroup) {
            spec = spec.param("groupName", groupName);
        }
        if (bySurname) {
            spec = spec.param("surname", surname);
        }
        return spec.param("offset", page * size)
                .param("size", size)
                .query(Student.class)
                .list();
    }

    @Override
    public List<Student> findAll() {
        return client.sql(SELECT + " ORDER BY s.id").query(Student.class).list();
    }

    @Override
    public List<Student> findByGroupId(long groupId) {
        return client.sql(SELECT + " WHERE s.group_id = :groupId ORDER BY s.id")
                .param("groupId", groupId)
                .query(Student.class)
                .list();
    }

    @Override
    public int countByGroupId(long groupId) {
        return client.sql("SELECT COUNT(*) FROM student WHERE group_id = :groupId")
                .param("groupId", groupId)
                .query(Integer.class)
                .single();
    }

    @Override
    public boolean update(long id, String surname, String name, long groupId) {
        return client.sql("""
                UPDATE student SET surname = :surname, name = :name, group_id = :groupId
                WHERE id = :id
                """)
                .param("surname", surname)
                .param("name", name)
                .param("groupId", groupId)
                .param("id", id)
                .update() > 0;   // update() повертає кількість змінених рядків
    }

    @Override
    public boolean delete(long id) {
        return client.sql("DELETE FROM student WHERE id = :id").param("id", id).update() > 0;
    }

    @Override
    public int moveAll(long fromGroupId, long toGroupId) {
        return client.sql("UPDATE student SET group_id = :to WHERE group_id = :from")
                .param("to", toGroupId)
                .param("from", fromGroupId)
                .update();
    }
}
