package com.example;

import java.time.LocalDateTime;

import com.example.model.Categoria;
import com.example.model.Mesero;
import com.example.model.Proveedor;
import com.example.model.Usuario;
import com.example.service.CrudService;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Gestión CRUD - Sistema de Inventario")
@Route("")
public class MainView extends VerticalLayout {

    private final CrudService crudService;

    public MainView(CrudService crudService) {
        this.crudService = crudService;
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        TabSheet tabs = new TabSheet();
        tabs.setWidthFull();
        tabs.add("Meseros", crearSeccionMeseros());
        tabs.add("Categorías", crearSeccionCategorias());
        tabs.add("Proveedores", crearSeccionProveedores());
        tabs.add("Usuarios", crearSeccionUsuarios());

        add(new H2("Sistema de Inventario - CRUD"), tabs);
    }

    // ==========================================
    // 1. MESERO
    // ==========================================
    private Component crearSeccionMeseros() {
    VerticalLayout layout = new VerticalLayout();

    TextField idField = new TextField("ID");
    TextField nombreField = new TextField("Nombre");
    TextField telefonoField = new TextField("Teléfono");

    ComboBox<String> turnoField = new ComboBox<>("Turnos");
    turnoField.setItems("Mañana", "Tarde", "Noche");

    FormLayout form = new FormLayout(
        idField,
        nombreField,
        telefonoField,
        turnoField
    );

    Grid<Mesero> grid = new Grid<>(Mesero.class, false);

    grid.addColumn(m -> m.getId()).setHeader("ID").setAutoWidth(true);
    grid.addColumn(m -> m.getNombre()).setHeader("Nombre").setAutoWidth(true);
    grid.addColumn(m -> m.getTelefono()).setHeader("Teléfono").setAutoWidth(true);
    grid.addColumn(m -> m.getTurno()).setHeader("Turnos").setAutoWidth(true);


    Runnable refrescar = () -> grid.setItems(
        crudService.listarTodos(Mesero.class)
    );

    refrescar.run();

    grid.asSingleSelect().addValueChangeListener(e -> {
        Mesero m = e.getValue();

        if (m != null) {
            idField.setValue(m.getId() != null ? m.getId().toString() : "");
            nombreField.setValue(m.getNombre() != null ? m.getNombre() : "");
            telefonoField.setValue(m.getTelefono() != null ? m.getTelefono() : "");
            turnoField.setValue(m.getTurno() != null ? m.getTurno() : "");
        }
    });

    Button btnCrear = new Button("Crear", e -> {

        Mesero m = new Mesero(
            null,
            nombreField.getValue(),
            telefonoField.getValue(),
            turnoField.getValue()
        );

        crudService.guardar(m);
        refrescar.run();

        Notification.show("Mesero guardado con ID: " + m.getId());
    });

    btnCrear.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

    Button btnConsultar = new Button("Consultar", e -> {

        Mesero m = crudService.buscarPorId(Mesero.class, parseId(idField));

        if (m != null) {
            grid.asSingleSelect().setValue(m);
            Notification.show("Encontrado: " + m.getNombre());
        } else {
            Notification.show("No encontrado");
        }
    });

    Button btnActualizar = new Button("Actualizar", e -> {

        Mesero m = crudService.buscarPorId(Mesero.class, parseId(idField));

        if (m != null) {
            m.setNombre(nombreField.getValue());
            m.setTelefono(telefonoField.getValue());
            m.setTurno(turnoField.getValue());

            crudService.actualizar(m);
            refrescar.run();

            Notification.show("Mesero actualizado");
        } else {
            Notification.show("ID no encontrado");
        }
    });

    Button btnEliminar = new Button("Eliminar", e -> {

        crudService.eliminar(Mesero.class, parseId(idField));
        refrescar.run();

        limpiar(idField, nombreField, telefonoField);
        turnoField.clear();

        Notification.show("Mesero eliminado");
    });

    btnEliminar.addThemeVariants(ButtonVariant.LUMO_ERROR);

    Button btnLimpiar = new Button("Limpiar", e -> {

        limpiar(idField, nombreField, telefonoField);
        turnoField.clear();

        grid.asSingleSelect().clear();
    });

    layout.add(
        form,
        crearAcciones(btnCrear, btnConsultar, btnActualizar, btnEliminar, btnLimpiar),
        grid
    );

    return layout;
}

