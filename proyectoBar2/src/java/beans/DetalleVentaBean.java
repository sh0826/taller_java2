package beans;

import dao.DetalleVentaDAO;
import dao.ProductoDAO;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import modelo.DetalleVenta;
import modelo.Producto;

@ManagedBean
@ViewScoped
public class DetalleVentaBean {

    private DetalleVenta detalle = new DetalleVenta();
    private List<DetalleVenta> lista;
    private DetalleVentaDAO dao = new DetalleVentaDAO();
    private List<Producto> listaProductos;
    private ProductoDAO productoDAO = new ProductoDAO();

    public DetalleVenta getDetalle() { return detalle; }
    public void setDetalle(DetalleVenta detalle) { this.detalle = detalle; }

    public List<DetalleVenta> getLista() {
        try {
            lista = dao.listar();
        } catch(Exception e){ 
            e.printStackTrace(); 
        }
        return lista;
    }

    public List<Producto> getListaProductos() {
        try { listaProductos = productoDAO.listar(); }
        catch(Exception e){ e.printStackTrace(); }
        return listaProductos;
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
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public void editar(DetalleVenta d) { 
        this.detalle = d; 
    }

    public void eliminar(int id) {
        try { dao.eliminar(id); 
        } catch(Exception e){ 
            e.printStackTrace(); 
        }
    }
}
