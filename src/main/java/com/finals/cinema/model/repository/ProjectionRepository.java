package com.finals.cinema.model.repository;

import com.finals.cinema.model.entity.Hall;
import com.finals.cinema.model.entity.Projection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ProjectionRepository extends JpaRepository<Projection, Integer> {


    List<Projection> findByHall(Hall hall);
    List<Projection> findByHallId(int hallId);
    List<Projection> findByMovie_id(int movieID);

}
