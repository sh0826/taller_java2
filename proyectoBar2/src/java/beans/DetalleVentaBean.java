package beans;

import dao.DetalleVentaDAO;
import dao.ProductoDAO;
import dao.ventaDao;
import java.io.OutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.servlet.http.HttpServletResponse;
import modelo.DetalleVenta;
import modelo.Producto;
import modelo.venta;

@ManagedBean
@ViewScoped
public class DetalleVentaBean {

    private DetalleVenta detalle = new DetalleVenta();
    private List<DetalleVenta> lista;
    private List<DetalleVenta> listaFiltrada;
    private DetalleVentaDAO dao = new DetalleVentaDAO();
    private List<Producto> listaProductos;
    private ProductoDAO productoDAO = new ProductoDAO();
    
    // Filtros
    private Integer filtroIdVenta;
    private Integer filtroIdProducto;
    private String filtroDescripcion = "";

    public DetalleVenta getDetalle() { return detalle; }
    public void setDetalle(DetalleVenta detalle) { this.detalle = detalle; }

    public Integer getFiltroIdVenta() { return filtroIdVenta; }
    public void setFiltroIdVenta(Integer filtroIdVenta) { this.filtroIdVenta = filtroIdVenta; }

    public Integer getFiltroIdProducto() { return filtroIdProducto; }
    public void setFiltroIdProducto(Integer filtroIdProducto) { this.filtroIdProducto = filtroIdProducto; }

    public String getFiltroDescripcion() { return filtroDescripcion; }
    public void setFiltroDescripcion(String filtroDescripcion) { this.filtroDescripcion = filtroDescripcion; }

    public List<DetalleVenta> getLista() {
        try {
            lista = dao.listar();
        } catch(Exception e){ 
            e.printStackTrace(); 
        }
        return lista;
    }
    
