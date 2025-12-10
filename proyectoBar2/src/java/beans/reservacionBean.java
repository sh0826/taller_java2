/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package beans;

import dao.reservacionDao;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import modelo.reservacion;

@ManagedBean // Anotación JSF: Hace que esta clase sea un Bean gestionado por JSF
@ApplicationScoped // Alcance: El Bean vive durante toda la aplicación (compartido por todos los usuarios)
public class reservacionBean {
    
    reservacion reservacion = new reservacion(); // Objeto para almacenar los datos del formulario
    List<reservacion> lstReserv = new ArrayList<>(); // Lista con todas las reservaciones
    List<reservacion> lstReservFiltered = new ArrayList<>(); // Lista filtrada para mostrar en la tabla
    reservacionDao reservDAO = new reservacionDao(); // Instancia del DAO para acceder a la base de datos
    private Date fechaHoy = new Date(); // Fecha actual (para validar que no se seleccionen fechas pasadas)
    
    // Filtros para búsqueda
    private Integer searchId; // Filtro por ID de reservación
    private String searchOcasion; // Filtro por ocasión (texto)
    
    // Método para cargar todas las reservaciones desde la base de datos
    public void listar(){
        reservacion = new reservacion(); // Limpiar el objeto del formulario
        lstReserv = reservDAO.listar(); // Llamar al DAO para obtener todas las reservaciones
        lstReservFiltered = new ArrayList<>(); // Limpiar la lista filtrada
    }
    
    // Método para GUARDAR una nueva reservación (retorna String para navegación JSF)
    public String guardar(){
        FacesContext facesContext = FacesContext.getCurrentInstance(); // Obtener contexto JSF para mostrar mensajes
        
        System.out.println("=== INICIANDO GUARDAR ===");
        System.out.println("Personas: " + reservacion.getCatindad_personas());
        System.out.println("Mesas: " + reservacion.getCantidad_mesas());
        System.out.println("Ocasión: " + reservacion.getOcasion());
        System.out.println("Fecha: " + reservacion.getFecha_reservacion());
        System.out.println("Reservación completa: " + reservacion);
        
        // Validaciones del lado del servidor
        if (reservacion.getFecha_reservacion() == null) { // Validar que la fecha no sea null
            System.err.println("Error: La fecha de reservación es null");
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La fecha de reservación es requerida")); // Mostrar mensaje de error
            return null; // Retornar null mantiene en la misma página
        }
        
        if (reservacion.getCatindad_personas() == 0) { // Validar que se haya ingresado cantidad de personas
            System.err.println("Error: La cantidad de personas es 0 (no se capturó el valor)");
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor ingrese la cantidad de personas"));
            return null;
        }
        
        if (reservacion.getCatindad_personas() < 1 || reservacion.getCatindad_personas() > 80) { // Validar rango 1-80
            System.err.println("Error: La cantidad de personas debe estar entre 1 y 80. Valor: " + reservacion.getCatindad_personas());
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La cantidad de personas debe estar entre 1 y 80"));
            return null;
        }
        
        if (reservacion.getCantidad_mesas() == 0) { // Validar que se haya ingresado cantidad de mesas
            System.err.println("Error: La cantidad de mesas es 0 (no se capturó el valor)");
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor ingrese la cantidad de mesas"));
            return null;
        }
        
        if (reservacion.getCantidad_mesas() < 1 || reservacion.getCantidad_mesas() > 20) { // Validar rango 1-20
            System.err.println("Error: La cantidad de mesas debe estar entre 1 y 20. Valor: " + reservacion.getCantidad_mesas());
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La cantidad de mesas debe estar entre 1 y 20"));
            return null;
        }
        
        if (reservacion.getOcasion() == null || reservacion.getOcasion().trim().isEmpty()) { // Validar que la ocasión no esté vacía
            System.err.println("Error: La ocasión es requerida");
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La ocasión es requerida"));
            return null;
        }
        
        System.out.println("Validaciones pasadas, intentando insertar en BD...");
        boolean exito = reservDAO.insertar(reservacion); // Llamar al DAO para insertar en la base de datos
        
        if (exito) { // Si se guardó correctamente
            System.out.println("Reservación guardada exitosamente");
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Reservación guardada correctamente")); // Mensaje de éxito
            reservacion = new reservacion(); // Limpiar el formulario
            return "/index?faces-redirect=true"; // Redirigir al menú principal (faces-redirect=true hace redirect HTTP)
        } else { // Si hubo error al guardar
            System.err.println("Error al guardar la reservación en la base de datos");
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo guardar la reservación. Verifique la consola del servidor para más detalles."));
            return null; // Mantener en la misma página si hay error
        }
    }
    
