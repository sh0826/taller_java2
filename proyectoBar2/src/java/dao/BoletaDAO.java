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
        List<Boleta> listaBole = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try{
            conn = ConnBD.conectar();
            
            if (conn == null) {
                System.err.println("Error: No se pudo establecer conexión con la base de datos");
                return listaBole;
            }
            
            String sql = "SELECT * FROM boleta";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            
            while(rs.next()){
                Boleta bole = new Boleta();
                bole.setId_boleta(rs.getInt("id_boleta"));
                bole.setPrecio_boleta(rs.getDouble("precio_boleta"));
                bole.setCantidad_boletos(rs.getInt("cantidad_boletos"));
                bole.setId_usuario(rs.getInt("id_usuario"));
                bole.setId_evento(rs.getInt("id_evento"));
                
                // Cargar el objeto Evento completo
                Evento evento = evenDAO.buscar(rs.getInt("id_evento"));
                if (evento != null) {
                    bole.setEven(evento);
                }
                
                // Cargar el objeto Usuario completo
                Usuario usuario = usuaDAO.buscar(rs.getInt("id_usuario"));
                if (usuario != null) {
                    bole.setUsuario(usuario);
                }
                
                listaBole.add(bole);
            }
                    
        }catch (SQLException e){
            System.err.println("Error al listar boletas: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar recursos: " + e.getMessage());
            }
        }
        
        return listaBole;
    }
    
    
public List<Boleta> listarPorUsuario(int idUsuario) {
    List<Boleta> lista = new ArrayList<>();

    try {
        String sql = "SELECT b.*, e.nombre_evento "
                   + "FROM boleta b "
                   + "INNER JOIN evento e ON b.id_evento = e.id_evento "
                   + "WHERE b.id_usuario = ?";
        
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, idUsuario);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Boleta b = new Boleta();
            b.setId_boleta(rs.getInt("id_boleta"));
            b.setId_usuario(rs.getInt("id_usuario"));
            b.setId_evento(rs.getInt("id_evento"));
            b.setPrecio_boleta(rs.getInt("precio_boleta"));
            b.setCantidad_boletos(rs.getInt("cantidad_boletos"));

            Evento ev = new Evento();
            ev.setNombre_evento(rs.getString("nombre_evento"));

            b.setEven(ev); 

            lista.add(b);
        }
    } catch (Exception e) {
        System.out.println("Error listarPorUsuario: " + e.getMessage());
    }

    return lista;
}

    
    
    public boolean guardar (Boleta bole){
        // Validar que la boleta no sea null
        if (bole == null) {
            System.err.println("Error: La boleta es null");
            return false;
        }
        
        // Validar datos requeridos
        if (bole.getCantidad_boletos() <= 0) {
            System.err.println("Error: La cantidad de boletos debe ser mayor a 0");
            return false;
        }
        
        if (bole.getId_usuario() <= 0) {
            System.err.println("Error: El ID de usuario no es válido");
            return false;
        }
        
        if (bole.getId_evento() <= 0) {
            System.err.println("Error: El ID de evento no es válido");
            return false;
        }
        
        // Especificar explícitamente las columnas para evitar problemas de orden
        String sql = "INSERT INTO boleta (precio_boleta, cantidad_boletos, id_usuario, id_evento) VALUES (?, ?, ?, ?)";
        
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            // Crear una nueva conexión para esta operación
            conn = ConnBD.conectar();
            
            if (conn == null) {
                System.err.println("Error: No se pudo establecer conexión con la base de datos");
                return false;
            }
            
            System.out.println("Conexión establecida: " + (conn != null));
            
            // Asegurar que autocommit esté activado
            if (!conn.getAutoCommit()) {
                conn.setAutoCommit(true);
            }
            
            ps = conn.prepareStatement(sql);
            
            ps.setDouble(1, bole.getPrecio_boleta());
            ps.setInt(2, bole.getCantidad_boletos());
            ps.setInt(3, bole.getId_usuario());
            ps.setInt(4, bole.getId_evento());
            
            System.out.println("Ejecutando INSERT boleta:");
            System.out.println("  SQL: " + sql);
            System.out.println("  Precio: " + bole.getPrecio_boleta());
            System.out.println("  Cantidad: " + bole.getCantidad_boletos());
            System.out.println("  ID Usuario: " + bole.getId_usuario());
            System.out.println("  ID Evento: " + bole.getId_evento());
            
            int resultado = ps.executeUpdate();
            System.out.println("Filas afectadas: " + resultado);
            
            if (resultado > 0) {
                System.out.println("Boleta guardada correctamente");
                return true;
            } else {
                System.err.println("No se insertó ninguna fila");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Error SQL al guardar boleta: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.err.println("Error inesperado al guardar boleta: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            // Cerrar recursos en el bloque finally
            try {
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println("Error al cerrar conexión: " + e.getMessage());
            }
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
                bole.setId_usuario(rs.getInt("id_usuario"));
                bole.setId_evento(rs.getInt("id_evento"));
                
                // Cargar el objeto Evento completo
                Evento evento = evenDAO.buscar(rs.getInt("id_evento"));
                if (evento != null) {
                    bole.setEven(evento);
                }
                
                // Cargar el objeto Usuario completo
                Usuario usuario = usuaDAO.buscar(rs.getInt("id_usuario"));
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
                    + "cantidad_boletos = ?, id_usuario = ?, id_evento = ? WHERE id_boleta = ?";
            ps = con.prepareStatement(sql);
            ps.setDouble(1, bole.getPrecio_boleta());
            ps.setInt(2, bole.getCantidad_boletos());
            ps.setInt(3, bole.getId_usuario());
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

