package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.example.demo.entity.GroupEntity;

public interface GroupRepository extends CrudRepository<GroupEntity, Long> {

    // 5.2. Методи пошуку, які Spring Data генерує за назвою
    Optional<GroupEntity> findByNameIgnoreCase(String name);

    List<GroupEntity> findAllByOrderByIdAsc();

    // 5.1.2. Запит із @NamedQuery: Spring Data шукає запит з іменем "GroupEntity.findWithFreeSeats"
    List<GroupEntity> findWithFreeSeats();
}
