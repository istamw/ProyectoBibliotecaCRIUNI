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
import Repositorio.RepositorioPersistente;
import Vista.CLI.SeccionAlumno;
import Vista.CLI.ControlCLI;
import Vista.CLI.SeccionLibro;
import Vista.CLI.SeccionPrestamo;
import Vista.CLI.ClienteCLI;

public class ProgramaCLI {
    public static void main(String[] args) {
        // inicializar repositorios
        RepositorioBase<Libro> libroRepo = new RepositorioLibro();
        RepositorioBase<Alumno> alumnoRepo = new RepositorioAlumno();
        RepositorioBase<Prestamo> prestamoRepo = new RepositorioPrestamo();

        //Repo Principal (Persistente)
        RepositorioPersistente repoPrincipal = new RepositorioPersistente(libroRepo, alumnoRepo, prestamoRepo);

        // inicializar controladores
        ControladorLibro libroController = new ControladorLibro(repoPrincipal);
        ControladorAlumno alumnoController = new ControladorAlumno(repoPrincipal);
        ControladorPrestamo prestamoController = new ControladorPrestamo(repoPrincipal);

        // inicializar vistas
        ControlCLI consolaView = new ControlCLI();
        SeccionLibro libroView = new SeccionLibro(libroController, consolaView);
        SeccionAlumno alumnoView = new SeccionAlumno(alumnoController, consolaView);
        SeccionPrestamo prestamoView = new SeccionPrestamo(prestamoController, consolaView);

        // menú Principal
        ClienteCLI sistema = new ClienteCLI(libroView, alumnoView, prestamoView, consolaView);
        sistema.arrancar();
    }
}