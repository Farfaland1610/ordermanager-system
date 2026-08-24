package com.ordermanager.client;

import com.ordermanager.dao.OrdenDAO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class OrderTcpClient {

    private static final String PAYFLOW_HOST = "127.0.0.1";
    private static final int PAYFLOW_PORT = 8080;
    private final OrdenDAO ordenDAO = new OrdenDAO();

    public boolean procesarPagoTcp(String idOrden, double monto, String metodoPago) {
        // 1. Registrar primero la orden localmente usando tu método existente
        ordenDAO.guardarPedido(idOrden, monto);

        System.out.println("📡 Conectando con PayFlow (" + PAYFLOW_HOST + ":" + PAYFLOW_PORT + ")...");

        try (Socket socket = new Socket(PAYFLOW_HOST, PAYFLOW_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            String mensajePago = String.format("PAGO|%s|%.2f|%s", idOrden, monto, metodoPago);
            out.println(mensajePago);

            String respuesta = in.readLine();
            if (respuesta != null && respuesta.startsWith("APROBADO")) {
                ordenDAO.actualizarEstado(idOrden, "PAGADO");
                System.out.println("✅ [PayFlow] Pago APROBADO. Estado: PAGADO");
                return true;
            }

        } catch (IOException e) {
            System.out.println("\n⚠️ [PayFlow offline] No se pudo conectar con el servidor de pagos en línea.");
            System.out.println("💡 Sistema de contingencia activado.");

            Scanner sc = new Scanner(System.in);
            System.out.print("👉 ¿Desea abonar en EFECTIVO al momento de la entrega? (S/N): ");
            String resp = sc.nextLine().trim();

            if (resp.equalsIgnoreCase("S")) {
                ordenDAO.actualizarEstado(idOrden, "EFECTIVO_PENDIENTE");
                System.out.println("💵 [DB] Orden " + idOrden + " actualizada a: EFECTIVO_PENDIENTE (Cobro en entrega).");
                return true;
            } else {
                ordenDAO.actualizarEstado(idOrden, "CANCELADO");
                System.out.println("❌ Orden cancelada por falta de medio de pago.");
                return false;
            }
        }
        return false;
    }
}