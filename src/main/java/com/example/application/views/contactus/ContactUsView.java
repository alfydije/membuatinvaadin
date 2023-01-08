package com.example.application.views.contactus;

import com.example.application.data.entity.ContactUs;
import com.example.application.data.service.ContactUsService;
import com.example.application.views.MainLayout;
import com.example.application.views.checkout.CheckoutView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import java.util.Optional;
import javax.annotation.security.RolesAllowed;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

@PageTitle("Contact Us")
@Route(value = "Contact-Us/:contactUsID?/:action?(edit)", layout = MainLayout.class)
@RolesAllowed("USER")
public class ContactUsView extends Div implements BeforeEnterObserver {

    private final String CONTACTUS_ID = "contactUsID";
    private final String CONTACTUS_EDIT_ROUTE_TEMPLATE = "Contact-Us/%s/edit";

    private final Grid<ContactUs> grid = new Grid<>(ContactUs.class, false);

    private TextField nama;
    private TextField email;
    private TextField subject;
    private TextField pesan;

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");
    private Button delete = new Button("Delete");

    private final BeanValidationBinder<ContactUs> binder;

    private ContactUs contactUs;

    private final ContactUsService contactUsService;

    public ContactUsView(ContactUsService contactUsService) {
        this.contactUsService = contactUsService;
        addClassNames("contact-us-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("nama").setAutoWidth(true);
        grid.addColumn("email").setAutoWidth(true);
        grid.addColumn("subject").setAutoWidth(true);
        grid.addColumn("pesan").setAutoWidth(true);
        grid.setItems(query -> contactUsService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(CONTACTUS_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(ContactUsView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(ContactUs.class);

        // Bind fields. This is where you'd define e.g. validation rules

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        delete.addClickListener(e -> {
            try{
            if (this.contactUs == null) {
                Notification.show("Silahkan Pilih Pesan");
            } else {
                binder.writeBean(this.contactUs);

                contactUsService.delete(this.contactUs.getId());
                clearForm();
                refreshGrid();
                Notification.show("Pesan Telah dihapus");
                UI.getCurrent().navigate(ContactUsView.class);
            }
        }catch (ValidationException validationException){
                Notification.show("Kesalahan saat Menghapus");
            }
        });

        save.addClickListener(e -> {
            try {
                if (this.contactUs == null) {
                    this.contactUs = new ContactUs();
                }
                binder.writeBean(this.contactUs);
                contactUsService.update(this.contactUs);
                clearForm();
                refreshGrid();
                Notification.show("Data updated");
                UI.getCurrent().navigate(ContactUsView.class);
            } catch (ObjectOptimisticLockingFailureException exception) {
                Notification n = Notification.show(
                        "Error updating the data. Somebody else has updated the record while you were making changes.");
                n.setPosition(Position.MIDDLE);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } catch (ValidationException validationException) {
                Notification.show("Failed to update the data. Check again that all values are valid");
            }
        });
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> contactUsId = event.getRouteParameters().get(CONTACTUS_ID).map(Long::parseLong);
        if (contactUsId.isPresent()) {
            Optional<ContactUs> contactUsFromBackend = contactUsService.get(contactUsId.get());
            if (contactUsFromBackend.isPresent()) {
                populateForm(contactUsFromBackend.get());
            } else {
                Notification.show(String.format("The requested contactUs was not found, ID = %s", contactUsId.get()),
                        3000, Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(ContactUsView.class);
            }
        }
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        nama = new TextField("Nama");
        email = new TextField("Email");
        subject = new TextField("Subject");
        pesan = new TextField("Pesan");
        formLayout.add(nama, email, subject, pesan);

        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        buttonLayout.add(save, delete,cancel);
        editorLayoutDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getDataProvider().refreshAll();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(ContactUs value) {
        this.contactUs = value;
        binder.readBean(this.contactUs);

    }
}
