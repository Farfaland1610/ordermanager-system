import socket
import psycopg2

# Configuración de conexión a PostgreSQL
DB_CONFIG = {
    "dbname": "bd_order_manager",
    "user": "postgres",
    "password": "your_password",  # Cambia por tu contraseña de Postgres
    "host": "localhost",
    "port": "5432"
}

PAYFLOW_HOST = "127.0.0.1"
PAYFLOW_PORT = 8080

def guardar_pedido_db(order_id, monto):
    """Guarda una nueva orden en estado PENDIENTE en Postgres."""
    try:
        conn = psycopg2.connect(**DB_CONFIG)
        cursor = conn.cursor()
        query = """
            INSERT INTO ordenes (id_orden, monto, estado) 
            VALUES (%s, %s, 'PENDIENTE')
            ON CONFLICT (id_orden) DO NOTHING;
        """
        cursor.execute(query, (order_id, monto))
        conn.commit()
        cursor.close()
        conn.close()
        print(f"🗄️ [DB] Orden {order_id} registrada localmente como PENDIENTE.")
    except Exception as e:
        print(f"⚠️ [DB Error] No se pudo guardar en Postgres: {e}")

def actualizar_estado_db(order_id, estado):
    """Actualiza el estado de la orden tras la respuesta TCP."""
    try:
        conn = psycopg2.connect(**DB_CONFIG)
        cursor = conn.cursor()
        cursor.execute("UPDATE ordenes SET estado = %s WHERE id_orden = %s;", (estado, order_id))
        conn.commit()
        cursor.close()
        conn.close()
        print(f"🗄️ [DB] Estado de {order_id} actualizado a: {estado}")
    except Exception as e:
        print(f"⚠️ [DB Error] No se pudo actualizar estado: {e}")

def procesar_pago_tcp(order_id, monto, metodo="TARJETA_VISA"):
    """Se conecta por TCP a PayFlow para solicitar la transacción."""
    guardar_pedido_db(order_id, monto)
    
    print(f"\n📡 Conectando con PayFlow ({PAYFLOW_HOST}:{PAYFLOW_PORT})...")
    try:
        # 1. Crear Socket TCP
        client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        client_socket.connect((PAYFLOW_HOST, PAYFLOW_PORT))
        
        # 2. Enviar mensaje de pago: "PAGO|ORD-101|50000|TARJETA_VISA"
        mensaje = f"PAGO|{order_id}|{monto}|{metodo}"
        client_socket.sendall(mensaje.encode('utf-8'))
        print(f"📤 Mensaje enviado a PayFlow: {mensaje}")
        
        # 3. Esperar la respuesta del Servidor
        respuesta = client_socket.recv(1024).decode('utf-8')
        print(f"📥 Respuesta de PayFlow: {respuesta}")
        
        # 4. Parsear respuesta y actualizar Postgres
        partes = respuesta.split('|')
        if partes[0] == "APROBADO":
            actualizar_estado_db(order_id, "PAGADO")
        else:
            actualizar_estado_db(order_id, "RECHAZADO")
            
        client_socket.close()
        
    except ConnectionRefusedError:
        print("❌ Error: No se pudo conectar a PayFlow. ¿El servidor server_payment_tcp.py está encendido?")

if __name__ == "__main__":
    print("--- SISTEMA DE PEDIDOS (OrderManager) ---")
    id_pedido = input("Ingrese ID de Pedido (ej. ORD-101): ") or "ORD-101"
    monto_input = float(input("Ingrese el Monto total (ej. 50000): ") or 50000)
    
    procesar_pago_tcp(id_pedido, monto_input)