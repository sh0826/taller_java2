/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.util.Date;


/**
 *
 * @author 2025
 */
public class reservacion {
    private int id_reservacion,catindad_personas,cantidad_mesas;
    private int id_usuario; // ID del usuario que hace la reservación
    private String ocasion;
    private String nombre_usuario; // Nombre del usuario que hace la reservación
    private Date fecha_reservacion;

    public int getId_reservacion() {
        return id_reservacion;
    }

    public void setId_reservacion(int id_reservacion) {
        this.id_reservacion = id_reservacion;
    }

    public int getCatindad_personas() {
        return catindad_personas;
    }

    public void setCatindad_personas(int catindad_personas) {
        this.catindad_personas = catindad_personas;
    }

    public int getCantidad_mesas() {
        return cantidad_mesas;
    }

    public void setCantidad_mesas(int cantidad_mesas) {
        this.cantidad_mesas = cantidad_mesas;
    }

    public String getOcasion() {
        return ocasion;
    }

    public void setOcasion(String ocasion) {
        this.ocasion = ocasion;
    }

    public Date getFecha_reservacion() {
        return fecha_reservacion;
    }

    public void setFecha_reservacion(Date fecha_reservacion) {
        this.fecha_reservacion = fecha_reservacion;
    }

    public int getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(int id_usuario) {
        this.id_usuario = id_usuario;
    }

    public String getNombre_usuario() {
        return nombre_usuario;
    }

    public void setNombre_usuario(String nombre_usuario) {
        this.nombre_usuario = nombre_usuario;
    }
}
