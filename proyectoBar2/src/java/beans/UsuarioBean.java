package beans;

import dao.UsuarioDAO;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import modelo.Usuario;

@ManagedBean
@SessionScoped
public class UsuarioBean {
    Usuario usuario = new Usuario();
    List<Usuario> listaU = new ArrayList<>();
    UsuarioDAO uDAO = new UsuarioDAO();
    
    // Filtros para búsqueda
    private String filtroNombre;
    private String filtroDocumento;
    private String filtroTipo;
    
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
    
    public void inicializar(){
        listar(); // Siempre recargar para tener datos actualizados
    }
    
    public void listar(){
        usuario = new Usuario();
        listaU = uDAO.listarU();
    }
    
    // Método para filtrar usuarios según los criterios de búsqueda
    public void listarConFiltros() {
        // Cargar todos los usuarios desde la base de datos
        usuario = new Usuario();
        List<Usuario> todosUsuarios = uDAO.listarU();
        List<Usuario> listaFiltrada = new ArrayList<>();
        
        // Si no hay filtros, mostrar todos
        boolean hayFiltros = false;
        if (filtroNombre != null && !filtroNombre.trim().isEmpty()) {
            hayFiltros = true;
        }
        if (filtroDocumento != null && !filtroDocumento.trim().isEmpty()) {
            hayFiltros = true;
        }
        if (filtroTipo != null && !filtroTipo.trim().isEmpty()) {
            hayFiltros = true;
        }
        
        if (!hayFiltros) {
            listaU = todosUsuarios;
            return;
        }
        
        // Recorrer todos los usuarios y aplicar filtros
        for (Usuario usr : todosUsuarios) {
            boolean match = true;
            
            // Filtrar por nombre
            if (filtroNombre != null && !filtroNombre.trim().isEmpty()) {
                String nombreFiltro = filtroNombre.trim().toLowerCase();
                String nombreUsuario = usr.getNombre_completo() != null ? 
                    usr.getNombre_completo().toLowerCase() : "";
                if (!nombreUsuario.contains(nombreFiltro)) {
                    match = false;
                }
            }
            
            // Filtrar por documento
            if (filtroDocumento != null && !filtroDocumento.trim().isEmpty()) {
                try {
                    int docFiltro = Integer.parseInt(filtroDocumento.trim());
                    if (usr.getDocumento() != docFiltro) {
                        match = false;
                    }
                } catch (NumberFormatException e) {
                    match = false;
                }
            }
            
            // Filtrar por tipo
            if (filtroTipo != null && !filtroTipo.trim().isEmpty()) {
                if (usr.getTipo() == null || !usr.getTipo().equals(filtroTipo)) {
                    match = false;
                }
            }
            
            if (match) {
                listaFiltrada.add(usr);
            }
        }
        
        listaU = listaFiltrada;
    }
    
    // Método para limpiar los filtros y mostrar todos los usuarios
    public void limpiarFiltros() {
        filtroNombre = null;
        filtroDocumento = null;
        filtroTipo = null;
        listar(); // Recargar todos los usuarios
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
    private int idUsuarioEliminar;
    
    public String eliminar(int id_usuario){
        System.out.println("Intentando eliminar usuario con ID: " + id_usuario);
        try {
            uDAO.eliminar(id_usuario);
            System.out.println("Usuario eliminado, recargando lista...");
            // Recargar la lista después de eliminar
            listar();
            System.out.println("Lista recargada. Total usuarios: " + listaU.size());
        } catch (Exception e) {
            System.err.println("Error al eliminar usuario: " + e.getMessage());
            e.printStackTrace();
        }
        // Retornar null para permanecer en la misma página pero recargada
        return null;
    }
    
    // Getters y Setters para los filtros
    public String getFiltroNombre() {
        return filtroNombre;
    }

    public void setFiltroNombre(String filtroNombre) {
        this.filtroNombre = filtroNombre;
    }

    public String getFiltroDocumento() {
        return filtroDocumento;
    }

    public void setFiltroDocumento(String filtroDocumento) {
        this.filtroDocumento = filtroDocumento;
    }

    public String getFiltroTipo() {
        return filtroTipo;
    }

    public void setFiltroTipo(String filtroTipo) {
        this.filtroTipo = filtroTipo;
    }
}
