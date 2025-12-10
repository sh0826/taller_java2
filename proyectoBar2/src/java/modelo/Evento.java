/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.sql.Time;
import java.util.Date;

/**
 *
 * @author marce
 */
public class Evento {
    private int id_evento, capacidad_maxima;
    private double precio_boleta;
    private String nombre_evento, descripcion, imagen;
    private Date fecha;
    private Time hora_inicio;

    public double getPrecio_boleta() {
        return precio_boleta;
    }

    public void setPrecio_boleta(double precio_boleta) {
        this.precio_boleta = precio_boleta;
    }


    public int getId_evento() {
        return id_evento;
    }

    public void setId_evento(int id_evento) {
        this.id_evento = id_evento;
    }

    public int getCapacidad_maxima() {
        return capacidad_maxima;
    }

    public void setCapacidad_maxima(int capacidad_maxima) {
        this.capacidad_maxima = capacidad_maxima;
    }



    public String getNombre_evento() {
        return nombre_evento;
    }

    public void setNombre_evento(String nombre_evento) {
        this.nombre_evento = nombre_evento;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Time getHora_inicio() {
        return hora_inicio;
    }

    public void setHora_inicio(Time hora_inicio) {
        this.hora_inicio = hora_inicio;
    }
    
    // Método adicional para aceptar java.util.Date y convertirlo a java.sql.Time
    public void setHora_inicio(Date date) {
        if (date != null) {
            this.hora_inicio = new Time(date.getTime());
        } else {
            this.hora_inicio = null;
        }
    }
    
}
