/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import modelo.Evento;

/**
 *
 * @author marce
 */
public class EventoDAO {
    private final Connection con = ConnBD.conectar();
    private PreparedStatement ps;
    private ResultSet rs;
    
    public List<Evento> listar() {
        List<Evento> listaEvento = null;
        
        try {
            String sql = "SELECT * FROM evento";
            ps = con.prepareStatement(sql);
            
            rs = ps.executeQuery();
            
            listaEvento = new ArrayList();
            
            while (rs.next()) {
                Evento evento = new Evento ();
                
                evento.setId_evento(rs.getInt("id_evento"));
                evento.setNombre_evento(rs.getString("nombre_evento"));
                evento.setCapacidad_maxima(rs.getInt("capacidad_maxima"));
                evento.setDescripcion(rs.getString("descripcion"));
                evento.setFecha(rs.getDate("fecha"));
                evento.setHora_inicio(rs.getTime("hora_inicio"));
                evento.setPrecio_boleta(rs.getDouble("precio_boleta"));
                evento.setImagen(rs.getString("imagen"));
                
                listaEvento.add(evento);
            }
        } catch (SQLException e){
            System.err.println("=== EVENTODAO A ERROR: " + e.getMessage());
            e.printStackTrace();
        }
        return listaEvento;
    }
    
    public int guardar (Evento evento) {
        try {
            String sql = "INSERT INTO evento VALUES (null, ?, ?, ?, ?, ?, ?, ?)";
            ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            
            ps.setString(1, evento.getNombre_evento());
            ps.setInt(2, evento.getCapacidad_maxima());
            ps.setString(3, evento.getDescripcion());
            if (evento.getFecha() != null) {
                ps.setDate(4, new Date(evento.getFecha().getTime()));
            } else {
                ps.setDate(4, null);
            }
            if (evento.getHora_inicio() != null) {
                ps.setTime(5, evento.getHora_inicio());
            } else {
                ps.setTime(5, null);
            }
            ps.setDouble(6, evento.getPrecio_boleta());
            ps.setString(7, evento.getImagen());
            
            ps.executeUpdate();
            
            // Obtener el ID generado
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return 0;
    }
    
    public Evento buscar (int id_evento) {
        try {
            String sql = "SELECT * FROM evento WHERE id_evento = ?";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id_evento);
            
            rs = ps.executeQuery();
            
            if (rs.next()){
                Evento evento = new Evento();
                evento.setId_evento(rs.getInt("id_evento"));
                evento.setNombre_evento(rs.getString("nombre_evento"));
                evento.setCapacidad_maxima(rs.getInt("capacidad_maxima"));
                evento.setDescripcion(rs.getString("descripcion"));
                evento.setFecha(rs.getDate("fecha"));
                evento.setHora_inicio(rs.getTime("hora_inicio"));
                evento.setPrecio_boleta(rs.getDouble("precio_boleta"));
                evento.setImagen(rs.getString("imagen"));
                
                return evento;
            }
        } catch (SQLException e){
            return null;
        }
        return null;
    }
    
    public boolean actualizar (Evento evento){
        try {
            String sql = "UPDATE evento SET nombre_evento = ?, "
                    + "capacidad_maxima = ?, descripcion = ?, fecha = ?, hora_inicio = ?, "
                    + "precio_boleta = ?, imagen = ? WHERE id_evento = ?";
            
            ps = con.prepareStatement(sql);
            
            ps.setString(1, evento.getNombre_evento());
            ps.setInt(2, evento.getCapacidad_maxima());
            ps.setString(3, evento.getDescripcion());
            if (evento.getFecha() != null) {
                ps.setDate(4, new Date(evento.getFecha().getTime()));
            } else {
                ps.setDate(4, null);
            }
            if (evento.getHora_inicio() != null) {
                ps.setTime(5, evento.getHora_inicio());
            } else {
                ps.setTime(5, null);
            }
            ps.setDouble(6, evento.getPrecio_boleta());
            ps.setString(7, evento.getImagen());
            ps.setInt(8, evento.getId_evento());
            
            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }
    
    public void eliminar (int id_evento){
        try {
            String sql = "DELETE FROM evento WHERE id_evento = ?";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id_evento);
            
            ps.executeUpdate();
        } catch (SQLException e){
            
        }
    }
    
    public List<Evento> buscarConFiltros(String nombre, String fecha, String capacidad, String precio) {
        List<Evento> listaEvento = new ArrayList<>();
        
        try {
            StringBuilder sql = new StringBuilder("SELECT * FROM evento WHERE 1=1");
            List<Object> parametros = new ArrayList<>();
            int paramIndex = 1;
            
            if (nombre != null && !nombre.trim().isEmpty()) {
                sql.append(" AND nombre_evento LIKE ?");
                parametros.add("%" + nombre.trim() + "%");
            }
            
            if (fecha != null && !fecha.trim().isEmpty()) {
                sql.append(" AND DATE(fecha) = ?");
                parametros.add(fecha.trim());
            }
            
            if (capacidad != null && !capacidad.trim().isEmpty()) {
                try {
                    int cap = Integer.parseInt(capacidad.trim());
                    sql.append(" AND capacidad_maxima = ?");
                    parametros.add(cap);
                } catch (NumberFormatException e) {
                    // Si no es un número válido, ignorar este filtro
                }
            }
            
            if (precio != null && !precio.trim().isEmpty()) {
                try {
                    double prec = Double.parseDouble(precio.trim());
                    sql.append(" AND precio_boleta = ?");
                    parametros.add(prec);
                } catch (NumberFormatException e) {
                    // Si no es un número válido, ignorar este filtro
                }
            }
            
            ps = con.prepareStatement(sql.toString());
            
            for (int i = 0; i < parametros.size(); i++) {
                Object param = parametros.get(i);
                if (param instanceof String) {
                    ps.setString(i + 1, (String) param);
                } else if (param instanceof Integer) {
                    ps.setInt(i + 1, (Integer) param);
                } else if (param instanceof Double) {
                    ps.setDouble(i + 1, (Double) param);
                }
            }
            
            rs = ps.executeQuery();
            
            while (rs.next()) {
                Evento evento = new Evento();
                
                evento.setId_evento(rs.getInt("id_evento"));
                evento.setNombre_evento(rs.getString("nombre_evento"));
                evento.setCapacidad_maxima(rs.getInt("capacidad_maxima"));
                evento.setDescripcion(rs.getString("descripcion"));
                evento.setFecha(rs.getDate("fecha"));
                evento.setHora_inicio(rs.getTime("hora_inicio"));
                evento.setPrecio_boleta(rs.getDouble("precio_boleta"));
                evento.setImagen(rs.getString("imagen"));
                
                listaEvento.add(evento);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return listaEvento;
    }
    
}
