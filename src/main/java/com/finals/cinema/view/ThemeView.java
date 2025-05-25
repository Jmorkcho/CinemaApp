package com.finals.cinema.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("dummy")
public class ThemeView extends VerticalLayout {
    protected void applyPersistedTheme() {
        UI.getCurrent().getPage().executeJs(
                "if (localStorage.getItem('darkTheme') === 'true') {" +
                        "  document.documentElement.setAttribute('theme', 'dark');" +
                        "}"
        );
    }
}

