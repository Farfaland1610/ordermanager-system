package com.ordermanager.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Orden {
    private String idOrden;
    private double monto;
    private String estado;
    private Double latitud;
    private Double longitud;
}