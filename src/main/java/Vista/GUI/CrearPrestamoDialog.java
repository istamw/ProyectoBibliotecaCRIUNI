package Vista.GUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import Controlador.ControladorPrestamo;
import Modelo.Libro;

import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class CrearPrestamoDialog extends JDialog {

    private boolean confirmado = false;
    private final ControladorPrestamo controlador;

    private JTextField txtAlumnoId = new JTextField(10);
    private JTextField txtLibroId = new JTextField(8);
    private JTextField txtFPrestamo = new JTextField(10);
    private JTextField txtFLimite = new JTextField(10);

    private DefaultTableModel tablaModel;
    private JTable tablaLibros;
    private List<Integer> libroIds = new ArrayList<>();

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public CrearPrestamoDialog(Frame parent, ControladorPrestamo controller) {
        super(parent, "Nueva Solicitud de Préstamo", true);
        this.controlador = controller;

        setLayout(new BorderLayout(10, 10));
        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        add(formulario(), BorderLayout.NORTH);
        add(tablaLibros(), BorderLayout.CENTER);
        add(botones(), BorderLayout.SOUTH);

        pack();
        setMinimumSize(new Dimension(480, 420));
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    private JPanel formulario() {
        txtFPrestamo.setText(LocalDate.now().format(FMT));
        txtFLimite.setText(LocalDate.now().format(FMT));

        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 4, 4, 4);
        g.fill = GridBagConstraints.HORIZONTAL;

        g.gridx = 0; g.gridy = 0; g.weightx = 0;
        p.add(new JLabel("CI. Alumno:"), g);
        g.gridx = 1;
        g.weightx = 1;
        p.add(txtAlumnoId, g);

        g.gridx = 0; g.gridy = 1; g.weightx = 0;
        p.add(new JLabel("Fecha préstamo (dd/MM/yyyy):"), g);
        g.gridx = 1;
        g.weightx = 1;
        p.add(txtFPrestamo, g);

        g.gridx = 0; g.gridy = 2; g.weightx = 0;
        p.add(new JLabel("Fecha límite (dd/MM/yyyy):"), g);
        g.gridx = 1;
        g.weightx = 1;
        p.add(txtFLimite, g);

        return p;
    }

    private JPanel tablaLibros() {
        JPanel p = new JPanel(new BorderLayout(6, 6));
        p.add(new JLabel("Libros a prestar:"), BorderLayout.NORTH);

        // Tabla
        tablaModel = new DefaultTableModel(new String[]{"ID", "Título"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaLibros = new JTable(tablaModel);
        tablaLibros.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaLibros.getColumnModel().getColumn(0).setMaxWidth(50);
        p.add(new JScrollPane(tablaLibros), BorderLayout.CENTER);

        JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        fila.add(new JLabel("ID Libro:"));
        fila.add(txtLibroId);

        JButton btnAgregar = new JButton("+ Agregar");
        JButton btnQuitar = new JButton("✖ Quitar");

        btnAgregar.addActionListener(e -> agregarLibro());
        btnQuitar.addActionListener(e -> quitarLibro());

        fila.add(btnAgregar);
        fila.add(btnQuitar);
        p.add(fila, BorderLayout.SOUTH);

        return p;
    }

    private JPanel botones() {
        JButton btnCancelar = new JButton("Cancelar");
        JButton btnCrear = new JButton("Crear");

        btnCancelar.addActionListener(e -> dispose());
        btnCrear.addActionListener(e -> confirmar());

        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        p.add(btnCancelar);
        p.add(btnCrear);
        return p;
    }

    private void agregarLibro() {
        int id;
        try {
            id = Integer.parseInt(txtLibroId.getText().trim());
        } catch (NumberFormatException ex) {
            error("El ID del libro debe ser un número entero.");
            return;
        }

        if (libroIds.contains(id)) {
            error("Ese libro ya fue agregado al préstamo.");
            return;
        }

        Libro libro = controlador.getRepoLibros().buscarPorId(id);
        if (libro == null) {
            error("No existe un libro con ID " + id + ".");
            return;
        }

        libroIds.add(id);
        tablaModel.addRow(new Object[]{id, libro.getTitulo()});
        txtLibroId.setText("");
        txtLibroId.requestFocus();
    }

    private void quitarLibro() {
        int fila = tablaLibros.getSelectedRow();
        if (fila == -1) {
            error("Seleccioná un libro de la tabla para quitarlo.");
            return;
        }
        libroIds.remove(fila);
        tablaModel.removeRow(fila);
    }

    private void confirmar() {
        // Validar alumno
        String alumnoDoc;
        alumnoDoc = txtAlumnoId.getText().trim();
        if (alumnoDoc.isEmpty()){
            error("Todos los campos son obligatorios.");
        }

        // Validar que haya al menos un libro
        if (libroIds.isEmpty()) {
            error("Agregá al menos un libro al préstamo.");
            return;
        }

        // Validar fechas
        LocalDate fPrestamo, fLimite;
        try {
            fPrestamo = LocalDate.parse(txtFPrestamo.getText().trim(), FMT);
            fLimite = LocalDate.parse(txtFLimite.getText().trim(), FMT);
        } catch (DateTimeParseException ex) {
            error("Las fechas deben tener el formato dd/MM/yyyy.");
            return;
        }

        if (!fLimite.isAfter(fPrestamo)) {
            error("La fecha límite debe ser posterior a la fecha de préstamo.");
            return;
        }

        // Llama al controller
        int respuesta = controlador.crearPrestamo(alumnoDoc, libroIds, fPrestamo, fLimite);
        switch (respuesta) {
            case 0 -> { confirmado = true; dispose(); }
            case 1 -> error("El alumno especificado no existe.");
            case 2 -> error("Uno o más IDs de libros no existen.");
            case 3 -> error("Uno o más libros no tienen stock disponible.");
        }
    }

    private void error(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public boolean isConfirmado() { return confirmado; }
}