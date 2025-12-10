/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelo.Boleta;
import modelo.Evento;
import modelo.Usuario;

/**
 *
 * @author marce
 */
public class BoletaDAO {
    private final Connection con = ConnBD.conectar();
    private PreparedStatement ps;
    private ResultSet rs;
    private final EventoDAO evenDAO = new EventoDAO();
    private final UsuarioDAO usuaDAO = new UsuarioDAO();
    
    public List<Boleta> listar(){
        List<Boleta> listaBole = null;
        
        try{
            String sql = "SELECT * FROM boleta";
            
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            
            listaBole = new ArrayList<>();
            
            while(rs.next()){
                Boleta bole = new Boleta();
                bole.setId_boleta(rs.getInt("id_boleta"));
                bole.setPrecio_boleta(rs.getDouble("precio_boleta"));
                bole.setCantidad_boletos(rs.getInt("cantidad_boletos"));
                bole.setId(rs.getInt("id"));
                bole.setId_evento(rs.getInt("id_evento"));
                
                // Cargar el objeto Evento completo
                Evento evento = evenDAO.buscar(rs.getInt("id_evento"));
                if (evento != null) {
                    bole.setEven(evento);
                }
                
                // Cargar el objeto Usuario completo
                Usuario usuario = usuaDAO.buscar(rs.getInt("id"));
                if (usuario != null) {
                    bole.setUsuario(usuario);
                }
                
                listaBole.add(bole);
            }
                    
            }catch (SQLException e){
                    }
            return listaBole;
    }
    public void guardar (Boleta bole){
        try{
            String sql = "INSERT INTO boleta VALUES (null, ?, ?, ?, ?)";
            ps = con.prepareStatement(sql);
            
            ps.setDouble(1, bole.getPrecio_boleta());
            ps.setInt(2, bole.getCantidad_boletos());
            ps.setInt(3, bole.getId());
            ps.setInt(4, bole.getId_evento());
            
            ps.executeUpdate();
        }catch (SQLException e){
            
        }
        
    }
    
    public Boleta buscar (int id_boleta){
        try {
            String sql = "SELECT * FROM boleta WHERE id_boleta = ?";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id_boleta);
            
            rs = ps.executeQuery();
            
            if(rs.next()){
                Boleta bole = new Boleta();
                bole.setId_boleta(rs.getInt("id_boleta"));
                bole.setPrecio_boleta(rs.getDouble("precio_boleta"));
                bole.setCantidad_boletos(rs.getInt("cantidad_boletos"));
                bole.setId(rs.getInt("id"));
                bole.setId_evento(rs.getInt("id_evento"));
                
                // Cargar el objeto Evento completo
                Evento evento = evenDAO.buscar(rs.getInt("id_evento"));
                if (evento != null) {
                    bole.setEven(evento);
                }
                
                // Cargar el objeto Usuario completo
                Usuario usuario = usuaDAO.buscar(rs.getInt("id"));
                if (usuario != null) {
                    bole.setUsuario(usuario);
                }
                
                return bole;
            }else{
                return null;
            }
        }catch (SQLException e){
            return null;
        }
    }
    
    public void actualizar (Boleta bole) {
        try {
            String sql = "UPDATE boleta SET precio_boleta = ?, "
                    + "cantidad_boletos = ?, id = ?, id_evento = ? WHERE id_boleta = ?";
            ps = con.prepareStatement(sql);
            ps.setDouble(1, bole.getPrecio_boleta());
            ps.setInt(2, bole.getCantidad_boletos());
            ps.setInt(3, bole.getId());
            ps.setInt(4, bole.getId_evento());
            ps.setInt(5, bole.getId_boleta());
            
            ps.executeUpdate();
        }catch (SQLException e){
        }
    }
    
    public void eliminar(int id_boleta){
        try {
            String sql = "DELETE FROM boleta WHERE id_boleta = ?";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id_boleta);
            
            ps.executeUpdate();
        }catch (SQLException e){
        }
    }
    
        public int obtenerBoletasVendidas (int id_evento){
        int total = 0;
        try {
            String sql = "SELECT SUM (cantidad_boletos) FROM boleta WHERE id_evento = ?";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id_evento);
            rs = ps.executeQuery();
            
            if(rs.next()){
                total = rs.getInt(1);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return total;
    }
}

