package Vista.GUI.components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class BotonBonito extends JButton {
  private Color colorFondoNormal = Color.WHITE;
  private Color colorTextoNormal = new Color(100, 100, 100);
  
  private Color colorFondoSeleccionado = new Color(140, 140, 140); // El gris de tu diseño
  private Color colorTextoSeleccionado = Color.WHITE;

  private boolean seleccionado = false;
  private int radioEsquina = 500;

  private boolean esMiniBoton = false;
  private Dimension tamanoCustom = null;

  public BotonBonito(String texto){
    this(texto, false, 100, true);
  }

  public BotonBonito(String texto, boolean seleccionadoInicial, int radio, boolean esMiniBoton) {
    super(texto);
    setSeleccionado(seleccionadoInicial);
    this.esMiniBoton = esMiniBoton;
    radioEsquina = radio;

    setFont(new Font("Arial", Font.BOLD, 14));
    setContentAreaFilled(false);
    setBorderPainted(false);
    setFocusPainted(false);
    
    if (esMiniBoton) {
      setHorizontalAlignment(SwingConstants.CENTER);
    } else {
      setHorizontalAlignment(SwingConstants.LEFT);
      setBorder(new EmptyBorder(15, 25, 15, 25));
    }
    actualizarColores();
  }

  //Cambiar el estado del boton desde afuera
  public void setSeleccionado(boolean b) {
    this.seleccionado = b;
    actualizarColores();
    repaint();
  }

  public boolean isSeleccionado() {
    return this.seleccionado;
  }

  private void actualizarColores() {
    if (seleccionado) {
      setForeground(colorTextoSeleccionado);
    } else {
      setForeground(colorTextoNormal);
    }
  }

  @Override
  protected void paintComponent(Graphics g) {
    Graphics2D g2 = (Graphics2D) g.create();
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    int desproporcionSombra = 0;
    
    // Efecto
    if (getModel().isPressed()) {
      desproporcionSombra = 0;
      g2.translate(0, 3); // se mueve para q parezca que se "presiona"
    } else {
      //Sombra normal cuando no se presiona
      desproporcionSombra = 4;
    }

    // Dibujar la sombra si esq se presiona
    if (desproporcionSombra > 0) {
      g2.setColor(new Color(0, 0, 0, 35));
      g2.fillRoundRect(2, desproporcionSombra, getWidth() - 4, getHeight() - desproporcionSombra, radioEsquina, radioEsquina);
    }

    if (seleccionado) {
      g2.setColor(colorFondoSeleccionado);
    } else {
      g2.setColor(colorFondoNormal);
    }
    
    g2.fillRoundRect(0, 0, getWidth() - 4, getHeight() - 4, radioEsquina, radioEsquina);

    g2.dispose();
    super.paintComponent(g);
  }

  public void setTamanoCustom(Dimension d) {
    this.tamanoCustom = d;
  }

  @Override
  public Dimension getPreferredSize() {
    if (esMiniBoton) {
      return new Dimension(65, 65);
    } else if (tamanoCustom != null) {
      return tamanoCustom;
    } else {
      return new Dimension(220, 75);
    }
  }
}