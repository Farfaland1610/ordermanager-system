package com.ordermanager;

import com.ordermanager.client.DeliveryGpsClient;
import com.ordermanager.client.OrderTcpClient;
import com.ordermanager.network.GpsUdpServer;

import java.util.Scanner;

public class MainApp {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("==================================================");
        System.out.println("   SISTEMA INTEGRADO DE PEDIDOS (OrderManager)");
        System.out.println("==================================================");

        // 1. Iniciar Servidor UDP en un hilo en segundo plano (Background)
        Thread udpThread = new Thread(() -> {
            GpsUdpServer udpServer = new GpsUdpServer();
            udpServer.iniciarServidor();
        });
        udpThread.setDaemon(true); // Se apaga automáticamente al salir de la app
        udpThread.start();

        // Breve pausa para asegurar que el socket UDP levante
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}

        // 2. Procesar Cobro de la Orden por TCP
        System.out.println("\n--- PASO 1: CREAR Y COBRAR PEDIDO ---");
        System.out.print("👉 Ingrese ID de Pedido (ej. ORD-300): ");
        String idOrden = scanner.nextLine().trim();
        if (idOrden.isEmpty()) idOrden = "ORD-300";

        System.out.print("👉 Ingrese el Monto (ej. 85000): ");
        String montoStr = scanner.nextLine().trim();
        double monto = montoStr.isEmpty() ? 85000 : Double.parseDouble(montoStr);

        OrderTcpClient orderClient = new OrderTcpClient();
        orderClient.procesarPagoTcp(idOrden, monto, "TARJETA_VISA");

        // 3. Simular el Rastreo GPS del Repartidor vía UDP
        System.out.println("\n--- PASO 2: RASTREO GPS EN TIEMPO REAL ---");
        System.out.print(" Presione [ENTER] para iniciar el envío GPS del repartidor...");
        scanner.nextLine();

        DeliveryGpsClient deliveryClient = new DeliveryGpsClient();
        deliveryClient.simularRepartidor(idOrden);

        System.out.println("\n==================================================");
        System.out.println(" Flujo E2E completado. Podés verificar pgAdmin.");
        System.out.println("==================================================");
    }
}