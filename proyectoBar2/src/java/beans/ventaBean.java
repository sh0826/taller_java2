/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package beans;

import dao.ventaDao;
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

@ManagedBean
@ApplicationScoped
public class ventaBean {
    
    venta venta = new venta();
    List<venta> lstVentas = new ArrayList<>();
    List<venta> lstVentasFiltered = new ArrayList<>();
    ventaDao ventaDAO = new ventaDao();
    
    // Filtros
    private String search;
    private String metodoPago;
    private Date fechaDesde;
    private Date fechaHasta;
    private BigDecimal totalMin;
    private BigDecimal totalMax;
    
    private String fechaDesdeStr;
    private String fechaHastaStr;
    
    public void listar(){
        venta = new venta();
        lstVentas = ventaDAO.listar();
    }
    
    public void listarConFiltros(){
        venta = new venta();
        
        // Convertir fechas Date a String para el SQL
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        fechaDesdeStr = null;
        fechaHastaStr = null;
        
        if (fechaDesde != null) {
            fechaDesdeStr = sdf.format(fechaDesde);
        }
        if (fechaHasta != null) {
            fechaHastaStr = sdf.format(fechaHasta);
        }
        
        // Si no hay filtros, listar todo
        if ((search == null || search.trim().isEmpty()) && 
            (metodoPago == null || metodoPago.trim().isEmpty()) &&
            fechaDesdeStr == null &&
            fechaHastaStr == null &&
            totalMin == null && totalMax == null) {
            listar();
        } else {
            lstVentas = ventaDAO.listarConFiltros(search, metodoPago, fechaDesdeStr, fechaHastaStr, totalMin, totalMax);
        }
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
        
        boolean exito = ventaDAO.insertar(venta);
        if (exito) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Venta guardada correctamente"));
            venta = new venta();
            // Redirigir según desde dónde se llamó
            String viewId = FacesContext.getCurrentInstance().getViewRoot().getViewId();
            if (viewId.contains("admin")) {
                return "/ventas/admin/index?faces-redirect=true";
            } else {
                return "/ventas/empleado/index?faces-redirect=true";
            }
        } else {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo guardar la venta"));
            return null;
        }
    }
    
    public void buscar(int id){
        venta = ventaDAO.buscarPorId(id);
    }
    
    public void actualizar(){
        ventaDAO.actualizar(venta);
        listar();
    }
    
    public void eliminar(int id){
        ventaDAO.eliminar(id);
        listar();
    }
    
    public void limpiarFiltros(){
        search = null;
        metodoPago = null;
        fechaDesde = null;
        fechaHasta = null;
        fechaDesdeStr = null;
        fechaHastaStr = null;
        totalMin = null;
        totalMax = null;
        listar();
    }
    
    // Getters y Setters
    public venta getVenta() {
        return venta;
    }

    public void setVenta(venta venta) {
        this.venta = venta;
    }

    public List<venta> getLstVentas() {
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

