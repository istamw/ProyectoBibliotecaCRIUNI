package Vista.GUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import Controlador.ControladorAlumno;
import Controlador.ControladorLibro;
import Controlador.ControladorPrestamo;
import Modelo.Alumno;
import Modelo.Libro;
import Modelo.Prestamo;
import Repositorio.RepositorioBase;
import Repositorio.RepositorioPersistente;
import Repositorio.Memoria.RepositorioAlumno;
import Repositorio.Memoria.RepositorioLibro;
import Repositorio.Memoria.RepositorioPrestamo;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.List;

import Vista.GUI.components.BotonBonito;

public class ClienteGUI extends JFrame {
  private RepositorioBase<Libro> libroRepo = new RepositorioLibro();
  private RepositorioBase<Alumno> alumnoRepo = new RepositorioAlumno();
  private RepositorioBase<Prestamo> prestamoRepo = new RepositorioPrestamo();
  private RepositorioPersistente repo = new RepositorioPersistente(libroRepo, alumnoRepo, prestamoRepo);
  private ControladorLibro controladorLibro = new ControladorLibro(repo);
  private ControladorAlumno controladorAlumno = new ControladorAlumno(repo);
  private ControladorPrestamo controladorPrestamo = new ControladorPrestamo(repo);

  private JTable tabla;
  private DefaultTableModel modeloTabla;

  private enum Seccion {PRESTAMOS, ALUMNOS, LIBROS}
  private Seccion seccionActiva = Seccion.PRESTAMOS;

  private BotonBonito filtrarButton = new BotonBonito("Filtrar", false, 20, false);
 
  private BotonBonito prestamoButton = new BotonBonito("Préstamos", true, 20, false);
  private BotonBonito alumnosButton = new BotonBonito("Alumnos", false, 20, false);
  private BotonBonito librosButton = new BotonBonito("Libros", false, 20, false);

  private BotonBonito agregarButton = new BotonBonito("➕");
  private BotonBonito editarButton = new BotonBonito("📝");
  private BotonBonito borrarButton = new BotonBonito("❌");
  
  public ClienteGUI() {
    setTitle("Gestor Biblioteca");
    setSize(800, 600);
    setMinimumSize(new Dimension(600, 400));
    setLocationRelativeTo(null);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    Image icono = new ImageIcon(
            Objects.requireNonNull(getClass().getResource("/icons/icon.png"))
    ).getImage();
    setIconImage(icono);

    modeloTabla = new DefaultTableModel();
    tabla = new JTable(modeloTabla);
    tabla.setRowHeight(40);

    mostrarContenido();
    actualizarTablaPrestamos();
    setVisible(true);
  }

  private void mostrarContenido(){
    menuSuperior();
    panelSecciones();
    configurarEventosMenu();
    panelPrincipal();
  }

