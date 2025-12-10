/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package beans;

import static com.sun.faces.el.FacesCompositeELResolver.ELResolverChainType.Faces;
import dao.BoletaDAO;
import dao.EventoDAO;
import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import modelo.Boleta;
import modelo.Evento;

/**
 *
 * @author marce
 */
@ManagedBean
@SessionScoped
public class BoletaBean {
    Boleta boleta = new Boleta();
    List<Boleta> lstBole = new ArrayList<>();
    List<Boleta> lstBoleFiltered = new ArrayList<>();
    List<Evento> lstEven = new ArrayList<>();
    BoletaDAO boleDAO = new BoletaDAO();
    
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
        boleta = new Boleta();
        lstBole = boleDAO.listar();
    }
    
    public void listarEven(){
        EventoDAO evenDAO = new EventoDAO();
        lstEven = evenDAO.listar();
    }
    
    public void nuevoBoleta(){
        boleta = new Boleta();
    }
    
public String guardar(){
    int idEvento = boleta.getId_evento();

    EventoDAO eventoDAO = new EventoDAO();
    Evento eventoSeleccionado = eventoDAO.buscar(idEvento);

    if (eventoSeleccionado == null){
        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_ERROR,
            "Error", "Debe seleccionar un evento."));
        return null;
    }
    
        //Precio de la boleta
    boleta.setPrecio_boleta(eventoSeleccionado.getPrecio_boleta());

    int capacidadMaxima = eventoSeleccionado.getCapacidad_maxima();
    int boletasVendidas = boleDAO.obtenerBoletasVendidas(idEvento);
    int nuevasBoletas = boleta.getCantidad_boletos();

    if (boletasVendidas + nuevasBoletas > capacidadMaxima){
        FacesMessage message = new FacesMessage (
            FacesMessage.SEVERITY_ERROR,
            "Capacidad excedida",
            "El aforo solo tiene " + (capacidadMaxima - boletasVendidas) + " boletas disponibles."
        );
        FacesContext.getCurrentInstance().addMessage(null, message);
        return null;
    }
    
    
    
    boleDAO.guardar(boleta);
    listar();
    return "index?faces-redirect=true";
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

    // Si llega aquí, todo OK — actualizamos
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

}
