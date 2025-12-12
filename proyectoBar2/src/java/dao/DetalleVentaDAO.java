package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import modelo.DetalleVenta;
import modelo.DetalleVentaAgrupado;

public class DetalleVentaDAO {

    Connection conn;
    PreparedStatement ps;
    ResultSet rs;

    public List<DetalleVenta> listar() throws Exception {
        List<DetalleVenta> lista = new ArrayList<>();
        conn = ConnBD.conectar();
        String sql = "SELECT dv.*, COALESCE(p.nombre, 'Producto Desconocido') AS nombre_producto " +
                     "FROM detalle_venta dv " +
                     "LEFT JOIN producto p ON dv.id_producto = p.id_producto " +
                     "ORDER BY dv.id_detalleV DESC";
        ps = conn.prepareStatement(sql);
        rs = ps.executeQuery();
        while(rs.next()) {
            DetalleVenta d = new DetalleVenta();
            d.setId_detalleV(rs.getInt("id_detalleV"));
            d.setId_venta(rs.getInt("id_venta"));
            d.setId_producto(rs.getInt("id_producto"));
            d.setDescripcion(rs.getString("descripcion"));
            d.setCantidad_productos(rs.getInt("cantidad_productos"));
            d.setPrecio_unitario(rs.getDouble("precio_unitario"));
            d.setNombre_producto(rs.getString("nombre_producto"));
            lista.add(d);
        }
        return lista;
    }

    public void insertar(DetalleVenta d) throws Exception {
        conn = ConnBD.conectar();
        
        // Validar que la cantidad sea mayor a 0
        if (d.getCantidad_productos() == null || d.getCantidad_productos() <= 0) {
            throw new Exception("La cantidad de productos debe ser mayor a 0");
        }
        
        // Disminuir el stock del producto antes de insertar el detalle
        ProductoDAO productoDAO = new ProductoDAO();
        boolean stockActualizado = productoDAO.disminuirStock(d.getId_producto(), d.getCantidad_productos());
        
        if (!stockActualizado) {
            throw new Exception("No se pudo disminuir el stock del producto. Verifique que haya suficiente stock disponible.");
        }
        
        // Insertar el detalle de venta
        String sql = "INSERT INTO detalle_venta (id_venta, id_producto, descripcion, cantidad_productos, precio_unitario) VALUES (?,?,?,?,?)";
        ps = conn.prepareStatement(sql);
        ps.setInt(1, d.getId_venta());
        ps.setInt(2, d.getId_producto());
        ps.setString(3, d.getDescripcion());
        ps.setObject(4, d.getCantidad_productos(), java.sql.Types.INTEGER);
        ps.setObject(5, d.getPrecio_unitario(), java.sql.Types.DECIMAL);
        ps.executeUpdate();
        
        System.out.println("Detalle de venta insertado correctamente. Stock del producto ID " + d.getId_producto() + " disminuido en " + d.getCantidad_productos());
    }

    public void modificar(DetalleVenta d) throws Exception {
        conn = ConnBD.conectar();
        String sql = "UPDATE detalle_venta SET id_venta=?, id_producto=?, descripcion=?, cantidad_productos=?, precio_unitario=? WHERE id_detalleV=?";
        ps = conn.prepareStatement(sql);
        ps.setInt(1, d.getId_venta());
        ps.setInt(2, d.getId_producto());
        ps.setString(3, d.getDescripcion());
        ps.setObject(4, d.getCantidad_productos(), java.sql.Types.INTEGER);
        ps.setObject(5, d.getPrecio_unitario(), java.sql.Types.DECIMAL);
        ps.setInt(6, d.getId_detalleV());
        ps.executeUpdate();
    }

    public void eliminar(int id) throws Exception {
        conn = ConnBD.conectar();
        String sql = "DELETE FROM detalle_venta WHERE id_detalleV=?";
        ps = conn.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
    }
    
