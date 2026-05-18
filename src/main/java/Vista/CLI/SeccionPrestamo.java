package Vista.CLI;

import Controlador.ControladorPrestamo;
import Modelo.Libro;
import Modelo.Prestamo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SeccionPrestamo {
    private final ControladorPrestamo controller;
    private final ControlCLI cv;

    public SeccionPrestamo(ControladorPrestamo controller, ControlCLI cv) {
        this.controller = controller;
        this.cv = cv;
    }

    public void iniciarMenu() {
        boolean salir = false;
        while (!salir) {
            cv.separador();
            cv.mostrarMensaje("=== GESTION DE PRESTAMOS ===");
            cv.mostrarMensaje("[ 1 ] - Registrar un Prestamo");
            cv.mostrarMensaje("[ 2 ] - Registrar Devolucion");
            cv.mostrarMensaje("[ 3 ] - Borrar un Prestamo");
            cv.mostrarMensaje("[ 4 ] - Listar todos los Prestamos");
            cv.mostrarMensaje("[ 5 ] - Informe de Prestamos Vencidos");
            cv.mostrarMensaje("[ 6 ] - Salir");
            int opcion = cv.pedirInt("Seleccione una opcion: ");

            switch (opcion) {
                case 1 -> formularioCrear();
                case 2 -> formularioDevolver();
                case 3 -> formularioBorrar();
                case 4 -> mostrarTablaPrestamos(controller.obtenerTodosLosPrestamos(), "HISTORIAL GENERAL DE PRESTAMOS");
                case 5 -> informeVencidos();
                case 6 -> salir = true;
                default -> cv.mostrarError("Opcion incorrecta.");
            }
        }
    }

    private void formularioCrear() {
        cv.mostrarMensaje("\n--- Nueva Solicitud de Prestamo ---");
        int alumnoId = cv.pedirInt("ID del Alumno solicitante: ");

        List<Integer> libroIds = new ArrayList<>();
        boolean agregarMas = true;
        while (agregarMas) {
            int libroId = cv.pedirInt("Ingrese ID del Libro a prestar: ");
            libroIds.add(libroId);
            String resp = cv.pedirString("¿Desea agregar otro libro al prestamo? (s/n): ");
            if (!resp.equalsIgnoreCase("s")) agregarMas = false;
        }

        LocalDate fPrestamo = cv.pedirFecha("Fecha del Prestamo");
        LocalDate fLimite = cv.pedirFecha("Fecha Limite de Entrega");

        int respuesta = controller.crearPrestamo(alumnoId, libroIds, fPrestamo, fLimite);
        switch (respuesta) {
            case 0 -> cv.mostrarMensaje("Prestamo registrado y stock actualizado.");
            case 1 -> cv.mostrarError("El Alumno especificado no existe.");
            case 2 -> cv.mostrarError("Uno o mas IDs de Libros introducidos no existen.");
            case 3 -> cv.mostrarError("Uno o mas libros no cuentan con Stock disponible.");
        }
    }

    private void formularioDevolver() {
        int id = cv.pedirInt("Ingrese el ID del Prestamo a devolver: ");
        LocalDate fDevolucion = cv.pedirFecha("Fecha Real de Devolucion");

        int res = controller.devolverPrestamo(id, fDevolucion);
        if (res == 0) {
            cv.mostrarMensaje("Devolucion asentada con exito.");
        } else if (res == 1) {
            cv.mostrarError("No se encontro ningun registro de prestamo con ese ID.");
        } else {
            cv.mostrarError("Este prestamo ya habia sido devuelto previamente.");
        }
    }

    private void formularioBorrar() {
        int id = cv.pedirInt("ID del prestamo a borrar: ");
        if (controller.borrarPrestamo(id)) {
            cv.mostrarMensaje("Registro eliminado exitosamente.");
        } else {
            cv.mostrarError("Prestamo no encontrado.");
        }
    }

    private void informeVencidos() {
        LocalDate hoy = cv.pedirFecha("Ingrese la fecha de control para calcular vencimientos");
        mostrarTablaPrestamos(controller.obtenerPrestamosVencidos(hoy), "INFORME DE PRESTAMOS VENCIDOS");
    }

    private void mostrarTablaPrestamos(Collection<Prestamo> lista, String titulo) {
        cv.mostrarMensaje("\n================================= " + titulo + " ==================================");
        System.out.printf("%-4s | %-20s | %-25s | %-11s | %-11s | %-8s%n",
                "ID", "ALUMNO", "LIBROS", "F. PRESTAMO", "F. LIMITE", "MULTA");
        cv.separador();

        for (Prestamo p : lista) {
            // Unimos los títulos en una sola cadena para pintarlos en la celda
            StringBuilder titulosLibros = new StringBuilder();
            for (Libro l : p.getLibrosPrestados()) {
                titulosLibros.append(l.getTitulo()).append(", ");
            }
            if (!titulosLibros.isEmpty()) titulosLibros = new StringBuilder(titulosLibros.substring(0, titulosLibros.length() - 2));

            System.out.printf("%-4d | %-20.20s | %-25.25s | %-11s | %-11s | $%-7.2f%n",
                    p.getId(),
                    p.getAlumno().getNombreCompleto(),
                    titulosLibros.toString(),
                    p.getFechaPrestamo().toString(),
                    p.getFechaLimite().toString(),
                    p.getMulta());
        }
        cv.separador();
    }
}
