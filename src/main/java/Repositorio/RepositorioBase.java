package Repositorio;

import java.util.Collection;

public interface RepositorioBase<T> {
    void guardar(T entidad);
    T buscarPorId(int id);
    void eliminar(int id);
    boolean existe(int id);
    Collection<T> listarTodos();
}