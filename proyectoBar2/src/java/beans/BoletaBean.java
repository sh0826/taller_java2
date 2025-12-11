/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package beans;

import dao.BoletaDAO;
import dao.EventoDAO;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import modelo.Boleta;
import modelo.Evento;
import modelo.Usuario;

/**
 *
 * @author marce
 */
@ManagedBean
@SessionScoped
public class BoletaBean implements Serializable {
    private static final long serialVersionUID = 1L;
    Boleta boleta = new Boleta();
    List<Boleta> lstBole = new ArrayList<>();
    List<Boleta> lstBoleFiltered = new ArrayList<>();
    List<Evento> lstEven = new ArrayList<>();
    BoletaDAO boleDAO = new BoletaDAO();
    private String usuarioNombre;
    
    @PostConstruct
    public void init() {
        listarEven();
        listar();
        nuevaBoleta(); // preparar formulario
    }

    
    
    
    public void nuevaBoleta(){
        boleta = new Boleta();
    }


    public Boleta getBoleta() {
        return boleta;
    }

    public void setBoleta(Boleta boleta) {
        this.boleta = boleta;
    }

    public List<Boleta> getLstBole() {
        return lstBole;
    }

    public void setLstBole(List<Boleta> lstBole) {
        this.lstBole = lstBole;
    }

    public List<Boleta> getLstBoleFiltered() {
        return lstBoleFiltered;
    }

    public void setLstBoleFiltered(List<Boleta> lstBoleFiltered) {
        this.lstBoleFiltered = lstBoleFiltered;
    }

    public List<Evento> getLstEven() {
        return lstEven;
    }

    public void setLstEven(List<Evento> lstEven) {
        this.lstEven = lstEven;
    }
    
    public void listar (){
        Usuario usuario = obtenerUsuarioSesion();
        
        if (usuario != null){
            // Si es administrador, mostrar todas las boletas
            if ("A".equals(usuario.getTipo())){
                lstBole = boleDAO.listar();
            } else {
                // Si no es admin, mostrar solo las boletas del usuario
                lstBole = boleDAO.listarPorUsuario(usuario.getId_usuario());
            }
        }else {
            lstBole = new ArrayList<>();
        }
    }
    
    public void listarEven(){
        EventoDAO evenDAO = new EventoDAO();
        lstEven = evenDAO.listar();
    }
    
