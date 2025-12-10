/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import modelo.reservacion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author 2025
 */
public class reservacionDao {
    
    // Método para INSERTAR una nueva reservación en la base de datos
    public boolean insertar(reservacion res) {
        // Validación: Verificar que el objeto reservación no sea null
        if (res == null) {
            System.err.println("Error: La reservación es null");
            return false; // Retorna false si es null
        }
        
        // Validación: Verificar que la fecha de reservación no sea null
        if (res.getFecha_reservacion() == null) {
            System.err.println("Error: La fecha de reservación es null");
            return false;
        }
        
        // Validación: La cantidad de personas debe estar entre 1 y 80
        if (res.getCatindad_personas() <= 0 || res.getCatindad_personas() > 80) {
            System.err.println("Error: La cantidad de personas debe estar entre 1 y 80. Valor recibido: " + res.getCatindad_personas());
            return false;
        }
        
        // Validación: La cantidad de mesas debe estar entre 1 y 20
        if (res.getCantidad_mesas() <= 0 || res.getCantidad_mesas() > 20) {
            System.err.println("Error: La cantidad de mesas debe estar entre 1 y 20. Valor recibido: " + res.getCantidad_mesas());
            return false;
        }
        
        // Validación: La ocasión no puede ser null o vacía
        if (res.getOcasion() == null || res.getOcasion().trim().isEmpty()) {
            System.err.println("Error: La ocasión es null o vacía");
            return false;
        }
        
        // SQL con parámetros (?) para prevenir inyección SQL
        String sql = "INSERT INTO reservacion (cantidad_de_personas, cantidad_mesas, ocasion, fecha_reservacion) VALUES (?, ?, ?, ?)";
        
        Connection conn = null; // Variable para la conexión a la BD
        PreparedStatement ps = null; // Variable para la consulta preparada
        
        try {
            conn = ConnBD.conectar(); // Obtener conexión desde la clase ConnBD
            System.out.println("Conexión establecida: " + (conn != null));
            System.out.println("Autocommit: " + conn.getAutoCommit());
            
            // Asegurar que autocommit esté activado (cada SQL se ejecuta inmediatamente)
            if (!conn.getAutoCommit()) {
                conn.setAutoCommit(true);
            }
            
            ps = conn.prepareStatement(sql); // Preparar la consulta SQL
            
            // Asignar valores a los parámetros (?) del SQL
            ps.setInt(1, res.getCatindad_personas()); // Primer ? = cantidad de personas
            ps.setInt(2, res.getCantidad_mesas()); // Segundo ? = cantidad de mesas
            ps.setString(3, res.getOcasion()); // Tercer ? = ocasión
            ps.setDate(4, new Date(res.getFecha_reservacion().getTime())); // Cuarto ? = fecha (convierte java.util.Date a java.sql.Date)
            
            System.out.println("Ejecutando INSERT con valores:");
            System.out.println("  SQL: " + sql);
            System.out.println("  Personas: " + res.getCatindad_personas());
            System.out.println("  Mesas: " + res.getCantidad_mesas());
            System.out.println("  Ocasión: " + res.getOcasion());
            System.out.println("  Fecha: " + new Date(res.getFecha_reservacion().getTime()));
            
            int filas = ps.executeUpdate(); // Ejecutar INSERT y obtener número de filas afectadas
            System.out.println("Filas afectadas: " + filas);
            
            if (filas > 0) { // Si se insertó al menos una fila, fue exitoso
                System.out.println("Reservación insertada correctamente");
                return true; // Retorna true si se insertó correctamente
            } else {
                System.err.println("No se insertó ninguna fila");
                return false; // Retorna false si no se insertó nada
            }
            
        } catch (SQLException e) { // Captura errores de SQL
            System.err.println("Error SQL al insertar reservación: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState()); // Código de estado SQL
            System.err.println("Error Code: " + e.getErrorCode()); // Código de error del driver
            e.printStackTrace(); // Imprime el stack trace completo
            return false;
        } catch (Exception e) { // Captura cualquier otro error
            System.err.println("Error inesperado al insertar reservación: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally { // Este bloque SIEMPRE se ejecuta, haya o no error
            try {
                if (ps != null) {
                    ps.close(); // Cerrar PreparedStatement para liberar recursos
                }
                if (conn != null) {
                    conn.close(); // Cerrar conexión para liberar recursos
                }
            } catch (SQLException e) {
                System.err.println("Error al cerrar conexión: " + e.getMessage());
            }
        }
    }
    
    // Método para LISTAR todas las reservaciones de la base de datos
    public List<reservacion> listar() {
        List<reservacion> lista = new ArrayList<>(); // Lista vacía para almacenar las reservaciones
        String sql = "SELECT * FROM reservacion"; // SQL para obtener todos los registros
        
        // try-with-resources: Cierra automáticamente los recursos al terminar
        try (Connection conn = ConnBD.conectar(); // Obtener conexión
             PreparedStatement ps = conn.prepareStatement(sql); // Preparar consulta
             ResultSet rs = ps.executeQuery()) { // Ejecutar SELECT y obtener resultados
            
            // Recorrer cada fila del resultado
            while (rs.next()) { // rs.next() avanza a la siguiente fila, retorna false si no hay más
                reservacion res = new reservacion(); // Crear nuevo objeto reservación
                // Llenar el objeto con los datos de la fila actual
                res.setId_reservacion(rs.getInt("id_reservacion")); // Obtener ID de la columna
                res.setCatindad_personas(rs.getInt("cantidad_de_personas")); // Obtener cantidad de personas
                res.setCantidad_mesas(rs.getInt("cantidad_mesas")); // Obtener cantidad de mesas
                res.setOcasion(rs.getString("ocasion")); // Obtener ocasión
                res.setFecha_reservacion(rs.getDate("fecha_reservacion")); // Obtener fecha
                lista.add(res); // Agregar el objeto a la lista
            }
            
        } catch (SQLException e) { // Captura errores de SQL
            System.err.println("Error al listar reservaciones: " + e.getMessage());
        }
        
        return lista; // Retornar la lista con todas las reservaciones
    }
    
    // Método para BUSCAR una reservación por su ID
    public reservacion buscarPorId(int id) {
        String sql = "SELECT * FROM reservacion WHERE id_reservacion = ?"; // SQL con parámetro
        reservacion res = null; // Inicializar como null (si no se encuentra, retorna null)
        
        try (Connection conn = ConnBD.conectar(); // Obtener conexión
             PreparedStatement ps = conn.prepareStatement(sql)) { // Preparar consulta
            
            ps.setInt(1, id); // Asignar el ID al primer parámetro (?)
            ResultSet rs = ps.executeQuery(); // Ejecutar SELECT
            
            if (rs.next()) { // Si hay al menos una fila en el resultado
                res = new reservacion(); // Crear nuevo objeto
                // Llenar el objeto con los datos encontrados
                res.setId_reservacion(rs.getInt("id_reservacion"));
                res.setCatindad_personas(rs.getInt("cantidad_de_personas"));
                res.setCantidad_mesas(rs.getInt("cantidad_mesas"));
                res.setOcasion(rs.getString("ocasion"));
                res.setFecha_reservacion(rs.getDate("fecha_reservacion"));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al buscar reservación: " + e.getMessage());
        }
        
        return res; // Retorna el objeto encontrado o null si no existe
    }
    
    // Método para ACTUALIZAR una reservación existente
    public boolean actualizar(reservacion res) {
        // SQL UPDATE: actualiza los campos donde el ID coincida
        String sql = "UPDATE reservacion SET cantidad_de_personas = ?, cantidad_mesas = ?, ocasion = ?, fecha_reservacion = ? WHERE id_reservacion = ?";
        
        try (Connection conn = ConnBD.conectar(); // Obtener conexión
             PreparedStatement ps = conn.prepareStatement(sql)) { // Preparar consulta
            
            // Asignar valores a los parámetros del UPDATE
            ps.setInt(1, res.getCatindad_personas()); // Primer ? = cantidad de personas
            ps.setInt(2, res.getCantidad_mesas()); // Segundo ? = cantidad de mesas
            ps.setString(3, res.getOcasion()); // Tercer ? = ocasión
            ps.setDate(4, new Date(res.getFecha_reservacion().getTime())); // Cuarto ? = fecha
            ps.setInt(5, res.getId_reservacion()); // Quinto ? = ID (para el WHERE)
            
            int filas = ps.executeUpdate(); // Ejecutar UPDATE
            return filas > 0; // Retorna true si se actualizó al menos una fila
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar reservación: " + e.getMessage());
            return false;
        }
    }
    
    // Método para ELIMINAR una reservación por su ID
    public boolean eliminar(int id) {
        String sql = "DELETE FROM reservacion WHERE id_reservacion = ?"; // SQL DELETE con parámetro
        
        try (Connection conn = ConnBD.conectar(); // Obtener conexión
             PreparedStatement ps = conn.prepareStatement(sql)) { // Preparar consulta
            
            ps.setInt(1, id); // Asignar el ID al parámetro (?)
            int filas = ps.executeUpdate(); // Ejecutar DELETE
            return filas > 0; // Retorna true si se eliminó al menos una fila
            
        } catch (SQLException e) {
            System.err.println("Error al eliminar reservación: " + e.getMessage());
            return false;
        }
    }
}