    public List<DetalleVenta> listarConFiltros(Integer idVenta, Integer idProducto, String descripcion) throws Exception {
        List<DetalleVenta> lista = new ArrayList<>();
        conn = ConnBD.conectar();
        StringBuilder sql = new StringBuilder("SELECT dv.*, COALESCE(p.nombre, 'Producto Desconocido') AS nombre_producto " +
                                               "FROM detalle_venta dv " +
                                               "LEFT JOIN producto p ON dv.id_producto = p.id_producto " +
                                               "WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        if (idVenta != null && idVenta > 0) {
            sql.append(" AND dv.id_venta = ?");
            params.add(idVenta);
        }
        
        if (idProducto != null && idProducto > 0) {
            sql.append(" AND dv.id_producto = ?");
            params.add(idProducto);
        }
        
        if (descripcion != null && !descripcion.trim().isEmpty()) {
            sql.append(" AND dv.descripcion LIKE ?");
            params.add("%" + descripcion + "%");
        }
        
        sql.append(" ORDER BY dv.id_detalleV DESC");
        
        ps = conn.prepareStatement(sql.toString());
        for (int i = 0; i < params.size(); i++) {
            ps.setObject(i + 1, params.get(i));
        }
        
        rs = ps.executeQuery();
        while(rs.next()) {
            DetalleVenta d = new DetalleVenta();
            d.setId_detalleV(rs.getInt("id_detalleV"));
            d.setId_venta(rs.getInt("id_venta"));
            d.setId_producto(rs.getInt("id_producto"));
            d.setDescripcion(rs.getString("descripcion"));
            d.setCantidad_productos(rs.getInt("cantidad_productos"));
            d.setPrecio_unitario(rs.getDouble("precio_unitario"));
            d.setNombre_producto(rs.getString("nombre_producto"));
            lista.add(d);
        }
        return lista;
    }
    
    /**
     * Agrupa los detalles de venta por descripción, sumando las cantidades totales,
     * contando cuántas compras se hicieron y mostrando el precio unitario
     */
    public List<DetalleVentaAgrupado> listarAgrupadoPorProducto(Integer idVenta) throws Exception {
        List<DetalleVentaAgrupado> lista = new ArrayList<>();
        conn = ConnBD.conectar();
        
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT dv.id_producto, ");
        sql.append("       dv.descripcion, ");
        sql.append("       COALESCE(p.nombre, 'Producto Desconocido') as nombre_producto, ");
        sql.append("       SUM(dv.cantidad_productos) as total_cantidad, ");
        sql.append("       MAX(dv.precio_unitario) as precio_unitario, ");
        sql.append("       COUNT(*) as cantidad_compras ");
        sql.append("FROM detalle_venta dv ");
        sql.append("LEFT JOIN producto p ON dv.id_producto = p.id_producto ");
        sql.append("WHERE 1=1 ");
        
        List<Object> params = new ArrayList<>();
        
        if (idVenta != null && idVenta > 0) {
            sql.append("AND dv.id_venta = ? ");
            params.add(idVenta);
        }
        
        sql.append("GROUP BY dv.id_producto, dv.descripcion ");
        sql.append("ORDER BY total_cantidad DESC, dv.descripcion");
        
        ps = conn.prepareStatement(sql.toString());
        for (int i = 0; i < params.size(); i++) {
            ps.setObject(i + 1, params.get(i));
        }
        
        rs = ps.executeQuery();
        while(rs.next()) {
            DetalleVentaAgrupado d = new DetalleVentaAgrupado();
            d.setId_producto(rs.getInt("id_producto"));
            d.setDescripcion(rs.getString("descripcion"));
            d.setNombreProducto(rs.getString("nombre_producto"));
            d.setTotalCantidad(rs.getInt("total_cantidad"));
            d.setPrecioUnitario(rs.getDouble("precio_unitario"));
            d.setCantidadCompras(rs.getInt("cantidad_compras"));
            lista.add(d);
        }
        return lista;
    }
}
