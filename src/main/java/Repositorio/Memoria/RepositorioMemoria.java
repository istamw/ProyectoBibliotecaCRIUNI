package Repositorio.Memoria;

import Modelo.ModeloBase;
import Repositorio.RepositorioBase;

import java.util.HashMap;
import java.util.Map;
import java.util.Collection;
import java.util.stream.Collectors;

public abstract class RepositorioMemoria<T extends ModeloBase> implements RepositorioBase<T> {
    // Uso de Map requerido en las instrucciones
    protected Map<Integer, T> datos = new HashMap<>();
    protected int nextId = 1;

    @Override
    public void guardar(T entidad) {
        if (entidad.getId() == 0) {
            entidad.setId(nextId++);
        }
        // El put en un Map sirve tanto para crear (nuevo ID) como para editar (ID existente)
        datos.put(entidad.getId(), entidad);
    }

    @Override
    public T buscarPorId(int id) {
        T entidad = datos.get(id);
        // Solo devolvemos si existe y no ha sido borrado lógicamente
        if (entidad != null && !entidad.isBorrado()) {
            return entidad;
        }
        return null;
    }

    @Override
    public void eliminar(int id) {
        T entidad = buscarPorId(id);
        if (entidad != null) {
            entidad.setBorrado(true); // Borrado lógico
        }
    }

    @Override
    public boolean existe(int id) {
        return buscarPorId(id) != null;
    }

    @Override
    public Collection<T> listarTodos() {
        // Retornamos una Collection filtrando los elementos borrados
        return datos.values().stream()
                .filter(e -> !e.isBorrado())
                .collect(Collectors.toList());
    }
}