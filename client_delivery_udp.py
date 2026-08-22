import socket
import time

SERVER_HOST = "127.0.0.1"
SERVER_PORT = 9090

def simular_repartidor(order_id):
    """Simula el movimiento del repartidor enviando paquetes UDP."""
    # 1. Crear Socket UDP
    udp_socket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    
    # Coordenada inicial (ej. Asunción / Ñu Guazú)
    lat_actual = -25.28610
    lng_actual = -57.64700

    print(f"🛵 Repartidor inició carrera para el pedido {order_id}.")
    print("📡 Transmitiendo señal GPS vía UDP...")

    try:
        for i in range(1, 11):  # Simula 10 actualizaciones de coordenadas
            # Avanzar ligeramente la posición
            lat_actual += 0.00015
            lng_actual += 0.00010
            
            # Formato del paquete: "GPS|ORD-101|-25.28610|-57.64700"
            mensaje = f"GPS|{order_id}|{lat_actual:.5f}|{lng_actual:.5f}"
            
            # sendto envía directamente el datagrama UDP sin connect()
            udp_socket.sendto(mensaje.encode('utf-8'), (SERVER_HOST, SERVER_PORT))
            print(f"📤 [UDP Enviado #{i}]: {mensaje}")
            
            time.sleep(2)  # Pausa de 2 segundos entre transmisiones
            
        print("✅ Repartidor llegó al destino. Fin de emisión GPS.")

    except KeyboardInterrupt:
        print("\n🔴 Emisión GPS cancelada por el repartidor.")
    finally:
        udp_socket.close()

if __name__ == "__main__":
    id_orden = input("Ingrese ID de Pedido a rastrear (ej. ORD-101): ") or "ORD-101"
    simular_repartidor(id_orden)