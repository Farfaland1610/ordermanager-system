package com.ordermanager.client;

import com.ordermanager.config.DatabaseConfig;
import com.ordermanager.dao.OrdenDAO;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class OrderTcpClient {

    private final OrdenDAO ordenDAO = new OrdenDAO();

    public void procesarPagoTcp(String idOrden, double monto, String metodo) {
        ordenDAO.guardarPedido(idOrden, monto);

        String host = DatabaseConfig.getPayflowHost();
        int port = DatabaseConfig.getPayflowPort();

        System.out.println("\n📡 Conectando con PayFlow (" + host + ":" + port + ")...");

        try (Socket socket = new Socket(host, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            String mensaje = String.format("PAGO|%s|%.0f|%s", idOrden, monto, metodo);
            out.println(mensaje);
            System.out.println("📤 Mensaje enviado a PayFlow: " + mensaje);

            String respuesta = in.readLine();
            System.out.println("📥 Respuesta de PayFlow: " + respuesta);

            if (respuesta != null && respuesta.startsWith("APROBADO")) {
                ordenDAO.actualizarEstado(idOrden, "PAGADO");
            } else {
                ordenDAO.actualizarEstado(idOrden, "RECHAZADO");
            }

        } catch (Exception e) {
            System.out.println("❌ Error: No se pudo conectar a PayFlow. ¿El servidor está encendido?");
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("--- SISTEMA DE PEDIDOS (OrderManager) ---");

        System.out.print("Ingrese ID de Pedido (ej. ORD-101): ");
        String idPedido = scanner.nextLine();
        if (idPedido.trim().isEmpty()) idPedido = "ORD-101";

        System.out.print("Ingrese el Monto total (ej. 50000): ");
        String montoStr = scanner.nextLine();
        double monto = montoStr.trim().isEmpty() ? 50000 : Double.parseDouble(montoStr);

        OrderTcpClient client = new OrderTcpClient();
        client.procesarPagoTcp(idPedido, monto, "TARJETA_VISA");
    }
}