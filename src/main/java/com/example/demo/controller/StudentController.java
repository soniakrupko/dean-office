package com.example.demo.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.model.Student;
import com.example.demo.service.StudentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/students")
@Tag(name = "Студенти", description = "Операції над студентами деканату")
public class StudentController {

    private final StudentService service;

    public StudentController(StudentService service) {
        this.service = service;
    }

    @Operation(summary = "Отримати список студентів",
            description = "Повертає список студентів з можливістю фільтрації за групою та прізвищем "
                    + "(без урахування регістру) і пагінації: page (з 0) та size (1–100).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список студентів успішно отримано (може бути порожнім)",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Student.class)))),
            @ApiResponse(responseCode = "400", description = "Некоректні параметри пагінації", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<Student>> getAllStudents(
            @Parameter(description = "Назва групи", example = "ІК-21") @RequestParam(required = false) String group,
            @Parameter(description = "Прізвище студента", example = "Шевченко") @RequestParam(required = false) String surname,
            @Parameter(description = "Номер сторінки (з 0)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Розмір сторінки (1–100)", example = "10") @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(service.search(group, surname, page, size));
    }

    @Operation(summary = "Отримати студента за ID",
            description = "Повертає інформацію про конкретного студента за його унікальним ідентифікатором.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Студента знайдено",
                    content = @Content(schema = @Schema(implementation = Student.class))),
            @ApiResponse(responseCode = "404", description = "Студента не знайдено", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentById(
            @Parameter(description = "Ідентифікатор студента", example = "101") @PathVariable Long id) {
        return ResponseEntity.ok(service.get(id));
    }

    @Operation(summary = "Створити нового студента",
            description = "Створює студента в існуючій групі. Ідентифікатор генерується послідовністю БД, "
                    + "повертається у відповіді та в заголовку Location. Усі поля тіла обов'язкові.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Студента успішно створено",
                    content = @Content(schema = @Schema(implementation = Student.class))),
            @ApiResponse(responseCode = "400", description = "Некоректні дані студента або групи не існує", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Student> createStudent(@RequestBody Student student) {
        Student created = service.create(student);
        return ResponseEntity.created(URI.create("/api/students/" + created.getId())).body(created);
    }

    @Operation(summary = "Повністю оновити студента",
            description = "Повністю замінює прізвище, ім'я та групу студента за вказаним ідентифікатором.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Студента успішно оновлено",
                    content = @Content(schema = @Schema(implementation = Student.class))),
            @ApiResponse(responseCode = "400", description = "Некоректні дані або групи не існує", content = @Content),
            @ApiResponse(responseCode = "404", description = "Студента не знайдено", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<Student> updateStudent(
            @Parameter(description = "Ідентифікатор студента", example = "101") @PathVariable Long id,
            @RequestBody Student updatedStudent) {
        return ResponseEntity.ok(service.update(id, updatedStudent));
    }

    @Operation(summary = "Частково оновити студента",
            description = "Частково оновлює дані студента (RFC 7386 JSON Merge Patch): змінюються лише передані поля.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Студента успішно оновлено",
                    content = @Content(schema = @Schema(implementation = Student.class))),
            @ApiResponse(responseCode = "400", description = "Вказаної групи не існує", content = @Content),
            @ApiResponse(responseCode = "404", description = "Студента не знайдено", content = @Content)
    })
    @PatchMapping("/{id}")
    public ResponseEntity<Student> patchStudent(
            @Parameter(description = "Ідентифікатор студента", example = "101") @PathVariable Long id,
            @RequestBody Student patch) {
        return ResponseEntity.ok(service.patch(id, patch));
    }

    @Operation(summary = "Видалити студента",
            description = "Видаляє студента за його унікальним ідентифікатором.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Студента успішно видалено"),
            @ApiResponse(responseCode = "404", description = "Студента не знайдено", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(
            @Parameter(description = "Ідентифікатор студента", example = "101") @PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
