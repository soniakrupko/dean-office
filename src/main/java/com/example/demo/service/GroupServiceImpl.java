package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.GroupEntity;
import com.example.demo.entity.StudentEntity;
import com.example.demo.exception.BusinessException;
import com.example.demo.exception.ConflictException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.EntityMapper;
import com.example.demo.model.Group;
import com.example.demo.model.GroupWithStudentsRequest;
import com.example.demo.model.Student;
import com.example.demo.model.TransferResult;
import com.example.demo.repository.GroupRepository;
import com.example.demo.repository.StudentRepository;

@Service
public class GroupServiceImpl implements GroupService {

    private static final int DEFAULT_CAPACITY = 30;

    private final GroupRepository groupRepository;
    private final StudentRepository studentRepository;

    public GroupServiceImpl(GroupRepository groupRepository, StudentRepository studentRepository) {
        this.groupRepository = groupRepository;
        this.studentRepository = studentRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Group> getAllGroups() {
        return groupRepository.findAllByOrderByIdAsc().stream().map(EntityMapper::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Group> withFreeSeats() {
        return groupRepository.findWithFreeSeats().stream().map(EntityMapper::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Group get(long id) {
        return EntityMapper.toDto(find(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Group getByName(String name) {
        return groupRepository.findByNameIgnoreCase(name)
                .map(EntityMapper::toDto)
                .orElseThrow(() -> new NotFoundException("Групу '" + name + "' не знайдено"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Student> students(long groupId) {
        find(groupId);
        return studentRepository.findByGroupIdOrdered(groupId).stream().map(EntityMapper::toDto).toList();
    }

    @Override
    @Transactional
    public Group create(Group g) {
        GroupEntity saved = groupRepository.save(new GroupEntity(requireName(g.getName()), capacityOf(g.getCapacity())));
        return EntityMapper.toDto(saved);
    }

    @Override
    @Transactional
    public Group update(long id, Group g) {
        GroupEntity entity = find(id);
        entity.setName(requireName(g.getName()));
        entity.setCapacity(capacityOf(g.getCapacity()));
        return EntityMapper.toDto(groupRepository.save(entity));
    }

    @Override
    @Transactional
    public void delete(long id) {
        find(id);
        if (studentRepository.existsByStudentGroupId(id)) {
            throw new ConflictException("Групу не можна видалити: у ній є студенти");
        }
        groupRepository.deleteById(id);
    }

    // ---------- транзакційні бізнес-операції ----------

    @Override
    @Transactional
    public TransferResult transferStudents(long fromGroupId, long toGroupId) {
        if (fromGroupId == toGroupId) {
            throw new BusinessException("Вихідна і цільова групи збігаються");
        }
        GroupEntity source = find(fromGroupId);
        GroupEntity target = find(toGroupId);

        // Крок 1: переносимо студентів
        int moved = studentRepository.moveAll(source, target);

        // Крок 2: перевіряємо місткість ПІСЛЯ зміни; виняток відкотить крок 1
        long total = studentRepository.countByStudentGroupId(target.getId());
        if (total > target.getCapacity()) {
            throw new BusinessException("Перевищено місткість групи " + target.getName()
                    + ": " + total + " > " + target.getCapacity() + ". Зміни скасовано");
        }
        return new TransferResult(moved, (int) total);
    }

    @Override
    @Transactional
    public Group createWithStudents(GroupWithStudentsRequest request) {
        return doCreateWithStudents(request);
    }

    @Override
    public Group createWithStudentsNoTx(GroupWithStudentsRequest request) {
        // Без @Transactional кожен виклик save() виконується у власній транзакції і фіксується одразу
        return doCreateWithStudents(request);
    }

    private Group doCreateWithStudents(GroupWithStudentsRequest request) {
        int capacity = capacityOf(request.capacity());
        List<Student> students = request.students() == null ? List.of() : request.students();

        GroupEntity group = groupRepository.save(new GroupEntity(requireName(request.name()), capacity));
        int i = 0;
        for (Student s : students) {
            i++;
            if (s.getSurname() == null || s.getSurname().isBlank()) {
                throw new BusinessException("Студент №" + i + ": не вказано прізвище");
            }
            if (s.getName() == null || s.getName().isBlank()) {
                throw new BusinessException("Студент №" + i + ": не вказано ім'я");
            }
            studentRepository.save(new StudentEntity(s.getSurname(), s.getName(), group));
        }
        if (students.size() > capacity) {
            throw new BusinessException("Студентів більше, ніж місткість групи");
        }
        return EntityMapper.toDto(group);
    }

    private GroupEntity find(long id) {
        return groupRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Групу з id=" + id + " не знайдено"));
    }

    private static String requireName(String name) {
        if (name == null || name.isBlank()) {
            throw new BusinessException("Назва групи обов'язкова");
        }
        return name;
    }

    private static int capacityOf(Integer capacity) {
        int c = capacity == null ? DEFAULT_CAPACITY : capacity;
        if (c < 1) {
            throw new BusinessException("Місткість групи має бути не менше 1");
        }
        return c;
    }
}
