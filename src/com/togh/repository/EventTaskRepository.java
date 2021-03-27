package com.togh.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.togh.entity.EventTaskEntity;

public interface EventTaskRepository extends JpaRepository<EventTaskEntity, Long>  {

    public EventTaskEntity findById(long id);

}