  private void menuSuperior(){
    JMenuBar barraMenu = new JMenuBar();

    JMenu menuArchivo = new JMenu("Archivo");
    menuArchivo.setFont(new Font("Arial", Font.PLAIN, 14));
    JMenu menuEditar = new JMenu("Editar");
    menuEditar.setFont(new Font("Arial", Font.PLAIN, 14));

    JMenuItem itemNuevo = new JMenuItem("Nueva Biblioteca");
    JMenuItem itemGuardar = new JMenuItem("Guardar Biblioteca");
    JMenuItem itemCargar = new JMenuItem("Cargar Biblioteca");
    JMenuItem itemExportar = new JMenuItem("Exportar Biblioteca");
    JMenuItem itemSalir = new JMenuItem("Salir");

    JMenuItem itemPrestamo = new JMenuItem("Nuevo Préstamo");
    JMenuItem itemAlumno = new JMenuItem("Nuevo Alumno");
    JMenuItem itemLibro = new JMenuItem("Nuevo Libro");
    JMenuItem itemEditAlumno = new JMenuItem("Editar Alumno");
    JMenuItem itemEditLibro = new JMenuItem("Editar Libro");

    itemNuevo.addActionListener(e -> {
      int confirm = JOptionPane.showConfirmDialog(this,
        "Se perderán todos los cambios no guardados. ¿Desea continuar?",
        "Nueva Biblioteca",
        JOptionPane.YES_NO_OPTION,
        JOptionPane.WARNING_MESSAGE);
      if (confirm != JOptionPane.YES_OPTION) return;

      libroRepo = new RepositorioLibro();
      alumnoRepo = new RepositorioAlumno();
      prestamoRepo = new RepositorioPrestamo();
      repo = new RepositorioPersistente(libroRepo, alumnoRepo, prestamoRepo);

      controladorLibro.setRepositorio((RepositorioLibro) libroRepo);
      controladorAlumno.setRepositorio((RepositorioAlumno) alumnoRepo);
      controladorPrestamo.setRepositorio(repo);

      actualizarTablaPrestamos();
      seleccionarMenu(prestamoButton);
    });

    itemGuardar.addActionListener(e -> {
      JFileChooser chooser = new JFileChooser();
      chooser.setDialogTitle("Guardar Biblioteca");
      chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Archivos de biblioteca (*.uni)", "uni"));

      if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
        String ruta = chooser.getSelectedFile().getAbsolutePath();
        if (!ruta.endsWith(".uni")) ruta += ".uni";

        File archivo = new File(ruta);
        if (archivo.exists()) {
          int confirm = JOptionPane.showConfirmDialog(this,
            "El archivo ya existe. ¿Desea sobreescribirlo?",
            "Confirmar sobreescritura",
            JOptionPane.YES_NO_OPTION);
          if (confirm != JOptionPane.YES_OPTION) return;
        }

        try {
          repo.guardar(ruta);
          JOptionPane.showMessageDialog(this, "Biblioteca guardada correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
          JOptionPane.showMessageDialog(this, "Error al guardar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
      }
    });

    itemCargar.addActionListener(e -> {
      JFileChooser chooser = new JFileChooser();
      chooser.setDialogTitle("Cargar Biblioteca");
      chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Archivos de biblioteca (*.uni)", "uni"));

      if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
        String ruta = chooser.getSelectedFile().getAbsolutePath();

        try {
          RepositorioPersistente cargado = RepositorioPersistente.cargar(ruta);
          prestamoRepo = (RepositorioPrestamo) cargado.getRepoPrestamo();
          alumnoRepo = (RepositorioAlumno) cargado.getRepoAlumno();
          libroRepo = (RepositorioLibro) cargado.getRepoLibro();

          repo = new RepositorioPersistente(libroRepo, alumnoRepo, prestamoRepo);

          controladorLibro.setRepositorio((RepositorioLibro) (libroRepo));
          controladorAlumno.setRepositorio((RepositorioAlumno) alumnoRepo);
          controladorPrestamo.setRepositorio((RepositorioPersistente) repo);

          actualizarTablaPrestamos();
          seleccionarMenu(prestamoButton);

          JOptionPane.showMessageDialog(this, "Biblioteca cargada correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException | ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
      }
    });

    itemExportar.addActionListener(e -> {
      JOptionPane.showMessageDialog(this, "Pague la version premium para exportar en PDF.", "Desbloquea la Biblioteca+", JOptionPane.INFORMATION_MESSAGE);
    });

    itemSalir.addActionListener(e -> System.exit(0));

    itemPrestamo.addActionListener(e -> {
      new CrearPrestamoDialog(this, controladorPrestamo);
      actualizarTablaPrestamos();
    });

    itemAlumno.addActionListener(e -> {
      new CrearAlumnoDialog(this, controladorAlumno);
      actualizarTablaAlumnos();
    });

    itemLibro.addActionListener(e -> {
      new CrearLibroDialog(this, controladorLibro);
      actualizarTablaLibros();
    });

    itemEditAlumno.addActionListener(e -> editarAlumno());
    itemEditLibro.addActionListener(e -> editarLibro());

    menuArchivo.add(itemNuevo);
    menuArchivo.add(itemGuardar);
    menuArchivo.add(itemCargar);
    menuArchivo.add(itemExportar);
    menuArchivo.add(itemSalir);

    menuEditar.add(itemPrestamo);
    menuEditar.add(itemAlumno);
    menuEditar.add(itemLibro);
    menuEditar.add(itemEditAlumno);
    menuEditar.add(itemEditLibro);

    barraMenu.add(menuArchivo);
    barraMenu.add(menuEditar);
    setJMenuBar(barraMenu);
  }

  private void panelSecciones(){
    JPanel panelSecciones = new JPanel();
    panelSecciones.setLayout(new BoxLayout(panelSecciones, BoxLayout.Y_AXIS));
    panelSecciones.setBorder(new EmptyBorder(30,25,30, -25));
    
    Dimension tamanoBoton = new Dimension(180, 75);

    configurarBoton(prestamoButton, tamanoBoton);
    configurarBoton(alumnosButton, tamanoBoton);
    configurarBoton(librosButton, tamanoBoton);

    configurarBoton(librosButton, tamanoBoton);

    add(panelSecciones, BorderLayout.WEST);

    //el vertical glue sirve para poder hacer separadores flotantes, ya que no agrego contenido arriba ni abajo.
    panelSecciones.add(Box.createVerticalGlue());
    panelSecciones.add(prestamoButton);
    panelSecciones.add(Box.createVerticalStrut(20));
    panelSecciones.add(alumnosButton);
    panelSecciones.add(Box.createVerticalStrut(20));
    panelSecciones.add(librosButton);
    panelSecciones.add(Box.createVerticalGlue());
  }

  private void panelPrincipal(){

    JPanel panelCentral = new JPanel(new BorderLayout());
    panelCentral.setBorder(new EmptyBorder(30, 20, 30, 30));

    JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
    panelInferior.setBorder(new EmptyBorder(5, 0, 0, 0));
    filtrarButton.setTamanoCustom(new Dimension(100, 35));
    panelInferior.add(filtrarButton);

    panelCentral.add(panelInferior, BorderLayout.SOUTH);
      
    JPanel panelAcciones = new JPanel(new GridBagLayout());
    panelAcciones.setBorder(new EmptyBorder(0, 0, 0, 20));

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.insets = new Insets(10, 0, 10, 0);

    Dimension tamanoMiniBoton = new Dimension(50, 50);

    
    configurarBoton(agregarButton, tamanoMiniBoton);
    gbc.gridy = 0;
    panelAcciones.add(agregarButton, gbc);
    
    configurarBoton(editarButton, tamanoMiniBoton);
    gbc.gridy = 1;
    panelAcciones.add(editarButton, gbc);
    
    configurarBoton(borrarButton, tamanoMiniBoton);
    gbc.gridy = 2;
    panelAcciones.add(borrarButton, gbc);

    panelCentral.add(panelAcciones, BorderLayout.WEST);

    JScrollPane scrollTabla = new JScrollPane(tabla);
    panelCentral.add(scrollTabla, BorderLayout.CENTER);

    add(panelCentral, BorderLayout.CENTER);
  }

  private void actualizarTablaPrestamos() {
    Collection<Prestamo> prestamos = controladorPrestamo.obtenerTodosLosPrestamos();
    
    String[] columnas = {"ID", "ALUMNO", "LIBROS", "F. PRESTAMO", "F. LIMITE", "MULTA", "Devuelto"};
    
    modeloTabla.setColumnIdentifiers(columnas);
    modeloTabla.setRowCount(0); 

    for (Prestamo p : prestamos) {
      StringBuilder titulosLibros = new StringBuilder();
      for (Libro l : p.getLibrosPrestados()) {
        titulosLibros.append(l.getTitulo()).append(", ");
      }
      String librosStr = titulosLibros.isEmpty() ? "" : titulosLibros.substring(0, titulosLibros.length() - 2);

      Object[] fila = {
        p.getId(),
        p.getAlumno().getNombreCompleto(),
        librosStr,
        p.getFechaPrestamo().toString(),
        p.getFechaLimite().toString(),
        "$" + String.format("%.2f", p.getMulta()),
        p.getEstaDevuelto() ? "SI" : "NO"
      };

      modeloTabla.addRow(fila);
    }
  }

  private void actualizarTablaAlumnos() {
    Collection<Alumno> alumnos = controladorAlumno.obtenerTodosLosAlumnos();

    String[] columnas = {"ID", "NOMBRE", "DOCUMENTO", "TELEFONO", "EMAIL", "FACULTAD"};
    
    modeloTabla.setColumnIdentifiers(columnas);
    modeloTabla.setRowCount(0);

    for (Alumno a : alumnos) {
      Object[] fila = {
        a.getId(),
        a.getNombreCompleto(),
        a.getNroDocumento(),
        a.getTelefono(),
        a.getEmail(),
        a.getFacultad()
      };
      modeloTabla.addRow(fila);
    }
  }

  private void actualizarTablaLibros() {
    Collection<Libro> libros = controladorLibro.obtenerTodosLosLibros();

    String[] columnas = {"ID", "TITULO", "AUTOR", "AÑO", "STOCK"};
    
    modeloTabla.setColumnIdentifiers(columnas);
    modeloTabla.setRowCount(0);

    for (Libro l : libros) {
      Object[] fila = {
        l.getId(),
        l.getTitulo(),
        l.getAutor(),
        l.getAnhoPublicacion(),
        l.getStock()
      };
      modeloTabla.addRow(fila);
    }
  }

  private void configurarEventosMenu(){
    prestamoButton.addActionListener(e -> {
      seleccionarMenu(prestamoButton);
      seccionActiva = Seccion.PRESTAMOS;
      actualizarTablaPrestamos();
    });

    alumnosButton.addActionListener(e -> {
      seleccionarMenu(alumnosButton);
      seccionActiva = Seccion.ALUMNOS;
      actualizarTablaAlumnos();
    });

    librosButton.addActionListener(e -> {
      seleccionarMenu(librosButton);
      seccionActiva = Seccion.LIBROS;
      actualizarTablaLibros();
    });

    agregarButton.addActionListener(e -> {
      switch (seccionActiva) {
        case PRESTAMOS:
          new CrearPrestamoDialog(this, controladorPrestamo);
          actualizarTablaPrestamos();
          break;
        case ALUMNOS:
          new CrearAlumnoDialog(this, controladorAlumno);
          actualizarTablaAlumnos();
          break;
        case LIBROS:
          new CrearLibroDialog(this, controladorLibro);
          actualizarTablaLibros();
          break;
        default:
          break;
      }
    });

    editarButton.addActionListener(e -> {
      switch (seccionActiva) {
        case PRESTAMOS:
          devolverPrestamo();
          break;
        case ALUMNOS:
          editarAlumno();
          break;
        case LIBROS: 
          editarLibro();
          break;
        default:
          break;
      }
    });

    borrarButton.addActionListener(e -> {
      switch (seccionActiva) {
        case PRESTAMOS:
          borrarPrestamo();
          actualizarTablaPrestamos();
          break;
        case ALUMNOS:
          borrarAlumno();
          actualizarTablaAlumnos();
          break;
        case LIBROS:
          borrarLibro();
          actualizarTablaLibros();
          break;
        default:
          break;
      }
    });

    filtrarButton.addActionListener(e -> {
      switch (seccionActiva) {
        case PRESTAMOS:
          filtrarPrestamos();
          break;
        case ALUMNOS:
          filtrarAlumnos();
          break;
        case LIBROS:
          break;
        default:
          break;
      }
    });
  }

  private void seleccionarMenu(BotonBonito botonActivo) {
    prestamoButton.setSeleccionado(false);
    alumnosButton.setSeleccionado(false);
    librosButton.setSeleccionado(false);
    
    botonActivo.setSeleccionado(true);
  }
  
  private void configurarBoton(JButton boton, Dimension dimension){
    boton.setMaximumSize(dimension);
    boton.setPreferredSize(dimension);
    boton.setMinimumSize(dimension);
    boton.setHorizontalAlignment(SwingConstants.LEFT);
    boton.setAlignmentX(Component.LEFT_ALIGNMENT);
    boton.setFont(new Font("Arial", Font.BOLD, 18));
    boton.setFocusPainted(false);
  }

  private void filtrarAlumnos() {
    String[] opciones = {"Por Nombre", "Por Documento"};
    String eleccion = (String) JOptionPane.showInputDialog(
      this, "Ordenar por:", "Filtrar Alumnos",
      JOptionPane.PLAIN_MESSAGE, null, opciones, opciones[0]
    );
    if (eleccion == null) return;

    List<Alumno> lista = new ArrayList<>(controladorAlumno.obtenerTodosLosAlumnos());
    switch (eleccion) {
      case "Por Nombre" -> lista.sort(Comparator.comparing(Alumno::getNombreCompleto));
      case "Por Documento" -> lista.sort(Comparator.comparing(Alumno::getNroDocumento));
    }
    rellenarTablaAlumnos(lista);
  }

  private void filtrarPrestamos() {
    String[] opciones = {"Por Fecha", "Por Alumno", "Vencidos"};
    String eleccion = (String) JOptionPane.showInputDialog(
        this, "Filtrar por:", "Filtrar Préstamos",
        JOptionPane.PLAIN_MESSAGE, null, opciones, opciones[0]
    );
    if (eleccion == null) return;

    switch (eleccion) {
        case "Por Fecha" -> filtrarPrestamosPorFecha();
        case "Por Alumno" -> filtrarPrestamosPorAlumno();
        case "Vencidos" -> filtrarPrestamosVencidos();
    }
}

  private void filtrarPrestamosPorFecha() {
    String input = JOptionPane.showInputDialog(this, "Ingrese fecha (dd/MM/yyyy):");
    if (input == null) return;
    try {
        LocalDate fecha = LocalDate.parse(input.trim(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        List<Prestamo> filtrados = controladorPrestamo.obtenerTodosLosPrestamos()
            .stream()
            .filter(p -> p.getFechaPrestamo().equals(fecha))
            .collect(Collectors.toList());
        if (filtrados.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay préstamos en esa fecha.", "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
        } else {
            rellenarTablaPrestamos(filtrados);
        }
    } catch (DateTimeParseException ex) {
        JOptionPane.showMessageDialog(this, "Formato inválido. Use dd/MM/yyyy.", "Error", JOptionPane.ERROR_MESSAGE);
    }
  }

  private void filtrarPrestamosPorAlumno() {
    String input = JOptionPane.showInputDialog(this, "Ingrese nombre del alumno:");
    if (input == null) return;
    String busqueda = input.trim().toLowerCase();
    List<Prestamo> filtrados = controladorPrestamo.obtenerTodosLosPrestamos()
      .stream()
      .filter(p -> p.getAlumno().getNombreCompleto().toLowerCase().contains(busqueda))
      .collect(Collectors.toList());
    if (filtrados.isEmpty()) {
      JOptionPane.showMessageDialog(this, "No hay préstamos para ese alumno.", "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
    } else {
      rellenarTablaPrestamos(filtrados);
    }
  }

  private void filtrarPrestamosVencidos() {
    Collection<Prestamo> prestamos = controladorPrestamo.obtenerPrestamosVencidos();
    
    String[] columnas = {"ID", "ALUMNO", "LIBROS", "F. PRESTAMO", "F. LIMITE", "MULTA", "Devuelto"};
    
    modeloTabla.setColumnIdentifiers(columnas);
    modeloTabla.setRowCount(0); 

    for (Prestamo p : prestamos) {
      StringBuilder titulosLibros = new StringBuilder();
      for (Libro l : p.getLibrosPrestados()) {
        titulosLibros.append(l.getTitulo()).append(", ");
      }
      String librosStr = titulosLibros.isEmpty() ? "" : titulosLibros.substring(0, titulosLibros.length() - 2);

      Object[] fila = {
        p.getId(),
        p.getAlumno().getNombreCompleto(),
        librosStr,
        p.getFechaPrestamo().toString(),
        p.getFechaLimite().toString(),
        "$" + String.format("%.2f", p.getMulta()),
        p.getEstaDevuelto() ? "SI" : "NO"
      };

      modeloTabla.addRow(fila);
    }
  }

  private void rellenarTablaAlumnos(List<Alumno> alumnos) {
    modeloTabla.setColumnIdentifiers(new String[]{"ID", "NOMBRE", "DOCUMENTO", "TELEFONO", "EMAIL", "FACULTAD"});
    modeloTabla.setRowCount(0);
    for (Alumno a : alumnos) {
      modeloTabla.addRow(new Object[]{
        a.getId(), a.getNombreCompleto(), a.getNroDocumento(),
        a.getTelefono(), a.getEmail(), a.getFacultad()
      });
    }
  }

  private void rellenarTablaPrestamos(List<Prestamo> prestamos) {
    modeloTabla.setColumnIdentifiers(new String[]{"ID", "ALUMNO", "LIBROS", "F. PRESTAMO", "F. LIMITE", "MULTA", "Devuelto"});
    modeloTabla.setRowCount(0);
    for (Prestamo p : prestamos) {
      StringBuilder sb = new StringBuilder();
      for (Libro l : p.getLibrosPrestados()) sb.append(l.getTitulo()).append(", ");
      String librosStr = sb.isEmpty() ? "" : sb.substring(0, sb.length() - 2);
      modeloTabla.addRow(new Object[]{
        p.getId(), p.getAlumno().getNombreCompleto(), librosStr,
        p.getFechaPrestamo().toString(), p.getFechaLimite().toString(),
        "$" + String.format("%.2f", p.getMulta()),
        p.getEstaDevuelto() ? "SI" : "NO"
      });
    }
  }

  private void editarAlumno(){
    String doc = JOptionPane.showInputDialog(this, "Ingrese el Nro de Documento del alumno a editar:");
    if (doc == null) return;
    doc = doc.trim();
    Alumno alumno = controladorAlumno.obtenerAlumno(doc);
    if (alumno == null) {
      JOptionPane.showMessageDialog(this, "Alumno no encontrado.", "Error", JOptionPane.ERROR_MESSAGE);
      return;
    }
    new CrearAlumnoDialog(this, controladorAlumno, alumno);
    actualizarTablaAlumnos();
  }

  private void editarLibro(){
    String input = JOptionPane.showInputDialog(this, "Ingrese el ID del libro a editar:");
    if (input == null) return;
    try {
      int id = Integer.parseInt(input.trim());
      Libro libro = controladorLibro.obtenerLibro(id);
      if (libro == null) {
        JOptionPane.showMessageDialog(this, "Libro no encontrado.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
      }
      new CrearLibroDialog(this, controladorLibro, libro);
      actualizarTablaLibros();
    } catch (NumberFormatException ex) {
      JOptionPane.showMessageDialog(this, "El ID debe ser un número entero.", "Error", JOptionPane.ERROR_MESSAGE);
    }
  }

  private void devolverPrestamo() {
    String input = JOptionPane.showInputDialog(this, "Ingrese el ID del préstamo a devolver:");
    if (input == null) return;
    try {
      int id = Integer.parseInt(input.trim());
      Prestamo prestamo = controladorPrestamo.obtenerPrestamo(id);
      if (prestamo == null) {
          JOptionPane.showMessageDialog(this, "Préstamo no encontrado.", "Error", JOptionPane.ERROR_MESSAGE);
          return;
      }
      if (prestamo.getEstaDevuelto()) {
          JOptionPane.showMessageDialog(this, "Este préstamo ya fue devuelto.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
          return;
      }
      int confirm = JOptionPane.showConfirmDialog(
          this,
          "¿Desea marcar el préstamo #" + id + " como devuelto?",
          "Confirmar devolución",
          JOptionPane.OK_CANCEL_OPTION
      );
      if (confirm == JOptionPane.OK_OPTION) {
          prestamo.setEstaDevuelto(true);
          actualizarTablaPrestamos();
      }
    } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(this, "El ID debe ser un número entero.", "Error", JOptionPane.ERROR_MESSAGE);
    }
  }

  private void borrarLibro(){
    String input = JOptionPane.showInputDialog(this, "Ingrese el ID del libro a borrar:");
    if (input == null) return;
    try {
      int id = Integer.parseInt(input.trim());
      Libro libro = controladorLibro.obtenerLibro(id);
      if (libro == null) {
        JOptionPane.showMessageDialog(this, "Libro no encontrado.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
      }
      controladorLibro.borrarLibro(id);
    } catch (NumberFormatException ex) {
      JOptionPane.showMessageDialog(this, "El ID debe ser un número entero.", "Error", JOptionPane.ERROR_MESSAGE);
    }
  }

  private void borrarAlumno(){
    String input = JOptionPane.showInputDialog(this, "Ingrese el ID del Alumno a borrar:");
    if (input == null) return;
    try {
      int id = Integer.parseInt(input.trim());
      Alumno alumno = controladorAlumno.obtenerAlumno(id);
      if (alumno == null) {
        JOptionPane.showMessageDialog(this, "Alumno no encontrado.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
      }
      controladorAlumno.borrarAlumno(id);
    } catch (NumberFormatException ex) {
      JOptionPane.showMessageDialog(this, "El ID debe ser un número entero.", "Error", JOptionPane.ERROR_MESSAGE);
    }
  }

  private void borrarPrestamo(){
    String input = JOptionPane.showInputDialog(this, "Ingrese el ID del libro a borrar:");
    if (input == null) return;
    try {
      int id = Integer.parseInt(input.trim());
      Prestamo prestamo = controladorPrestamo.obtenerPrestamo(id);
      if (prestamo == null) {
        JOptionPane.showMessageDialog(this, "Libro no encontrado.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
      }
      controladorPrestamo.borrarPrestamo(id);
    } catch (NumberFormatException ex) {
      JOptionPane.showMessageDialog(this, "El ID debe ser un número entero.", "Error", JOptionPane.ERROR_MESSAGE);
    }
  }
}