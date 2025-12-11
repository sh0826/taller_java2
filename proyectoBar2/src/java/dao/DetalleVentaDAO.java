package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import modelo.DetalleVenta;

public class DetalleVentaDAO {

    Connection conn;
    PreparedStatement ps;
    ResultSet rs;

    public List<DetalleVenta> listar() throws Exception {
        List<DetalleVenta> lista = new ArrayList<>();
        conn = ConnBD.conectar();
        String sql = "SELECT * FROM detalle_venta";
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
            lista.add(d);
        }
        return lista;
    }

    public void insertar(DetalleVenta d) throws Exception {
        conn = ConnBD.conectar();
        String sql = "INSERT INTO detalle_venta (id_venta, id_producto, descripcion, cantidad_productos, precio_unitario) VALUES (?,?,?,?,?)";
        ps = conn.prepareStatement(sql);
        ps.setInt(1, d.getId_venta());
        ps.setInt(2, d.getId_producto());
        ps.setString(3, d.getDescripcion());
        ps.setObject(4, d.getCantidad_productos(), java.sql.Types.INTEGER);
        ps.setObject(5, d.getPrecio_unitario(), java.sql.Types.DECIMAL);
        ps.executeUpdate();
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
        StringBuilder sql = new StringBuilder("SELECT * FROM detalle_venta WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        if (idVenta != null && idVenta > 0) {
            sql.append(" AND id_venta = ?");
            params.add(idVenta);
        }
        
        if (idProducto != null && idProducto > 0) {
            sql.append(" AND id_producto = ?");
            params.add(idProducto);
        }
        
        if (descripcion != null && !descripcion.trim().isEmpty()) {
            sql.append(" AND descripcion LIKE ?");
            params.add("%" + descripcion + "%");
        }
        
        sql.append(" ORDER BY id_detalleV DESC");
        
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
            lista.add(d);
        }
        return lista;
    }
}
