package beans;

import dao.ProductoDAO;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import modelo.Producto;
import org.primefaces.model.file.UploadedFile;


@Named
@ViewScoped
public class ProductoBean implements Serializable {

    private Producto producto = new Producto();
    private List<Producto> lista;
    private List<Producto> listaFiltrada;
    private String filtroNombre = "";
    private String filtroTipo = "";
    private ProductoDAO dao = new ProductoDAO();
    private UploadedFile archivoImagen;

    public UploadedFile getArchivoImagen() {
    return archivoImagen;
    }

    public void setArchivoImagen(UploadedFile archivoImagen) {
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
            if (producto.getId_producto() > 0) {
                dao.modificar(producto);
            } else {
                dao.insertar(producto);
            }

            producto = new Producto(); 
            actualizarLista();

        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private void actualizarLista() {
        try {
            listaFiltrada = dao.listar();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
public void exportarPDF() {
    System.out.println("Exportando PDF...");
    // Aquí colocarás el código real para generar el PDF
}

public void exportarExcel() {
    System.out.println("Exportando Excel...");
    // Aquí colocarás el código real para generar el Excel
}

}
