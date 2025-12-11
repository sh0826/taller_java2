package beans;

import dao.UsuarioDAO;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import modelo.Usuario;

@ManagedBean
@SessionScoped
public class UsuarioBean implements Serializable {
    private static final long serialVersionUID = 1L;
    
    Usuario usuario = new Usuario();
    List<Usuario> listaU = new ArrayList<>();
    private transient UsuarioDAO uDAO; // transient para que no se serialice
    
    // Método para obtener el DAO (crearlo si no existe)
    private UsuarioDAO getDAO() {
        if (uDAO == null) {
            uDAO = new UsuarioDAO();
        }
        return uDAO;
    }
    
    // Filtros para búsqueda
    private String filtroNombre;
    private String filtroDocumento;
    private String filtroTipo;
    
    public void registrarCliente(){
        usuario.setPass(Utils.encriptar(usuario.getPass()));
        getDAO().guardar(usuario);
        usuario.setTipo("C");
        getDAO().guardar(usuario);
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
        // Solo cargar si no hay filtros aplicados
        // Si hay filtros, mantener la lista filtrada
        // NO limpiar usuario si tiene ID (está siendo editado)
        boolean hayFiltros = (filtroNombre != null && !filtroNombre.trim().isEmpty()) ||
                             (filtroDocumento != null && !filtroDocumento.trim().isEmpty()) ||
                             (filtroTipo != null && !filtroTipo.trim().isEmpty());
        
        if (!hayFiltros && (listaU == null || listaU.isEmpty())) {
            listar(); // Solo cargar si no hay filtros y la lista está vacía
        }
    }
    
    public void listar(){
        // Solo limpiar usuario si no está siendo editado (no tiene ID)
        if (usuario == null || usuario.getId_usuario() == 0) {
            usuario = new Usuario();
        }
        listaU = getDAO().listarU();
    }
    
    // Método para filtrar usuarios según los criterios de búsqueda
    public void listarConFiltros() {
        System.out.println("=== INICIANDO FILTRADO ===");
        System.out.println("Filtro Nombre: '" + filtroNombre + "'");
        System.out.println("Filtro Documento: '" + filtroDocumento + "'");
        System.out.println("Filtro Tipo: '" + filtroTipo + "'");
        
        // Cargar todos los usuarios desde la base de datos
        // Solo limpiar usuario si no está siendo editado (no tiene ID)
        if (usuario == null || usuario.getId_usuario() == 0) {
            usuario = new Usuario();
        }
        List<Usuario> todosUsuarios = getDAO().listarU();
        System.out.println("Total usuarios cargados: " + todosUsuarios.size());
        
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
            System.out.println("No hay filtros, mostrando todos los usuarios");
            listaU = todosUsuarios;
            return;
        }
        
