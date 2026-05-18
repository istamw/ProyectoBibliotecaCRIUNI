package Controlador;

import Modelo.Alumno;
import Repositorio.RepositorioBase;
import java.time.LocalDate;
import java.util.Collection;

public class ControladorAlumno {
    private final RepositorioBase<Alumno> repo;

    public ControladorAlumno(RepositorioBase<Alumno> repo) {
        this.repo = repo;
    }

    public void crearAlumno(String nombre, String documento, String email, String telefono, LocalDate fechaNac, String facultad) {
        // Aquí podrías agregar validaciones de negocio (ej. que el email tenga '@')
        Alumno nuevo = new Alumno(0, nombre, documento, email, telefono, fechaNac, facultad);
        repo.guardar(nuevo);
    }

    public boolean editarAlumno(int id, String nuevoNombre, String nuevoEmail) {
        Alumno a = repo.buscarPorId(id);
        if (a != null) {
            a.setNombreCompleto(nuevoNombre);
            a.setEmail(nuevoEmail);
            repo.guardar(a);
            return true;
        }
        return false;
    }

    public boolean borrarAlumno(int id) {
        if (repo.existe(id)) {
            repo.eliminar(id);
            return true;
        }
        return false;
    }

    public Collection<Alumno> obtenerTodosLosAlumnos() {
        return repo.listarTodos();
    }

    public Alumno obtenerAlumno(int id) {
        return repo.buscarPorId(id);
    }
}