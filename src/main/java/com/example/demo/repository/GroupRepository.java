package com.example.demo.repository;

import com.example.demo.model.Group;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class GroupRepository {

    private final List<Group> groups = new ArrayList<>();

    public GroupRepository() {
        groups.add(new Group(1L, "ІК-21"));
        groups.add(new Group(2L, "ІК-22"));
        groups.add(new Group(3L, "ІК-23"));
    }

    public List<Group> findAll() {
        return groups;
    }

    public void add(Group group) {
        groups.add(group);
    }

    public void delete(Long id) {
        groups.removeIf(group -> group.getId().equals(id));
    }

    public List<Group> findByName(String name) {
        return groups.stream()
                .filter(group ->
                        group.getName().equalsIgnoreCase(name))
                .toList();
    }
}