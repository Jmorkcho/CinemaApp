package com.finals.cinema.view;

import com.finals.cinema.DAO.SeatDAO;
import com.finals.cinema.model.entity.Projection;
import com.finals.cinema.model.entity.User;
import com.finals.cinema.repository.ProjectionRepository;
import com.finals.cinema.service.TicketService;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * BookingView - Handles seat selection and ticket reservation
 * Route: booking/:projectionId
 * Features:
 *  - Display seat grid based on hall capacity
 *  - Show available/reserved/selected seats with different colors
 *  - Allow multi-seat selection
 *  - Reserve tickets to database
 */
@Route(value = "booking/:projectionId", layout = MainLayout.class)
public class BookingView extends VerticalLayout implements BeforeEnterObserver {

    @Autowired
    private ProjectionRepository projectionRepository;

    @Autowired
    private TicketService ticketService;

    @Autowired
    private SeatDAO seatDAO;

    private Set<Integer> selectedSeats = new HashSet<>();
    private Set<Integer> reservedSeats = new HashSet<>();
    private Projection currentProjection;
    private Button confirmButton;
    private Paragraph selectedSeatsDisplay;
    private Paragraph totalPriceDisplay;

    private static final int PRICE_PER_SEAT = 15; // BGN
    private static final int SEATS_PER_ROW = 20;

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        String projectionId = event.getRouteParameters().get("projectionId").orElse(null);
        removeAll();
        setPadding(true);
        setSpacing(true);
        getStyle()
                .set("background-color", "#1a1a1a")
                .set("color", "#ffffff")
                .set("min-height", "100vh");

