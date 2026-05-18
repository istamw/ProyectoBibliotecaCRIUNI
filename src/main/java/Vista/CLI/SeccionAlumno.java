package Vista.CLI;

import Controlador.ControladorAlumno;
import Modelo.Alumno;
import java.time.LocalDate;
import java.util.Collection;

public class SeccionAlumno {
    private final ControladorAlumno controller;
    private final ControlCLI inputView; // Reutilizamos tu clase anterior para los Scanners

    public SeccionAlumno(ControladorAlumno controller, ControlCLI inputView) {
        this.controller = controller;
        this.inputView = inputView;
    }

    public void iniciarMenu() {
        boolean salir = false;
        while (!salir) {
            inputView.separador();
            inputView.mostrarMensaje("=== GESTION DE ALUMNOS ===");
            inputView.mostrarMensaje("[ 1 ] - Crear Alumno");
            inputView.mostrarMensaje("[ 2 ] - Editar Alumno");
            inputView.mostrarMensaje("[ 3 ] - Borrar Alumno");
            inputView.mostrarMensaje("[ 4 ] - Listar Alumnos");
            inputView.mostrarMensaje("[ 5 ] - Volver al Menu Principal");
            int opcion = inputView.pedirInt("Seleccione una opcion: ");

            switch (opcion) {
                case 1 -> mostrarFormularioCrear();
                case 2 -> mostrarFormularioEditar();
                case 3 -> mostrarFormularioBorrar();
                case 4 -> mostrarTablaAlumnos();
                case 5 -> salir = true;
                default -> inputView.mostrarError("Opcion no valida.");
            }
        }
    }

    private void mostrarFormularioCrear() {
        inputView.mostrarMensaje("--- Nuevo Alumno ---");
        String nombre = inputView.pedirString("Nombre Completo: ");
        String doc = inputView.pedirString("Nro Documento: ");
        String email = inputView.pedirString("Email: ");
        String tel = inputView.pedirString("Telefono: ");
        LocalDate fechaNac = inputView.pedirFecha("Fecha Nacimiento");
        String facultad = inputView.pedirString("Facultad: ");

        // Le pasamos los datos al controlador para que él haga el trabajo lógico
        controller.crearAlumno(nombre, doc, email, tel, fechaNac, facultad);
        inputView.mostrarMensaje("Alumno registrado con exito!");
    }

    private void mostrarFormularioEditar() {
        mostrarTablaAlumnos();
        int id = inputView.pedirInt("Ingrese ID del alumno a editar: ");

        // Le pedimos el alumno al controlador solo para mostrar sus datos actuales
        Alumno a = controller.obtenerAlumno(id);

        if (a != null) {
            String nuevoNombre = inputView.pedirString("Nuevo Nombre (" + a.getNombreCompleto() + "): ");
            String nuevoEmail = inputView.pedirString("Nuevo Email (" + a.getEmail() + "): ");

            // El controlador hace la actualización
            controller.editarAlumno(id, nuevoNombre, nuevoEmail);
            inputView.mostrarMensaje("Alumno actualizado.");
        } else {
            inputView.mostrarError("Alumno no encontrado.");
        }
    }

    private void mostrarFormularioBorrar() {
        int id = inputView.pedirInt("Ingrese ID del alumno a borrar: ");
        boolean exito = controller.borrarAlumno(id);

        if (exito) {
            inputView.mostrarMensaje("Alumno eliminado.");
        } else {
            inputView.mostrarError("Alumno no encontrado.");
        }
    }

    private void mostrarTablaAlumnos() {
        // Pedimos la lista cruda al controlador
        Collection<Alumno> alumnos = controller.obtenerTodosLosAlumnos();

        // La vista formatea y dibuja
        inputView.mostrarMensaje("\n--- LISTA DE ALUMNOS ---");
        System.out.printf("%-5s | %-25s | %-15s | %-20s | %-15s%n", "ID", "NOMBRE", "DOCUMENTO", "EMAIL", "FACULTAD");
        inputView.separador();
        for (Alumno a : alumnos) {
            System.out.printf("%-5d | %-25.25s | %-15.15s | %-20.20s | %-15.15s%n",
                    a.getId(), a.getNombreCompleto(), a.getNroDocumento(), a.getEmail(), a.getFacultad());
        }
        inputView.separador();
    }
}