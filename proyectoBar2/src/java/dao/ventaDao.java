/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import modelo.venta;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author 2025
 */
public class ventaDao {
    
    public boolean insertar(venta v) {
        if (v == null) {
            System.err.println("Error: La venta es null");
            return false;
        }
        
        if (v.getTotal() == null || v.getTotal().compareTo(BigDecimal.ZERO) <= 0) {
            System.err.println("Error: El total debe ser mayor a 0");
            return false;
        }
        
        if (v.getMetodo_pago() == null || v.getMetodo_pago().trim().isEmpty()) {
            System.err.println("Error: El método de pago es requerido");
            return false;
        }
        
        // Obtener el siguiente ID disponible
        int siguienteId = obtenerSiguienteId();
        
        String sql = "INSERT INTO venta (id_venta, total, metodo_pago, id_usuario) VALUES (?, ?, ?, ?)";
        
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = ConnBD.conectar();
            ps = conn.prepareStatement(sql);
            
            ps.setInt(1, siguienteId);
            ps.setBigDecimal(2, v.getTotal());
            ps.setString(3, v.getMetodo_pago());
            ps.setInt(4, v.getId()); // ID del usuario (foreign key a usuario.id_usuario)
            
            int filas = ps.executeUpdate();
            
            if (filas > 0) {
                System.out.println("Venta insertada correctamente");
                return true;
            } else {
                System.err.println("No se insertó ninguna fila");
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("Error SQL al insertar venta: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.err.println("Error inesperado al insertar venta: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar conexión: " + e.getMessage());
            }
        }
    }
    
    public List<venta> listar() {
        List<venta> lista = new ArrayList<>();
        String sql = "SELECT v.*, u.nombre_completo AS nombre_usuario " +
                     "FROM venta v " +
                     "INNER JOIN usuario u ON v.id_usuario = u.id_usuario " +
                     "ORDER BY v.fecha DESC";
        
        try (Connection conn = ConnBD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                venta v = new venta();
                v.setId_venta(rs.getInt("id_venta"));
                // Obtener el Timestamp directamente
                Timestamp fecha = rs.getTimestamp("fecha");
                v.setFecha(fecha);
                if (fecha != null) {
                    System.out.println("Fecha obtenida: " + fecha.toString());
                } else {
                    System.out.println("ADVERTENCIA: Fecha es null para venta ID: " + rs.getInt("id_venta"));
                }
                v.setTotal(rs.getBigDecimal("total"));
                v.setMetodo_pago(rs.getString("metodo_pago"));
                v.setId(rs.getInt("id_usuario")); // ID del usuario (foreign key)
                v.setNombre_usuario(rs.getString("nombre_usuario")); // Nombre del usuario
                lista.add(v);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al listar ventas: " + e.getMessage());
        }
        
        return lista;
    }
    
    public List<venta> listarConFiltros(String search, String metodoPago, String fechaDesde, 
                                       String fechaHasta, BigDecimal totalMin, BigDecimal totalMax) {
        List<venta> lista = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT v.*, u.nombre_completo AS nombre_usuario " +
                                              "FROM venta v " +
                                              "INNER JOIN usuario u ON v.id_usuario = u.id_usuario " +
                                              "WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        if (search != null && !search.trim().isEmpty()) {
            sql.append(" AND (CAST(v.id_venta AS CHAR) LIKE ? OR CAST(v.id_usuario AS CHAR) LIKE ? OR u.nombre_completo LIKE ?)");
            String searchParam = "%" + search + "%";
            params.add(searchParam);
            params.add(searchParam);
            params.add(searchParam);
        }
        
        if (metodoPago != null && !metodoPago.trim().isEmpty()) {
            sql.append(" AND v.metodo_pago = ?");
            params.add(metodoPago);
        }
        
        if (fechaDesde != null && !fechaDesde.trim().isEmpty()) {
            sql.append(" AND DATE(v.fecha) >= ?");
            params.add(fechaDesde);
        }
        
