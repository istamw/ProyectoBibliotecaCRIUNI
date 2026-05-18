package Modelo;

import java.time.LocalDate;

public class Alumno extends ModeloBase {
    private String nombreCompleto;
    private String nroDocumento;
    private String email;
    private String telefono;
    private LocalDate fechaNacimiento;
    private String facultad;

    public Alumno(int id, String nombreCompleto, String nroDocumento, String email, String telefono, LocalDate fechaNacimiento, String facultad) {
        super(id);
        this.nombreCompleto = nombreCompleto;
        this.nroDocumento = nroDocumento;
        this.email = email;
        this.telefono = telefono;
        this.fechaNacimiento = fechaNacimiento;
        this.facultad = facultad;
    }

    // Getters y Setters
    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }
    public String getNroDocumento() { return nroDocumento; }
    public void setNroDocumento(String nroDocumento) { this.nroDocumento = nroDocumento; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }
    public String getFacultad() { return facultad; }
    public void setFacultad(String facultad) { this.facultad = facultad; }
}