package com.finals.cinema.view;

import com.finals.cinema.model.entity.User;
import com.finals.cinema.model.DTO.ResponseTicketDTO;
import com.finals.cinema.service.TicketService;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.finals.cinema.util.Constants.TICKET_VIEW_ROUTE;

@Route(value = TICKET_VIEW_ROUTE, layout = MainLayout.class)
public class TicketView extends VerticalLayout {

    @Autowired
    private TicketService ticketService;

    @Override
    protected void onAttach(com.vaadin.flow.component.AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        buildTicketView();
    }

    private void buildTicketView() {
        removeAll();
        setPadding(true);
        setSpacing(true);
        getStyle()
                .set("background-color", "#1a1a1a")
                .set("color", "#ffffff")
                .set("min-height", "100vh");

        // Header
        H1 title = new H1("My Tickets");
        title.getStyle()
                .set("color", "#ffffff")
                .set("margin-bottom", "1.5em");
        add(title);

        // Get logged-in user
        User currentUser = VaadinSession.getCurrent().getAttribute(User.class);
        if (currentUser == null) {
            Paragraph notLogged = new Paragraph("Please log in to view your tickets");
            notLogged.getStyle().set("color", "#888888");
            add(notLogged);
            return;
        }

        // Get user's tickets
        List<ResponseTicketDTO> userTickets = ticketService.getAllUserTickets(currentUser);

        if (userTickets == null || userTickets.isEmpty()) {
            Paragraph noTickets = new Paragraph("You have no tickets yet.");
            noTickets.getStyle()
                    .set("color", "#888888")
                    .set("font-size", "1.1em")
                    .set("padding", "2em");
            add(noTickets);
            return;
        }

        // Display tickets
        VerticalLayout ticketsContainer = new VerticalLayout();
        ticketsContainer.setSpacing(true);
        ticketsContainer.setPadding(false);
        ticketsContainer.setWidthFull();

        for (ResponseTicketDTO ticket : userTickets) {
            ticketsContainer.add(createTicketCard(ticket));
        }

        add(ticketsContainer);
    }

    private VerticalLayout createTicketCard(ResponseTicketDTO ticket) {
        VerticalLayout card = new VerticalLayout();
        card.setWidthFull();
        card.setSpacing(true);
        card.setPadding(true);
        card.getStyle()
                .set("background-color", "#2a2a2a")
                .set("border-radius", "8px")
                .set("border-left", "4px solid #d32f2f")
                .set("padding", "1.5em");

        // Movie title
        H3 movieTitle = new H3(ticket.getProjection().getMovie().getTitle());
        movieTitle.getStyle()
                .set("margin", "0 0 0.5em 0")
                .set("color", "#ffffff");
        card.add(movieTitle);

        // Cinema and Hall
        HorizontalLayout cinemaLayout = new HorizontalLayout();
        cinemaLayout.setSpacing(true);
        cinemaLayout.setDefaultVerticalComponentAlignment(Alignment.CENTER);

        Span cinema = new Span(ticket.getProjection().getCinema().getName());
        cinema.getStyle()
                .set("color", "#d4af37")
                .set("font-weight", "bold");

        Span hall = new Span("Hall " + ticket.getProjection().getHall().getNumber());
        hall.getStyle()
                .set("color", "#888888")
                .set("margin-left", "1em");

        cinemaLayout.add(cinema, hall);
        card.add(cinemaLayout);

        // Projection date and time
        String dateTime = ticket.getProjection().getStartAt()
                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
        Paragraph projectionInfo = new Paragraph(dateTime);
        projectionInfo.getStyle()
                .set("color", "#cccccc")
                .set("margin", "0.5em 0")
                .set("font-size", "0.95em");
        card.add(projectionInfo);

        // Seat number
        HorizontalLayout seatLayout = new HorizontalLayout();
        seatLayout.setSpacing(true);
        seatLayout.setDefaultVerticalComponentAlignment(Alignment.CENTER);

        Span seatLabel = new Span("Seat:");
        seatLabel.getStyle().set("color", "#888888");

        Span seatNumber = new Span(String.valueOf(ticket.getSeat()));
        seatNumber.getStyle()
                .set("color", "#d4af37")
                .set("font-weight", "bold")
                .set("font-size", "1.2em");

        seatLayout.add(seatLabel, seatNumber);
        card.add(seatLayout);

        // Purchased date
        String purchasedDate = ticket.getPurchasedAt() != null ?
                ticket.getPurchasedAt().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) : "N/A";
        Paragraph purchasedInfo = new Paragraph("Purchased: " + purchasedDate);
        purchasedInfo.getStyle()
                .set("color", "#888888")
                .set("font-size", "0.85em")
                .set("margin", "1em 0 0 0");
        card.add(purchasedInfo);

        return card;
    }
}
