package RuletaC;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class RuletaCircular extends JFrame {
    private String[] opciones = {"Premio 1", "Premio 2", "Premio 3", "Premio 4", "Jackpot!"};
    private RuletaPanel ruletaPanel;
    private JButton girarButton;
    private JLabel resultadoLabel;
    private Timer timer;
    private double anguloActual = 0;
    private double velocidad = 0;
    private double desaceleracion = 0.8;
    private boolean girando = false;

    public RuletaCircular() {
        setTitle("Ruleta con Flecha Fija");
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

        setSize(600, 700);
        setLocationRelativeTo(null);
    }

    private void girar() {
        if (girando) return;
        girando = true;
        girarButton.setEnabled(false);
        resultadoLabel.setText("Girando...");

        double vueltas = 4 + new Random().nextInt(4);
        double anguloFinal = new Random().nextDouble() * 360;
        double anguloTotal = vueltas * 360 + anguloFinal;

        velocidad = 25;
        timer = new Timer(30, e -> {
            anguloActual += velocidad;
            velocidad -= desaceleracion;

            if (velocidad <= 0.1 || anguloActual >= anguloTotal) {
                timer.stop();
                velocidad = 0;
                anguloActual = anguloTotal;

                double anguloNormalizado = anguloActual % 360;
                if (anguloNormalizado < 0) anguloNormalizado += 360;

                // La flecha apunta a la parte SUPERIOR (0° en el círculo)
                // En coordenadas de dibujo, 0° está a la derecha, pero la flecha está arriba → 90°
                // Para alinear, restamos 90°
                double anguloRelativo = (anguloNormalizado - 90 + 360) % 360;
                double anguloPorSector = 360.0 / opciones.length;
                int sectorIndex = (int) (anguloRelativo / anguloPorSector) % opciones.length;

                resultadoLabel.setText("¡Ganaste: " + opciones[sectorIndex] + "!");
                girarButton.setEnabled(true);
                girando = false;
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

            int margenSuperior = 80;
            int tamaño = Math.min(ancho, alto - margenSuperior) - 60;
            int centroX = ancho / 2;
            int centroY = margenSuperior + (alto - margenSuperior) / 2;

            // === DIBUJAR FLECHA FIJA APUNTANDO HACIA ABAJO ===
            int bordeSuperiorRuleta = centroY - tamaño / 2;
            int puntaY = bordeSuperiorRuleta - 10; // punta de la flecha, encima del círculo

            g2d.setColor(Color.RED);
            int[] xFlecha = {centroX, centroX - 15, centroX + 15};
            int[] yFlecha = {puntaY, puntaY + 20, puntaY + 20}; // ¡APUNTA HACIA ABAJO!
            g2d.fillPolygon(xFlecha, yFlecha, 3);

            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawPolygon(xFlecha, yFlecha, 3);

            // === DIBUJAR LA RULETA GIRATORIA ===
            double anguloPorSector = 360.0 / opciones.length;
            double anguloInicio = anguloActual;
            Color[] colores = {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.MAGENTA};

            for (int i = 0; i < opciones.length; i++) {
                g2d.setColor(colores[i]);
                g2d.fillArc(
                    centroX - tamaño / 2,
                    centroY - tamaño / 2,
                    tamaño, tamaño,
                    (int) anguloInicio,
                    (int) anguloPorSector
                );
                g2d.setColor(Color.BLACK);
                g2d.drawArc(
                    centroX - tamaño / 2,
                    centroY - tamaño / 2,
                    tamaño, tamaño,
                    (int) anguloInicio,
                    (int) anguloPorSector
                );

                double anguloMitad = Math.toRadians(anguloInicio + anguloPorSector / 2);
                int radioTexto = tamaño / 3;
                int textoX = (int) (centroX + radioTexto * Math.cos(anguloMitad));
                int textoY = (int) (centroY - radioTexto * Math.sin(anguloMitad));

                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 14));
                FontMetrics fm = g2d.getFontMetrics();
                String texto = opciones[i];
                int textoAncho = fm.stringWidth(texto);
                g2d.drawString(texto, textoX - textoAncho / 2, textoY + fm.getAscent() / 2);

                anguloInicio += anguloPorSector;
            }

            // Círculo central
            g2d.setColor(Color.BLACK);
            g2d.fillOval(centroX - 20, centroY - 20, 40, 40);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new RuletaCircular().setVisible(true);
        });
    }
}