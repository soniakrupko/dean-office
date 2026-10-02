package com.example.demo.service;

import java.util.List;

import com.example.demo.model.Group;
import com.example.demo.model.GroupWithStudentsRequest;
import com.example.demo.model.Student;
import com.example.demo.model.TransferResult;

public interface GroupService {
    List<Group> getAllGroups();
    Group get(long id);
    List<Student> students(long groupId);
    Group create(Group group);
    Group update(long id, Group group);
    void delete(long id);

    /** Транзакційно переводить усіх студентів з однієї групи в іншу. */
    TransferResult transferStudents(long fromGroupId, long toGroupId);

    /** Транзакційно створює групу зі студентами (усе або нічого). */
    Group createWithStudents(GroupWithStudentsRequest request);

    /** Те саме, але БЕЗ транзакції — для порівняння поведінки при помилці. */
    Group createWithStudentsNoTx(GroupWithStudentsRequest request);
}
