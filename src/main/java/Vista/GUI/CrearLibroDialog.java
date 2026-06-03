package Vista.GUI;

import javax.swing.*;
import Controlador.ControladorLibro;
import Modelo.Libro;
import java.awt.*;

public class CrearLibroDialog extends JDialog {
  private boolean confirmado = false;

  private JTextField tituloField = new JTextField(20);
  private JTextField editoriaField = new JTextField(20);
  private JTextField anhoField = new JTextField(20);
  private JTextField autorField = new JTextField(20);
  private JTextField stockField = new JTextField(20);

  public CrearLibroDialog(Frame parent, ControladorLibro controlador) {
    this(parent, controlador, null);
    for (Libro l : controlador.obtenerTodosLosLibros()) {
        System.out.println("  -> ID: " + l.getId() + " | Titulo: " + l.getTitulo());
    }
  }

  //Si se introduce un libro en el construcctor, se entiende que se quiere EDITAR
  public CrearLibroDialog(Frame parent, ControladorLibro controlador, Libro libro) {
    super(parent, libro == null ? "Crear nuevo Libro" : "Editar Libro", true);

    boolean esEdicion = libro != null;

    if (esEdicion) {
      tituloField.setText(libro.getTitulo());
      editoriaField.setText(libro.getEditorial());
      anhoField.setText(String.valueOf(libro.getAnhoPublicacion()));
      autorField.setText(libro.getAutor());
      stockField.setText(String.valueOf(libro.getStock()));
    }

    JPanel panel = new JPanel(new GridBagLayout());
    panel.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(6, 6, 6, 6);
    gbc.fill = GridBagConstraints.HORIZONTAL;

    String[] labels = {"Título", "Editorial", "Año", "Autor", "Stock"};
    JTextField[] fields = {tituloField, editoriaField, anhoField, autorField, stockField};

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
      try {
        String titulo = tituloField.getText().trim();
        String editorial = editoriaField.getText().trim();
        int anho = Integer.parseInt(anhoField.getText().trim());
        String autor = autorField.getText().trim();
        int stock = Integer.parseInt(stockField.getText().trim());

        if (titulo.isEmpty() || editorial.isEmpty() || autor.isEmpty()) {
          JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.", "Error", JOptionPane.ERROR_MESSAGE);
          return;
        }

        if (esEdicion) {
          controlador.editarLibro(libro.getId(), titulo, editorial, anho, autor, stock);
        } else {
          controlador.crearLibro(titulo, editorial, anho, autor, stock);
        }

        confirmado = true;
        dispose();
      } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(this, "Año y Stock deben ser números enteros.", "Error", JOptionPane.ERROR_MESSAGE);
      }
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