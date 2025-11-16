package com.finals.cinema.view.cinemas;

import com.finals.cinema.view.cinemas.AbstractCinemaView;
import com.finals.cinema.view.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "cinemas/plovdiv_mall", layout = MainLayout.class)
@PageTitle("Кино Арена Мол Марково тепе")
public class PlovdivMallView extends AbstractCinemaView {
    public PlovdivMallView() {
        addHeader();
        initializeTabs();
        populateTabs();
    }

    private void addHeader() {
        H1 header = new H1("КИНО АРЕНА МОЛ МАРКОВО ТЕПЕ");
        header.getStyle()
                .set("color", "var(--lumo-primary-text-color)")
                .set("margin-bottom", "1rem");
        add(header);
    }

    private void populateTabs() {
        populateAboutTab();
        populateMoviesTab();
        populateBusinessTab();
    }

    @Override
    protected void populateAboutTab() {
        aboutTabContent.add(
                new Paragraph("4 модерни зали, включително Dolby Atmos зала"),
                new Paragraph("Общо 877 места за комфортен преглед."),
                new Paragraph("Мол Марково тепе предлага удобен паркинг."),
                createWorkingHours(
                        "Понеделник - Петък: 15:00 - 23:00",
                        "Събота - Неделя: 11:00 - 21:00",
                        "* в зависимост от програмата за съответния ден"
                ),
                new Button("ЛОКАЦИЯ", event ->{
                    UI.getCurrent().getPage().setLocation("https://www.google.com/maps?q=42.1412192,24.7405071");
                }),
                new Paragraph("Пловдив, бул. \"Руски\" 22, мол Марково тепе")
        );
    }

    @Override
    protected void populateMoviesTab() {
        moviesTabContent.add(
                new H2("ФИЛМОВА ПРОГРАМА"),
                new Paragraph("Актуални прожекции в Пловдив:"),
                createMovieList() // Implement this method
        );
    }

    @Override
    protected void populateBusinessTab() {
        businessTabContent.add(
                new H2("БИЗНЕС ОФЕРТИ"),
                new Paragraph("Специални условия за компании:"),
                new UnorderedList(
                        new ListItem("Групови резервации"),
                        new ListItem("Частни прожекции"),
                        new ListItem("Корпоративни събития")
                ),
                new H3("КОНТАКТИ:"),
                new Paragraph("Телефон: 032 123 456"),
                new Paragraph("Email: events@cinema-plovdiv.bg")
        );
    }

    private Component createMovieList() {
        return new Paragraph("Списък с филми ще бъде показан тук");
    }
}