        if (fechaHasta != null && !fechaHasta.trim().isEmpty()) {
            sql.append(" AND DATE(v.fecha) <= ?");
            params.add(fechaHasta);
        }
        
        if (totalMin != null && totalMin.compareTo(BigDecimal.ZERO) > 0) {
            sql.append(" AND v.total >= ?");
            params.add(totalMin);
        }
        
        if (totalMax != null && totalMax.compareTo(BigDecimal.ZERO) > 0) {
            sql.append(" AND v.total <= ?");
            params.add(totalMax);
        }
        
        sql.append(" ORDER BY v.fecha DESC");
        
        try (Connection conn = ConnBD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                venta v = new venta();
                v.setId_venta(rs.getInt("id_venta"));
                // Obtener el Timestamp directamente
                Timestamp fecha = rs.getTimestamp("fecha");
                v.setFecha(fecha);
                if (fecha != null) {
                    System.out.println("Fecha obtenida: " + fecha.toString());
                } else {
                    System.out.println("ADVERTENCIA: Fecha es null para venta ID: " + rs.getInt("id_venta"));
                }
                v.setTotal(rs.getBigDecimal("total"));
                v.setMetodo_pago(rs.getString("metodo_pago"));
                v.setId(rs.getInt("id_usuario")); // ID del usuario (foreign key)
                v.setNombre_usuario(rs.getString("nombre_usuario")); // Nombre del usuario
                lista.add(v);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al listar ventas con filtros: " + e.getMessage());
            e.printStackTrace();
            // Retornar lista vacía en caso de error en lugar de null
            if (lista == null) {
                lista = new ArrayList<>();
            }
        }
        
        return lista;
    }
    
    public venta buscarPorId(int id) {
        String sql = "SELECT v.*, u.nombre_completo AS nombre_usuario " +
                     "FROM venta v " +
                     "INNER JOIN usuario u ON v.id_usuario = u.id_usuario " +
                     "WHERE v.id_venta = ?";
        venta v = null;
        
        try (Connection conn = ConnBD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                v = new venta();
                v.setId_venta(rs.getInt("id_venta"));
                v.setFecha(rs.getTimestamp("fecha"));
                v.setTotal(rs.getBigDecimal("total"));
                v.setMetodo_pago(rs.getString("metodo_pago"));
                v.setId(rs.getInt("id_usuario")); // ID del usuario (foreign key)
                v.setNombre_usuario(rs.getString("nombre_usuario")); // Nombre del usuario
            }
            
        } catch (SQLException e) {
            System.err.println("Error al buscar venta: " + e.getMessage());
        }
        
        return v;
    }
    
    public boolean actualizar(venta v) {
        String sql = "UPDATE venta SET total = ?, metodo_pago = ?, id_usuario = ? WHERE id_venta = ?";
        
        try (Connection conn = ConnBD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setBigDecimal(1, v.getTotal());
            ps.setString(2, v.getMetodo_pago());
            ps.setInt(3, v.getId()); // ID del usuario (foreign key)
            ps.setInt(4, v.getId_venta());
            
            int filas = ps.executeUpdate();
            return filas > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar venta: " + e.getMessage());
            return false;
        }
    }
    
    public boolean eliminar(int id) {
        String sql = "DELETE FROM venta WHERE id_venta = ?";
        
        try (Connection conn = ConnBD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            int filas = ps.executeUpdate();
            return filas > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al eliminar venta: " + e.getMessage());
            return false;
        }
    }
    
    private int obtenerSiguienteId() {
        String sql = "SELECT COALESCE(MAX(id_venta), 0) + 1 AS siguiente_id FROM venta";
        
        try (Connection conn = ConnBD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt("siguiente_id");
            }
            return 1; // Si no hay registros, empezar en 1
            
        } catch (SQLException e) {
            System.err.println("Error al obtener siguiente ID: " + e.getMessage());
            return 1; // Valor por defecto
        }
    }
}

