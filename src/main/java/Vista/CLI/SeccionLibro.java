package Vista.CLI;

import Controlador.ControladorLibro;
import Modelo.Libro;
import java.util.Collection;

public class SeccionLibro {
    private final ControladorLibro controller;
    private final ControlCLI cv;

    public SeccionLibro(ControladorLibro controller, ControlCLI cv) {
        this.controller = controller;
        this.cv = cv;
    }

    public void iniciarMenu() {
        boolean salir = false;
        while (!salir) {
            cv.separador();
            cv.mostrarMensaje("=== GESTION DE LIBROS ===");
            cv.mostrarMensaje("[ 1 ] - Crear un Libro");
            cv.mostrarMensaje("[ 2 ] - Editar un Libro");
            cv.mostrarMensaje("[ 3 ] - Borrar un Libro");
            cv.mostrarMensaje("[ 4 ] - Listar Libros");
            cv.mostrarMensaje("[ 5 ] - Salir");
            int opcion = cv.pedirInt("Seleccione una opcion: ");

            switch (opcion) {
                case 1 -> formularioCrear();
                case 2 -> formularioEditar();
                case 3 -> formularioBorrar();
                case 4 -> mostrarTablaLibros();
                case 5 -> salir = true;
                default -> cv.mostrarError("Opcion invalida.");
            }
        }
    }

    private void formularioCrear() {
        cv.mostrarMensaje("\n--- Registrar Nuevo Libro ---");
        String titulo = cv.pedirString("Titulo: ");
        String editorial = cv.pedirString("Editorial: ");
        int anho = cv.pedirInt("Anho Publicacion: ");
        String autor = cv.pedirString("Autor: ");
        int stock = cv.pedirInt("Stock Inicial: ");

        controller.crearLibro(titulo, editorial, anho, autor, stock);
        cv.mostrarMensaje("Libro registrado exitosamente.");
    }

    private void formularioEditar() {
        int id = cv.pedirInt("Ingrese el ID del libro a editar: ");
        Libro l = controller.obtenerLibro(id);
        if (l != null) {
            String titulo = cv.pedirString("Nuevo Titulo (" + l.getTitulo() + "): ");
            String ed = cv.pedirString("Nueva Editorial (" + l.getEditorial() + "): ");
            int anho = cv.pedirInt("Nuevo Anho (" + l.getAnhoPublicacion() + "): ");
            String autor = cv.pedirString("Nuevo Autor (" + l.getAutor() + "): ");
            int stock = cv.pedirInt("Nuevo Stock (" + l.getStock() + "): ");

            controller.editarLibro(id, titulo, ed, anho, autor, stock);
            cv.mostrarMensaje("Libro actualizado correctamente.");
        } else {
            cv.mostrarError("Libro no encontrado.");
        }
    }

    private void formularioBorrar() {
        int id = cv.pedirInt("ID del libro a eliminar: ");
        if (controller.borrarLibro(id)) {
            cv.mostrarMensaje("Libro eliminado de forma logica.");
        } else {
            cv.mostrarError("Libro no encontrado.");
        }
    }

    public void mostrarTablaLibros() {
        Collection<Libro> libros = controller.obtenerTodosLosLibros();
        cv.mostrarMensaje("\n================================= LISTA DE LIBROS ==================================");
        System.out.printf("%-5s | %-25s | %-20s | %-6s | %-12s%n", "ID", "TITULO", "AUTOR", "ANHO", "STOCK");
        cv.separador();
        for (Libro l : libros) {
            System.out.printf("%-5d | %-25.25s | %-20.20s | %-6d | %-12d%n",
                    l.getId(), l.getTitulo(), l.getAutor(), l.getAnhoPublicacion(), l.getStock());
        }
        cv.separador();
    }
}
