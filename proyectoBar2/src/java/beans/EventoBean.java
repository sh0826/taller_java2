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
import java.io.Serializable;
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
public class EventoBean implements Serializable {
    private static final long serialVersionUID = 1L;
    
    Evento evento = new Evento();
    List<Evento> lstEvento = new ArrayList<>();
    private transient EventoDAO eventoDAO;
    private transient Part imagen;
    
    private EventoDAO getEventoDAO() {
        if (eventoDAO == null) {
            eventoDAO = new EventoDAO();
        }
        return eventoDAO;
    }
    
    public void listar(){
        lstEvento = getEventoDAO().listar();
    }
    
    public void nuevoEvento(){
        evento = new Evento();
        imagen = null;
    }
    
    public String guardar(){
        FacesContext facesContext = FacesContext.getCurrentInstance();
        
        try {
            //Validación capacidad establecimiento
            int capacidadEstablecimiento = 50;
            if (evento.getCapacidad_maxima() > capacidadEstablecimiento){
                FacesMessage msg = new FacesMessage (
                FacesMessage.SEVERITY_ERROR,
                "Capacidad del establecimiento superada. Capacidad máxima: " + capacidadEstablecimiento , null);
                facesContext.addMessage(null, msg);
                return null;
            }
            
            //Validación de fecha
            Date hoy = new Date();
            if (evento.getFecha() != null && evento.getFecha().before(hoy)){
                FacesMessage msg = new FacesMessage (
                FacesMessage.SEVERITY_ERROR,
                "La fecha no puede ser pasada.", null);
                facesContext.addMessage(null, msg);
                return null;
            }
            
            // Primero guardar el evento para obtener el ID
            int idGenerado = getEventoDAO().guardar(evento);
            
            if (idGenerado == 0) {
                FacesMessage msg = new FacesMessage(
                        FacesMessage.SEVERITY_ERROR,
                        "Error al guardar el evento en la base de datos", null
                );
                facesContext.addMessage(null, msg);
                return null;
            }
            
            // Ahora guardar la imagen con el ID correcto
            if (imagen != null && imagen.getSize() > 0) {
                String path = facesContext.getExternalContext().getRealPath("/");
                
                InputStream in = imagen.getInputStream();
                File dir = new File(path + "ImgEventos");
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                
                // Usar el ID generado para nombrar el archivo
                File f = new File(dir, idGenerado + ".png");
                FileOutputStream out = new FileOutputStream(f);
                
                byte[] buffer = new byte[1024];
                int tamaño;
                
                while ((tamaño = in.read(buffer)) > 0){
                    out.write(buffer, 0, tamaño);
                }
                
                out.close();
                in.close();
                
                // Actualizar la ruta de la imagen en la base de datos
                evento.setId_evento(idGenerado);
                // Usar ruta relativa (compatible con las rutas existentes)
                evento.setImagen("../ImgEventos/" + idGenerado + ".png");
                getEventoDAO().actualizar(evento);
            }
            
            listar();
            
            // Usar Flash scope para mantener el mensaje después del redirect
            facesContext.getExternalContext().getFlash().setKeepMessages(true);
            FacesMessage msg = new FacesMessage(
                    FacesMessage.SEVERITY_INFO,
                    "Evento guardado correctamente", null
            );
            facesContext.addMessage(null, msg);
            
        } catch (IOException e){
            FacesMessage msg = new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "Error al guardar la imagen: " + e.getMessage(), null
            );
            facesContext.addMessage(null, msg);
            return null;
        } catch (Exception e) {
            FacesMessage msg = new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "Error al guardar el evento: " + e.getMessage(), null
            );
            facesContext.addMessage(null, msg);
            return null;
        }
        return "index?faces-redirect=true";
    }
    
    
    
    public void buscar (int id_evento){
        evento = getEventoDAO().buscar(id_evento);
    }
    
    
    
    public String actualizar(){
        FacesContext facesContext = FacesContext.getCurrentInstance();
        
        //Validar que el evento tenga un ID válido
        if (evento == null || evento.getId_evento() == 0) {
            FacesMessage msg = new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "Error: No se puede actualizar un evento sin ID válido", null
            );
            facesContext.addMessage(null, msg);
            return null;
        }
        
        //Preservar la imagen existente antes de cualquier cambio
        String imagenAnterior = evento.getImagen();
        
        //Validación capacidad establecimiento
        int capacidadEstablecimiento = 50;
        if (evento.getCapacidad_maxima() > capacidadEstablecimiento){
            FacesMessage msg = new FacesMessage (
            FacesMessage.SEVERITY_ERROR,
            "Capacidad del establecimiento superada. Capacidad máxima: " + capacidadEstablecimiento , null);
            facesContext.addMessage(null, msg);
            return null;
        }
        
        //Validación de fecha 
        Date hoy = new Date();
        if(evento.getFecha() != null && evento.getFecha().before(hoy)){
            FacesMessage msg = new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "La fecha no puede ser pasada", null
            );
            facesContext.addMessage(null, msg);
            return null;
        }
        
        //Control de imagen - solo actualizar si se sube una nueva
        if(imagen != null && imagen.getSize() > 0){
            try {
                String path = facesContext.getExternalContext().getRealPath("/");
                
                InputStream in = imagen.getInputStream();
                File dir = new File(path + "ImgEventos");
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                
                // Usar el ID del evento para nombrar el archivo
                String nombreArchivo = evento.getId_evento() + ".png";
                File f = new File(dir, nombreArchivo);
                
                // Eliminar imagen anterior si existe
                if (f.exists()) {
                    f.delete();
                }
                
                FileOutputStream out = new FileOutputStream(f);
                
                byte[] buffer = new byte[1024];
                int tamaño;
                
                while ((tamaño = in.read(buffer)) > 0){
                    out.write(buffer, 0, tamaño);
                }
                
                out.close();
                in.close();
                
                // Establecer la ruta correcta de la imagen (ruta relativa compatible)
                evento.setImagen("../ImgEventos/" + nombreArchivo);
            } catch (IOException e){
                e.printStackTrace();
                FacesMessage msg = new FacesMessage(
                        FacesMessage.SEVERITY_ERROR,
                        "Error al guardar la imagen: " + e.getMessage(), null
                );
                facesContext.addMessage(null, msg);
                return null;
            }
        } else {
            // Si no se sube nueva imagen, preservar la existente
            evento.setImagen(imagenAnterior);
        }
        
        boolean actualizado = getEventoDAO().actualizar(evento);
        
        if (actualizado) {
            listar();
            
            // Usar Flash scope para mantener el mensaje después del redirect
            facesContext.getExternalContext().getFlash().setKeepMessages(true);
            FacesMessage msg = new FacesMessage(
                    FacesMessage.SEVERITY_INFO,
                    "Evento actualizado correctamente", null
            );
            facesContext.addMessage(null, msg);
            return "index?faces-redirect=true";
        } else {
            FacesMessage msg = new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "Error: No se pudo actualizar el evento. Verifique los datos e intente nuevamente.", null
            );
            facesContext.addMessage(null, msg);
            return null;
        }
    }
    
    
    
    public String eliminar(int id_evento){
        getEventoDAO().eliminar(id_evento);
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
    
    // Métodos para estadísticas del dashboard
    public int getTotalEventos() {
        return lstEvento != null ? lstEvento.size() : 0;
    }
    
    public int getProximosEventos() {
        if (lstEvento == null || lstEvento.isEmpty()) {
            return 0;
        }
        Date hoy = new Date();
        int count = 0;
        for (Evento e : lstEvento) {
            if (e.getFecha() != null && e.getFecha().after(hoy)) {
                count++;
            }
        }
        return count;
    }
    
    public int getCapacidadTotal() {
        if (lstEvento == null || lstEvento.isEmpty()) {
            return 0;
        }
        int total = 0;
        for (Evento e : lstEvento) {
            total += e.getCapacidad_maxima();
        }
        return total;
    }
    
}