    // Método para BUSCAR una reservación por ID y cargarla en el objeto reservacion
    public void buscar(int id){
        reservacion = reservDAO.buscarPorId(id); // Buscar en BD y asignar al objeto del formulario
    }
    
    // Método para ACTUALIZAR una reservación existente
    public void actualizar(){
        reservDAO.actualizar(reservacion); // Actualizar en la base de datos
        listar(); // Recargar la lista para ver los cambios
    }
    
    // Método para ELIMINAR una reservación por ID
    public void eliminar(int id){
        reservDAO.eliminar(id); // Eliminar de la base de datos
        listar(); // Recargar la lista para actualizar la tabla
    }
    
    public reservacion getReservacion() {
        return reservacion;
    }

    public void setReservacion(reservacion reservacion) {
        this.reservacion = reservacion;
    }

    public List<reservacion> getLstReserv() {
        return lstReserv;
    }

    public void setLstReserv(List<reservacion> lstReserv) {
        this.lstReserv = lstReserv;
    }

    public List<reservacion> getLstReservFiltered() {
        return lstReservFiltered;
    }

    public void setLstReservFiltered(List<reservacion> lstReservFiltered) {
        this.lstReservFiltered = lstReservFiltered;
    }
    
    public Date getFechaHoy() {
        fechaHoy = new Date();
        return fechaHoy;
    }
    
    public void setFechaHoy(Date fechaHoy) {
        this.fechaHoy = fechaHoy;
    }
    
    // Método para FILTRAR las reservaciones según los criterios de búsqueda
    public void listarConFiltros() {
        listar(); // Primero cargar todas las reservaciones
        lstReservFiltered = new ArrayList<>(); // Inicializar lista filtrada vacía
        
        // Recorrer todas las reservaciones
        for (reservacion res : lstReserv) {
            boolean match = true; // Bandera para indicar si cumple con los filtros
            
            // Filtrar por ID: si se ingresó un ID, debe coincidir exactamente
            if (searchId != null && searchId > 0) {
                if (res.getId_reservacion() != searchId) {
                    match = false; // No coincide, excluir de resultados
                }
            }
            
            // Filtrar por ocasión: si se ingresó texto, debe contenerlo (búsqueda parcial, sin distinguir mayúsculas)
            if (searchOcasion != null && !searchOcasion.trim().isEmpty()) {
                if (res.getOcasion() == null || !res.getOcasion().toLowerCase().contains(searchOcasion.toLowerCase())) {
                    match = false; // No contiene el texto, excluir de resultados
                }
            }
            
            if (match) { // Si cumple con todos los filtros
                lstReservFiltered.add(res); // Agregar a la lista filtrada
            }
        }
    }
    
    // Método para LIMPIAR los filtros y mostrar todas las reservaciones
    public void limpiarFiltros() {
        searchId = null; // Limpiar filtro de ID
        searchOcasion = null; // Limpiar filtro de ocasión
        listar(); // Recargar todas las reservaciones
    }

    public Integer getSearchId() {
        return searchId;
    }

    public void setSearchId(Integer searchId) {
        this.searchId = searchId;
    }

    public String getSearchOcasion() {
        return searchOcasion;
    }

    public void setSearchOcasion(String searchOcasion) {
        this.searchOcasion = searchOcasion;
    }
}
