/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package beans;

import dao.EventoDAO;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.Part;
import modelo.Evento;

/**
 *
 * @author marce
 */
@ManagedBean
@SessionScoped
public class EventoBean {
    Evento evento = new Evento();
    List<Evento> lstEvento = new ArrayList<>();
    EventoDAO eventoDAO = new EventoDAO();
    Part imagen;
    
    public void listar(){
        lstEvento = eventoDAO.listar();
    }
    
    public void nuevoEvento(){
        evento = new Evento();
        imagen = null;
    }
    
    public String guardar(){
        try {
            //Validación capacidad establecimiento
            int capacidadEstablecimiento = 50;
            if (evento.getCapacidad_maxima() > capacidadEstablecimiento){
                FacesMessage msg = new FacesMessage (
                FacesMessage.SEVERITY_ERROR,
                "Capacidad del establecimeinto superada. Capacidad" + capacidadEstablecimiento , null);
                FacesContext.getCurrentInstance().addMessage(null, msg);
                return null;
            }
            
            //Validación de fecha
            Date hoy = new Date();
            if (evento.getFecha().before(hoy)){
                FacesMessage msg = new FacesMessage (
                FacesMessage.SEVERITY_ERROR,
                "La fecha no puede ser pasada.", null);
                FacesContext.getCurrentInstance().addMessage(null, msg);
                return null;
            }
            
            //Control de la imagen
            String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
            
            InputStream in = imagen.getInputStream();
            File dir = new File(path + "ImgEventos");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File f = new File (dir, evento.getId_evento() + ".png");
            FileOutputStream out = new FileOutputStream (f);
            
            byte[] buffer = new byte [1024];
            int tamaño;
            
            while ((tamaño = in.read(buffer)) > 0){
                out.write(buffer, 0, tamaño);
            }
            
            out.close();
            in.close();
            
            evento.setImagen("../ImgEventos/" + f.getName());
            
            eventoDAO.guardar(evento);
            listar();
        } catch (IOException e){
        }
        return "index?faces-redirect=true";
    }
    
    
    
    public void buscar (int id_evento){
        evento = eventoDAO.buscar(id_evento);
    }
    
    
    
    public String actualizar(){
        //Validación capacidad establecimiento
        int capacidadEstablecimiento = 50;
        if (evento.getCapacidad_maxima() > capacidadEstablecimiento){
            FacesMessage msg = new FacesMessage (
            FacesMessage.SEVERITY_ERROR,
            "Capacidad del establecimeinto superada. Capacidad" + capacidadEstablecimiento , null);
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return null;
        }
        
        //Validación de fecha 
        Date hoy = new Date();
        if(evento.getFecha().before(hoy)){
            FacesMessage msg = new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "La fecha no puede ser pasada", null
            );
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return null;
        }
        
        //Control de imagen
        if(imagen != null){
            try {
                String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
                
                InputStream in = imagen.getInputStream();
                File dir = new File(path + "ImgEventos");
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                File f = new File (dir, evento.getId_evento() + ".png");
                FileOutputStream out = new FileOutputStream (f);
                
                byte[] buffer = new byte [1024];
                int tamaño;
                
                while ((tamaño = in.read(buffer)) > 0){
                    out.write(buffer, 0, tamaño);
                }
                
                out.close();
                in.close();
                
                evento.setImagen("../ImgEventos/" + f.getName());
            } catch (IOException e){
            }
        }
        eventoDAO.actualizar(evento);
        listar();
        return "index?faces-redirect=true";
    }
    
    
    
    public String eliminar(int id_evento){
        eventoDAO.eliminar(id_evento);
        listar();
        return "index?faces-redirect=true";
    }
    
    
    public Evento getEvento() {
        return evento;
    }
    
    public void setEvento (Evento evento) {
        this.evento = evento;
    }
    
    public List<Evento> getLstEvento() {
        return lstEvento;
    }
    
    public void setLstEvento (List<Evento> lstEvento){
        this.lstEvento = lstEvento;
    }
    
    public Part getImagen (){
        return imagen;
    }
    
    public void setImagen(Part imagen){
        this.imagen = imagen;
    }
    
    // Propiedades auxiliares para manejar la conversión de Date a Time
    public Date getHoraInicioDate() {
        if (evento.getHora_inicio() != null) {
            return new Date(evento.getHora_inicio().getTime());
        }
        return null;
    }
    
    public void setHoraInicioDate(Date date) {
        if (date != null) {
            evento.setHora_inicio(new Time(date.getTime()));
        } else {
            evento.setHora_inicio((Time) null);
        }
    }
    
}


