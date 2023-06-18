package com.finals.cinema.view;

import com.finals.cinema.model.DTO.AddMovieDTO;
import com.finals.cinema.util.exceptions.UnauthorizedException;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

@Route(value = "admin_panel")
public class AdminView extends Component {

    TextField tittle;
    NumberField ageRes;
    NumberField genreId;

//    ///why is this here
//    tittle = new TextField("title");
//    ageRes = new NumberField("age");
//    genreId = new NumberField("genre");
//    ///why is this here
//
//    //select.setLabel("Admin Panel");
//
//
//    Button addMovie = new Button("Add Movie", e -> {
//        AddMovieDTO dto = AddMovieDTO.builder().title(tittle.getValue())
//                .ageRestriction(ageRes.getValue().intValue())
//                .genreId(genreId.getValue().intValue()).build();
//        try {
//            movieService.addMovie(dto, 2);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        } catch (UnauthorizedException ex) {
//            ex.printStackTrace();
//        }
//    });
}
