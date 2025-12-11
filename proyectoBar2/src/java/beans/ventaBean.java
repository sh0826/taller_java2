/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package beans;

import dao.ventaDao;
import dao.UsuarioDAO;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import modelo.venta;
import modelo.Usuario;

@ManagedBean
@ApplicationScoped
public class ventaBean {
    
    venta venta = new venta();
    List<venta> lstVentas = new ArrayList<>();
    List<venta> lstVentasFiltered = new ArrayList<>();
    ventaDao ventaDAO = new ventaDao();
    UsuarioDAO usuarioDAO = new UsuarioDAO();
    
    // Filtros
    private String search;
    private String metodoPago;
    private Date fechaDesde;
    private Date fechaHasta;
    private BigDecimal totalMin;
    private BigDecimal totalMax;
    
    private String fechaDesdeStr;
    private String fechaHastaStr;
    
    private boolean filtrosAplicados = false;
    
    public void listarInicial(){
        // Solo listar si no hay filtros aplicados
        if (!filtrosAplicados && (search == null || search.trim().isEmpty()) && 
            (metodoPago == null || metodoPago.trim().isEmpty())) {
            listar();
        }
    }
    
    public void listar(){
        venta = new venta();
        lstVentas = ventaDAO.listar();
        lstVentasFiltered = null; // Limpiar filtros de la tabla para mostrar todos los resultados
        filtrosAplicados = false;
    }
    
    public String listarConFiltros(){
        venta = new venta();
        
        // Normalizar valores vacíos - manejar strings vacíos como null
        String searchValue = null;
        if (search != null && !search.trim().isEmpty()) {
            searchValue = search.trim();
        }
        
        String metodoPagoValue = null;
        if (metodoPago != null && !metodoPago.trim().isEmpty()) {
            metodoPagoValue = metodoPago.trim();
        }
        
        // Limpiar lista filtrada para que muestre todos los resultados
        lstVentasFiltered = null;
        
        // Debug
        System.out.println("Filtros aplicados - Search: '" + searchValue + "', MetodoPago: '" + metodoPagoValue + "'");
        
        // Si no hay filtros, listar todo
        if (searchValue == null && metodoPagoValue == null) {
            System.out.println("No hay filtros, listando todas las ventas");
            filtrosAplicados = false;
            listar();
        } else {
            // Solo pasar búsqueda y método de pago, sin fechas ni totales
            System.out.println("Aplicando filtros...");
            filtrosAplicados = true;
            lstVentas = ventaDAO.listarConFiltros(searchValue, metodoPagoValue, null, null, null, null);
            if (lstVentas == null) {
                lstVentas = new ArrayList<>();
            }
            System.out.println("Ventas encontradas: " + lstVentas.size());
            System.out.println("Lista actualizada, tamaño: " + lstVentas.size());
        }
        
        return null; // Mantener en la misma página
    }
    
    public String guardar(){
        FacesContext facesContext = FacesContext.getCurrentInstance();
        
        if (venta.getTotal() == null || venta.getTotal().compareTo(BigDecimal.ZERO) <= 0) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El total debe ser mayor a 0"));
            return null;
        }
        
        if (venta.getMetodo_pago() == null || venta.getMetodo_pago().trim().isEmpty()) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El método de pago es requerido"));
            return null;
        }
        
        // Obtener automáticamente el ID del usuario desde la sesión (foreign key)
        Object idUsuarioObj = facesContext.getExternalContext().getSessionMap().get("id_usuario");
        if (idUsuarioObj != null) {
            int idUsuario = ((Number) idUsuarioObj).intValue();
            venta.setId(idUsuario);
            System.out.println("ID de usuario asignado automáticamente: " + idUsuario);
        } else {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se encontró el usuario en la sesión. Por favor, inicie sesión nuevamente."));
            return null;
        }
        
        boolean exito = ventaDAO.insertar(venta);
        if (exito) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Venta guardada correctamente"));
            venta = new venta();
            // Redirigir según desde dónde se llamó
            String viewId = FacesContext.getCurrentInstance().getViewRoot().getViewId();
            if (viewId.contains("admin")) {
                return "/faces/admin/ventas/index?faces-redirect=true";
            } else {
                return "/faces/emp/ventas/index?faces-redirect=true";
            }
        } else {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo guardar la venta"));
            return null;
        }
    }
    
    public void buscar(int id){
        venta = ventaDAO.buscarPorId(id);
        if (venta == null) {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia", "No se encontró la venta con ID: " + id));
            venta = new venta(); // Inicializar para evitar NullPointerException
        }
    }
    
    public void verDetalle(int id){
        // Guardar el ID de la venta en la sesión para que detalleVentaBean lo use
        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.getExternalContext().getSessionMap().put("id_venta_filtro", id);
    }
    
    public String actualizar(){
        FacesContext facesContext = FacesContext.getCurrentInstance();
        
        if (venta.getTotal() == null || venta.getTotal().compareTo(BigDecimal.ZERO) <= 0) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El total debe ser mayor a 0"));
            return null;
        }
        
        if (venta.getMetodo_pago() == null || venta.getMetodo_pago().trim().isEmpty()) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El método de pago es requerido"));
            return null;
        }
        
        if (venta.getId() == 0) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Debe seleccionar un usuario"));
            return null;
        }
        
        boolean exito = ventaDAO.actualizar(venta);
        if (exito) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Venta actualizada correctamente"));
            listar();
            return "/faces/admin/ventas/index?faces-redirect=true";
        } else {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo actualizar la venta"));
            return null;
        }
    }
    
    public List<Usuario> getLstUsuarios() {
        return usuarioDAO.listarU();
    }
    
    public void eliminar(int id){
        ventaDAO.eliminar(id);
        listar();
    }
    
    public String limpiarFiltros(){
        search = null;
        metodoPago = null;
        lstVentasFiltered = null;
        filtrosAplicados = false;
        listar();
        return null; // Mantener en la misma página
    }
    
    // Getters y Setters
    public venta getVenta() {
        return venta;
    }

    public void setVenta(venta venta) {
        this.venta = venta;
    }

    public List<venta> getLstVentas() {
        // Debug para verificar que se está llamando el getter
        if (lstVentas != null) {
            System.out.println("getLstVentas() llamado - Tamaño de lista: " + lstVentas.size());
        }
        return lstVentas;
    }

    public void setLstVentas(List<venta> lstVentas) {
        this.lstVentas = lstVentas;
    }

    public List<venta> getLstVentasFiltered() {
        return lstVentasFiltered;
    }

    public void setLstVentasFiltered(List<venta> lstVentasFiltered) {
        this.lstVentasFiltered = lstVentasFiltered;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public Date getFechaDesde() {
        return fechaDesde;
    }

    public void setFechaDesde(Date fechaDesde) {
        this.fechaDesde = fechaDesde;
    }

    public Date getFechaHasta() {
        return fechaHasta;
    }

    public void setFechaHasta(Date fechaHasta) {
        this.fechaHasta = fechaHasta;
    }

    public BigDecimal getTotalMin() {
        return totalMin;
    }

    public void setTotalMin(BigDecimal totalMin) {
        this.totalMin = totalMin;
    }

    public BigDecimal getTotalMax() {
        return totalMax;
    }

    public void setTotalMax(BigDecimal totalMax) {
        this.totalMax = totalMax;
    }
}

