package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelo.Usuario;

public class UsuarioDAO {
    private Connection con;
    private PreparedStatement ps;
    private ResultSet rs;

    public UsuarioDAO() {
        con = ConnBD.conectar();
        try {
            if (con != null && con.getAutoCommit()) {
                // Si autocommit está activado, está bien
                System.out.println("Conexión establecida con autocommit activado");
            } else if (con != null) {
                System.out.println("Conexión establecida sin autocommit");
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar conexión: " + e.getMessage());
        }
    }
    
    public List<Usuario> listarU(){
        List<Usuario> listUsr = null;
        
        try {
            String sql = "SELECT * FROM usuario";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            
            listUsr = new ArrayList<>();
            
            while(rs.next()){
                Usuario u = new Usuario();
                
                u.setId_usuario(rs.getInt("id_usuario"));
                u.setDocumento(rs.getInt("documento"));
                u.setNombre_completo(rs.getString("nombre_completo"));
                u.setCorreo(rs.getString("correo"));
                u.setPass(rs.getString("pass"));
                
                String tipo = "";
                
                switch(rs.getString("tipo")){
                    case "A":
                        tipo = "Administrador";
                        break;
                    case "C":
                        tipo = "Cliente";
                        break;
                    case "E":
                        tipo = "Empleado";
                        break;
                }
                
                u.setTipo(tipo);
                
                listUsr.add(u);
            }
        } catch (SQLException e) {
        }
        
        return listUsr;
    }
    
    public void guardar(Usuario usr){
        try {
            String sql = "INSERT INTO usuario VALUES(null, ?, ?, ?, ?, ?)";
            ps = con.prepareStatement(sql);
            ps.setInt(1, usr.getDocumento());
            ps.setString(2, usr.getNombre_completo());
            ps.setString(3, usr.getCorreo());
            ps.setString(4, usr.getPass());
            ps.setString(5, usr.getTipo());
            
            ps.executeUpdate();
            
        } catch (SQLException e) {
        }
    }
    
    public Usuario buscar(int id_usuario){
        Usuario usr = null;
        
        try {
            String sql = "SELECT * FROM usuario WHERE id_usuario = ?";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id_usuario);
            
            rs = ps.executeQuery();
            
            if(rs.next()){
                usr = new Usuario();
                
                usr.setId_usuario(rs.getInt("id_usuario"));
                usr.setDocumento(rs.getInt("documento"));
                usr.setNombre_completo(rs.getString("nombre_completo"));
                usr.setCorreo(rs.getString("correo"));
                usr.setPass(rs.getString("pass"));
                usr.setPass1(rs.getString("pass"));
                usr.setTipo(rs.getString("tipo"));
            }                        
        } catch (SQLException e) {
        }
        
        return usr;
    }
    
    public void actualizar(Usuario usr){
        try {
            String sql = "UPDATE usuario SET documento = ?, nombre_completo = ?, correo = ?, pass = ?, tipo = ? WHERE id_usuario = ?";
            ps = con.prepareStatement(sql);
            ps.setInt(1, usr.getDocumento());
            ps.setString(2, usr.getNombre_completo());
            ps.setString(3, usr.getCorreo());
            ps.setString(4, usr.getPass());
            ps.setString(5, usr.getTipo());
            ps.setInt(6, usr.getId_usuario());
                        
            ps.executeUpdate();
        } catch (SQLException e) {
        }        
    }
    
    public void eliminar(int id_usuario){
        PreparedStatement psEliminar = null;
        try {
            String sql = "DELETE FROM usuario WHERE id_usuario = ?";
            psEliminar = con.prepareStatement(sql);
            psEliminar.setInt(1, id_usuario);
            
            System.out.println("Ejecutando DELETE para usuario ID: " + id_usuario);
            int resultado = psEliminar.executeUpdate();
            
            if (resultado > 0) {
                System.out.println("Usuario eliminado correctamente de la BD: " + id_usuario);
                // Si autocommit está desactivado, hacer commit manual
                if (con != null && !con.getAutoCommit()) {
                    con.commit();
                    System.out.println("Commit realizado");
                }
            } else {
                System.out.println("No se encontró el usuario con ID: " + id_usuario);
            }
            
            if (psEliminar != null) {
                psEliminar.close();
            }
        } catch (SQLException e) {
            System.err.println("Error al eliminar usuario: " + e.getMessage());
            e.printStackTrace();
            try {
                if (con != null && !con.getAutoCommit()) {
                    con.rollback();
                    System.out.println("Rollback realizado");
                }
            } catch (SQLException ex) {
                System.err.println("Error al hacer rollback: " + ex.getMessage());
            }
        }
    }
}
