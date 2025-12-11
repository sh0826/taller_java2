/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author marce
 */
public class Boleta {
    private int id_boleta, cantidad_boletos, id_usuario, id_evento;
    private double precio_boleta;
    private Evento even;
    private Usuario usuario;

    public int getId_boleta() {
        return id_boleta;
    }

    public void setId_boleta(int id_boleta) {
        this.id_boleta = id_boleta;
    }
    
    public int getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(int id_usuario) {
        this.id_usuario = id_usuario;
    }
    
    public int getCantidad_boletos() {
        return cantidad_boletos;
    }

    public void setCantidad_boletos(int cantidad_boletos) {
        this.cantidad_boletos = cantidad_boletos;
    }



    public int getId_evento() {
        return id_evento;
    }

    public void setId_evento(int id_evento) {
        this.id_evento = id_evento;
    }

    public double getPrecio_boleta() {
        return precio_boleta;
    }

    public void setPrecio_boleta(double precio_boleta) {
        this.precio_boleta = precio_boleta;
    }

    public Evento getEven() {
        return even;
    }

    public void setEven(Evento even) {
        this.even = even;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
