package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.exception.BusinessException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.model.Group;
import com.example.demo.model.GroupWithStudentsRequest;
import com.example.demo.model.Student;
import com.example.demo.model.TransferResult;
import com.example.demo.repository.GroupDao;
import com.example.demo.repository.StudentDao;

@Service
public class GroupServiceImpl implements GroupService {

    private static final int DEFAULT_CAPACITY = 30;

    private final GroupDao groupDao;
    private final StudentDao studentDao;

    public GroupServiceImpl(GroupDao groupDao, StudentDao studentDao) {
        this.groupDao = groupDao;
        this.studentDao = studentDao;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Group> getAllGroups() {
        return groupDao.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Group get(long id) {
        return groupDao.findById(id)
                .orElseThrow(() -> new NotFoundException("Групу з id=" + id + " не знайдено"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Student> students(long groupId) {
        get(groupId);
        return studentDao.findByGroupId(groupId);
    }

    @Override
    @Transactional
    public Group create(Group g) {
        long id = groupDao.create(requireName(g.getName()), capacityOf(g.getCapacity()));
        return get(id);
    }

    @Override
    @Transactional
    public Group update(long id, Group g) {
        if (!groupDao.update(id, requireName(g.getName()), capacityOf(g.getCapacity()))) {
            throw new NotFoundException("Групу з id=" + id + " не знайдено");
        }
        return get(id);
    }

    @Override
    @Transactional
    public void delete(long id) {
        if (!groupDao.delete(id)) {
            throw new NotFoundException("Групу з id=" + id + " не знайдено");
        }
    }

    // ---------- транзакційні бізнес-операції ----------

    @Override
    @Transactional
    public TransferResult transferStudents(long fromGroupId, long toGroupId) {
        if (fromGroupId == toGroupId) {
            throw new BusinessException("Вихідна і цільова групи збігаються");
        }
        get(fromGroupId);
        Group target = get(toGroupId);

        // Крок 1: переносимо студентів
        int moved = studentDao.moveAll(fromGroupId, toGroupId);

        // Крок 2: перевіряємо місткість ПІСЛЯ зміни; виняток відкотить крок 1
        int total = studentDao.countByGroupId(toGroupId);
        if (total > target.getCapacity()) {
            throw new BusinessException("Перевищено місткість групи " + target.getName()
                    + ": " + total + " > " + target.getCapacity() + ". Зміни скасовано");
        }
        return new TransferResult(moved, total);
    }

    @Override
    @Transactional
    public Group createWithStudents(GroupWithStudentsRequest request) {
        return doCreateWithStudents(request);
    }

    @Override
    public Group createWithStudentsNoTx(GroupWithStudentsRequest request) {
        return doCreateWithStudents(request);
    }

    private Group doCreateWithStudents(GroupWithStudentsRequest request) {
        int capacity = capacityOf(request.capacity());
        List<Student> students = request.students() == null ? List.of() : request.students();

        long groupId = groupDao.create(requireName(request.name()), capacity);
        int i = 0;
        for (Student s : students) {
            i++;
            if (s.getSurname() == null || s.getSurname().isBlank()) {
                throw new BusinessException("Студент №" + i + ": не вказано прізвище");
            }
            if (s.getName() == null || s.getName().isBlank()) {
                throw new BusinessException("Студент №" + i + ": не вказано ім'я");
            }
            studentDao.create(s.getSurname(), s.getName(), groupId);
        }
        if (students.size() > capacity) {
            throw new BusinessException("Студентів більше, ніж місткість групи");
        }
        return groupDao.findById(groupId).orElseThrow();
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
