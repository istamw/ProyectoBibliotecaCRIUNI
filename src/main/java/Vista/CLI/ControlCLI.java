package Vista.CLI;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class ControlCLI {
    private final Scanner scanner = new Scanner(System.in);
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public void mostrarMensaje(String msg) { System.out.println(msg); }
    public void mostrarError(String err) { System.err.println("[ERROR] " + err); }
    public void separador() { System.out.println("--------------------------------------------------------------------------------"); }

    public String pedirString(String mensaje) {
        System.out.print(mensaje);
        return scanner.nextLine().trim();
    }

    public int pedirInt(String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                mostrarError("Entrada invalida. Ingrese un numero entero.");
            }
        }
    }

    public LocalDate pedirFecha(String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje + " (DD/MM/AAAA): ");
                String input = scanner.nextLine().trim();
                return LocalDate.parse(input, formatter);
            } catch (DateTimeParseException e) {
                mostrarError("Formato de fecha invalido. Pruebe nuevamente.");
            }
        }
    }
}