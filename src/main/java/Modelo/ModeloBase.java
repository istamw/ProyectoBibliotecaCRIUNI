package Modelo;

public abstract class ModeloBase {
    private int id = 0;
    private boolean borrado = false;
    private String modificadoPor = null;

    public ModeloBase(int id) {
        this.id = id;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public boolean isBorrado() { return borrado; }
    public void setBorrado(boolean borrado) { this.borrado = borrado; }

    public String getModificadoPor() { return modificadoPor; }
    public void setModificadoPor(String modificadoPor) { this.modificadoPor = modificadoPor; }
}