# OrderManager - Sistema de Gestión de Pedidos y Delivery

Este repositorio contiene la implementación del sistema **OrderManager**, encargado de la logística operativa, procesamiento de pedidos con cobro TCP y recepción de coordenadas GPS en tiempo real vía UDP.

Obs: Primero se debe ejecutar 


## 🛠️ Tecnología y Arquitectura de Red
- **Lenguaje:** Python 3.8+
- **Protocolos de Transporte:**
  - **TCP (Cliente):** Se conecta activamente a `PayFlow` en el puerto `8080` para procesar cobros al confirmar pedidos.
  - **UDP (Servidor):** Mantiene un socket escuchando en el puerto `9090` para recibir la telemetría en tiempo real del repartidor.

## 📡 Protocolos de Aplicación (Mensajería)

1. **Cliente TCP (Cobro a PayFlow):**
   - **Solicitud enviada:** `PAGO|<id_orden>|<monto>|<metodo_pago>`
   - **Respuesta esperada:** `APROBADO|<id_transaccion>|<mensaje>`

2. **Servidor UDP (Rastreo GPS Repartidor):**
   - **Datagrama entrante:** `GPS|<id_orden>|<latitud>|<longitud>`
   - *Ejemplo:* `GPS|ORD-101|-25.2861|-57.6470`

## 🚀 Componentes y Ejecución

1. **Clonar el repositorio:**
   ```bash
   git clone [https://github.com/Farfaland1610/ordermanager-system.git](https://github.com/Farfaland1610/ordermanager-system.git)
   cd ordermanager-system