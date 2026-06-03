package Repositorio.Memoria;
import Modelo.Alumno;

public class RepositorioAlumno extends RepositorioMemoria<Alumno> {
  public Alumno buscarPorDocumento(String nroDocumento) {
    return datos.values().stream()
      .filter(a -> !a.isBorrado() && a.getNroDocumento().equals(nroDocumento))
      .findFirst()
      .orElse(null);
}
}