package com.example.demo.controller;

import com.example.demo.model.Student;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final List<Student> students = new ArrayList<>();

    public StudentController() {
        students.add(new Student(1L, "Шевченко", "Тарас", "ІК-21"));
        students.add(new Student(2L, "Коваленко", "Олена", "ІК-22"));
        students.add(new Student(3L, "Бондаренко", "Андрій", "ІК-21"));
    }

    @Operation(
            summary = "Отримати список студентів",
            description = "Повертає список студентів з можливістю фільтрації за групою та прізвищем, а також пагінації."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список студентів успішно отримано"),
            @ApiResponse(responseCode = "400", description = "Некоректні параметри пагінації")
    })
    // GET — отримати всіх студентів
    // Фільтрація + пагінація
    @GetMapping
    public ResponseEntity<List<Student>> getAllStudents(
            @RequestParam(required = false) String group,
            @RequestParam(required = false) String surname,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        List<Student> result = new ArrayList<>(students);

        // Фільтрація за групою
        if (group != null && !group.isBlank()) {
            result = result.stream()
                    .filter(student ->
                            student.getGroupName().equalsIgnoreCase(group))
                    .toList();
        }

        // Фільтрація за прізвищем
        if (surname != null && !surname.isBlank()) {
            result = result.stream()
                    .filter(student ->
                            student.getSurname().equalsIgnoreCase(surname))
                    .toList();
        }

        // Перевірка параметрів пагінації
        if (page < 0 || size <= 0) {
            return ResponseEntity.badRequest().build();
        }

        // Пагінація
        int fromIndex = page * size;

        if (fromIndex >= result.size()) {
            return ResponseEntity.ok(new ArrayList<>());
        }

        int toIndex = Math.min(fromIndex + size, result.size());

        List<Student> paginatedResult =
                result.subList(fromIndex, toIndex);

        return ResponseEntity.ok(paginatedResult);
    }

    @Operation(
            summary = "Отримати студента за ID",
            description = "Повертає інформацію про конкретного студента за його унікальним ідентифікатором."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Студента знайдено"),
            @ApiResponse(responseCode = "404", description = "Студента не знайдено")
    })
    // GET — отримати студента за ID
    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentById(
            @PathVariable Long id) {

        return students.stream()
                .filter(student -> student.getId().equals(id))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Створити нового студента",
            description = "Створює нового студента. Ідентифікатор генерується сервером автоматично."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Студента успішно створено"),
            @ApiResponse(responseCode = "400", description = "Некоректні дані студента")
    })
    // POST — створити нового студента
    @PostMapping
    public ResponseEntity<Student> createStudent(
            @RequestBody Student student) {

        long newId = students.stream()
                .mapToLong(Student::getId)
                .max()
                .orElse(0) + 1;

        student.setId(newId);
        students.add(student);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(student);
    }

    @Operation(
            summary = "Повністю оновити студента",
            description = "Повністю замінює дані студента за вказаним ідентифікатором."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Студента успішно оновлено"),
            @ApiResponse(responseCode = "404", description = "Студента не знайдено")
    })
    // PUT — повністю оновити студента
    @PutMapping("/{id}")
    public ResponseEntity<Student> updateStudent(
            @PathVariable Long id,
            @RequestBody Student updatedStudent) {

        for (Student student : students) {

            if (student.getId().equals(id)) {

                student.setSurname(updatedStudent.getSurname());
                student.setName(updatedStudent.getName());
                student.setGroupName(updatedStudent.getGroupName());

                return ResponseEntity.ok(student);
            }
        }

        return ResponseEntity.notFound().build();
    }

    @Operation(
            summary = "Частково оновити студента",
            description = "Частково оновлює дані студента відповідно до RFC 7386 JSON Merge Patch."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Студента успішно оновлено"),
            @ApiResponse(responseCode = "404", description = "Студента не знайдено")
    })
    // PATCH — часткове оновлення студента
    // RFC 7386 — JSON Merge Patch
    @PatchMapping("/{id}")
    public ResponseEntity<Student> patchStudent(
            @PathVariable Long id,
            @RequestBody Student patch) {

        for (Student student : students) {

            if (student.getId().equals(id)) {

                // Змінюємо тільки ті поля,
                // які були передані в JSON
                if (patch.getSurname() != null) {
                    student.setSurname(patch.getSurname());
                }

                if (patch.getName() != null) {
                    student.setName(patch.getName());
                }

                if (patch.getGroupName() != null) {
                    student.setGroupName(patch.getGroupName());
                }

                return ResponseEntity.ok(student);
            }
        }

        return ResponseEntity.notFound().build();
    }

    @Operation(
            summary = "Видалити студента",
            description = "Видаляє студента за його унікальним ідентифікатором."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Студента успішно видалено"),
            @ApiResponse(responseCode = "404", description = "Студента не знайдено")
    })
    // DELETE — видалити студента
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(
            @PathVariable Long id) {

        boolean removed = students.removeIf(
                student -> student.getId().equals(id)
        );

        if (removed) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }
}