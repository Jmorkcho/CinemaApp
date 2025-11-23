package com.finals.cinema.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("access_denied")
@PageTitle("Access Denied")
@AnonymousAllowed
public class AccessDeniedView extends VerticalLayout {

    public AccessDeniedView() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        add(
                new H1("403 - Access Denied"),
                new Paragraph("You don't have permission to access this page."),
                new Button("Return to Home", e -> {
                    UI.getCurrent().navigate(MainView.class);
                })
        );
    }
}