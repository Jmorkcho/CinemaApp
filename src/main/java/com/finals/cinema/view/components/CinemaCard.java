package com.finals.cinema.view.components;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.flow.component.orderedlayout.FlexComponent;

public class CinemaCard extends Div {

    public CinemaCard(String title, String address, String phone, String imageUrl, String navigateTo) {
        addClassName("cinema-card");
        getStyle()
                .set("width", "308px")
                .set("height", "320px")
                .set("border-radius", "8px")
                .set("overflow", "hidden")
                .set("cursor", "pointer")
                .set("background", "var(--lumo-base-color)")
                .set("transition", "transform 0.3s ease, background-color 0.3s ease")
                .set("display", "flex")
                .set("flex-direction", "column");

        Image image = new Image(imageUrl, title);
        image.setWidth("100%");
        image.setHeight("180px");
        image.getStyle()
                .set("object-fit", "cover")
                .set("flex-shrink", "0");

        Paragraph titleText = new Paragraph(title);
        titleText.addClassNames(
                LumoUtility.TextColor.HEADER,
                LumoUtility.FontWeight.BOLD,
                LumoUtility.FontSize.LARGE,
                LumoUtility.Margin.NONE,
                LumoUtility.TextAlignment.CENTER
        );

        Span addressText = new Span(address);
        addressText.addClassNames(
                LumoUtility.TextColor.SECONDARY,
                LumoUtility.FontSize.SMALL,
                LumoUtility.Margin.NONE,
                LumoUtility.TextAlignment.CENTER
        );
        addressText.getStyle().set("white-space", "pre-line");

        Span phoneText = new Span(phone);
        phoneText.addClassNames(
                LumoUtility.TextColor.SECONDARY,
                LumoUtility.FontSize.SMALL,
                LumoUtility.Margin.NONE,
                LumoUtility.TextAlignment.CENTER
        );

        VerticalLayout textContainer = new VerticalLayout(titleText, addressText, phoneText);
        textContainer.addClassNames(LumoUtility.Padding.SMALL);
        textContainer.setSpacing(false);
        textContainer.setPadding(false);
        textContainer.setAlignItems(FlexComponent.Alignment.CENTER);
        textContainer.getStyle()
                .set("flex-grow", "1")
                .set("overflow", "hidden");

        addClickListener(event -> UI.getCurrent().navigate(navigateTo));

        add(image, textContainer);
    }
}