package com.example.demo.service;

import com.example.demo.model.Group;
import com.example.demo.repository.GroupRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupService {

    private GroupRepository groupRepository;

    public GroupService() {
    }

    public List<Group> getAllGroups() {
        return groupRepository.findAll();
    }

    public void addGroup(Group group) {
        groupRepository.add(group);
    }

    public void deleteGroup(Long id) {
        groupRepository.delete(id);
    }

    public List<Group> searchByName(String name) {
        return groupRepository.findByName(name);
    }

    @org.springframework.beans.factory.annotation.Autowired
    public void setGroupRepository(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }
}