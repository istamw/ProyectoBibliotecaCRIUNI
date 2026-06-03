package Repositorio;

import java.io.*;

import Modelo.Alumno;
import Modelo.Libro;
import Modelo.Prestamo;

public class RepositorioPersistente implements Serializable {
  private final RepositorioBase<Libro> repoLibro;
  private final RepositorioBase<Alumno> repoAlumno;
  private final RepositorioBase<Prestamo> repoPrestamo;

  public RepositorioPersistente(RepositorioBase<Libro> repoLibro, RepositorioBase<Alumno> repoAlumno, RepositorioBase<Prestamo> repoPrestamo) {
    this.repoLibro = repoLibro;
    this.repoAlumno = repoAlumno;
    this.repoPrestamo = repoPrestamo;
  }

  public RepositorioBase<Libro> getRepoLibro() {return repoLibro;}
  public RepositorioBase<Alumno> getRepoAlumno() { return repoAlumno;}
  public RepositorioBase<Prestamo> getRepoPrestamo() { return repoPrestamo;}

  //Persistencia
  public void guardar(String rutaArchivo) throws IOException {
    try (ObjectOutputStream salida = new ObjectOutputStream(
        new FileOutputStream(rutaArchivo))) {
      salida.writeObject(this);
    }
  }

  public static RepositorioPersistente cargar(String rutaArchivo)
      throws IOException, ClassNotFoundException {
    try (ObjectInputStream biblioteca = new ObjectInputStream(
        new FileInputStream(rutaArchivo))) {
      return (RepositorioPersistente) biblioteca.readObject();
    }
  }

}