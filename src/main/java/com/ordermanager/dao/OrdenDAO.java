package com.ordermanager.dao;

import com.ordermanager.db.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class OrdenDAO {

    public void guardarPedido(String idOrden, double monto) {
        String query = "INSERT INTO ordenes (id_orden, monto, estado) VALUES (?, ?, 'PENDIENTE') ON CONFLICT (id_orden) DO NOTHING;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, idOrden);
            stmt.setDouble(2, monto);
            stmt.executeUpdate();
            System.out.println("🗄️ [DB] Orden " + idOrden + " registrada localmente como PENDIENTE.");
        } catch (Exception e) {
            System.out.println("⚠️ [DB Error] No se pudo guardar en Postgres: " + e.getMessage());
        }
    }

    public void actualizarEstado(String idOrden, String estado) {
        String query = "UPDATE ordenes SET estado = ? WHERE id_orden = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, estado);
            stmt.setString(2, idOrden);
            stmt.executeUpdate();
            System.out.println("🗄️ [DB] Estado de " + idOrden + " actualizado a: " + estado);
        } catch (Exception e) {
            System.out.println("⚠️ [DB Error] No se pudo actualizar estado: " + e.getMessage());
        }
    }

    public void actualizarGps(String idOrden, double lat, double lng) {
        String query = "UPDATE ordenes SET latitud = ?, longitud = ? WHERE id_orden = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setDouble(1, lat);
            stmt.setDouble(2, lng);
            stmt.setString(3, idOrden);
            stmt.executeUpdate();
            System.out.printf("🗄️ [DB] Posición GPS de %s guardada: (%.5f, %.5f)%n", idOrden, lat, lng);
        } catch (Exception e) {
            System.out.println("⚠️ [DB Error] Error guardando GPS: " + e.getMessage());
        }
    }
}