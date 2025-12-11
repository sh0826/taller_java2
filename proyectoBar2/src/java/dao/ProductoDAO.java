package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import modelo.Producto;

public class ProductoDAO {

    Connection conn;
    PreparedStatement ps;
    ResultSet rs;

    public List<Producto> listar() throws Exception {
        List<Producto> lista = new ArrayList<>();
        conn = ConnBD.conectar(); 

        String sql = "SELECT * FROM producto";
        ps = conn.prepareStatement(sql);
        rs = ps.executeQuery();

        while (rs.next()) {
            Producto p = new Producto();
            p.setId_producto(rs.getInt("id_producto"));
            p.setNombre(rs.getString("nombre"));
            p.setTipo_producto(rs.getString("tipo_producto"));
            p.setStock(rs.getInt("stock"));
            p.setPrecio_unitario(rs.getDouble("precio_unitario"));
            lista.add(p);
        }
        return lista;
    }

    public void insertar(Producto p) throws Exception {
        conn = ConnBD.conectar();
        String sql = "INSERT INTO producto (nombre, tipo_producto, stock, precio_unitario) VALUES (?, ?, ?, ?)";
        ps = conn.prepareStatement(sql);

        ps.setString(1, p.getNombre());
        ps.setString(2, p.getTipo_producto());
        ps.setInt(3, p.getStock());
        ps.setDouble(4, p.getPrecio_unitario());

        ps.executeUpdate();
    }

    public void modificar(Producto p) throws Exception {
        conn = ConnBD.conectar();
        String sql = "UPDATE producto SET nombre=?, tipo_producto=?, stock=?, precio_unitario=? WHERE id_producto=?";
        ps = conn.prepareStatement(sql);

        ps.setString(1, p.getNombre());
        ps.setString(2, p.getTipo_producto());
        ps.setInt(3, p.getStock());
        ps.setDouble(4, p.getPrecio_unitario());
        ps.setInt(5, p.getId_producto());

        ps.executeUpdate();
    }

    public void eliminar(int id) throws Exception {
        conn = ConnBD.conectar();
        String sql = "DELETE FROM producto WHERE id_producto=?";
        ps = conn.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
    }
}
