package com.finals.cinema.repository;

import com.finals.cinema.model.entity.Hall;
import com.finals.cinema.model.entity.Projection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface ProjectionRepository extends JpaRepository<Projection, Integer> {


    List<Projection> findByHall(Hall hall);
    List<Projection> findByHallId(int hallId);
    List<Projection> findByMovie_id(int movieID);

}