    // ==========================================
    // 2. CATEGORÍAS
    // ==========================================
    private Component crearSeccionCategorias() {
        VerticalLayout layout = new VerticalLayout();
        TextField idField = new TextField("ID");
        TextField nombreField = new TextField("Nombre");
        TextField descripcionField = new TextField("Descripción");

        FormLayout form = new FormLayout(idField, nombreField, descripcionField);

        Grid<Categoria> grid = new Grid<>(Categoria.class, false);
        grid.addColumn(c -> c.getId()).setHeader("ID").setAutoWidth(true);
        grid.addColumn(c -> c.getNombre()).setHeader("Nombre").setAutoWidth(true);
        grid.addColumn(c -> c.getDescripcion()).setHeader("Descripción").setAutoWidth(true);

        Runnable refrescar = () -> grid.setItems(crudService.listarTodos(Categoria.class));
        refrescar.run();

        grid.asSingleSelect().addValueChangeListener(e -> {
            Categoria c = e.getValue();
            if (c != null) {
                idField.setValue(c.getId() != null ? c.getId().toString() : "");
                nombreField.setValue(c.getNombre() != null ? c.getNombre() : "");
                descripcionField.setValue(c.getDescripcion() != null ? c.getDescripcion() : "");
            }
        });

        Button btnCrear = new Button("Crear", e -> {
            Categoria c = new Categoria(nombreField.getValue(), descripcionField.getValue());
            crudService.guardar(c);
            refrescar.run();
            idField.setValue(c.getId() != null ? c.getId().toString() : "");
            Notification.show("Categoría guardada con ID: " + c.getId());
        });
        btnCrear.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button btnConsultar = new Button("Consultar", e -> {
            Categoria c = crudService.buscarPorId(Categoria.class, parseId(idField));
            if (c != null) {
                grid.asSingleSelect().setValue(c);
                Notification.show("Encontrada: " + c.getNombre());
            } else {
                Notification.show("No encontrada");
            }
        });

        Button btnActualizar = new Button("Actualizar", e -> {
            Categoria c = crudService.buscarPorId(Categoria.class, parseId(idField));
            if (c != null) {
                c.setNombre(nombreField.getValue());
                c.setDescripcion(descripcionField.getValue());
                crudService.actualizar(c);
                refrescar.run();
                Notification.show("Categoría actualizada");
            } else {
                Notification.show("ID no encontrado");
            }
        });

        Button btnEliminar = new Button("Eliminar", e -> {
            crudService.eliminar(Categoria.class, parseId(idField));
            refrescar.run();
            limpiar(idField, nombreField, descripcionField);
            Notification.show("Categoría eliminada");
        });
        btnEliminar.addThemeVariants(ButtonVariant.LUMO_ERROR);

        Button btnLimpiar = new Button("Limpiar", e -> {
            limpiar(idField, nombreField, descripcionField);
            grid.asSingleSelect().clear();
        });

        layout.add(form, crearAcciones(btnCrear, btnConsultar, btnActualizar, btnEliminar, btnLimpiar), grid);
        return layout;
    }

