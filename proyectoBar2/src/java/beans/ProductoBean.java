package beans;

import dao.ProductoDAO;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import modelo.Producto;


@Named
@SessionScoped
@ManagedBean
@ViewScoped
public class ProductoBean {

    private Producto producto = new Producto();
    private List<Producto> lista;
      @Inject
    private ProductoDAO dao = new ProductoDAO();

    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }

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
            producto = new Producto(); // limpiar
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
