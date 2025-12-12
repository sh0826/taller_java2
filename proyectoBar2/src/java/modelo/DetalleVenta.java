package modelo;

public class DetalleVenta {
    private int id_detalleV;
    private int id_venta;
    private int id_producto;
    private String descripcion;
    private Integer cantidad_productos;
    private Double precio_unitario;
    private String nombre_producto; // Nombre del producto (obtenido del JOIN)

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
    
    public String getNombre_producto() {
        return nombre_producto;
    }
    
    public void setNombre_producto(String nombre_producto) {
        this.nombre_producto = nombre_producto;
    }
    
    /**
     * Calcula el precio final (subtotal) multiplicando cantidad por precio unitario
     * @return Precio final calculado
     */
    public Double getPrecioFinal() {
        if (cantidad_productos != null && precio_unitario != null) {
            return cantidad_productos * precio_unitario;
        }
        return 0.0;
    }
    
}
