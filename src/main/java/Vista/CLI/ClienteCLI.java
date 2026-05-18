package Vista.CLI;

public class ClienteCLI {
    private final SeccionLibro libroView;
    private final SeccionAlumno alumnoView; // La vista de alumnos hecha en el ejemplo anterior
    private final SeccionPrestamo prestamoView;
    private final ControlCLI cv;

    public ClienteCLI(SeccionLibro lv, SeccionAlumno av, SeccionPrestamo pv, ControlCLI cv) {
        this.libroView = lv;
        this.alumnoView = av;
        this.prestamoView = pv;
        this.cv = cv;
    }

    public void arrancar() {
        boolean finalizar = false;
        while (!finalizar) {
            cv.separador();
            cv.mostrarMensaje("=== MENU PRINCIPAL ===");
            cv.mostrarMensaje("[ 1 ] - Gestion de Libros");
            cv.mostrarMensaje("[ 2 ] - Gestion de Alumnos");
            cv.mostrarMensaje("[ 3 ] - Gestion de Prestamos");
            cv.mostrarMensaje("[ 4 ] - Salir");
            int op = cv.pedirInt("Seleccione una opcion: ");

            switch (op) {
                case 1 -> libroView.iniciarMenu();
                case 2 -> alumnoView.iniciarMenu();
                case 3 -> prestamoView.iniciarMenu();
                case 4 -> {
                    cv.mostrarMensaje("Saliendo del sistema de biblioteca... ¡Hasta luego!");
                    finalizar = true;
                }
                default -> cv.mostrarError("Opcion invalida, reintente.");
            }
        }
    }
}