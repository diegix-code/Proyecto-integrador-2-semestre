package com.example.view;

import com.example.model.Categoria;
import com.example.model.Producto;
import com.example.model.Proveedor;
import com.example.model.Usuario;
import com.example.service.CrudService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@Route("")
@PageTitle("Sistema de Inventario - Hamburguesería")
public class MainView extends VerticalLayout {

    private final CrudService crudService;

    public MainView(@Autowired CrudService crudService) {
        this.crudService = crudService;
        setSizeFull();

        // Estructura de Pestañas (TabSheet) para el sistema de la hamburguesería
        TabSheet tabSheet = new TabSheet();
        tabSheet.setSizeFull();

        tabSheet.add("🍔 Productos", createProductosView());
        tabSheet.add("🏷️ Categorías", createCategoriasView());
        tabSheet.add("🚚 Proveedores", createProveedoresView());
        tabSheet.add("👤 Usuarios", createUsuariosView());

        add(tabSheet);
    }

    private VerticalLayout createProductosView() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();

        Grid<Producto> grid = new Grid<>(Producto.class, false);
        grid.addColumn(Producto::getId).setHeader("ID");
        grid.addColumn(Producto::getNombre).setHeader("Nombre Producto");
        grid.addColumn(p -> p.getPrecio() != null ? "$" + p.getPrecio() : "$0.00").setHeader("Precio");
        grid.addColumn(Producto::getStock).setHeader("Stock");
        grid.addColumn(p -> p.getCategoria() != null ? p.getCategoria().getNombre() : "N/A").setHeader("Categoría");
        grid.addColumn(p -> p.getProveedor() != null ? p.getProveedor().getNombre() : "N/A").setHeader("Proveedor");
        grid.setSizeFull();

        TextField nombre = new TextField("Nombre (Ej: Hamburguesa Doble)");
        BigDecimalField precio = new BigDecimalField("Precio ($)");
        IntegerField stock = new IntegerField("Stock Disponible");
        
        ComboBox<Categoria> categoria = new ComboBox<>("Categoría");
        categoria.setItems(crudService.findAll(Categoria.class));

        ComboBox<Proveedor> proveedor = new ComboBox<>("Proveedor");
        proveedor.setItems(crudService.findAll(Proveedor.class));

        Button guardar = new Button("Guardar Producto", e -> {
            try {
                Producto p = new Producto(nombre.getValue(), precio.getValue(), stock.getValue(), categoria.getValue(), proveedor.getValue());
                crudService.save(p);
                grid.setItems(crudService.findAll(Producto.class));
                Notification.show("¡Producto guardado con éxito!");
                nombre.clear();
                precio.clear();
                stock.clear();
                categoria.clear();
                proveedor.clear();
            } catch (Exception ex) {
                Notification.show("Error al guardar: " + ex.getMessage());
            }
        });

        FormLayout form = new FormLayout(nombre, precio, stock, categoria, proveedor, guardar);
        form.setMaxWidth("600px");

        grid.setItems(crudService.findAll(Producto.class));
        layout.add(form, grid);
        return layout;
    }

    private VerticalLayout createCategoriasView() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();

        Grid<Categoria> grid = new Grid<>(Categoria.class);
        grid.setColumns("id", "nombre", "descripcion");
        grid.setSizeFull();

        TextField nombre = new TextField("Nombre (Ej: Hamburguesas, Bebidas)");
        TextField descripcion = new TextField("Descripción");

        Button guardar = new Button("Guardar Categoría", e -> {
            Categoria c = new Categoria(nombre.getValue(), descripcion.getValue());
            crudService.save(c);
            grid.setItems(crudService.findAll(Categoria.class));
            Notification.show("¡Categoría guardada!");
            nombre.clear();
            descripcion.clear();
        });

        FormLayout form = new FormLayout(nombre, descripcion, guardar);
        form.setMaxWidth("500px");

        grid.setItems(crudService.findAll(Categoria.class));
        layout.add(form, grid);
        return layout;
    }

    private VerticalLayout createProveedoresView() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();

        Grid<Proveedor> grid = new Grid<>(Proveedor.class);
        grid.setColumns("id", "nombre", "telefono", "email", "direccion");
        grid.setSizeFull();

        TextField nombre = new TextField("Nombre Proveedor / Empresa");
        TextField telefono = new TextField("Teléfono");
        TextField email = new TextField("Correo Electrónico");
        TextField direccion = new TextField("Dirección");

        Button guardar = new Button("Guardar Proveedor", e -> {
            Proveedor p = new Proveedor(nombre.getValue(), telefono.getValue(), email.getValue(), direccion.getValue());
            crudService.save(p);
            grid.setItems(crudService.findAll(Proveedor.class));
            Notification.show("¡Proveedor guardado!");
            nombre.clear();
            telefono.clear();
            email.clear();
            direccion.clear();
        });

        FormLayout form = new FormLayout(nombre, telefono, email, direccion, guardar);
        form.setMaxWidth("500px");

        grid.setItems(crudService.findAll(Proveedor.class));
        layout.add(form, grid);
        return layout;
    }

    private VerticalLayout createUsuariosView() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();

        Grid<Usuario> grid = new Grid<>(Usuario.class);
        grid.setColumns("id", "username", "nombreCompleto", "rol", "activo");
        grid.setSizeFull();

        TextField username = new TextField("Nombre de Usuario");
        PasswordField password = new PasswordField("Contraseña");
        TextField nombreCompleto = new TextField("Nombre Completo");
        ComboBox<String> rol = new ComboBox<>("Rol de Sistema", "ADMIN", "CAJERO");

        Button guardar = new Button("Guardar Usuario", e -> {
            Usuario u = new Usuario(username.getValue(), password.getValue(), nombreCompleto.getValue(), rol.getValue(), true);
            crudService.save(u);
            grid.setItems(crudService.findAll(Usuario.class));
            Notification.show("¡Usuario guardado!");
            username.clear();
            password.clear();
            nombreCompleto.clear();
            rol.clear();
        });

        FormLayout form = new FormLayout(username, password, nombreCompleto, rol, guardar);
        form.setMaxWidth("500px");

        grid.setItems(crudService.findAll(Usuario.class));
        layout.add(form, grid);
        return layout;
    }
}
