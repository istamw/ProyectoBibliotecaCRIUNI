package Controlador;

import Modelo.Libro;
import Repositorio.RepositorioBase;
import java.util.Collection;

public class ControladorLibro {
    private final RepositorioBase<Libro> repo;

    public ControladorLibro(RepositorioBase<Libro> repo) {
        this.repo = repo;
    }

    public void crearLibro(String titulo, String editorial, int anho, String autor, int stock) {
        Libro nuevo = new Libro(0, titulo, editorial, anho, autor, stock);
        repo.guardar(nuevo);
    }

    public boolean editarLibro(int id, String titulo, String editorial, int anho, String autor, int stock) {
        Libro libro = repo.buscarPorId(id);
        if (libro != null) {
            libro.setTitulo(titulo);
            libro.setEditorial(editorial);
            libro.setAnhoPublicacion(anho);
            libro.setAutor(autor);
            libro.setStock(stock);
            repo.guardar(libro);
            return true;
        }
        return false;
    }

    public boolean borrarLibro(int id) {
        if (repo.existe(id)) {
            repo.eliminar(id);
            return true;
        }
        return false;
    }

    public Collection<Libro> obtenerTodosLosLibros() {
        return repo.listarTodos();
    }

    public Libro obtenerLibro(int id) {
        return repo.buscarPorId(id);
    }
}