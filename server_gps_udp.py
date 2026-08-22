import socket
import psycopg2

HOST = "127.0.0.1"
UDP_PORT = 9090

DB_CONFIG = {
    "dbname": "bd_order_manager",
    "user": "postgres",
    "password": "admin",
    "host": "localhost",
    "port": "5432"
}

def actualizar_gps_db(order_id, lat, lng):
    """Guarda la última ubicación del repartidor en Postgres."""
    try:
        conn = psycopg2.connect(**DB_CONFIG)
        cursor = conn.cursor()
        query = """
            UPDATE ordenes 
            SET latitud = %s, longitud = %s 
            WHERE id_orden = %s;
        """
        cursor.execute(query, (lat, lng, order_id))
        conn.commit()
        cursor.close()
        conn.close()
        print(f"🗄️ [DB] Posición GPS de {order_id} guardada: ({lat}, {lng})")
    except Exception as e:
        print(f"⚠️ [DB Error] Error guardando GPS: {e}")

def iniciar_servidor_udp():
    """Inicia el socket UDP sin conexión para escuchar datagramas del repartidor."""
    # AF_INET = IPv4, SOCK_DGRAM = UDP
    udp_socket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    udp_socket.bind((HOST, UDP_PORT))

    print("=" * 60)
    print(f"📡 [OrderManager] Servidor UDP de Rastreo iniciado.")
    print(f"📍 Escuchando datagramas GPS en puerto {UDP_PORT}...")
    print("=" * 60)

    try:
        while True:
            # recvfrom recibe el paquete sin necesidad de haber establecido conexión previa
            data, addr = udp_socket.recvfrom(1024)
            mensaje = data.decode('utf-8')
            
            # Formato esperado: "GPS|ORD-101|-25.2861|-57.6470"
            partes = mensaje.split('|')
            if partes[0] == "GPS" and len(partes) >= 4:
                order_id = partes[1]
                lat = float(partes[2])
                lng = float(partes[3])
                
                print(f"📥 [UDP Packet] De: {addr} -> Pedido: {order_id} | Lat: {lat}, Lng: {lng}")
                actualizar_gps_db(order_id, lat, lng)
            else:
                print(f"⚠️ Paquete UDP descartado (formato inválido): {mensaje}")

    except KeyboardInterrupt:
        print("\n🔴 Servidor UDP detenido.")
    finally:
        udp_socket.close()

if __name__ == "__main__":
    iniciar_servidor_udp()