    // ==========================================
    // 3. PROVEEDORES
    // ==========================================
    private Component crearSeccionProveedores() {
        VerticalLayout layout = new VerticalLayout();
        TextField idField = new TextField("ID");
        TextField nombreField = new TextField("Nombre");
        TextField contactoField = new TextField("Contacto");
        TextField telefonoField = new TextField("Teléfono");
        TextField emailField = new TextField("Email");
        TextField direccionField = new TextField("Dirección");

        FormLayout form = new FormLayout(idField, nombreField, contactoField, telefonoField, emailField, direccionField);

        Grid<Proveedor> grid = new Grid<>(Proveedor.class, false);
        grid.addColumn(p -> p.getId()).setHeader("ID").setAutoWidth(true);
        grid.addColumn(p -> p.getNombre()).setHeader("Nombre").setAutoWidth(true);
        grid.addColumn(p -> p.getContacto()).setHeader("Contacto").setAutoWidth(true);
        grid.addColumn(p -> p.getTelefono()).setHeader("Teléfono").setAutoWidth(true);
        grid.addColumn(p -> p.getEmail()).setHeader("Email").setAutoWidth(true);
        grid.addColumn(p -> p.getDireccion()).setHeader("Dirección").setAutoWidth(true);

        Runnable refrescar = () -> grid.setItems(crudService.listarTodos(Proveedor.class));
        refrescar.run();

        grid.asSingleSelect().addValueChangeListener(e -> {
            Proveedor p = e.getValue();
            if (p != null) {
                idField.setValue(p.getId() != null ? p.getId().toString() : "");
                nombreField.setValue(p.getNombre() != null ? p.getNombre() : "");
                contactoField.setValue(p.getContacto() != null ? p.getContacto() : "");
                telefonoField.setValue(p.getTelefono() != null ? p.getTelefono() : "");
                emailField.setValue(p.getEmail() != null ? p.getEmail() : "");
                direccionField.setValue(p.getDireccion() != null ? p.getDireccion() : "");
            }
        });

        Button btnCrear = new Button("Crear", e -> {
            Proveedor p = new Proveedor(
                nombreField.getValue(),
                contactoField.getValue(),
                telefonoField.getValue(),
                emailField.getValue(),
                direccionField.getValue()
            );
            crudService.guardar(p);
            refrescar.run();
            idField.setValue(p.getId() != null ? p.getId().toString() : "");
            Notification.show("Proveedor guardado con ID: " + p.getId());
        });
        btnCrear.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button btnConsultar = new Button("Consultar", e -> {
            Proveedor p = crudService.buscarPorId(Proveedor.class, parseId(idField));
            if (p != null) {
                grid.asSingleSelect().setValue(p);
                Notification.show("Encontrado: " + p.getNombre());
            } else {
                Notification.show("No encontrado");
            }
        });

        Button btnActualizar = new Button("Actualizar", e -> {
            Proveedor p = crudService.buscarPorId(Proveedor.class, parseId(idField));
            if (p != null) {
                p.setNombre(nombreField.getValue());
                p.setContacto(contactoField.getValue());
                p.setTelefono(telefonoField.getValue());
                p.setEmail(emailField.getValue());
                p.setDireccion(direccionField.getValue());
                crudService.actualizar(p);
                refrescar.run();
                Notification.show("Proveedor actualizado");
            } else {
                Notification.show("ID no encontrado");
            }
        });

        Button btnEliminar = new Button("Eliminar", e -> {
            crudService.eliminar(Proveedor.class, parseId(idField));
            refrescar.run();
            limpiar(idField, nombreField, contactoField, telefonoField, emailField, direccionField);
            Notification.show("Proveedor eliminado");
        });
        btnEliminar.addThemeVariants(ButtonVariant.LUMO_ERROR);

        Button btnLimpiar = new Button("Limpiar", e -> {
            limpiar(idField, nombreField, contactoField, telefonoField, emailField, direccionField);
            grid.asSingleSelect().clear();
        });

        layout.add(form, crearAcciones(btnCrear, btnConsultar, btnActualizar, btnEliminar, btnLimpiar), grid);
        return layout;
    }

