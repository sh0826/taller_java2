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
            p.setImagen(rs.getString("imagen"));
            lista.add(p);
        }
        return lista;
    }

    public void insertar(Producto p) throws Exception {
        conn = ConnBD.conectar();
        String sql = "INSERT INTO producto (nombre, tipo_producto, stock, precio_unitario, imagen) VALUES (?, ?, ?, ?, ?)";
        ps = conn.prepareStatement(sql);

        ps.setString(1, p.getNombre());
        ps.setString(2, p.getTipo_producto());
        ps.setInt(3, p.getStock());
        ps.setDouble(4, p.getPrecio_unitario());
        ps.setString(5, p.getImagen());

        ps.executeUpdate();
    }

    public void modificar(Producto p) throws Exception {
        conn = ConnBD.conectar();
        String sql = "UPDATE producto SET nombre=?, tipo_producto=?, stock=?, precio_unitario=?, imagen=? WHERE id_producto=?";
        ps = conn.prepareStatement(sql);

        ps.setString(1, p.getNombre());
        ps.setString(2, p.getTipo_producto());
        ps.setInt(3, p.getStock());
        ps.setDouble(4, p.getPrecio_unitario());
        ps.setString(5, p.getImagen());
        ps.setInt(6, p.getId_producto());

        ps.executeUpdate();
    }

    public void eliminar(int id) throws Exception {
        conn = ConnBD.conectar();
        String sql = "DELETE FROM producto WHERE id_producto=?";
        ps = conn.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
    }
    
    public Producto buscarPorId(int id) throws Exception {
        conn = ConnBD.conectar();
        String sql = "SELECT * FROM producto WHERE id_producto=?";
        ps = conn.prepareStatement(sql);
        ps.setInt(1, id);
        rs = ps.executeQuery();
        
        if (rs.next()) {
            Producto p = new Producto();
            p.setId_producto(rs.getInt("id_producto"));
            p.setNombre(rs.getString("nombre"));
            p.setTipo_producto(rs.getString("tipo_producto"));
            p.setStock(rs.getInt("stock"));
            p.setPrecio_unitario(rs.getDouble("precio_unitario"));
            p.setImagen(rs.getString("imagen"));
            return p;
        }
        return null;
    }
    
    /**
     * Disminuye el stock de un producto en la cantidad especificada
     * @param idProducto ID del producto
     * @param cantidad Cantidad a disminuir del stock
     * @return true si se actualizó correctamente, false si no hay suficiente stock o el producto no existe
     */
    public boolean disminuirStock(int idProducto, int cantidad) throws Exception {
        conn = ConnBD.conectar();
        
        // Primero verificar que el producto existe y tiene suficiente stock
        String sqlVerificar = "SELECT stock FROM producto WHERE id_producto = ?";
        ps = conn.prepareStatement(sqlVerificar);
        ps.setInt(1, idProducto);
        rs = ps.executeQuery();
        
        if (!rs.next()) {
            System.err.println("Error: El producto con ID " + idProducto + " no existe");
            return false;
        }
        
        int stockActual = rs.getInt("stock");
        if (stockActual < cantidad) {
            System.err.println("Error: Stock insuficiente. Stock actual: " + stockActual + ", Cantidad solicitada: " + cantidad);
            return false;
        }
        
        // Actualizar el stock
        String sqlActualizar = "UPDATE producto SET stock = stock - ? WHERE id_producto = ?";
        ps = conn.prepareStatement(sqlActualizar);
        ps.setInt(1, cantidad);
        ps.setInt(2, idProducto);
        
        int filas = ps.executeUpdate();
        if (filas > 0) {
            System.out.println("Stock actualizado: Producto ID " + idProducto + " - Stock disminuido en " + cantidad);
            return true;
        }
        
        return false;
    }
}
