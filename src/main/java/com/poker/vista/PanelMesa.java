package com.poker.vista;

import com.poker.dto.AsientoDTO;
import com.poker.dto.CartaDTO;
import com.poker.dto.MesaDTO;
import com.poker.modelo.Valor;

import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class PanelMesa extends JPanel {
    private MesaDTO mesa;

    public PanelMesa() {
        setPreferredSize(new Dimension(820, 560));
        setBackground(new Color(15, 46, 32));
    }

    public void actualizar(MesaDTO mesa) {
        this.mesa = mesa;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        int ancho = getWidth();
        int alto = getHeight();

        // Mesa ovalada con borde de madera
        g2.setColor(new Color(92, 58, 30));
        g2.fillOval(40, 60, ancho - 80, alto - 120);
        g2.setColor(new Color(31, 92, 63));
        g2.fillOval(55, 75, ancho - 110, alto - 150);

        // Antes de empezar solo se muestra el nombre del juego
        g2.setColor(new Color(250, 247, 240));
        if (mesa == null) {
            g2.setFont(new Font("SansSerif", Font.BOLD, 24));
            String titulo = "Póker Texas Hold'em";
            g2.drawString(titulo, (ancho - g2.getFontMetrics().stringWidth(titulo)) / 2, alto / 2);
            return;
        }

        // Fase de la mano
        g2.setColor(new Color(240, 210, 122));
        g2.setFont(new Font("SansSerif", Font.BOLD, 16));
        String fase = mesa.fase().replace("_", "-");
        g2.drawString(fase, (ancho - g2.getFontMetrics().stringWidth(fase)) / 2, alto / 2 - 55);

        // Cartas comunitarias y huecos de las que faltan
        int x = ancho / 2 - 170;
        int y = alto / 2 - 42;
        for (int i = 0; i < 5; i++) {
            if (i < mesa.comunitarias().size()) {
                dibujarCarta(g2, mesa.comunitarias().get(i), x + i * 70, y);
            } else {
                g2.setColor(new Color(250, 247, 240, 90));
                g2.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 0, new float[] {6, 4}, 0));
                g2.drawRoundRect(x + i * 70, y, 60, 84, 10, 10);
                g2.setStroke(new BasicStroke(1));
            }
        }

        // Bote
        g2.setColor(new Color(240, 210, 122));
        g2.setFont(new Font("SansSerif", Font.BOLD, 16));
        String bote = "Bote: " + mesa.bote();
        g2.drawString(bote, (ancho - g2.getFontMetrics().stringWidth(bote)) / 2, alto / 2 + 70);

        // Asientos de los jugadores
        for (int i = 0; i < mesa.asientos().size(); i++) {
            dibujarAsiento(g2, mesa.asientos().get(i), i);
        }
    }

    private void dibujarCarta(Graphics2D g, CartaDTO carta, int x, int y) {
        // Carta boca abajo
        if (carta == null) {
            g.setColor(new Color(36, 71, 122));
            g.fillRoundRect(x, y, 60, 84, 10, 10);
            g.setColor(new Color(250, 247, 240));
            g.setStroke(new BasicStroke(3));
            g.drawRoundRect(x + 3, y + 3, 54, 78, 8, 8);
            g.setStroke(new BasicStroke(1));
            return;
        }

        // Fondo de la carta
        g.setColor(new Color(250, 247, 240));
        g.fillRoundRect(x, y, 60, 84, 10, 10);
        g.setColor(new Color(189, 189, 189));
        g.drawRoundRect(x, y, 60, 84, 10, 10);

        // Valor corto: 2 a 10, J, Q, K y A
        int numero = Valor.valueOf(carta.valor()).getNumero();
        String valor = String.valueOf(numero);
        if (numero == 11) {
            valor = "J";
        } else if (numero == 12) {
            valor = "Q";
        } else if (numero == 13) {
            valor = "K";
        } else if (numero == 14) {
            valor = "A";
        }

        // Símbolo y color del palo
        String simbolo = "♠";
        Color color = new Color(27, 27, 27);
        if (carta.palo().equals("CORAZONES")) {
            simbolo = "♥";
            color = new Color(184, 50, 39);
        } else if (carta.palo().equals("DIAMANTES")) {
            simbolo = "♦";
            color = new Color(184, 50, 39);
        } else if (carta.palo().equals("TREBOLES")) {
            simbolo = "♣";
        }

        // Valor arriba a la izquierda y palo en el centro
        g.setColor(color);
        g.setFont(new Font("SansSerif", Font.BOLD, 18));
        g.drawString(valor, x + 7, y + 22);
        g.setFont(new Font("SansSerif", Font.PLAIN, 32));
        g.drawString(simbolo, x + 30 - g.getFontMetrics().stringWidth(simbolo) / 2, y + 62);
    }

    private void dibujarAsiento(Graphics2D g, AsientoDTO asiento, int posicion) {
        // Posición: 0 abajo (el jugador humano), 1 izquierda, 2 arriba y 3 derecha
        int ancho = getWidth();
        int alto = getHeight();
        int x = ancho / 2 - 90;
        int y = alto - 70;
        if (posicion == 1) {
            x = 20;
            y = alto / 2 - 80;
        } else if (posicion == 2) {
            x = ancho / 2 - 90;
            y = 10;
        } else if (posicion == 3) {
            x = ancho - 200;
            y = alto / 2 - 80;
        }

        // Placa con nombre, fichas, apuesta y estado
        boolean fuera = asiento.estado().equals("RETIRADO") || asiento.estado().equals("ELIMINADO");
        g.setColor(new Color(17, 17, 17, 220));
        g.fillRoundRect(x, y, 180, 60, 14, 14);
        g.setColor(fuera ? new Color(150, 150, 150) : new Color(250, 247, 240));
        g.setFont(new Font("SansSerif", Font.BOLD, 14));
        g.drawString(asiento.nombre(), x + 10, y + 18);
        g.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g.drawString("Fichas: " + asiento.fichas(), x + 10, y + 35);
        g.drawString(asiento.estado(), x + 10, y + 51);
        if (asiento.apuestaActual() > 0) {
            g.setColor(new Color(240, 210, 122));
            g.drawString("Apuesta: " + asiento.apuestaActual(), x + 95, y + 35);
        }

        // Cartas: arriba de la placa para el humano y debajo para los bots
        int yCartas = y + 66;
        if (posicion == 0) {
            yCartas = y - 90;
        }
        if (!asiento.cartasVisibles().isEmpty()) {
            for (int i = 0; i < asiento.cartasVisibles().size(); i++) {
                dibujarCarta(g, asiento.cartasVisibles().get(i), x + 25 + i * 70, yCartas);
            }
        } else if (!fuera) {
            dibujarCarta(g, null, x + 25, yCartas);
            dibujarCarta(g, null, x + 95, yCartas);
        }
    }
}
