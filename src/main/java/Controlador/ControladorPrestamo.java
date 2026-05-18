package Controlador;

import Modelo.Prestamo;
import Modelo.Alumno;
import Modelo.Libro;
import Repositorio.RepositorioBase;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ControladorPrestamo {
    private final RepositorioBase<Prestamo> prestamoRepo;
    private final RepositorioBase<Alumno> alumnoRepo;
    private final RepositorioBase<Libro> libroRepo;

    public ControladorPrestamo(RepositorioBase<Prestamo> pRepo, RepositorioBase<Alumno> aRepo, RepositorioBase<Libro> lRepo) {
        this.prestamoRepo = pRepo;
        this.alumnoRepo = aRepo;
        this.libroRepo = lRepo;
    }

    /**
     * Códigos de retorno: 0 = Éxito, 1 = Alumno no existe, 2 = Libro no existe, 3 = Libro sin stock
     */
    public int crearPrestamo(int alumnoId, List<Integer> libroIds, LocalDate fechaPrestamo, LocalDate fechaLimite) {
        Alumno alumno = alumnoRepo.buscarPorId(alumnoId);
        if (alumno == null) return 1;

        List<Libro> librosAPrestar = new ArrayList<>();
        for (int id : libroIds) {
            Libro libro = libroRepo.buscarPorId(id);
            if (libro == null) return 2;
            if (libro.getStock() <= 0) return 3;
            librosAPrestar.add(libro);
        }

        // Lógica de negocio: Descontar stock de libros
        for (Libro libro : librosAPrestar) {
            libro.setStock(libro.getStock() - 1);
            libroRepo.guardar(libro);
        }

        Prestamo nuevo = new Prestamo(0, alumno, librosAPrestar, fechaPrestamo, fechaLimite);
        prestamoRepo.guardar(nuevo);
        return 0;
    }

    /**
     * Códigos de retorno: 0 = Éxito, 1 = Préstamo no encontrado, 2 = Ya devuelto
     */
    public int devolverPrestamo(int id, LocalDate fechaDevolucion) {
        Prestamo prestamo = prestamoRepo.buscarPorId(id);
        if (prestamo == null) return 1;
        if (prestamo.getFechaDevolucion() != null) return 2;

        prestamo.setFechaDevolucion(fechaDevolucion);

        // Lógica de negocio: Calcular multa ($1000 por cada día de retraso)
        if (fechaDevolucion.isAfter(prestamo.getFechaLimite())) {
            long diasRetraso = ChronoUnit.DAYS.between(prestamo.getFechaLimite(), fechaDevolucion);
            prestamo.setMulta(diasRetraso * 1000.0);
        }

        // Lógica de negocio: Devolver el stock a los libros correspondientes
        for (Libro libro : prestamo.getLibrosPrestados()) {
            libro.setStock(libro.getStock() + 1);
            libroRepo.guardar(libro);
        }

        prestamoRepo.guardar(prestamo);
        return 0;
    }

    public boolean borrarPrestamo(int id) {
        if (prestamoRepo.existe(id)) {
            prestamoRepo.eliminar(id);
            return true;
        }
        return false;
    }

    public Collection<Prestamo> obtenerTodosLosPrestamos() {
        return prestamoRepo.listarTodos();
    }

    public Collection<Prestamo> obtenerPrestamosVencidos(LocalDate fechaActual) {
        return prestamoRepo.listarTodos().stream()
                .filter(p -> p.estaVencido(fechaActual))
                .collect(Collectors.toList());
    }
}