package com.finals.cinema.view;

import com.finals.cinema.model.entity.Cinema;
import com.finals.cinema.model.repository.CinemaRepository;
import com.finals.cinema.service.CinemaService;
import com.finals.cinema.view.components.CinemaCard;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import java.awt.*;

import static com.finals.cinema.util.Constants.CINEMA_VIEW_ROUTE;

@Route(value = CINEMA_VIEW_ROUTE, layout = MainLayout.class)
public class CinemaView extends VerticalLayout {

    public CinemaView(CinemaRepository repository) {
        CinemaCard westMallCard = new CinemaCard(
                "КИНО АРЕНА WEST MALL",
                "гр.София, Люлин 7, Бул \"Царица Йоанна\" 15, търговски център West Mall, етаж 2",
                "02 4047 141",
                "/img/west-mall.png",
                "cinemas/west_mall"
        );

        CinemaCard theMallCard = new CinemaCard(
                "КИНО АРЕНА THE MALL",
                "гр. София, бул. Цариградско шосе 115, Търговски център The MALL",
                "02 4047 121",
                "/img/the-mall.png",
                "cinemas/the_mall"
        );

        CinemaCard plovdivCard = new CinemaCard(
                "КИНО АРЕНА МОЛ МАРКОВО ТЕПЕ ПЛОВДИВ",
                "гр. Пловдив, бул. Руски 54, Търговски център Мол Марково Тепе",
                "02 4047 125",
                "/img/plovdiv.png",
                "cinemas/plovdiv_mall"
        );

        HorizontalLayout cinemaRow = new HorizontalLayout(westMallCard, theMallCard, plovdivCard);
        cinemaRow.setSpacing(true);

        addClassName("cinema-view");
        setSizeFull();

        add(cinemaRow);
    }

}

