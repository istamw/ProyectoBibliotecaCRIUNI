package Vista.GUI;

import javax.swing.*;
import java.awt.Image;

public class ClienteGUI extends JFrame {
  public ClienteGUI() {
    JOptionPane.showMessageDialog(null, "Hola bro");
    setTitle("Gestor Biblioteca");
    setSize(800, 600);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    Image icono = new ImageIcon(
            getClass().getResource("/icons/icon.png")
    ).getImage();
    setIconImage(icono);
    setVisible(true);
  }
}