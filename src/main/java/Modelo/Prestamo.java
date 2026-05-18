package Modelo;

import java.time.LocalDate;
import java.util.List;

public class Prestamo extends ModeloBase {
    private final Alumno alumno;
    private final List<Libro> librosPrestados;
    private final LocalDate fechaPrestamo;
    private final LocalDate fechaLimite;
    private LocalDate fechaDevolucion;
    private double multa;

    public Prestamo(int id, Alumno alumno, List<Libro> librosPrestados, LocalDate fechaPrestamo, LocalDate fechaLimite) {
        super(id);
        this.alumno = alumno;
        this.librosPrestados = librosPrestados;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaLimite = fechaLimite;
        this.fechaDevolucion = null;
        this.multa = 0.0;
    }

    // Getters y Setters
    public Alumno getAlumno() { return alumno; }
    public List<Libro> getLibrosPrestados() { return librosPrestados; }
    public LocalDate getFechaPrestamo() { return fechaPrestamo; }
    public LocalDate getFechaLimite() { return fechaLimite; }
    public LocalDate getFechaDevolucion() { return fechaDevolucion; }
    public void setFechaDevolucion(LocalDate fechaDevolucion) { this.fechaDevolucion = fechaDevolucion; }
    public double getMulta() { return multa; }
    public void setMulta(double multa) { this.multa = multa; }

    public boolean estaVencido(LocalDate fechaActual) {
        return fechaDevolucion == null && fechaActual.isAfter(fechaLimite);
    }
}