    // ==========================================
    // 4. USUARIOS
    // ==========================================
    private Component crearSeccionUsuarios() {
        VerticalLayout layout = new VerticalLayout();
        TextField idField = new TextField("ID");
        TextField nombreField = new TextField("Nombre");
        TextField apellidoField = new TextField("Apellido");
        TextField emailField = new TextField("Email");
        TextField telefonoField = new TextField("Teléfono");
        TextField direccionField = new TextField("Dirección");
        TextField rolField = new TextField("Rol");
        TextField estadoField = new TextField("Estado");

        FormLayout form = new FormLayout(idField, nombreField, apellidoField, emailField, telefonoField, direccionField, rolField, estadoField);

        Grid<Usuario> grid = new Grid<>(Usuario.class, false);
        grid.addColumn(u -> u.getId()).setHeader("ID").setAutoWidth(true);
        grid.addColumn(u -> u.getNombre()).setHeader("Nombre").setAutoWidth(true);
        grid.addColumn(u -> u.getApellido()).setHeader("Apellido").setAutoWidth(true);
        grid.addColumn(u -> u.getEmail()).setHeader("Email").setAutoWidth(true);
        grid.addColumn(u -> u.getTelefono()).setHeader("Teléfono").setAutoWidth(true);
        grid.addColumn(u -> u.getRol()).setHeader("Rol").setAutoWidth(true);
        grid.addColumn(u -> u.getEstado()).setHeader("Estado").setAutoWidth(true);

        Runnable refrescar = () -> grid.setItems(crudService.listarTodos(Usuario.class));
        refrescar.run();

        grid.asSingleSelect().addValueChangeListener(e -> {
            Usuario u = e.getValue();
            if (u != null) {
                idField.setValue(u.getId() != null ? u.getId().toString() : "");
                nombreField.setValue(u.getNombre() != null ? u.getNombre() : "");
                apellidoField.setValue(u.getApellido() != null ? u.getApellido() : "");
                emailField.setValue(u.getEmail() != null ? u.getEmail() : "");
                telefonoField.setValue(u.getTelefono() != null ? u.getTelefono() : "");
                direccionField.setValue(u.getDireccion() != null ? u.getDireccion() : "");
                rolField.setValue(u.getRol() != null ? u.getRol() : "");
                estadoField.setValue(u.getEstado() != null ? u.getEstado() : "");
            }
        });

        Button btnCrear = new Button("Crear", e -> {
            Usuario u = new Usuario(
                nombreField.getValue(),
                apellidoField.getValue(),
                emailField.getValue(),
                telefonoField.getValue(),
                direccionField.getValue(),
                rolField.getValue(),
                estadoField.getValue(),
                LocalDateTime.now()
            );
            crudService.guardar(u);
            refrescar.run();
            idField.setValue(u.getId() != null ? u.getId().toString() : "");
            Notification.show("Usuario guardado con ID: " + u.getId());
        });
        btnCrear.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button btnConsultar = new Button("Consultar", e -> {
            Usuario u = crudService.buscarPorId(Usuario.class, parseId(idField));
            if (u != null) {
                grid.asSingleSelect().setValue(u);
                Notification.show("Encontrado: " + u.getNombre() + " " + u.getApellido());
            } else {
                Notification.show("No encontrado");
            }
        });

        Button btnActualizar = new Button("Actualizar", e -> {
            Usuario u = crudService.buscarPorId(Usuario.class, parseId(idField));
            if (u != null) {
                u.setNombre(nombreField.getValue());
                u.setApellido(apellidoField.getValue());
                u.setEmail(emailField.getValue());
                u.setTelefono(telefonoField.getValue());
                u.setDireccion(direccionField.getValue());
                u.setRol(rolField.getValue());
                u.setEstado(estadoField.getValue());
                crudService.actualizar(u);
                refrescar.run();
                Notification.show("Usuario actualizado");
            } else {
                Notification.show("ID no encontrado");
            }
        });

        Button btnEliminar = new Button("Eliminar", e -> {
            crudService.eliminar(Usuario.class, parseId(idField));
            refrescar.run();
            limpiar(idField, nombreField, apellidoField, emailField, telefonoField, direccionField, rolField, estadoField);
            Notification.show("Usuario eliminado");
        });
        btnEliminar.addThemeVariants(ButtonVariant.LUMO_ERROR);

        Button btnLimpiar = new Button("Limpiar", e -> {
            limpiar(idField, nombreField, apellidoField, emailField, telefonoField, direccionField, rolField, estadoField);
            grid.asSingleSelect().clear();
        });

        layout.add(form, crearAcciones(btnCrear, btnConsultar, btnActualizar, btnEliminar, btnLimpiar), grid);
        return layout;
    }

    // ==========================================
    // MÉTODOS AUXILIARES
    // ==========================================
    private Long parseId(TextField field) {
        try {
            return Long.parseLong(field.getValue().trim());
        } catch (Exception e) {
            return null;
        }
    }

    private Double parseDouble(String val) {
        try {
            return Double.parseDouble(val.trim());
        } catch (Exception e) {
            return 0.0;
        }
    }

    private Integer parseInt(String val) {
        try {
            return Integer.parseInt(val.trim());
        } catch (Exception e) {
            return 0;
        }
    }

    private void limpiar(TextField... campos) {
        for (TextField c : campos) {
            c.clear();
        }
    }

    private HorizontalLayout crearAcciones(Button... botones) {
        HorizontalLayout layout = new HorizontalLayout(botones);
        layout.getStyle().set("flex-wrap", "wrap");
        return layout;
    }
}
