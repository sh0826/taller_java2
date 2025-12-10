package beans;

import dao.ConnBD;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import modelo.Usuario;

@ManagedBean
public class LoginBean {
    Usuario usuario = new Usuario();
    String nombreUsuario = "";

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
    
    public void autenticar(){        
        try {
            Connection con = ConnBD.conectar();
            
            String pwd = Utils.encriptar(usuario.getPass());
            
            String sql = "SELECT * FROM usuario WHERE documento = ? AND pass = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, usuario.getDocumento());
            ps.setString(2, pwd);
            
            ResultSet rs = ps.executeQuery();
                       
            if(rs.next()){
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("user", rs.getString("nombre_completo"));
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("tipo", rs.getString("tipo"));
                
                String dir = "";
                switch(rs.getString("tipo")){
                    case "A":
                        dir = "/faces/admin";
                        break;
                    case "C":
                        dir = "/faces/cliente";
                        break;
                    case "E":
                        dir = "/faces/empl";
                        break;
                }
                
                String rootPath = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
                System.out.println(rootPath);
                FacesContext.getCurrentInstance().getExternalContext().redirect(rootPath + dir + "/index.xhtml");
                FacesContext.getCurrentInstance().responseComplete();
            }else{
                FacesContext.getCurrentInstance().getExternalContext().redirect("/inicio_sesion/error.xhtml");
            }            
        } catch (SQLException | IOException e) {
        }        
    }
    
    public void cerrar_sesion(){
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().clear();
        String rootPath = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect(rootPath + "/faces/inicio_sesion/login.xhtml");
        } catch (IOException e) {
        }
    }
    
    public void verif_sesion(String t){
        String nom = (String)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("user");
        String tipo = (String)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("tipo");
        
        if(nom == null){
            try {
                FacesContext.getCurrentInstance().getExternalContext().redirect("inicio_sesion/sinacceso.xhtml");
            } catch (IOException e) {
            }
        }else{
            if(!tipo.equals(t)){
                try {
                    FacesContext.getCurrentInstance().getExternalContext().redirect("inicio_sesion/sinacceso.xhtml");
                } catch (IOException e) {
                }
            }else{
                nombreUsuario = nom;
            }
        }
    }   
    
}
