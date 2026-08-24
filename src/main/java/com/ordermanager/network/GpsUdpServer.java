package com.ordermanager.network;

import com.ordermanager.dao.OrdenDAO;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;

public class GpsUdpServer {

    private static final int UDP_PORT = 9090;
    private final OrdenDAO ordenDAO = new OrdenDAO();

    public void iniciarServidor() {
        System.out.println("============================================================");
        System.out.println("[OrderManager] Servidor UDP de Rastreo iniciado.");
        System.out.println("Escuchando datagramas GPS en puerto " + UDP_PORT + "...");
        System.out.println("============================================================");

        try (DatagramSocket socket = new DatagramSocket(UDP_PORT)) {
            byte[] buffer = new byte[1024];

            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                String mensaje = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8).trim();
                String[] partes = mensaje.split("\\|");

                if ("GPS".equals(partes[0]) && partes.length >= 4) {
                    String orderId = partes[1];
                    double lat = Double.parseDouble(partes[2]);
                    double lng = Double.parseDouble(partes[3]);

                    System.out.printf("📥 [UDP Packet] De: %s -> Pedido: %s | Lat: %.5f, Lng: %.5f%n",
                            packet.getSocketAddress(), orderId, lat, lng);

                    ordenDAO.actualizarGps(orderId, lat, lng);
                } else {
                    System.out.println("⚠️ Paquete UDP descartado (formato inválido): " + mensaje);
                }
            }
        } catch (Exception e) {
            System.out.println("🔴 Servidor UDP detenido: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new GpsUdpServer().iniciarServidor();
    }
}