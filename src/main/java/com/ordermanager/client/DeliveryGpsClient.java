package com.ordermanager.client;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class DeliveryGpsClient {

    private static final String SERVER_HOST = "127.0.0.1";
    private static final int SERVER_PORT = 9090;

    public void simularRepartidor(String orderId) {
        double latActual = -25.28610;
        double lngActual = -57.64700;

        System.out.println("🛵 Repartidor inició carrera para el pedido " + orderId + ".");
        System.out.println("📡 Transmitiendo señal GPS vía UDP...");

        try (DatagramSocket socket = new DatagramSocket()) {
            InetAddress address = InetAddress.getByName(SERVER_HOST);

            for (int i = 1; i <= 10; i++) {
                latActual += 0.00015;
                lngActual += 0.00010;

                String mensaje = String.format("GPS|%s|%.5f|%.5f", orderId, latActual, lngActual);
                byte[] buffer = mensaje.getBytes(StandardCharsets.UTF_8);

                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, SERVER_PORT);
                socket.send(packet);

                System.out.printf("📤 [UDP Enviado #%d]: %s%n", i, mensaje);
                Thread.sleep(2000);
            }

            System.out.println("✅ Repartidor llegó al destino. Fin de emisión GPS.");

        } catch (InterruptedException e) {
            System.out.println("\n🔴 Emisión GPS cancelada por el repartidor.");
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            System.out.println("❌ Error enviando datagrama UDP: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese ID de Pedido a rastrear (ej. ORD-101): ");
        String idOrden = scanner.nextLine();
        if (idOrden.trim().isEmpty()) idOrden = "ORD-101";

        new DeliveryGpsClient().simularRepartidor(idOrden);
    }
}