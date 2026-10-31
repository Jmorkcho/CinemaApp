package com.finals.cinema.model.DTO;

import com.finals.cinema.model.entity.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ResponseTicketDTO {

    private int id;
    private UserWithoutTicketAndPassDTO owner;
    private ResponseProjectionDTO projection;
    private int seat;
    private LocalDateTime purchasedAt;

    public ResponseTicketDTO(Ticket ticket) {
        id = ticket.getId();
        owner = new UserWithoutTicketAndPassDTO(ticket.getOwner());
        projection = new ResponseProjectionDTO(ticket.getProjection());
        seat = ticket.getSeatNumber();
        purchasedAt = ticket.getPurchasedAt();
    }
}
