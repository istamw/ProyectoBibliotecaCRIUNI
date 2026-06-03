package Controlador;

import Modelo.Prestamo;
import Modelo.Alumno;
import Modelo.Libro;
import Repositorio.RepositorioBase;
import Repositorio.RepositorioPersistente;
import Repositorio.Memoria.RepositorioAlumno;
import Repositorio.Memoria.RepositorioLibro;
import Repositorio.Memoria.RepositorioPrestamo;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ControladorPrestamo {
    private RepositorioPrestamo prestamoRepo;
    private RepositorioAlumno alumnoRepo;
    private RepositorioLibro libroRepo;

    public ControladorPrestamo(RepositorioPersistente repo) {
        this.prestamoRepo = (RepositorioPrestamo) repo.getRepoPrestamo();
        this.alumnoRepo = (RepositorioAlumno) repo.getRepoAlumno();
        this.libroRepo = (RepositorioLibro) repo.getRepoLibro();
    }

    /**
     * 0 = Correcto, 1 = Alumno no existe, 2 = Libro no existe, 3 = Libro sin stock
     */
    public int crearPrestamo(String alumnoId, List<Integer> libroIds, LocalDate fechaPrestamo, LocalDate fechaLimite) {
        Alumno alumno = alumnoRepo.buscarPorDocumento(alumnoId);
        if (alumno == null) return 1;

        List<Libro> librosAPrestar = new ArrayList<>();
        for (int id : libroIds) {
            Libro libro = libroRepo.buscarPorId(id);
            if (libro == null) return 2;
            if (libro.getStock() <= 0) return 3;
            librosAPrestar.add(libro);
        }

        for (Libro libro : librosAPrestar) {
            libro.setStock(libro.getStock() - 1);
            libroRepo.guardar(libro);
        }

        Prestamo nuevo = new Prestamo(0, alumno, librosAPrestar, fechaPrestamo, fechaLimite);
        prestamoRepo.guardar(nuevo);
        return 0;
    }

    /**
     * 0 = Correcto, 1 = Préstamo no encontrado, 2 = ya devuelto
     */
    public int devolverPrestamo(int id) {
        Prestamo prestamo = prestamoRepo.buscarPorId(id);
        if (prestamo == null) return 1;
        if (prestamo.getFechaDevolucion() != null) return 2;

        LocalDate fechaDevolucion = LocalDate.now();
        prestamo.setFechaDevolucion(fechaDevolucion);

        // poner en el stock los libros devueltos
        for (Libro libro : prestamo.getLibrosPrestados()) {
            libro.setStock(libro.getStock() + 1);
            //libroRepo.guardar(libro);
        }

        prestamoRepo.guardar(prestamo);
        return 0;
    }

    public void actualizarMultasPendientes() {
        LocalDate hoy = LocalDate.now();
        for (Prestamo p : prestamoRepo.listarTodos()) {
            if (p.getFechaDevolucion() != null) continue;
            if (!hoy.isAfter(p.getFechaLimite())) continue;
            if (p.getEstaDevuelto()) continue;

            long diasRetraso = ChronoUnit.DAYS.between(p.getFechaLimite(), hoy);
            p.setMulta(diasRetraso * 1000.0);
            prestamoRepo.guardar(p);
        }
    }

    public boolean borrarPrestamo(int id) {
        if (prestamoRepo.existe(id)) {
            prestamoRepo.eliminar(id);
            return true;
        }
        return false;
    }

    public Collection<Prestamo> obtenerTodosLosPrestamos() {
        actualizarMultasPendientes();
        return prestamoRepo.listarTodos();
    }

    public Collection<Prestamo> obtenerPrestamosVencidos() {

        return prestamoRepo.listarTodos().stream()
                .filter(p -> p.estaVencido(LocalDate.now()))
                .collect(Collectors.toList());
    }

    public Prestamo obtenerPrestamo(int id) {
        return prestamoRepo.buscarPorId(id);
    }

    public RepositorioBase<Libro> getRepoLibros(){
        return libroRepo;
    }

    public void setRepositorio(RepositorioPersistente repo) {
        this.prestamoRepo = (RepositorioPrestamo) repo.getRepoPrestamo();
        this.alumnoRepo = (RepositorioAlumno) repo.getRepoAlumno();
        this.libroRepo = (RepositorioLibro) repo.getRepoLibro();
    }
}