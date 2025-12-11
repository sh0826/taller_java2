package beans;

import dao.ProductoDAO;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.context.ExternalContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import modelo.Producto;


@Named
@ViewScoped
public class ProductoBean implements Serializable {

    private Producto producto = new Producto();
    private List<Producto> lista;
    private List<Producto> listaFiltrada;
    private String filtroNombre = "";
    private String filtroTipo = "";
    private ProductoDAO dao = new ProductoDAO();
    private Part archivoImagen;

    public Part getArchivoImagen() {
        return archivoImagen;
    }

    public void setArchivoImagen(Part archivoImagen) {
        this.archivoImagen = archivoImagen;
    }


    public Producto getProducto() { 
        return producto; 
    }
    public void setProducto(Producto producto) { 
        this.producto = producto; 
    }

    public String getFiltroNombre() { 
        return filtroNombre; 
    }
    public void setFiltroNombre(String filtroNombre) { 
        this.filtroNombre = filtroNombre; 
    }

    public String getFiltroTipo() { 
        return filtroTipo; 
    }
    public void setFiltroTipo(String filtroTipo) { 
        this.filtroTipo = filtroTipo; 
    }

    public List<Producto> getListaFiltrada() {
        if (listaFiltrada == null) {  
            try {
                listaFiltrada = dao.listar();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return listaFiltrada;
    }

    public void setListaFiltrada(List<Producto> listaFiltrada) {
        this.listaFiltrada = listaFiltrada;
    }

    public List<Producto> getLista() {
        try {
            lista = dao.listar();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    public void guardar() {
        try {
            // Procesar imagen si se subió un archivo
            if (archivoImagen != null && archivoImagen.getSize() > 0) {
                String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
                
                // Obtener nombre del archivo
                String nombreArchivo = getFileName(archivoImagen);
                String extension = ".png"; // Extensión por defecto
                
                if (nombreArchivo != null && !nombreArchivo.isEmpty()) {
                    if (nombreArchivo.contains(".")) {
                        extension = nombreArchivo.substring(nombreArchivo.lastIndexOf("."));
                    }
                } else {
                    // Si no se puede obtener el nombre, intentar obtenerlo del content-type
                    String contentType = archivoImagen.getContentType();
                    if (contentType != null) {
                        if (contentType.contains("jpeg") || contentType.contains("jpg")) {
                            extension = ".jpg";
                        } else if (contentType.contains("png")) {
                            extension = ".png";
                        } else if (contentType.contains("gif")) {
                            extension = ".gif";
                        }
                    }
                }
                
                // Crear directorio dentro de web/ para que sea accesible desde el navegador
                String imgPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/ImgProductos");
                if (imgPath == null) {
                    // Fallback: usar el path raíz + ImgProductos
                    imgPath = path + "ImgProductos";
                }
                
                File dir = new File(imgPath);
                if (!dir.exists()) {
                    boolean creado = dir.mkdirs();
                    if (!creado) {
                        throw new IOException("No se pudo crear el directorio ImgProductos");
                    }
                }
                
                // Generar nombre único para el archivo
                String nombreFinal = System.currentTimeMillis() + extension;
                File archivo = new File(dir, nombreFinal);
                
                // Guardar archivo
                try (InputStream input = archivoImagen.getInputStream();
                     FileOutputStream output = new FileOutputStream(archivo)) {
                    
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = input.read(buffer)) != -1) {
                        output.write(buffer, 0, bytesRead);
                    }
                }
                
                // Guardar ruta en el producto (ruta absoluta desde el contexto)
                producto.setImagen("/ImgProductos/" + nombreFinal);
            }
            
            // Si no hay imagen y es un producto existente, mantener la imagen actual
            if (producto.getId_producto() > 0 && (producto.getImagen() == null || producto.getImagen().isEmpty())) {
                // Obtener la imagen actual del producto
                try {
                    List<Producto> productos = dao.listar();
                    for (Producto prod : productos) {
                        if (prod.getId_producto() == producto.getId_producto()) {
                            producto.setImagen(prod.getImagen());
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            
            if (producto.getId_producto() > 0) {
                dao.modificar(producto);
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Producto modificado correctamente"));
            } else {
                dao.insertar(producto);
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Producto guardado correctamente"));
            }

            producto = new Producto();
            archivoImagen = null;
            actualizarLista();

        } catch (IOException e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al guardar la imagen: " + e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al guardar el producto: " + e.getMessage()));
        }
    }
    
    private String getFileName(Part part) {
        try {
            String contentDisp = part.getHeader("content-disposition");
            if (contentDisp != null) {
                String[] tokens = contentDisp.split(";");
                for (String token : tokens) {
                    if (token.trim().startsWith("filename")) {
                        String fileName = token.substring(token.indexOf("=") + 2, token.length() - 1);
                        // Limpiar el nombre del archivo (puede venir con ruta completa)
                        if (fileName.contains("\\")) {
                            fileName = fileName.substring(fileName.lastIndexOf("\\") + 1);
                        }
                        if (fileName.contains("/")) {
                            fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
                        }
                        return fileName;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void editar(Producto p) {
        this.producto = p;
    }

    public void eliminar(int id) {
        try {
            dao.eliminar(id);
            actualizarLista();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void filtrar() {
        try {
            List<Producto> todos = dao.listar();
            listaFiltrada = new ArrayList<>();

            for (Producto p : todos) {

                boolean coincide = true;

                if (filtroNombre != null && !filtroNombre.isEmpty()) {
                    coincide &= p.getNombre().toLowerCase().contains(filtroNombre.toLowerCase());
                }

                if (filtroTipo != null && !filtroTipo.isEmpty()) {
                    coincide &= p.getTipo_producto().equalsIgnoreCase(filtroTipo);
                }

                if (coincide) {
                    listaFiltrada.add(p);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();    
        }
    }
    
    public void limpiarFiltros() {
        filtroNombre = "";
        filtroTipo = "";
        actualizarLista();
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
            response.setHeader("Content-Disposition", "attachment;filename=Productos.pdf");
            
            OutputStream outputStream = response.getOutputStream();
            
            List<Producto> productos = getListaFiltrada();
            
            // Construir contenido de texto primero
            StringBuilder textContent = new StringBuilder();
            textContent.append("BT\n/F1 12 Tf\n50 750 Td\n");
            textContent.append("(LISTA DE PRODUCTOS) Tj\n");
            textContent.append("0 -25 Td\n/F1 10 Tf\n");
            textContent.append("(Fecha: ").append(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date())).append(") Tj\n");
            textContent.append("0 -30 Td\n");
            textContent.append("(ID    Nombre              Tipo            Stock    Precio) Tj\n");
            textContent.append("0 -20 Td\n");
            
            for (Producto p : productos) {
                String nombre = p.getNombre() != null ? (p.getNombre().length() > 18 ? p.getNombre().substring(0, 18) : p.getNombre()) : "";
                String tipo = p.getTipo_producto() != null ? (p.getTipo_producto().length() > 13 ? p.getTipo_producto().substring(0, 13) : p.getTipo_producto()) : "";
                String line = String.format("%-5d %-18s %-13s %-10d %-15.2f",
                    p.getId_producto(), nombre, tipo, p.getStock(), p.getPrecio_unitario());
                textContent.append("(").append(escapePDF(line)).append(") Tj\n");
                textContent.append("0 -20 Td\n");
            }
            textContent.append("ET\n");
            
            // Generar PDF con offsets calculados
            List<Integer> offsets = new ArrayList<>();
            StringBuilder pdf = new StringBuilder();
            
            // PDF Header
            pdf.append("%PDF-1.4\n");
            
            // Objeto 1: Catalog
            offsets.add(pdf.length());
            pdf.append("1 0 obj\n<<\n/Type /Catalog\n/Pages 2 0 R\n>>\nendobj\n");
            
            // Objeto 2: Pages
            offsets.add(pdf.length());
            pdf.append("2 0 obj\n<<\n/Type /Pages\n/Kids [3 0 R]\n/Count 1\n>>\nendobj\n");
            
            // Objeto 3: Page
            offsets.add(pdf.length());
            pdf.append("3 0 obj\n<<\n/Type /Page\n/Parent 2 0 R\n/MediaBox [0 0 612 792]\n");
            pdf.append("/Contents 4 0 R\n/Resources <<\n/Font <<\n/F1 5 0 R\n>>\n>>\n>>\nendobj\n");
            
            // Objeto 4: Contents
            offsets.add(pdf.length());
            pdf.append("4 0 obj\n<<\n/Length ").append(textContent.length()).append("\n>>\nstream\n");
            pdf.append(textContent.toString());
            pdf.append("endstream\nendobj\n");
            
            // Objeto 5: Font
            offsets.add(pdf.length());
            pdf.append("5 0 obj\n<<\n/Type /Font\n/Subtype /Type1\n/BaseFont /Helvetica\n>>\nendobj\n");
            
            // xref
            int xrefPos = pdf.length();
            pdf.append("xref\n0 ").append(offsets.size() + 1).append("\n");
            pdf.append("0000000000 65535 f \n");
            for (int offset : offsets) {
                pdf.append(String.format("%010d 00000 n \n", offset));
            }
            
            // trailer
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
    
    private String escapePDF(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                 .replace("(", "\\(")
                 .replace(")", "\\)")
                 .replace("\n", "\\n")
                 .replace("\r", "\\r");
    }
    
    public void exportarExcel() {
        try {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            ExternalContext externalContext = facesContext.getExternalContext();
            HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();
            
            response.reset();
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment;filename=Productos.xls");
            
            OutputStream outputStream = response.getOutputStream();
            
            // Generar contenido Excel (formato HTML que Excel puede abrir)
            StringBuilder excelContent = new StringBuilder();
            excelContent.append("<html xmlns:o='urn:schemas-microsoft-com:office:office' ");
            excelContent.append("xmlns:x='urn:schemas-microsoft-com:office:excel' ");
            excelContent.append("xmlns='http://www.w3.org/TR/REC-html40'>\n");
            excelContent.append("<head>\n");
            excelContent.append("<meta http-equiv='Content-Type' content='text/html; charset=UTF-8'>\n");
            excelContent.append("<!--[if gte mso 9]><xml><x:ExcelWorkbook><x:ExcelWorksheets><x:ExcelWorksheet>");
            excelContent.append("<x:Name>Productos</x:Name><x:WorksheetOptions><x:DisplayGridlines/></x:WorksheetOptions>");
            excelContent.append("</x:ExcelWorksheet></x:ExcelWorksheets></x:ExcelWorkbook></xml><![endif]-->\n");
            excelContent.append("<style>td{border:1px solid #ccc; padding:5px;}</style>\n");
            excelContent.append("</head>\n<body>\n");
            excelContent.append("<table>\n");
            
            // Encabezados
            excelContent.append("<tr style='background-color:#4472C4; color:white; font-weight:bold;'>\n");
            excelContent.append("<td>ID</td>\n");
            excelContent.append("<td>Nombre</td>\n");
            excelContent.append("<td>Tipo</td>\n");
            excelContent.append("<td>Stock</td>\n");
            excelContent.append("<td>Precio</td>\n");
            excelContent.append("</tr>\n");
            
            // Datos
            List<Producto> productos = getListaFiltrada();
            for (Producto p : productos) {
                excelContent.append("<tr>\n");
                excelContent.append("<td>").append(p.getId_producto()).append("</td>\n");
                excelContent.append("<td>").append(p.getNombre() != null ? p.getNombre() : "").append("</td>\n");
                excelContent.append("<td>").append(p.getTipo_producto() != null ? p.getTipo_producto() : "").append("</td>\n");
                excelContent.append("<td>").append(p.getStock()).append("</td>\n");
                excelContent.append("<td>").append(p.getPrecio_unitario()).append("</td>\n");
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
    
}
