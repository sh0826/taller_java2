package modelo;

/**
 * Clase para representar detalles de venta agrupados por producto
 * Muestra el total de ventas de un producto y su precio unitario
 */
public class DetalleVentaAgrupado {
    private int id_producto;
    private String descripcion;
    private String nombreProducto;
    private Integer totalCantidad; // Suma de todas las cantidades vendidas
    private Double precioUnitario; // Precio unitario del producto
    
    public DetalleVentaAgrupado() {}
    
    public DetalleVentaAgrupado(int id_producto, String descripcion, String nombreProducto, 
                                Integer totalCantidad, Double precioUnitario) {
        this.id_producto = id_producto;
        this.descripcion = descripcion;
        this.nombreProducto = nombreProducto;
        this.totalCantidad = totalCantidad;
        this.precioUnitario = precioUnitario;
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

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public Integer getTotalCantidad() {
        return totalCantidad;
    }

    public void setTotalCantidad(Integer totalCantidad) {
        this.totalCantidad = totalCantidad;
    }

    public Double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(Double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }
    
    public Double getTotalVenta() {
        if (totalCantidad != null && precioUnitario != null) {
            return totalCantidad * precioUnitario;
        }
        return 0.0;
    }
}

