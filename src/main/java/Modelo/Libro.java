package Modelo;

public class Libro extends ModeloBase {
    private String titulo;
    private String editorial;
    private int anhoPublicacion;
    private String autor;
    private int stock;

    public Libro(int id, String titulo, String editorial, int anhoPublicacion, String autor, int stock) {
        super(id);
        this.titulo = titulo;
        this.editorial = editorial;
        this.anhoPublicacion = anhoPublicacion;
        this.autor = autor;
        this.stock = stock;
    }

    // Getters y Setters
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getEditorial() { return editorial; }
    public void setEditorial(String editorial) { this.editorial = editorial; }
    public int getAnhoPublicacion() { return anhoPublicacion; }
    public void setAnhoPublicacion(int anhoPublicacion) { this.anhoPublicacion = anhoPublicacion; }
    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
}