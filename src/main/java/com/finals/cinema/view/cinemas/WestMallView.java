package com.finals.cinema.view.cinemas;

import com.finals.cinema.view.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import static com.finals.cinema.util.Constants.MAIN_VIEW_ROUTE;

@Route(value = "cinemas/west_mall", layout = MainLayout.class)
@PageTitle("Кино Арена West Mall")
public class WestMallView extends com.finals.cinema.view.AbstractCinemaView {
    public WestMallView() {
        addHeader();
        initializeTabs();
        populateTabs();
    }

    private void addHeader() {
        H1 header = new H1("КИНО АРЕНА WEST MALL");
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
                new Paragraph("Десет зали, оборудвани с удобни реклайнери с масичка и USB порт"),
                new Paragraph("WEST MALL разполага с паркинг с 600 места."),
                createWorkingHours(
                        "Понеделник - Петък: 15:00 - 23:00",
                        "Събота - Неделя: 11:00 - 23:00",
                        "* в зависимост от програмата за съответния ден"
                ),
                new Button("ЛОКАЦИЯ", event ->{
                    UI.getCurrent().getPage().setLocation("http://maps.google.com/maps?q=42.7099350,%2023.2718697");
                }),
                new Paragraph("София, кв. Люлин / бул. \"Цар Борис III\" 15, търговски център West Mall")
        );
    }

    @Override
    protected void populateMoviesTab() {
        // In a real app, you would fetch these from a service
        moviesTabContent.add(
                new H2("ТЕКУЩИ ФИЛМОВИ ПРОЕКЦИИ"),
                new Paragraph("Тук ще намерите актуалната програма за кино West Mall"),
                createMovieList() // Implement this method
        );
    }

    @Override
    protected void populateBusinessTab() {
        businessTabContent.add(
                new H2("УСЛУГИ ЗА БИЗНЕС КЛИЕНТИ"),
                new Paragraph("Организирайте вашите корпоративни събития в нашите кина:"),
                new UnorderedList(
                        new ListItem("Кино прожекции за вашия екип"),
                        new ListItem("Частни прожекции за вашите клиенти"),
                        new ListItem("Специални условия за групови резервации")
                ),
                new H3("КОНТАКТИ:"),
                new Paragraph("Телефон: 02 4047 141"),
                new Paragraph("Email: business@cinema-westmall.bg")
        );
    }

    private Component createMovieList() {
        // Implement actual movie list component
        return new Paragraph("Списък с филми ще бъде показан тук");
    }
}