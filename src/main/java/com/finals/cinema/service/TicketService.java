package com.finals.cinema.service;

import com.finals.cinema.DAO.SeatDAO;
import com.finals.cinema.repository.ProjectionRepository;
import com.finals.cinema.repository.TicketRepository;
import com.finals.cinema.util.exceptions.NotFoundException;
import com.finals.cinema.model.DTO.ReserveTicketDTO;
import com.finals.cinema.model.DTO.ResponseTicketDTO;
import com.finals.cinema.model.entity.Projection;
import com.finals.cinema.model.entity.Ticket;
import com.finals.cinema.model.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final ProjectionRepository projectionRepository;
    private final SeatDAO seatDAO;

    public List<ResponseTicketDTO> getAllUserTickets(User user) {
        return ticketRepository.findAllByOwnerId(user.getId());
    }

    /**
     * Reserve a single ticket for a specific seat
     */
    @Transactional
    public ResponseTicketDTO reserveTicket(int projectionId, User user, ReserveTicketDTO reserveTicketDTO) throws SQLException {
        Optional<Projection> sProjection = projectionRepository.findById(projectionId);
        if (sProjection.isEmpty()) {
            throw new NotFoundException("Projection does not exist");
        }
        Projection projection = sProjection.get();
        
        // Check if seat is already taken
        if (!seatDAO.isSeatAvailable(projectionId, reserveTicketDTO.getSeat())) {
            throw new IllegalArgumentException("Seat already taken. Please select a different seat");
        }
        
        // Check if hall is full
        if (seatDAO.getAvailableSeatsCount(projectionId, projection.getHall().getCapacity()) <= 0) {
            throw new IllegalArgumentException("All seats are sold out for this projection");
        }
        
        Ticket ticket = new Ticket();
        ticket.setOwner(user);
        ticket.setProjection(projection);
        ticket.setSeatNumber(reserveTicketDTO.getSeat());
        ticket.setPurchasedAt(LocalDateTime.now());
        
        return new ResponseTicketDTO(ticketRepository.save(ticket));
    }

    /**
     * Reserve multiple tickets for multiple seats
     */
    @Transactional
    public List<ResponseTicketDTO> reserveMultipleTickets(int projectionId, User user, Set<Integer> seatNumbers) throws SQLException {
        Optional<Projection> sProjection = projectionRepository.findById(projectionId);
        if (sProjection.isEmpty()) {
            throw new NotFoundException("Projection does not exist");
        }
        Projection projection = sProjection.get();
        
        // Check if all seats are available
        if (!seatDAO.areSeatAvailable(projectionId, seatNumbers)) {
            throw new IllegalArgumentException("One or more selected seats are already taken");
        }
        
        // Check if hall has enough capacity
        if (seatDAO.getAvailableSeatsCount(projectionId, projection.getHall().getCapacity()) < seatNumbers.size()) {
            throw new IllegalArgumentException("Not enough available seats for this booking");
        }
        
        List<ResponseTicketDTO> reservedTickets = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        
        for (Integer seatNumber : seatNumbers) {
            Ticket ticket = new Ticket();
            ticket.setOwner(user);
            ticket.setProjection(projection);
            ticket.setSeatNumber(seatNumber);
            ticket.setPurchasedAt(now);
            reservedTickets.add(new ResponseTicketDTO(ticketRepository.save(ticket)));
        }
        
        return reservedTickets;
    }
}