package com.example.demo.model;

public class Student {

    private Long id;
    private String surname;
    private String name;
    private String groupName;

    public Student() {
    }

    public Student(Long id, String surname, String name, String groupName) {
        this.id = id;
        this.surname = surname;
        this.name = name;
        this.groupName = groupName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}