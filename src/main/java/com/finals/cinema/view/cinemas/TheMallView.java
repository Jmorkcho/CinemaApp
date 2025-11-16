package com.finals.cinema.view.cinemas;

import com.finals.cinema.view.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "cinemas/the_mall", layout = MainLayout.class)
@PageTitle("Кино Арена The Mall")
public class TheMallView extends AbstractCinemaView {
    public TheMallView() {
        addHeader();
        initializeTabs();
        populateTabs();
    }

    private void addHeader() {
        H1 header = new H1("КИНО АРЕНА THE MALL");
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
                new Paragraph("12 модерни зали, включително IMAX и 4DX"),
                new Paragraph("THE MALL разполага с подземен паркинг с 800 места."),
                createWorkingHours(
                        "Понеделник - Петък: 15:00 - 23:00",
                        "Събота - Неделя: 11:00 - 23:00",
                        "* в зависимост от програмата за съответния ден"
                ),
                new Button("ЛОКАЦИЯ", event ->{
                    UI.getCurrent().getPage().setLocation("http://maps.google.com/maps?q=42.660950,%2023.381726");
                }),
                new Paragraph("София, бул. \"Цариградско шосе\" 115, търговски център The Mall")
        );
    }

    @Override
    protected void populateMoviesTab() {
        moviesTabContent.add(
                new H2("ТЕКУЩА ПРОГРАМА"),
                new Paragraph("Актуални филми и прожекции в The Mall:"),
                createMovieList() // Implement this method
        );
    }

    @Override
    protected void populateBusinessTab() {
        businessTabContent.add(
                new H2("КОРПОРАТИВНИ УСЛУГИ"),
                new Paragraph("Специални предложения за бизнес клиенти:"),
                new UnorderedList(
                        new ListItem("Кино вечери за вашия екип"),
                        new ListItem("Прожекции за партньори и клиенти"),
                        new ListItem("Рекламни възможности преди филмите")
                ),
                new H3("КОНТАКТИ:"),
                new Paragraph("Телефон: 02 4047 142"),
                new Paragraph("Email: corporate@cinema-themall.bg")
        );
    }

    private Component createMovieList() {
        return new Paragraph("Списък с филми ще бъде показан тук");
    }
}