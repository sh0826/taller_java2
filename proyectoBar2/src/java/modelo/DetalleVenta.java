package modelo;

public class DetalleVenta {
    private int id_detalleV;
    private int id_venta;
    private int id_producto;
    private String descripcion;
    private Integer cantidad_productos;
    private Double precio_unitario;

    public DetalleVenta() {}

    public int getId_detalleV() {
        return id_detalleV;
    }

    public void setId_detalleV(int id_detalleV) {
        this.id_detalleV = id_detalleV;
    }

    public int getId_venta() {
        return id_venta;
    }

    public void setId_venta(int id_venta) {
        this.id_venta = id_venta;
    }

    public int getId_producto() {
        return id_producto;
    }

    public void setId_producto(int id_producto) {
        this.id_producto = id_producto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getCantidad_productos() {
        return cantidad_productos;
    }

    public void setCantidad_productos(Integer cantidad_productos) {
        this.cantidad_productos = cantidad_productos;
    }

    public Double getPrecio_unitario() {
        return precio_unitario;
    }

    public void setPrecio_unitario(Double precio_unitario) {
        this.precio_unitario = precio_unitario;
    }
    
}
