package Vista.GUI;

import javax.swing.*;
import Controlador.ControladorAlumno;
import Modelo.Alumno;

import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class CrearAlumnoDialog extends JDialog {
  private boolean confirmado = false;

  private JTextField nombreField = new JTextField(20);
  private JTextField docField = new JTextField(20);
  private JTextField emailField = new JTextField(20);
  private JTextField telField = new JTextField(20);
  private JTextField fechaNacField = new JTextField(20);
  private JTextField facultadField = new JTextField(20);

  private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

  public CrearAlumnoDialog(Frame parent, ControladorAlumno controlador) {
    this(parent, controlador, null);
  }

  public CrearAlumnoDialog(Frame parent, ControladorAlumno controlador, Alumno alumno) {
    super(parent, alumno == null ? "Crear nuevo Alumno" : "Editar Alumno", true);

    boolean esEdicion = alumno != null;

    fechaNacField.setText(LocalDate.now().format(FMT));

    if (esEdicion) {
      nombreField.setText(alumno.getNombreCompleto());
      docField.setText(alumno.getNroDocumento());
      emailField.setText(alumno.getEmail());
      telField.setText(alumno.getTelefono());
      fechaNacField.setText(alumno.getFechaNacimiento().format(FMT));
      facultadField.setText(alumno.getFacultad());
    }


    JPanel panel = new JPanel(new GridBagLayout());
    panel.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(6, 6, 6, 6);
    gbc.fill = GridBagConstraints.HORIZONTAL;

    String[] labels = {"Nombre Completo", "Nro Documento", "Email", "Teléfono", "Fecha Nacimiento (dd/MM/yyyy)", "Facultad"};
    JTextField[] fields = {nombreField, docField, emailField, telField, fechaNacField, facultadField};

    for (int i = 0; i < labels.length; i++) {
      gbc.gridx = 0; gbc.gridy = i; gbc.weightx = 0;
      panel.add(new JLabel(labels[i]), gbc);
      gbc.gridx = 1; gbc.weightx = 1.0;
      panel.add(fields[i], gbc);
    }

    JButton cancelarButton = new JButton("Cancelar");
    JButton confirmarButton = new JButton(esEdicion ? "Guardar" : "Crear");

    cancelarButton.addActionListener(e -> dispose());
    confirmarButton.addActionListener(e -> {
      String nombre = nombreField.getText().trim();
      String doc = docField.getText().trim();
      String email = emailField.getText().trim();
      String tel = telField.getText().trim();
      String fechaStr = fechaNacField.getText().trim();
      String facultad = facultadField.getText().trim();

      if (nombre.isEmpty() || doc.isEmpty() || email.isEmpty() ||
        tel.isEmpty() || fechaStr.isEmpty() || facultad.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
      }

      LocalDate fechaNac;
      try {
        fechaNac = LocalDate.parse(fechaStr, FMT);
      } catch (DateTimeParseException ex) {
        JOptionPane.showMessageDialog(this, "La fecha debe tener el formato dd/MM/yyyy.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
      }

      if (esEdicion) {
        controlador.editarAlumno(alumno.getId(), nombre, doc, email, tel, fechaNac, facultad);
      } else {
        controlador.crearAlumno(nombre, doc, email, tel, fechaNac, facultad);
      }

      confirmado = true;
      dispose();
    });

    JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    panelBotones.add(cancelarButton);
    panelBotones.add(confirmarButton);

    setLayout(new BorderLayout());
    add(panel, BorderLayout.CENTER);
    add(panelBotones, BorderLayout.SOUTH);

    pack();
    setLocationRelativeTo(parent);
    setVisible(true);
  }

  public boolean isConfirmado() {
    return confirmado;
  }
}