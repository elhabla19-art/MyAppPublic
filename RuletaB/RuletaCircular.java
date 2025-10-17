package RuletaB;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class RuletaCircular extends JFrame {
    private String[] numeros = new String[38];
    private Color[] colores = new Color[38];
    private RuletaPanel ruletaPanel;
    private JButton girarButton;
    private JLabel resultadoLabel;
    private Timer timer;
    private double anguloRuleta = 0;   // horario (+)
    private double anguloBola = 0;     // antihorario (-)
    private double radioBola = 12;
    private double radioOrbita;
    private boolean girando = false;
    private int estado = 0; // 1=girando inicial, 2=buscando ganador, 3=centro
    private int sectorGanador = 0;
    private double radioActual;
    private static final Random RAND = new Random();

    public RuletaCircular() {
        configurarRuleta();
        setTitle("Ruleta Americana - Versión Final");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        ruletaPanel = new RuletaPanel();
        add(ruletaPanel, BorderLayout.CENTER);

        resultadoLabel = new JLabel("Haz clic en 'Girar'", SwingConstants.CENTER);
        resultadoLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(resultadoLabel, BorderLayout.NORTH);

        girarButton = new JButton("Girar");
        girarButton.setFont(new Font("Arial", Font.PLAIN, 16));
        girarButton.addActionListener(e -> girar());
        add(girarButton, BorderLayout.SOUTH);

        setSize(800, 900);
        setLocationRelativeTo(null);
    }

    private void configurarRuleta() {
        // Orden auténtico de la ruleta americana
        String[] ordenRuleta = {
            "0", "28", "9", "26", "30", "11", "7", "20", "32", "17",
            "5", "22", "34", "15", "3", "24", "36", "13", "1", "00",
            "27", "10", "25", "29", "12", "8", "19", "31", "18", "6",
            "21", "33", "16", "4", "23", "35", "14", "2"
        };
        System.arraycopy(ordenRuleta, 0, numeros, 0, 38);

        // Números rojos
        java.util.Set<String> rojos = new java.util.HashSet<>();
        rojos.add("1"); rojos.add("3"); rojos.add("5"); rojos.add("7"); rojos.add("9");
        rojos.add("12"); rojos.add("14"); rojos.add("16"); rojos.add("18"); rojos.add("19");
        rojos.add("21"); rojos.add("23"); rojos.add("25"); rojos.add("27"); rojos.add("30");
        rojos.add("32"); rojos.add("34"); rojos.add("36");

        // Asignar colores
        for (int i = 0; i < 38; i++) {
            String num = numeros[i];
            if ("0".equals(num) || "00".equals(num)) {
                colores[i] = Color.GREEN;
            } else if (rojos.contains(num)) {
                colores[i] = Color.RED;
            } else {
                colores[i] = Color.BLACK;
            }
        }
    }

    private void girar() {
        if (girando) return;
        girando = true;
        girarButton.setEnabled(false);
        resultadoLabel.setText("Girando...");

        sectorGanador = RAND.nextInt(38);

        anguloRuleta = 0;
        anguloBola = 0;
        estado = 1;

        timer = new Timer(30, e -> {
            switch (estado) {
                case 1: // 2 vueltas iniciales
                    anguloRuleta += 12;
                    anguloBola -= 12;
                    if (anguloRuleta >= 720) {
                        estado = 2;
                    }
                    break;

                case 2: // Bola gira hasta encontrar el ganador
                    anguloBola -= 8;
                    double anguloPorSector = 360.0 / 38;
                    double anguloGanador = sectorGanador * anguloPorSector;

                    double bolaNorm = anguloBola % 360;
                    if (bolaNorm < 0) bolaNorm += 360;
                    double ganadorNorm = anguloGanador % 360;

                    double diferencia = Math.abs(bolaNorm - ganadorNorm);
                    if (diferencia > 180) diferencia = 360 - diferencia;

                    if (diferencia < 4.7) { // Medio sector
                        anguloBola = ganadorNorm;
                        estado = 3;
                        radioActual = radioOrbita;
                    }
                    break;

                case 3: // Bola regresa al centro
                    radioActual -= 6;
                    if (radioActual <= 20) {
                        radioActual = 20;
                        timer.stop();
                        resultadoLabel.setText("¡Ganaste: " + numeros[sectorGanador] + "!");
                        girarButton.setEnabled(true);
                        girando = false;
                    }
                    break;
            }
            ruletaPanel.repaint();
        });
        timer.start();
    }

    class RuletaPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int ancho = getWidth();
            int alto = getHeight();
            int tamaño = Math.min(ancho, alto) - 180;
            int centroX = ancho / 2;
            int centroY = alto / 2;
            radioOrbita = tamaño / 2 - 10;

            double anguloPorSector = 360.0 / 38;
            for (int i = 0; i < 38; i++) {
                double anguloInicio = i * anguloPorSector - 90 + anguloRuleta;
                g2d.setColor(colores[i]);
                g2d.fillArc(centroX - tamaño/2, centroY - tamaño/2, tamaño, tamaño,
                           (int)anguloInicio, (int)anguloPorSector);
                g2d.setColor(Color.WHITE);
                g2d.setStroke(new BasicStroke(1));
                g2d.drawArc(centroX - tamaño/2, centroY - tamaño/2, tamaño, tamaño,
                           (int)anguloInicio, (int)anguloPorSector);

                // Dibujar números más cerca del borde (¡visibles!)
                double anguloMitad = Math.toRadians(anguloInicio + anguloPorSector / 2);
                int radioTexto = (int)(tamaño * 0.46); // ← clave: más al borde
                int textoX = (int)(centroX + radioTexto * Math.cos(anguloMitad));
                int textoY = (int)(centroY - radioTexto * Math.sin(anguloMitad));

                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 24));
                FontMetrics fm = g2d.getFontMetrics();
                String texto = numeros[i];
                int w = fm.stringWidth(texto);
                g2d.drawString(texto, textoX - w/2, textoY + fm.getAscent()/2);
            }

            // Círculo central: 3 veces más grande y amarillo
            int radioCentro = 240;
            g2d.setColor(Color.DARK_GRAY);
            g2d.fillOval(centroX - radioCentro, centroY - radioCentro, radioCentro * 2, radioCentro * 2);
            g2d.setColor(Color.YELLOW);
            g2d.setStroke(new BasicStroke(4));
            g2d.drawOval(centroX - radioCentro, centroY - radioCentro, radioCentro * 2, radioCentro * 2);

            // Dibujar bola
            if (girando) {
                double r = (estado >= 3) ? radioActual : radioOrbita;
                double anguloDibujo = anguloBola - 90;
                double rad = Math.toRadians(anguloDibujo);
                int x = (int)(centroX + r * Math.cos(rad));
                int y = (int)(centroY - r * Math.sin(rad));

                g2d.setColor(new Color(80, 80, 80, 120));
                g2d.fillOval(x - (int)radioBola + 2, y - (int)radioBola + 2,
                            (int)(radioBola * 2), (int)(radioBola * 2));
                g2d.setColor(Color.WHITE);
                g2d.fillOval(x - (int)radioBola, y - (int)radioBola,
                            (int)(radioBola * 2), (int)(radioBola * 2));
                g2d.setColor(Color.BLACK);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawOval(x - (int)radioBola, y - (int)radioBola,
                            (int)(radioBola * 2), (int)(radioBola * 2));
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new RuletaCircular().setVisible(true);
        });
    }
}