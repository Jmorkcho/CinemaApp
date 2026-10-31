package com.finals.cinema.view.cinemas;

import com.finals.cinema.model.entity.Projection;
import com.finals.cinema.repository.CinemaRepository;
import com.finals.cinema.repository.ProjectionRepository;
import com.finals.cinema.view.components.CinemaProgramme;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;

import java.time.LocalDateTime;
import java.util.List;

public abstract class AbstractCinemaView extends VerticalLayout {
    protected VerticalLayout aboutTabContent;
    protected VerticalLayout moviesTabContent;
    protected VerticalLayout businessTabContent;

    private final ProjectionRepository projectionRepository;
    private final CinemaRepository cinemaRepository;
    private final String cinemaName;

    /**
     * @param cinemaName name of the matching row in the cinemas table, used to load the projections
     */
    protected AbstractCinemaView(ProjectionRepository projectionRepository,
                                 CinemaRepository cinemaRepository,
                                 String cinemaName) {
        this.projectionRepository = projectionRepository;
        this.cinemaRepository = cinemaRepository;
        this.cinemaName = cinemaName;
        setPadding(true);
        setSpacing(false);
    }

    protected Component createMovieList() {
        List<Projection> upcoming = cinemaRepository.findFirstByNameIgnoreCase(cinemaName)
                .map(cinema -> projectionRepository
                        .findByHall_Cinema_IdAndStartAtGreaterThanEqualOrderByStartAtAsc(
                                cinema.getId(), LocalDateTime.now()))
                .orElse(List.of());
        return new CinemaProgramme(upcoming);
    }

    protected void initializeTabs() {
        // Create tabs
        Tab aboutTab = new Tab(createTabLabel("ЗА КИНОТО"));
        Tab moviesTab = new Tab(createTabLabel("ФИЛМИ"));
        Tab businessTab = new Tab(createTabLabel("БИЗНЕС КЛИЕНТИ"));
        Tabs tabs = new Tabs(aboutTab, moviesTab, businessTab);
        tabs.setWidthFull();

        // Create content containers
        aboutTabContent = new VerticalLayout();
        moviesTabContent = new VerticalLayout();
        businessTabContent = new VerticalLayout();

        // Configure content containers
        configureTabContent(aboutTabContent);
        configureTabContent(moviesTabContent);
        configureTabContent(businessTabContent);

        // Only show the selected tab's content
        aboutTabContent.setVisible(true);
        moviesTabContent.setVisible(false);
        businessTabContent.setVisible(false);

        // Tab selection listener
        tabs.addSelectedChangeListener(event -> {
            aboutTabContent.setVisible(event.getSelectedTab() == aboutTab);
            moviesTabContent.setVisible(event.getSelectedTab() == moviesTab);
            businessTabContent.setVisible(event.getSelectedTab() == businessTab);
        });

        add(tabs);
        add(aboutTabContent, moviesTabContent, businessTabContent);
    }

    private Span createTabLabel(String text) {
        Span span = new Span(text);
        span.getStyle().set("font-weight", "600");
        return span;
    }

    private void configureTabContent(VerticalLayout content) {
        content.setSpacing(false);
        content.setPadding(true);
        content.getStyle()
                .set("border", "1px solid var(--lumo-contrast-10pct)")
                .set("border-top", "none")
                .set("border-radius", "0 0 4px 4px");
    }

    protected Component createWorkingHours(String weekdays, String weekends, String note) {
        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(false);
        layout.setPadding(false);

        layout.add(new H3("РАБОТНО ВРЕМЕ:"));
        layout.add(new UnorderedList(
                new ListItem(weekdays),
                new ListItem(weekends)
        ));
        Paragraph noteParagraph = new Paragraph(note);
        noteParagraph.getStyle()
                .set("font-style", "italic")
                .set("margin-top", "0.5rem");
        layout.add(noteParagraph);

        return layout;
    }

    protected abstract void populateAboutTab();
    protected abstract void populateMoviesTab();
    protected abstract void populateBusinessTab();
}