    public void nuevoBoleta(){
        boleta = new Boleta();
    }
        private Usuario obtenerUsuarioSesion() {
        try {
            return (Usuario) FacesContext.getCurrentInstance()
                    .getExternalContext()
                    .getSessionMap()
                    .get("usuario");
        } catch (Exception e){
            return null;
        }
    }
    
    
    public String guardar(){
        System.out.println("=== INICIANDO MÉTODO GUARDAR() ===");
        
        Usuario usuario = obtenerUsuarioSesion();
        if(usuario == null){
            System.err.println("Error: Usuario no encontrado en sesión");
            FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_ERROR,
            "Error", "debe iniciar sesión."));
            return null;
        }
        
        System.out.println("Usuario en sesión: " + usuario.getId_usuario());

        //Asignación de usuario automaticamente
        boleta.setId_usuario(usuario.getId_usuario());

        int idEvento = boleta.getId_evento();
        System.out.println("ID Evento seleccionado: " + idEvento);
        
        // Validar que se haya seleccionado un evento válido
        if (idEvento <= 0){
            System.err.println("Error: No se seleccionó un evento válido");
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                "Error", "Debe seleccionar un evento."));
            return null;
        }

        EventoDAO eventoDAO = new EventoDAO();
        Evento eventoSeleccionado = eventoDAO.buscar(idEvento);

        if (eventoSeleccionado == null){
            System.err.println("Error: El evento con ID " + idEvento + " no existe");
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                "Error", "El evento seleccionado no existe."));
            return null;
        }
        
        System.out.println("Evento encontrado: " + eventoSeleccionado.getNombre_evento());

        // Validar cantidad de boletos
        int cantidad = boleta.getCantidad_boletos();
        System.out.println("Cantidad de boletos: " + cantidad);
        
        if (cantidad <= 0){
            System.err.println("Error: Cantidad de boletos inválida: " + cantidad);
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                "Error", "La cantidad de boletos debe ser mayor a cero."));
            return null;
        }

        // Validar que el precio del evento sea válido
        if (eventoSeleccionado.getPrecio_boleta() <= 0){
            System.err.println("Error: Precio del evento inválido: " + eventoSeleccionado.getPrecio_boleta());
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                "Error", "El precio del evento no es válido."));
            return null;
        }

        //Precio de la boleta
        boleta.setPrecio_boleta(eventoSeleccionado.getPrecio_boleta());
        System.out.println("Precio asignado: " + boleta.getPrecio_boleta());
        
        // Validar que todos los valores necesarios estén presentes
        if (boleta.getId_usuario() <= 0 || boleta.getId_evento() <= 0){
            System.err.println("Error: Faltan datos requeridos - Usuario: " + boleta.getId_usuario() + ", Evento: " + boleta.getId_evento());
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                "Error", "Faltan datos requeridos para guardar la boleta."));
            return null;
        }

        int capacidadMaxima = eventoSeleccionado.getCapacidad_maxima();
        int boletasVendidas = boleDAO.obtenerBoletasVendidas(idEvento);
        int nuevasBoletas = boleta.getCantidad_boletos();
        
        System.out.println("Capacidad máxima: " + capacidadMaxima);
        System.out.println("Boletas vendidas: " + boletasVendidas);
        System.out.println("Nuevas boletas: " + nuevasBoletas);

        if (boletasVendidas + nuevasBoletas > capacidadMaxima){
            System.err.println("Error: Capacidad excedida. Disponibles: " + (capacidadMaxima - boletasVendidas));
            FacesMessage message = new FacesMessage (
                FacesMessage.SEVERITY_ERROR,
                "Capacidad excedida",
                "El aforo solo tiene " + (capacidadMaxima - boletasVendidas) + " boletas disponibles."
            );
            FacesContext.getCurrentInstance().addMessage(null, message);
            return null;
        }
        
        System.out.println("Llamando a boleDAO.guardar()...");
        // Intentar guardar y verificar si fue exitoso
        boolean guardado = boleDAO.guardar(boleta);
        System.out.println("Resultado del guardado: " + guardado);
        
        if (guardado) {
            System.out.println("Boleta guardada exitosamente");
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO,
                "Éxito", "Boleta guardada correctamente."));
            listar(); // Actualizar la lista
            nuevaBoleta(); // Limpiar el formulario
            return "index?faces-redirect=true";
        } else {
            System.err.println("Error: No se pudo guardar la boleta");
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                "Error", "No se pudo guardar la boleta. Por favor, intente nuevamente."));
            return null;
        }
        }


    
    public void buscar (int id_boleta){
        EventoDAO eventoDAO = new EventoDAO();
        Evento evento = eventoDAO.buscar(boleta.getId_evento());

        if (evento != null) {
            boleta.setPrecio_boleta(evento.getPrecio_boleta());
        }
        boleta = boleDAO.buscar(id_boleta);
    }
    
    
    
    public String actualizar() {
        int idEventoNuevo = boleta.getId_evento();

        EventoDAO eventoDAO = new EventoDAO();
        Evento eventoSeleccionado = eventoDAO.buscar(idEventoNuevo);

        if (eventoSeleccionado == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Debe seleccionar un evento."));
            return null;
        }

        // Aseguramos precio actualizado
        boleta.setPrecio_boleta(eventoSeleccionado.getPrecio_boleta());

        int capacidadMaxima = eventoSeleccionado.getCapacidad_maxima();
        int boletasVendidasEnEventoNuevo = boleDAO.obtenerBoletasVendidas(idEventoNuevo); // SUM(cantidad_boletos)

        // Obtener boleta original (antes de cambios)
        Boleta boletaOriginal = boleDAO.buscar(boleta.getId_boleta());
        if (boletaOriginal == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se encontró la boleta original."));
            return null;
        }
        int cantidadAnterior = boletaOriginal.getCantidad_boletos();
        int idEventoAnterior = boletaOriginal.getId_evento();

        int nuevasBoletas = boleta.getCantidad_boletos();

        // Si el evento no cambia: hay que quitar la cantidad anterior antes de sumar la nueva
        // Si el evento cambia: no se resta nada en el evento nuevo (porque la boleta anterior pertenece a otro evento)
        int ocupacionDespues;
        int disponibles;

        if (idEventoAnterior == idEventoNuevo) {
            ocupacionDespues = (boletasVendidasEnEventoNuevo - cantidadAnterior) + nuevasBoletas;
            disponibles = capacidadMaxima - (boletasVendidasEnEventoNuevo - cantidadAnterior);
        } else {
            // Evento distinto: boletasVendidasEnEventoNuevo ya NO incluye la boleta anterior
            ocupacionDespues = boletasVendidasEnEventoNuevo + nuevasBoletas;
            disponibles = capacidadMaxima - boletasVendidasEnEventoNuevo;
        }

        if (ocupacionDespues > capacidadMaxima) {
            // Si disponibles es negativo, mostrar 0 disponibles para no confundir
            int mostrable = Math.max(disponibles, 0);
            FacesMessage message = new FacesMessage(
                FacesMessage.SEVERITY_ERROR,
                "Capacidad excedida",
                "El aforo solo tiene " + mostrable + " boleta(s) disponibles."
            );
            FacesContext.getCurrentInstance().addMessage(null, message);
            return null;
        }

        boleDAO.actualizar(boleta);
        listar();
        return "index?faces-redirect=true";
    }
    
    
    public String eliminar(int id_boleta){
        boleDAO.eliminar(id_boleta);
        listar();
        return "index?faces-redirect=true";
    }
    
    public void cargarPrecioEvento() {
    if (boleta.getId_evento() > 0) {
        EventoDAO eventoDAO = new EventoDAO();
        Evento evento = eventoDAO.buscar(boleta.getId_evento());

        if (evento != null) {
            boleta.setPrecio_boleta(evento.getPrecio_boleta());
        }
    }
    }
    
    public String getUsuarioNombre(){
        Usuario u = obtenerUsuarioSesion();
        if (u != null){
            return u.getNombre_completo();
        }
        return "";
    }
}


