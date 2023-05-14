package com.finals.cinema.model.repository;

import com.finals.cinema.model.DTO.ResponseTicketDTO;
import com.finals.cinema.model.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface TicketRepository extends JpaRepository<Ticket, Integer> {

    List<ResponseTicketDTO> findAllByOwnerId(int id);


}