        if (projectionId != null) {
            try {
                int id = Integer.parseInt(projectionId);
                Optional<Projection> projectionOpt = projectionRepository.findById(id);

                if (projectionOpt.isPresent()) {
                    currentProjection = projectionOpt.get();
                    reservedSeats = seatDAO.getReservedSeats(id);
                    buildBookingUI(currentProjection);
                } else {
                    add(new H1("Projection not found."));
                }
            } catch (NumberFormatException e) {
                add(new H1("Invalid projection ID."));
            }
        } else {
            add(new H1("No projection ID provided."));
        }
    }

    private void buildBookingUI(Projection projection) {
        // Header section
        HorizontalLayout headerLayout = new HorizontalLayout();
        headerLayout.setWidthFull();
        headerLayout.setSpacing(true);
        headerLayout.setPadding(true);
        headerLayout.getStyle()
                .set("background-color", "#0a0a0a")
                .set("margin-bottom", "2em")
                .set("border-bottom", "2px solid #d32f2f");

        VerticalLayout headerContent = new VerticalLayout();
        headerContent.setSpacing(false);
        headerContent.setPadding(false);

        H1 title = new H1(projection.getMovie().getTitle());
        title.getStyle()
                .set("margin", "0")
                .set("color", "#ffffff");

        String projectionInfo = String.format(
                "%s • Hall %d • %s",
                projection.getHall().getCinema().getName(),
                projection.getHall().getNumber(),
                projection.getStartAt().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
        );
        Paragraph info = new Paragraph(projectionInfo);
        info.getStyle()
                .set("margin", "0.5em 0 0 0")
                .set("color", "#888888")
                .set("font-size", "0.95em");

        headerContent.add(title, info);
        headerLayout.add(headerContent);
        add(headerLayout);

        // Main booking layout
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setWidthFull();
        mainLayout.setSpacing(true);
        mainLayout.setPadding(true);
        mainLayout.getStyle()
                .set("margin", "0")
                .set("padding", "0");

        // Seat map section
        HorizontalLayout contentLayout = new HorizontalLayout();
        contentLayout.setWidthFull();
        contentLayout.setSpacing(true);
        contentLayout.getStyle()
                .set("width", "100%")
                .set("max-width", "none")
                .set("flex-wrap", "wrap");

        // Left: Seat grid
        VerticalLayout seatSection = createSeatGridSection(projection);

        // Right: Summary and confirm
        VerticalLayout summarySection = createSummarySection(projection);
        summarySection.setWidth("300px");
        summarySection.getStyle()
                .set("flex", "0 0 300px");

        contentLayout.add(seatSection, summarySection);
        mainLayout.add(contentLayout);
        add(mainLayout);

        // Footer with navigation
        HorizontalLayout footerLayout = new HorizontalLayout();
        footerLayout.setWidthFull();
        footerLayout.setSpacing(true);
        footerLayout.setPadding(true);
        footerLayout.getStyle()
                .set("margin-top", "2em")
                .set("border-top", "1px solid #3a3a3a");

        Button backButton = new Button("Back to Movie", e -> {
            getUI().ifPresent(ui -> ui.navigate("movie-details/" + projection.getMovie().getId()));
        });
        backButton.getStyle()
                .set("background-color", "#3a3a3a")
                .set("color", "#ffffff");

        footerLayout.add(backButton);
        add(footerLayout);
    }

    private VerticalLayout createSeatGridSection(Projection projection) {
        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(true);
        layout.setPadding(true);
        layout.getStyle()
                .set("background-color", "#2a2a2a")
                .set("border-radius", "8px")
                .set("padding", "1.5em")
                .set("box-sizing", "border-box")
                .set("min-width", "0")
                .set("flex", "1 1 560px");

        // Screen illustration, centered above the seats
        Div screen = new Div();
        screen.setText("SCREEN");
        screen.addClassName("seat-screen");
        layout.add(screen);

        int capacity = projection.getHall().getCapacity();
        int numRows = (int) Math.ceil((double) capacity / SEATS_PER_ROW);

        // The scroller only shows a scrollbar when the map cannot shrink any further
        Div seatMap = new Div();
        seatMap.addClassName("seat-map");
        seatMap.getStyle().set("--seats-per-row", String.valueOf(SEATS_PER_ROW));

        for (int row = 0; row < numRows; row++) {
            Div rowLabel = new Div();
            rowLabel.setText(String.valueOf((char) ('A' + row)));
            rowLabel.addClassName("seat-row-label");
            seatMap.add(rowLabel);

            int seatsInRow = Math.min(SEATS_PER_ROW, capacity - (row * SEATS_PER_ROW));
            for (int seatNum = 1; seatNum <= seatsInRow; seatNum++) {
                seatMap.add(createSeat(row * SEATS_PER_ROW + seatNum));
            }
            // Keep every row aligned to the same number of columns
            for (int filler = seatsInRow; filler < SEATS_PER_ROW; filler++) {
                seatMap.add(new Div());
            }
        }

        Div scroller = new Div(seatMap);
        scroller.addClassName("seat-scroller");
        layout.add(scroller);

        // Legend
        HorizontalLayout legend = createLegend();
        legend.setWidthFull();
        legend.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        layout.add(legend);

        return layout;
    }

    private Div createSeat(int seatId) {
        Div seat = new Div();
        seat.addClassName("seat");
        seat.setText(String.valueOf(seatId));

        if (reservedSeats.contains(seatId)) {
            seat.addClassName("seat-reserved");
            seat.setText("✗");
            return seat;
        }

        seat.addClassName("seat-available");
        seat.addClickListener(e -> {
            if (selectedSeats.remove(seatId)) {
                seat.removeClassName("seat-selected");
            } else {
                selectedSeats.add(seatId);
                seat.addClassName("seat-selected");
            }
            updateSummary();
        });

        return seat;
    }


    private HorizontalLayout createLegend() {
        HorizontalLayout legend = new HorizontalLayout();
        legend.setSpacing(true);
        legend.setPadding(true);
        legend.getStyle()
                .set("margin-top", "2em")
                .set("padding-top", "1em")
                .set("border-top", "1px solid #4a4a4a");

        legend.add(createLegendItem("Available", "#2a7f2a"));
        legend.add(createLegendItem("Selected", "#d4af37"));
        legend.add(createLegendItem("Reserved", "#8b0000"));

        return legend;
    }

    private HorizontalLayout createLegendItem(String label, String color) {
        HorizontalLayout item = new HorizontalLayout();
        item.setSpacing(true);
        item.setDefaultVerticalComponentAlignment(Alignment.CENTER);

        Div colorBox = new Div();
        colorBox.setWidth("20px");
        colorBox.setHeight("20px");
        colorBox.getStyle()
                .set("background-color", color)
                .set("border-radius", "3px");

        Span labelSpan = new Span(label);
        labelSpan.getStyle()
                .set("color", "#cccccc")
                .set("font-size", "0.9em");

        item.add(colorBox, labelSpan);
        return item;
    }

    private VerticalLayout createSummarySection(Projection projection) {
        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(true);
        layout.setPadding(true);
        layout.getStyle()
                .set("background-color", "#2a2a2a")
                .set("border-radius", "8px")
                .set("border", "1px solid #3a3a3a");

        H3 summaryTitle = new H3("Order Summary");
        summaryTitle.getStyle()
                .set("margin", "0 0 1em 0")
                .set("color", "#ffffff")
                .set("font-size", "1.1em");
        layout.add(summaryTitle);

        // Movie info
        Div movieInfo = new Div();
        movieInfo.getStyle()
                .set("padding-bottom", "1em")
                .set("border-bottom", "1px solid #3a3a3a");

        Span movieTitle = new Span(projection.getMovie().getTitle());
        movieTitle.getStyle()
                .set("color", "#ffffff")
                .set("font-weight", "bold")
                .set("display", "block")
                .set("margin-bottom", "0.5em");

        Span movieTime = new Span(
                projection.getStartAt().format(DateTimeFormatter.ofPattern("HH:mm"))
        );
        movieTime.getStyle()
                .set("color", "#888888")
                .set("font-size", "0.9em");

        movieInfo.add(movieTitle, movieTime);
        layout.add(movieInfo);

        // Selected seats display
        selectedSeatsDisplay = new Paragraph("No seats selected");
        selectedSeatsDisplay.getStyle()
                .set("color", "#d4af37")
                .set("margin", "1em 0")
                .set("font-weight", "600");
        layout.add(selectedSeatsDisplay);

        // Price display
        totalPriceDisplay = new Paragraph("Total: 0.00 BGN");
        totalPriceDisplay.getStyle()
                .set("color", "#d4af37")
                .set("font-size", "1.2em")
                .set("font-weight", "bold")
                .set("margin", "1em 0");
        layout.add(totalPriceDisplay);

        // Confirm button
        confirmButton = new Button("RESERVE TICKETS");
        confirmButton.setWidthFull();
        confirmButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        confirmButton.getStyle()
                .set("background-color", "#6c757d")
                .set("padding", "1em")
                .set("font-weight", "bold")
                .set("cursor", "pointer");
        confirmButton.getElement().addEventListener("mouseenter", e -> {
            confirmButton.getStyle().set("background-color", "#5a6268");
        });
        confirmButton.getElement().addEventListener("mouseleave", e -> {
            if (confirmButton.isEnabled()) {
                confirmButton.getStyle().set("background-color", "#6c757d");
            }
        });
        confirmButton.setEnabled(false);

        confirmButton.addClickListener(e -> handleReservation());
        layout.add(confirmButton);

        // Available seats info
        int availableCount = projection.getHall().getCapacity() - reservedSeats.size();
        Div availableInfo = new Div();
        availableInfo.setText(availableCount + " seats available");
        availableInfo.getStyle()
                .set("color", "#888888")
                .set("font-size", "0.85em")
                .set("margin-top", "1em")
                .set("padding-top", "1em")
                .set("border-top", "1px solid #3a3a3a");
        layout.add(availableInfo);

        return layout;
    }

    private void updateSummary() {
        if (selectedSeats.isEmpty()) {
            selectedSeatsDisplay.setText("No seats selected");
            totalPriceDisplay.setText("Total: 0.00 BGN");
            confirmButton.setEnabled(false);
        } else {
            List<Integer> sortedSeats = new ArrayList<>(selectedSeats);
            Collections.sort(sortedSeats);

            StringBuilder seatText = new StringBuilder("Seats: ");
            for (int i = 0; i < sortedSeats.size(); i++) {
                if (i > 0) seatText.append(", ");
                int seatNum = sortedSeats.get(i);
                int row = (seatNum - 1) / SEATS_PER_ROW;
                int col = (seatNum - 1) % SEATS_PER_ROW + 1;
                seatText.append((char) ('A' + row)).append(col);
            }
            selectedSeatsDisplay.setText(seatText.toString());

            double totalPrice = selectedSeats.size() * PRICE_PER_SEAT;
            totalPriceDisplay.setText(String.format("Total: %.2f BGN", totalPrice));

            confirmButton.setEnabled(true);
        }
    }

    private void handleReservation() {
        if (selectedSeats.isEmpty()) {
            showNotification("Please select at least one seat", "error");
            return;
        }

        User currentUser = VaadinSession.getCurrent().getAttribute(User.class);
        if (currentUser == null) {
            showNotification("Please log in to book tickets", "error");
            getUI().ifPresent(ui -> ui.navigate("login"));
            return;
        }

        try {
            int reservedCount = selectedSeats.size();
            double totalPrice = (double) reservedCount * PRICE_PER_SEAT;
            ticketService.reserveMultipleTickets(currentProjection.getId(), currentUser, selectedSeats);
            showNotification("Tickets reserved successfully!", "success");

            // Reload seat state so the booked seats are shown as reserved and the selection is cleared
            selectedSeats.clear();
            reservedSeats = seatDAO.getReservedSeats(currentProjection.getId());
            removeAll();
            buildBookingUI(currentProjection);

            Dialog confirmDialog = new Dialog();
            confirmDialog.setHeaderTitle("Reservation Confirmed");

            VerticalLayout dialogLayout = new VerticalLayout();
            dialogLayout.add(
                    new Paragraph("Your tickets have been successfully reserved!"),
                    new Paragraph(String.format("Total seats: %d", reservedCount)),
                    new Paragraph(String.format("Total price: %.2f BGN", totalPrice))
            );

            Button goBackButton = new Button("Back to Movie", e -> {
                confirmDialog.close();
                getUI().ifPresent(ui -> ui.navigate("movie-details/" + currentProjection.getMovie().getId()));
            });

            Button viewTicketsButton = new Button("View My Tickets", e -> {
                confirmDialog.close();
                getUI().ifPresent(ui -> ui.navigate("tickets"));
            });

            HorizontalLayout buttonLayout = new HorizontalLayout(goBackButton, viewTicketsButton);
            dialogLayout.add(buttonLayout);

            confirmDialog.add(dialogLayout);
            confirmDialog.open();

        } catch (Exception e) {
            showNotification("Error reserving tickets: " + e.getMessage(), "error");
        }
    }

    private void showNotification(String message, String type) {
        Notification notification = Notification.show(message, 4000, Notification.Position.TOP_CENTER);
        notification.addThemeVariants("success".equals(type)
                ? NotificationVariant.LUMO_SUCCESS
                : NotificationVariant.LUMO_ERROR);
    }
}
