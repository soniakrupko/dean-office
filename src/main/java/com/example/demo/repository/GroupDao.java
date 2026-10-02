package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import com.example.demo.model.Group;

public interface GroupDao {

    long create(String name, int capacity);

    Optional<Group> findById(long id);

    Optional<Group> findByName(String name);

    List<Group> findAll();

    boolean update(long id, String name, int capacity);

    boolean delete(long id);
}
