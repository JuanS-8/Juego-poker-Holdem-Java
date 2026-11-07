package com.poker.app;

import com.poker.controlador.ControladorJuego;
import com.poker.dao.ExcepcionAccesoDatos;
import com.poker.dao.jdbc.*;
import com.poker.evaluador.EvaluadorManoEstandar;
import com.poker.modelo.Baraja;
import com.poker.modelo.BarajadorAleatorio;
import com.poker.modelo.Crupier;
import com.poker.modelo.Mesa;
import com.poker.servicio.*;
import com.poker.vista.VistaConsola;

public class ConfiguracionApp {

    public ControladorJuego crearControlador() {
        // Base de datos: crear las tablas
        ProveedorConexion proveedor = new ProveedorConexionH2();
        try {
            new InicializadorBD(proveedor).crearTablas();
        } catch (ExcepcionAccesoDatos excepcion) {
            System.err.println("No se pudo preparar la base de datos, se juega sin guardar datos: " + excepcion.getMessage());
        }

        // Vista, mesa y servicios
        VistaConsola vista = new VistaConsola();
        Mesa mesa = new Mesa(new Crupier("Crupier", new Baraja(new BarajadorAleatorio())));
        ServicioApuestas servicioApuestas = new ServicioApuestas(new ValidadorAccion(), vista);
        ServicioShowdown servicioShowdown = new ServicioShowdown(new EvaluadorManoEstandar());

        // Servicio principal con sus dependencias
        ServicioJuego servicioJuego = new ServicioJuego(mesa, servicioApuestas, servicioShowdown, new GestorBote(),
                new JugadorDAOH2(proveedor), new PartidaDAOH2(proveedor), new HistorialManoDAOH2(proveedor), vista, vista);
        return new ControladorJuego(servicioJuego, vista);
    }
}
