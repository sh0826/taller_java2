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
            
        }
        return listaEvento;
    }
    
    public void guardar (Evento evento) {
        try {
            String sql = "INSERT INTO evento VALUES (null, ?, ?, ?, ?, ?, ?, ?)";
            ps = con.prepareStatement (sql);
            
            ps.setString(1, evento.getNombre_evento());
            ps.setInt(2, evento.getCapacidad_maxima());
            ps.setString(3, evento.getDescripcion());
            if (evento.getFecha() != null) {
                ps.setDate(4, new Date(evento.getFecha().getTime()));
            } else {
                ps.setDate(4, null);
            }
            ps.setTime(5, evento.getHora_inicio());
            ps.setDouble(6, evento.getPrecio_boleta());
            ps.setString(7, evento.getImagen());
            
            ps.executeUpdate();
        }catch (SQLException e){
        }
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
    
    public void actualizar (Evento evento){
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
            ps.setTime(5, evento.getHora_inicio());
            ps.setDouble(6, evento.getPrecio_boleta());
            ps.setString(7, evento.getImagen());
            ps.setInt(8, evento.getId_evento());
            
            ps.executeUpdate();
        } catch (SQLException e){
            
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
    
}
