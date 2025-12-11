package modelo;

public class Producto {
    private int id_producto;
    private String nombre;
    private String tipo_producto;
    private int stock;
    private double precio_unitario;
    private String imagen;

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
    

    public Producto() {}

    public int getId_producto() {
        return id_producto;
    }
    public void setId_producto(int id_producto) {
        this.id_producto = id_producto;
    }

    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo_producto() {
        return tipo_producto;
    }
    public void setTipo_producto(String tipo_producto) {
        this.tipo_producto = tipo_producto;
    }

    public int getStock() {
        return stock;
    }
    public void setStock(int stock) {
        this.stock = stock;
    }

    public double getPrecio_unitario() {
        return precio_unitario;
    }
    public void setPrecio_unitario(double precio_unitario) {
        this.precio_unitario = precio_unitario;
    }
}
