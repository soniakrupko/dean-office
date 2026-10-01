package com.example.demo.controller;

import com.example.demo.service.GroupService;
import com.example.demo.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DeanOfficeController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private GroupService groupService;

    @GetMapping("/")
    public String index(Model model) {

        model.addAttribute("students",
                studentService.getAllStudents());

        model.addAttribute("groups",
                groupService.getAllGroups());

        return "index";
    }
}