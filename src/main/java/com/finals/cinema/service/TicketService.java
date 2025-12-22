package com.finals.cinema.service;

import com.finals.cinema.repository.ProjectionRepository;
import com.finals.cinema.repository.TicketRepository;
import com.finals.cinema.util.exceptions.NotFoundException;
import com.finals.cinema.model.DTO.ReserveTicketDTO;
import com.finals.cinema.model.DTO.ResponseTicketDTO;
import com.finals.cinema.model.DTO.StatisticsDTO;
import com.finals.cinema.model.entity.Projection;
import com.finals.cinema.model.entity.Ticket;
import com.finals.cinema.model.entity.User;
import com.finals.cinema.util.exceptions.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final ProjectionRepository projectionRepository;

    public List<ResponseTicketDTO> getAllUserTickets(User user) {
        return ticketRepository.findAllByOwnerId(user.getId());
    }

    //TODO

//    @Transactional
//    public ResponseTicketDTO reserveTicket(int projectionId, User user, ReserveTicketDTO reserveTicketDTO) throws BadRequestException, SQLException {
//        Optional<Projection> sProjection = projectionRepository.findById(projectionId);
//        if (sProjection.isEmpty()) {
//            throw new NotFoundException("Projection does not exist");
//        }
//        Projection projection = sProjection.get();
//        if (seatsAreTaken(projection, reserveTicketDTO.getSeat())) {
//            throw new IllegalArgumentException("Seat already taken.Please select different seat");
//        }
//        Ticket ticket = Ticket.builder()
//                .owner(user)
//                .projection(projection)
//                .seat(reserveTicketDTO.getSeat())
//                .purchasedAt(LocalDateTime.now())
//                .build();
////        seatDAO.reserveSeat(projectionId, reserveTicketDTO.getSeat());
//        return new ResponseTicketDTO(ticketRepository.save(ticket));
//    }

//    private boolean seatsAreTaken(Projection projection, int seat) throws BadRequestException {
//        List<Integer> freeSeats = seatDAO.getReservedSeats(projection.getId());
//        if (freeSeats.size() == projection.getHall().getCapacity()) {
//            throw new IllegalArgumentException("We are sorry, all seats are taken");
//        }
//        return freeSeats.contains(seat);
//    }

//    public List<StatisticsDTO> getAllSoldTickets(User user) throws UnauthorizedException {
//        if (!isNotAdmin(user.getId())) {
//            throw new UnauthorizedException("Only admins can get statistic");
//        }
//        return statisticsDAO.soldTicketsPerProjection();
//    }
}