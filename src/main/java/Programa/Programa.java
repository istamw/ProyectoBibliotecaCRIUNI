package Programa;

import Controlador.ControladorAlumno;
import Controlador.ControladorLibro;
import Controlador.ControladorPrestamo;
import Modelo.Alumno;
import Modelo.Libro;
import Modelo.Prestamo;
import Repositorio.Memoria.RepositorioAlumno;
import Repositorio.Memoria.RepositorioLibro;
import Repositorio.Memoria.RepositorioPrestamo;
import Repositorio.RepositorioBase;
import Vista.CLI.SeccionAlumno;
import Vista.CLI.ControlCLI;
import Vista.CLI.SeccionLibro;
import Vista.CLI.SeccionPrestamo;
import Vista.CLI.ClienteCLI;

public class Programa {
    public static void main(String[] args) {
        // Inicializar Repositorios
        RepositorioBase<Libro> libroRepo = new RepositorioLibro();
        RepositorioBase<Alumno> alumnoRepo = new RepositorioAlumno();
        RepositorioBase<Prestamo> prestamoRepo = new RepositorioPrestamo();

        // Inicializar Controladores (Inyección de dependencias)
        ControladorLibro libroController = new ControladorLibro(libroRepo);
        ControladorAlumno alumnoController = new ControladorAlumno(alumnoRepo);
        ControladorPrestamo prestamoController = new ControladorPrestamo(prestamoRepo, alumnoRepo, libroRepo);

        // Inicializar Vistas
        ControlCLI consolaView = new ControlCLI();
        SeccionLibro libroView = new SeccionLibro(libroController, consolaView);
        SeccionAlumno alumnoView = new SeccionAlumno(alumnoController, consolaView);
        SeccionPrestamo prestamoView = new SeccionPrestamo(prestamoController, consolaView);

        // Lanzar Menú Principal
        ClienteCLI sistema = new ClienteCLI(libroView, alumnoView, prestamoView, consolaView);
        sistema.arrancar();
    }
}