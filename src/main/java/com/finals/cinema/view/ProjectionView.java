package com.finals.cinema.view;

import com.finals.cinema.model.entity.Cinema;
import com.finals.cinema.model.entity.Projection;
import com.finals.cinema.repository.CinemaRepository;
import com.finals.cinema.repository.ProjectionRepository;
import com.finals.cinema.view.components.CinemaProgramme;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import static com.finals.cinema.util.Constants.PROJECTION_VIEW_ROUTE;

/**
 * Programme page: pick a cinema first, then see its movies grouped by day,
 * starting from the first day that has projections.
 */
@Route(value = PROJECTION_VIEW_ROUTE, layout = MainLayout.class)
@PageTitle("Programme")
public class ProjectionView extends VerticalLayout {

    private final ProjectionRepository projectionRepository;
    private final VerticalLayout programmeContainer = new VerticalLayout();

    public ProjectionView(ProjectionRepository projectionRepository, CinemaRepository cinemaRepository) {
        this.projectionRepository = projectionRepository;
        addClassName("projections-view");
        setPadding(true);

        List<Cinema> cinemas = cinemaRepository.findAll().stream()
                .sorted(Comparator.comparing(Cinema::getName, String.CASE_INSENSITIVE_ORDER))
                .toList();

        H2 title = new H2("Програма");
        title.getStyle().set("margin", "0");

        ComboBox<Cinema> cinemaSelect = new ComboBox<>("Избор на кино");
        cinemaSelect.setItems(cinemas);
        cinemaSelect.setItemLabelGenerator(c -> c.getName() + " (" + c.getCity() + ")");
        cinemaSelect.setAllowCustomValue(false);
        cinemaSelect.setWidth("22em");
        cinemaSelect.setMaxWidth("100%");

        programmeContainer.setPadding(false);
        programmeContainer.setWidthFull();

        cinemaSelect.addValueChangeListener(e -> showProgramme(e.getValue()));

        add(title, cinemaSelect, programmeContainer);

        if (cinemas.isEmpty()) {
            programmeContainer.add(new Paragraph("Няма налични кина."));
        } else {
            cinemaSelect.setValue(cinemas.get(0));
        }
    }

    private void showProgramme(Cinema cinema) {
        programmeContainer.removeAll();
        if (cinema == null) {
            return;
        }
        List<Projection> upcoming = projectionRepository
                .findByHall_Cinema_IdAndStartAtGreaterThanEqualOrderByStartAtAsc(cinema.getId(), LocalDateTime.now());
        programmeContainer.add(new CinemaProgramme(upcoming));
    }
}
