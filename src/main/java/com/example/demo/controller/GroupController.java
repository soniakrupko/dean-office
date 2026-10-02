package com.example.demo.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.model.Group;
import com.example.demo.model.GroupWithStudentsRequest;
import com.example.demo.model.Student;
import com.example.demo.model.TransferResult;
import com.example.demo.service.GroupService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/groups")
@Tag(name = "Групи", description = "Операції над групами та транзакційні операції")
public class GroupController {

    private final GroupService service;

    public GroupController(GroupService service) {
        this.service = service;
    }

    @Operation(summary = "Отримати список груп", description = "Повертає всі групи, відсортовані за ідентифікатором.")
    @ApiResponse(responseCode = "200", description = "Список груп",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = Group.class))))
    @GetMapping
    public ResponseEntity<List<Group>> list() {
        return ResponseEntity.ok(service.getAllGroups());
    }

    @Operation(summary = "Отримати групу за ID", description = "Повертає групу разом з її місткістю.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Групу знайдено",
                    content = @Content(schema = @Schema(implementation = Group.class))),
            @ApiResponse(responseCode = "404", description = "Групу не знайдено", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Group> get(@Parameter(description = "Ідентифікатор групи", example = "3") @PathVariable long id) {
        return ResponseEntity.ok(service.get(id));
    }

    @Operation(summary = "Студенти групи", description = "Повертає всіх студентів вказаної групи.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список студентів групи",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Student.class)))),
            @ApiResponse(responseCode = "404", description = "Групу не знайдено", content = @Content)
    })
    @GetMapping("/{id}/students")
    public ResponseEntity<List<Student>> students(
            @Parameter(description = "Ідентифікатор групи", example = "3") @PathVariable long id) {
        return ResponseEntity.ok(service.students(id));
    }

    @Operation(summary = "Створити групу",
            description = "Створює групу; ідентифікатор генерується IDENTITY-полем і повертається у відповіді.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Групу створено",
                    content = @Content(schema = @Schema(implementation = Group.class))),
            @ApiResponse(responseCode = "400", description = "Некоректні дані групи", content = @Content),
            @ApiResponse(responseCode = "409", description = "Група з такою назвою вже існує", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Group> create(@RequestBody Group group) {
        Group g = service.create(group);
        return ResponseEntity.created(URI.create("/api/groups/" + g.getId())).body(g);
    }

    @Operation(summary = "Оновити групу", description = "Замінює назву та місткість групи.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Групу оновлено",
                    content = @Content(schema = @Schema(implementation = Group.class))),
            @ApiResponse(responseCode = "400", description = "Некоректні дані групи", content = @Content),
            @ApiResponse(responseCode = "404", description = "Групу не знайдено", content = @Content),
            @ApiResponse(responseCode = "409", description = "Назва вже зайнята", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<Group> update(
            @Parameter(description = "Ідентифікатор групи", example = "3") @PathVariable long id,
            @RequestBody Group group) {
        return ResponseEntity.ok(service.update(id, group));
    }

    @Operation(summary = "Видалити групу", description = "Видаляє групу. Групу, в якій є студенти, видалити не можна.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Групу видалено"),
            @ApiResponse(responseCode = "404", description = "Групу не знайдено", content = @Content),
            @ApiResponse(responseCode = "409", description = "У групі є студенти", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Ідентифікатор групи", example = "3") @PathVariable long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Перевести всіх студентів в іншу групу (транзакція)",
            description = "В одній транзакції переносить усіх студентів групи fromId до групи toGroupId, "
                    + "після чого перевіряє місткість цільової групи. Якщо місткість перевищено — "
                    + "вся зміна відкочується і дані залишаються без змін.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Студентів переведено",
                    content = @Content(schema = @Schema(implementation = TransferResult.class))),
            @ApiResponse(responseCode = "400", description = "Групи збігаються або перевищено місткість (зміни відкочено)", content = @Content),
            @ApiResponse(responseCode = "404", description = "Одну з груп не знайдено", content = @Content)
    })
    @PostMapping("/{fromId}/transfer-students")
    public ResponseEntity<TransferResult> transfer(
            @Parameter(description = "Ідентифікатор вихідної групи", example = "2") @PathVariable long fromId,
            @Parameter(description = "Ідентифікатор цільової групи", example = "4") @RequestParam long toGroupId) {
        return ResponseEntity.ok(service.transferStudents(fromId, toGroupId));
    }

    @Operation(summary = "Створити групу разом зі студентами (транзакція)",
            description = "Створює групу та додає до неї студентів. При transactional=true (за замовчуванням) "
                    + "помилка на будь-якому студенті скасовує все, включно зі створеною групою. "
                    + "При transactional=false збережеться все, що встигло виконатись до помилки (для порівняння).")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Групу зі студентами створено",
                    content = @Content(schema = @Schema(implementation = Group.class))),
            @ApiResponse(responseCode = "400", description = "Помилка в даних студента або перевищено місткість", content = @Content),
            @ApiResponse(responseCode = "409", description = "Група з такою назвою вже існує", content = @Content)
    })
    @PostMapping("/with-students")
    public ResponseEntity<Group> createWithStudents(
            @RequestBody GroupWithStudentsRequest request,
            @Parameter(description = "Виконувати в транзакції", example = "true")
            @RequestParam(defaultValue = "true") boolean transactional) {
        Group g = transactional ? service.createWithStudents(request) : service.createWithStudentsNoTx(request);
        return ResponseEntity.created(URI.create("/api/groups/" + g.getId())).body(g);
    }
}
