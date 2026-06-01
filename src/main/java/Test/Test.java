package Test;

import java.util.Scanner;

public class Test {
  public static void main(String[] args) {
    Scanner teclado = new Scanner(System.in);

    //String testo = teclado.nextLine().trim();
    Integer num = Integer.parseInt(teclado.nextLine().trim());

    System.out.println(num);

    teclado.close();
  }  
}
