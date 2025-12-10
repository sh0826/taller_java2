package beans;

import dao.UsuarioDAO;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import modelo.Usuario;

@ManagedBean
@ApplicationScoped
public class UsuarioBean {
    Usuario usuario = new Usuario();
    List<Usuario> listaU = new ArrayList<>();
    UsuarioDAO uDAO = new UsuarioDAO();
    
    public void registrarCliente(){
        usuario.setPass(Utils.encriptar(usuario.getPass()));
        uDAO.guardar(usuario);
        usuario.setTipo("C");
        uDAO.guardar(usuario);
        usuario = new Usuario();
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<Usuario> getListaU() {        
        return listaU;
    }

    public void setListaU(List<Usuario> listaU) {
        this.listaU = listaU;
    }
    
    public void listar(){
        usuario = new Usuario();
        listaU = uDAO.listarU();
    }
    
    public void guardar(){
        usuario.setPass(Utils.encriptar(usuario.getPass()));
        uDAO.guardar(usuario);
    }
    public void buscar(int id_usuario){
        usuario = uDAO.buscar(id_usuario);
    }
    public void actualizar(){
        if(usuario.getPass().equals("")){
            usuario.setPass(usuario.getPass1());
        }else{
            usuario.setPass(Utils.encriptar(usuario.getPass()));
        }        
        uDAO.actualizar(usuario);
    }
    public void eliminar(int id_usuario){
        uDAO.eliminar(id_usuario);
    }
}
