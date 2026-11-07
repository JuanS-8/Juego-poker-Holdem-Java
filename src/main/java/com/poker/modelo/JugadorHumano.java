package com.poker.modelo;

import com.poker.vista.ProveedorAccion;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JugadorHumano extends Jugador {
    private ProveedorAccion proveedor;

    public JugadorHumano(String id, String nombre, int fichas, ProveedorAccion proveedor) {
        super(id, nombre, fichas);
        this.proveedor = proveedor;
    }

    @Override
    public Accion decidirAccion(ContextoDecision contexto) {
        return proveedor.pedirAccion(contexto);
    }
}