        System.out.println("Aplicando filtros...");
        
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
                } else {
                    System.out.println("Nombre coincide: " + usr.getNombre_completo());
                }
            }
            
            // Filtrar por documento
            if (filtroDocumento != null && !filtroDocumento.trim().isEmpty() && match) {
                try {
                    int docFiltro = Integer.parseInt(filtroDocumento.trim());
                    if (usr.getDocumento() != docFiltro) {
                        match = false;
                    } else {
                        System.out.println("Documento coincide: " + usr.getDocumento());
                    }
                } catch (NumberFormatException e) {
                    match = false;
                }
            }
            
            // Filtrar por tipo
            if (filtroTipo != null && !filtroTipo.trim().isEmpty() && match) {
                // El filtro viene como texto ("Administrador", "Cliente", "Empleado")
                // pero en la BD está como código ("A", "C", "E")
                // Necesitamos comparar ambos formatos
                String tipoUsuario = usr.getTipo();
                if (tipoUsuario == null) {
                    match = false;
                } else {
                    // Convertir el tipo del usuario a texto para comparar
                    String tipoUsuarioTexto = "";
                    switch(tipoUsuario) {
                        case "A":
                            tipoUsuarioTexto = "Administrador";
                            break;
                        case "C":
                            tipoUsuarioTexto = "Cliente";
                            break;
                        case "E":
                            tipoUsuarioTexto = "Empleado";
                            break;
                        default:
                            tipoUsuarioTexto = tipoUsuario; // Si ya está en texto
                    }
                    // Comparar con el filtro (puede venir como texto o código)
                    if (!tipoUsuarioTexto.equals(filtroTipo) && !tipoUsuario.equals(filtroTipo)) {
                        match = false;
                    } else {
                        System.out.println("Tipo coincide: " + tipoUsuarioTexto);
                    }
                }
            }
            
            if (match) {
                listaFiltrada.add(usr);
            }
        }
        
        System.out.println("Usuarios encontrados después de filtrar: " + listaFiltrada.size());
        listaU = listaFiltrada;
        System.out.println("=== FIN FILTRADO ===");
    }
    
    // Método para limpiar los filtros y mostrar todos los usuarios
    public void limpiarFiltros() {
        System.out.println("=== LIMPIANDO FILTROS ===");
        filtroNombre = null;
        filtroDocumento = null;
        filtroTipo = null;
        listar(); // Recargar todos los usuarios
        System.out.println("Filtros limpiados, total usuarios: " + listaU.size());
    }
    
    public void guardar(){
        usuario.setPass(Utils.encriptar(usuario.getPass()));
        getDAO().guardar(usuario);
    }
    public void buscar(int id_usuario){
        System.out.println("=== BUSCAR USUARIO ===");
        System.out.println("ID recibido: " + id_usuario);
        
        if (id_usuario <= 0) {
            System.err.println("ID de usuario inválido: " + id_usuario);
            return;
        }
        
        usuario = getDAO().buscar(id_usuario);
        if (usuario == null) {
            System.err.println("No se encontró el usuario con ID: " + id_usuario);
            javax.faces.application.FacesMessage mensaje = new javax.faces.application.FacesMessage(
                javax.faces.application.FacesMessage.SEVERITY_ERROR, 
                "Error", 
                "No se encontró el usuario"
            );
            javax.faces.context.FacesContext.getCurrentInstance().addMessage(null, mensaje);
            return;
        }
        
        System.out.println("Usuario encontrado:");
        System.out.println("  - ID: " + usuario.getId_usuario());
        System.out.println("  - Documento: " + usuario.getDocumento());
        System.out.println("  - Nombre: " + usuario.getNombre_completo());
        System.out.println("  - Correo: " + usuario.getCorreo());
        System.out.println("  - Tipo: " + usuario.getTipo());
        
        // Guardar la contraseña original en pass1 (ya está guardada por el DAO)
        // Limpiar el campo pass para que el usuario pueda dejarlo vacío o cambiarlo
        usuario.setPass("");
        
        System.out.println("Objeto usuario cargado correctamente en el bean");
        System.out.println("=== FIN BUSCAR ===");
    }
    
    public void verificarUsuarioCargado(){
        System.out.println("=== VERIFICANDO USUARIO CARGADO ===");
        if (usuario == null || usuario.getId_usuario() == 0) {
            System.err.println("No hay usuario cargado para editar");
            try {
                javax.faces.context.FacesContext.getCurrentInstance().getExternalContext().redirect("index.xhtml");
            } catch (java.io.IOException e) {
                System.err.println("Error al redirigir: " + e.getMessage());
            }
        } else {
            System.out.println("Usuario cargado correctamente - ID: " + usuario.getId_usuario() + ", Nombre: " + usuario.getNombre_completo());
        }
        System.out.println("=== FIN VERIFICACIÓN ===");
    }

    public String actualizar(){
        try {
            // Validar campos requeridos
            if (usuario.getDocumento() <= 0) {
                javax.faces.application.FacesMessage mensaje = new javax.faces.application.FacesMessage(
                    javax.faces.application.FacesMessage.SEVERITY_ERROR, 
                    "Error", 
                    "El documento es requerido"
                );
                javax.faces.context.FacesContext.getCurrentInstance().addMessage(null, mensaje);
                return null;
            }
            
            if (usuario.getNombre_completo() == null || usuario.getNombre_completo().trim().isEmpty()) {
                javax.faces.application.FacesMessage mensaje = new javax.faces.application.FacesMessage(
                    javax.faces.application.FacesMessage.SEVERITY_ERROR, 
                    "Error", 
                    "El nombre completo es requerido"
                );
                javax.faces.context.FacesContext.getCurrentInstance().addMessage(null, mensaje);
                return null;
            }
            
            // Validar que el usuario tenga ID
            if (usuario.getId_usuario() <= 0) {
                javax.faces.application.FacesMessage mensaje = new javax.faces.application.FacesMessage(
                    javax.faces.application.FacesMessage.SEVERITY_ERROR, 
                    "Error", 
                    "ID de usuario inválido"
                );
                javax.faces.context.FacesContext.getCurrentInstance().addMessage(null, mensaje);
                return null;
            }
            
            System.out.println("Actualizando usuario ID: " + usuario.getId_usuario());
            System.out.println("Datos antes de actualizar - Documento: " + usuario.getDocumento() + ", Nombre: " + usuario.getNombre_completo() + ", Tipo: " + usuario.getTipo());
            
            // Manejar la contraseña: si está vacía, usar la anterior
            if(usuario.getPass() == null || usuario.getPass().trim().equals("")){
                System.out.println("Contraseña vacía, usando la anterior");
                usuario.setPass(usuario.getPass1());
            } else {
                // Solo encriptar si se cambió la contraseña
                System.out.println("Nueva contraseña proporcionada, encriptando...");
                usuario.setPass(Utils.encriptar(usuario.getPass()));
            }
            
            // El tipo ya viene como código ("A", "C", "E") del select, así que no necesita conversión
            System.out.println("Tipo de usuario: " + usuario.getTipo());
            
            // Actualizar en la base de datos
            getDAO().actualizar(usuario);
            
            // Mensaje de éxito
            javax.faces.application.FacesMessage mensaje = new javax.faces.application.FacesMessage(
                javax.faces.application.FacesMessage.SEVERITY_INFO, 
                "Éxito", 
                "Usuario actualizado correctamente"
            );
            javax.faces.context.FacesContext.getCurrentInstance().addMessage(null, mensaje);
            
            // Recargar la lista
            listar();
            
            // Redirigir a la lista de usuarios
            return "index";
        } catch (Exception e) {
            System.err.println("Error al actualizar usuario: " + e.getMessage());
            e.printStackTrace();
            javax.faces.application.FacesMessage mensaje = new javax.faces.application.FacesMessage(
                javax.faces.application.FacesMessage.SEVERITY_ERROR, 
                "Error", 
                "No se pudo actualizar el usuario: " + e.getMessage()
            );
            javax.faces.context.FacesContext.getCurrentInstance().addMessage(null, mensaje);
            return null;
        }


    

    }
    public String eliminar(int id_usuario){
        System.out.println("Intentando eliminar usuario con ID: " + id_usuario);
        try {
            getDAO().eliminar(id_usuario);
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
