package com.example.application.views.dataproduk;

import com.example.application.data.entity.DataProduk;
import com.example.application.data.service.DataProdukService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

@PageTitle("Data Produk")
@Route(value = "Data-Produk/:dataProdukID?/:action?(edit)", layout = MainLayout.class)
@AnonymousAllowed
@Uses(Icon.class)
public class DataProdukView extends Div implements BeforeEnterObserver {

    private final String DATAPRODUK_ID = "dataProdukID";
    private final String DATAPRODUK_EDIT_ROUTE_TEMPLATE = "Data-Produk/%s/edit";

    private final Grid<DataProduk> grid = new Grid<>(DataProduk.class, false);

    private TextField nama;
    private TextField harga;
    private TextField detail;
    private Upload gambar;
    private Image gambarPreview;

    private Checkbox important;

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");

    private final BeanValidationBinder<DataProduk> binder;

    private DataProduk dataProduk;

    private final DataProdukService dataProdukService;

    public DataProdukView(DataProdukService dataProdukService) {
        this.dataProdukService = dataProdukService;
        addClassNames("data-produk-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("nama").setAutoWidth(true);
        grid.addColumn("harga").setAutoWidth(true);
        grid.addColumn("detail").setAutoWidth(true);
        LitRenderer<DataProduk> gambarRenderer = LitRenderer.<DataProduk>of(
                "<span style='border-radius: 50%; overflow: hidden; display: flex; align-items: center; justify-content: center; width: 64px; height: 64px'><img style='max-width: 100%' src=${item.gambar} /></span>")
                .withProperty("gambar", item -> {
                    if (item != null && item.getGambar() != null) {
                        return "data:image;base64," + Base64.getEncoder().encodeToString(item.getGambar());
                    } else {
                        return "";
                    }
                });
        grid.addColumn(gambarRenderer).setHeader("Gambar").setWidth("96px").setFlexGrow(0);

        LitRenderer<DataProduk> importantRenderer = LitRenderer.<DataProduk>of(
                "<vaadin-icon icon='vaadin:${item.icon}' style='width: var(--lumo-icon-size-s); height: var(--lumo-icon-size-s); color: ${item.color};'></vaadin-icon>")
                .withProperty("icon", important -> important.isImportant() ? "check" : "minus").withProperty("color",
                        important -> important.isImportant()
                                ? "var(--lumo-primary-text-color)"
                                : "var(--lumo-disabled-text-color)");

        grid.addColumn(importantRenderer).setHeader("Important").setAutoWidth(true);

        grid.setItems(query -> dataProdukService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(DATAPRODUK_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(DataProdukView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(DataProduk.class);

        // Bind fields. This is where you'd define e.g. validation rules

        binder.bindInstanceFields(this);

        attachImageUpload(gambar, gambarPreview);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.dataProduk == null) {
                    this.dataProduk = new DataProduk();
                }
                binder.writeBean(this.dataProduk);
                dataProdukService.update(this.dataProduk);
                clearForm();
                refreshGrid();
                Notification.show("Data updated");
                UI.getCurrent().navigate(DataProdukView.class);
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
        Optional<Long> dataProdukId = event.getRouteParameters().get(DATAPRODUK_ID).map(Long::parseLong);
        if (dataProdukId.isPresent()) {
            Optional<DataProduk> dataProdukFromBackend = dataProdukService.get(dataProdukId.get());
            if (dataProdukFromBackend.isPresent()) {
                populateForm(dataProdukFromBackend.get());
            } else {
                Notification.show(String.format("The requested dataProduk was not found, ID = %s", dataProdukId.get()),
                        3000, Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(DataProdukView.class);
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
        harga = new TextField("Harga");
        detail = new TextField("Detail");
        Label gambarLabel = new Label("Gambar");
        gambarPreview = new Image();
        gambarPreview.setWidth("100%");
        gambar = new Upload();
        gambar.getStyle().set("box-sizing", "border-box");
        gambar.getElement().appendChild(gambarPreview.getElement());
        important = new Checkbox("Important");
        formLayout.add(nama, harga, detail, gambarLabel, gambar, important);

        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(save, cancel);
        editorLayoutDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

    private void attachImageUpload(Upload upload, Image preview) {
        ByteArrayOutputStream uploadBuffer = new ByteArrayOutputStream();
        upload.setAcceptedFileTypes("image/*");
        upload.setReceiver((fileName, mimeType) -> {
            uploadBuffer.reset();
            return uploadBuffer;
        });
        upload.addSucceededListener(e -> {
            StreamResource resource = new StreamResource(e.getFileName(),
                    () -> new ByteArrayInputStream(uploadBuffer.toByteArray()));
            preview.setSrc(resource);
            preview.setVisible(true);
            if (this.dataProduk == null) {
                this.dataProduk = new DataProduk();
            }
            this.dataProduk.setGambar(uploadBuffer.toByteArray());
        });
        preview.setVisible(false);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getDataProvider().refreshAll();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(DataProduk value) {
        this.dataProduk = value;
        binder.readBean(this.dataProduk);
        this.gambarPreview.setVisible(value != null);
        if (value == null || value.getGambar() == null) {
            this.gambar.clearFileList();
            this.gambarPreview.setSrc("");
        } else {
            this.gambarPreview.setSrc("data:image;base64," + Base64.getEncoder().encodeToString(value.getGambar()));
        }

    }
}