    public List<DetalleVenta> getListaFiltrada() {
        if (listaFiltrada == null) {
            try {
                if (filtroIdVenta != null && filtroIdVenta > 0) {
                    // Si hay un filtro de ID de venta, filtrar automáticamente
                    Integer idProductoFiltro = (filtroIdProducto != null && filtroIdProducto == 0) ? null : filtroIdProducto;
                    listaFiltrada = dao.listarConFiltros(filtroIdVenta, idProductoFiltro, filtroDescripcion);
                } else {
                    listaFiltrada = dao.listar();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return listaFiltrada;
    }
    
    private venta ventaSeleccionada;
    private ventaDao ventaDAO = new ventaDao();
    
    public venta getVentaSeleccionada() {
        return ventaSeleccionada;
    }
    
    public void inicializarDesdeSesion() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (facesContext != null) {
            Object idVentaObj = facesContext.getExternalContext().getSessionMap().get("id_venta_filtro");
            if (idVentaObj != null) {
                filtroIdVenta = ((Number) idVentaObj).intValue();
                // Cargar la información de la venta
                ventaSeleccionada = ventaDAO.buscarPorId(filtroIdVenta);
                // Aplicar el filtro automáticamente
                listaFiltrada = null; // Forzar recarga
                try {
                    Integer idProductoFiltro = (filtroIdProducto != null && filtroIdProducto == 0) ? null : filtroIdProducto;
                    listaFiltrada = dao.listarConFiltros(filtroIdVenta, idProductoFiltro, filtroDescripcion);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // NO limpiar el valor de la sesión aquí, para que se mantenga al recargar
            }
        }
    }
    
    public void setListaFiltrada(List<DetalleVenta> listaFiltrada) {
        this.listaFiltrada = listaFiltrada;
    }

    public List<Producto> getListaProductos() {
        try { listaProductos = productoDAO.listar(); }
        catch(Exception e){ e.printStackTrace(); }
        return listaProductos;
    }
    
    public void filtrar() {
        try {
            // Si filtroIdProducto es 0 (Todos), pasar null
            Integer idProductoFiltro = (filtroIdProducto != null && filtroIdProducto == 0) ? null : filtroIdProducto;
            listaFiltrada = null; // Forzar recarga
            listaFiltrada = dao.listarConFiltros(filtroIdVenta, idProductoFiltro, filtroDescripcion);
            // Si hay un ID de venta, cargar la información de la venta
            if (filtroIdVenta != null && filtroIdVenta > 0) {
                ventaSeleccionada = ventaDAO.buscarPorId(filtroIdVenta);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void limpiarFiltros() {
        filtroIdVenta = null;
        filtroIdProducto = null;
        filtroDescripcion = "";
        try {
            listaFiltrada = dao.listar();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void guardar() {
        System.out.println("Esto es guardar");

        try {
            detalle.setId_venta(1);  // ← TEMPORAL (prueba)

            if(detalle.getId_detalleV() > 0) {
                dao.modificar(detalle);
            } else {
                dao.insertar(detalle);
            }
            detalle = new DetalleVenta();
            actualizarLista();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public void editar(DetalleVenta d) { 
        this.detalle = d; 
    }

    public void eliminar(int id) {
        try { 
            dao.eliminar(id);
            actualizarLista();
        } catch(Exception e){ 
            e.printStackTrace(); 
        }
    }
    
    private void actualizarLista() {
        try {
            listaFiltrada = dao.listar();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void exportarPDF() {
        try {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            ExternalContext externalContext = facesContext.getExternalContext();
            HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();
            
            response.reset();
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment;filename=DetalleVenta.pdf");
            
            OutputStream outputStream = response.getOutputStream();
            
            List<DetalleVenta> detalles = getListaFiltrada();
            
            // Construir contenido de texto primero
            StringBuilder textContent = new StringBuilder();
            textContent.append("BT\n/F1 12 Tf\n50 750 Td\n");
            textContent.append("(LISTA DE DETALLES DE VENTA) Tj\n");
            textContent.append("0 -25 Td\n/F1 10 Tf\n");
            textContent.append("(Fecha: ").append(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date())).append(") Tj\n");
            textContent.append("0 -30 Td\n");
            textContent.append("(ID    ID Venta  ID Prod  Descripcion        Cantidad  Precio) Tj\n");
            textContent.append("0 -20 Td\n");
            
            for (DetalleVenta d : detalles) {
                String desc = d.getDescripcion() != null ? (d.getDescripcion().length() > 15 ? d.getDescripcion().substring(0, 15) : d.getDescripcion()) : "";
                String line = String.format("%-5d %-10d %-9d %-15s %-10d %-15.2f",
                    d.getId_detalleV(), d.getId_venta(), d.getId_producto(), desc, 
                    d.getCantidad_productos() != null ? d.getCantidad_productos() : 0,
                    d.getPrecio_unitario() != null ? d.getPrecio_unitario() : 0.0);
                textContent.append("(").append(escapePDF(line)).append(") Tj\n");
                textContent.append("0 -20 Td\n");
            }
            textContent.append("ET\n");
            
            // Generar PDF con offsets calculados
            List<Integer> offsets = new ArrayList<>();
            StringBuilder pdf = new StringBuilder();
            
            pdf.append("%PDF-1.4\n");
            
            offsets.add(pdf.length());
            pdf.append("1 0 obj\n<<\n/Type /Catalog\n/Pages 2 0 R\n>>\nendobj\n");
            
            offsets.add(pdf.length());
            pdf.append("2 0 obj\n<<\n/Type /Pages\n/Kids [3 0 R]\n/Count 1\n>>\nendobj\n");
            
            offsets.add(pdf.length());
            pdf.append("3 0 obj\n<<\n/Type /Page\n/Parent 2 0 R\n/MediaBox [0 0 612 792]\n");
            pdf.append("/Contents 4 0 R\n/Resources <<\n/Font <<\n/F1 5 0 R\n>>\n>>\n>>\nendobj\n");
            
            offsets.add(pdf.length());
            pdf.append("4 0 obj\n<<\n/Length ").append(textContent.length()).append("\n>>\nstream\n");
            pdf.append(textContent.toString());
            pdf.append("endstream\nendobj\n");
            
            offsets.add(pdf.length());
            pdf.append("5 0 obj\n<<\n/Type /Font\n/Subtype /Type1\n/BaseFont /Helvetica\n>>\nendobj\n");
            
            int xrefPos = pdf.length();
            pdf.append("xref\n0 ").append(offsets.size() + 1).append("\n");
            pdf.append("0000000000 65535 f \n");
            for (int offset : offsets) {
                pdf.append(String.format("%010d 00000 n \n", offset));
            }
            
            pdf.append("trailer\n<<\n/Size ").append(offsets.size() + 1).append("\n/Root 1 0 R\n>>\n");
            pdf.append("startxref\n").append(xrefPos).append("\n%%EOF");
            
            outputStream.write(pdf.toString().getBytes("ISO-8859-1"));
            outputStream.close();
            facesContext.responseComplete();
            
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al exportar PDF: " + e.getMessage()));
        }
    }
    
    public void exportarExcel() {
        try {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            ExternalContext externalContext = facesContext.getExternalContext();
            HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();
            
            response.reset();
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment;filename=DetalleVenta.xls");
            
            OutputStream outputStream = response.getOutputStream();
            
            List<DetalleVenta> detalles = getListaFiltrada();
            
            StringBuilder excelContent = new StringBuilder();
            excelContent.append("<html xmlns:o='urn:schemas-microsoft-com:office:office' ");
            excelContent.append("xmlns:x='urn:schemas-microsoft-com:office:excel' ");
            excelContent.append("xmlns='http://www.w3.org/TR/REC-html40'>\n");
            excelContent.append("<head>\n");
            excelContent.append("<meta http-equiv='Content-Type' content='text/html; charset=UTF-8'>\n");
            excelContent.append("<!--[if gte mso 9]><xml><x:ExcelWorkbook><x:ExcelWorksheets><x:ExcelWorksheet>");
            excelContent.append("<x:Name>DetalleVenta</x:Name><x:WorksheetOptions><x:DisplayGridlines/></x:WorksheetOptions>");
            excelContent.append("</x:ExcelWorksheet></x:ExcelWorksheets></x:ExcelWorkbook></xml><![endif]-->\n");
            excelContent.append("<style>td{border:1px solid #ccc; padding:5px;} th{background-color:#4472C4; color:white; font-weight:bold; border:1px solid #ccc; padding:5px;}</style>\n");
            excelContent.append("</head>\n<body>\n");
            excelContent.append("<table>\n");
            
            excelContent.append("<tr>\n");
            excelContent.append("<th>ID</th>\n");
            excelContent.append("<th>ID Venta</th>\n");
            excelContent.append("<th>ID Producto</th>\n");
            excelContent.append("<th>Descripción</th>\n");
            excelContent.append("<th>Cantidad</th>\n");
            excelContent.append("<th>Precio Unitario</th>\n");
            excelContent.append("</tr>\n");
            
            for (DetalleVenta d : detalles) {
                excelContent.append("<tr>\n");
                excelContent.append("<td>").append(d.getId_detalleV()).append("</td>\n");
                excelContent.append("<td>").append(d.getId_venta()).append("</td>\n");
                excelContent.append("<td>").append(d.getId_producto()).append("</td>\n");
                excelContent.append("<td>").append(d.getDescripcion() != null ? d.getDescripcion() : "").append("</td>\n");
                excelContent.append("<td>").append(d.getCantidad_productos() != null ? d.getCantidad_productos() : 0).append("</td>\n");
                excelContent.append("<td>").append(d.getPrecio_unitario() != null ? d.getPrecio_unitario() : 0.0).append("</td>\n");
                excelContent.append("</tr>\n");
            }
            
            excelContent.append("</table>\n");
            excelContent.append("</body>\n</html>");
            
            outputStream.write(excelContent.toString().getBytes("UTF-8"));
            outputStream.close();
            facesContext.responseComplete();
            
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al exportar Excel: " + e.getMessage()));
        }
    }
    
    private String escapePDF(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                 .replace("(", "\\(")
                 .replace(")", "\\)")
                 .replace("\n", "\\n")
                 .replace("\r", "\\r");
    }
}
