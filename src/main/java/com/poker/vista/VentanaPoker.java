package com.poker.vista;

import com.poker.bot.DificultadBot;
import com.poker.dto.HistorialManoDTO;
import com.poker.dto.MesaDTO;
import com.poker.modelo.Accion;
import com.poker.modelo.ContextoDecision;
import com.poker.modelo.FaseJuego;
import com.poker.modelo.Jugador;
import com.poker.modelo.JugadorBot;
import com.poker.modelo.TipoAccion;
import com.poker.servicio.ConfiguracionPartida;
import com.poker.servicio.EscuchaEventosJuego;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class VentanaPoker extends JFrame implements VistaJuego, ProveedorAccion, EscuchaEventosJuego {
    private PanelMesa panelMesa;
    private ReproductorSonido sonido;
    private Map<TipoAccion, JButton> botones;
    private JSpinner montoApuesta;
    private JTextArea registro;
    private BlockingQueue<Accion> acciones;

    public VentanaPoker(ReproductorSonido sonido) {
        super("Póker Texas Hold'em");
        this.sonido = sonido;
        this.panelMesa = new PanelMesa();
        this.botones = new LinkedHashMap<>();
        this.montoApuesta = new JSpinner(new SpinnerNumberModel(0, 0, 0, 10));
        this.registro = new JTextArea(20, 26);
        this.acciones = new LinkedBlockingQueue<>();

        // Registro de la mano a la derecha
        registro.setEditable(false);
        registro.setLineWrap(true);
        registro.setWrapStyleWord(true);
        registro.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane panelRegistro = new JScrollPane(registro);
        panelRegistro.setBorder(BorderFactory.createTitledBorder("Registro de la mano"));

        // Al pulsar un botón se guarda la acción elegida con las fichas del selector
        ActionListener alPulsar = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evento) {
                TipoAccion tipo = TipoAccion.valueOf(evento.getActionCommand());
                acciones.offer(new Accion(tipo, (Integer) montoApuesta.getValue()));
            }
        };

        // Un botón por acción, desactivados hasta que sea el turno del jugador
        JPanel panelAcciones = new JPanel();
        for (TipoAccion tipo : TipoAccion.values()) {
            String nombre = tipo.name().charAt(0) + tipo.name().substring(1).toLowerCase().replace("_", "-");
            JButton boton = new JButton(nombre);
            boton.setActionCommand(tipo.name());
            boton.addActionListener(alPulsar);
            boton.setEnabled(false);
            botones.put(tipo, boton);
            panelAcciones.add(boton);
        }
        panelAcciones.add(new JLabel("  Fichas:"));
        montoApuesta.setPreferredSize(new Dimension(80, 26));
        montoApuesta.setEnabled(false);
        panelAcciones.add(montoApuesta);

        // Armar la ventana
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        add(panelMesa, BorderLayout.CENTER);
        add(panelRegistro, BorderLayout.EAST);
        add(panelAcciones, BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    @Override
    public ConfiguracionPartida pedirConfiguracion() {
        // Campos con los valores por defecto
        JTextField nombre = new JTextField("Jugador", 12);
        JSpinner fichas = new JSpinner(new SpinnerNumberModel(1000, 100, 100000, 100));
        JSpinner pequena = new JSpinner(new SpinnerNumberModel(10, 1, 10000, 5));
        JSpinner grande = new JSpinner(new SpinnerNumberModel(20, 2, 20000, 10));
        JComboBox<DificultadBot> dificultad = new JComboBox<>(DificultadBot.values());
        dificultad.setSelectedItem(DificultadBot.MEDIO);

        // Formulario de la nueva partida
        JPanel formulario = new JPanel(new GridLayout(0, 2, 6, 6));
        formulario.add(new JLabel("Nombre:"));
        formulario.add(nombre);
        formulario.add(new JLabel("Fichas iniciales:"));
        formulario.add(fichas);
        formulario.add(new JLabel("Ciega pequeña:"));
        formulario.add(pequena);
        formulario.add(new JLabel("Ciega grande:"));
        formulario.add(grande);
        formulario.add(new JLabel("Dificultad de los bots:"));
        formulario.add(dificultad);
        JOptionPane.showMessageDialog(this, formulario, "Nueva partida", JOptionPane.PLAIN_MESSAGE);

        setTitle("Póker Texas Hold'em - " + nombre.getText().trim());
        return new ConfiguracionPartida(nombre.getText().trim(), (Integer) fichas.getValue(), (Integer) pequena.getValue(),
                (Integer) grande.getValue(), (DificultadBot) dificultad.getSelectedItem());
    }

    @Override
    public void mostrarMesa(MesaDTO mesa) {
        panelMesa.actualizar(mesa);
    }

    @Override
    public void mostrarMensaje(String mensaje) {
        registro.append(mensaje + "\n");
        registro.setCaretPosition(registro.getDocument().getLength());
    }

    @Override
    public void mostrarResultado(HistorialManoDTO historial) {
        if (historial == null)
            return;
        mostrarMensaje("Resultado de la mano " + historial.numeroMano());
        for (String nombre : historial.ganancias().keySet()) {
            mostrarMensaje("   " + nombre + " gana " + historial.ganancias().get(nombre) + " fichas");
        }
        sonido.reproducir(TipoSonido.GANAR);
    }

    @Override
    public boolean preguntarContinuar() {
        return JOptionPane.showConfirmDialog(this, "¿Jugar otra mano?", "Póker Texas Hold'em",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    @Override
    public boolean preguntarGuardar() {
        return JOptionPane.showConfirmDialog(this, "¿Guardar la partida antes de salir?", "Póker Texas Hold'em",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    @Override
    public boolean preguntarCargarPartida() {
        return JOptionPane.showConfirmDialog(this, "¿Continuar la última partida guardada?", "Póker Texas Hold'em",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    @Override
    public Accion pedirAccion(ContextoDecision contexto) {
        mostrarMensaje(">> Tu turno. Por igualar: " + contexto.porIgualar() + ", fichas: " + contexto.fichas());

        // Selector de fichas con el mínimo y el máximo para apostar o subir
        int minimo = contexto.subidaMinima();
        if (contexto.accionesValidas().contains(TipoAccion.SUBIR)) {
            minimo = contexto.porIgualar() + contexto.subidaMinima();
        }
        int maximo = Math.max(minimo, contexto.fichas());
        montoApuesta.setModel(new SpinnerNumberModel(minimo, minimo, maximo, 10));

        // Mostrar cuánto cuesta igualar y el all-in
        botones.get(TipoAccion.IGUALAR).setText("Igualar");
        if (contexto.porIgualar() > 0) {
            botones.get(TipoAccion.IGUALAR).setText("Igualar " + contexto.porIgualar());
        }
        botones.get(TipoAccion.ALL_IN).setText("All-in " + contexto.fichas());

        // Habilitar solo las acciones válidas y esperar a que el jugador elija
        acciones.clear();
        habilitarBotones(contexto.accionesValidas());
        Accion elegida = esperarAccion();
        habilitarBotones(EnumSet.noneOf(TipoAccion.class));

        // Solo apostar y subir usan las fichas del selector
        if (elegida.tipo() == TipoAccion.IGUALAR)
            return new Accion(TipoAccion.IGUALAR, contexto.porIgualar());
        if (elegida.tipo() == TipoAccion.ALL_IN)
            return new Accion(TipoAccion.ALL_IN, contexto.fichas());
        if (elegida.tipo() == TipoAccion.PASAR || elegida.tipo() == TipoAccion.RETIRARSE)
            return new Accion(elegida.tipo(), 0);
        return elegida;
    }

    @Override
    public void alRealizarAccion(Jugador jugador, Accion accion) {
        String monto = "";
        if (accion.monto() > 0) {
            monto = " " + accion.monto();
            sonido.reproducir(TipoSonido.FICHAS);
        }
        mostrarMensaje("   " + jugador.getNombre() + ": " + accion.tipo() + monto);

        // Pausa corta para que se alcance a ver la jugada de cada bot
        if (jugador instanceof JugadorBot) {
            try {
                Thread.sleep(600);
            } catch (InterruptedException interruptedException) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public void alCambiarFase(FaseJuego fase, MesaDTO mesa) {
        // Una mano nueva empieza con el registro limpio y el sonido de barajar
        if (fase == FaseJuego.PRE_FLOP) {
            registro.setText("");
            sonido.reproducir(TipoSonido.BARAJAR);
        } else if (fase != FaseJuego.SHOWDOWN) {
            sonido.reproducir(TipoSonido.REPARTIR);
        }
        mostrarMensaje("== " + fase + " ==");
        mostrarMesa(mesa);
    }

    @Override
    public void alTerminarMano(HistorialManoDTO historial) {
        mostrarResultado(historial);
    }

    private void habilitarBotones(Set<TipoAccion> validas) {
        for (TipoAccion tipo : botones.keySet()) {
            botones.get(tipo).setEnabled(validas.contains(tipo));
        }
        montoApuesta.setEnabled(validas.contains(TipoAccion.APOSTAR) || validas.contains(TipoAccion.SUBIR));
    }

    private Accion esperarAccion() {
        // Espera hasta que el jugador pulse un botón
        try {
            return acciones.take();
        } catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
            return new Accion(TipoAccion.RETIRARSE, 0);
        }
    }
}
