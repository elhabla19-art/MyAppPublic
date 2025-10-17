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
    private double anguloBola = 0;
    private double anguloVisualRuleta = 0;
    private double radioBola = 12;
    private double radioOrbita;
    private boolean girando = false;
    private int fase = 0;
    private double radioActual;
    private static final Random RAND = new Random();

    public RuletaCircular() {
        configurarRuleta();
        setTitle("Ruleta Americana Auténtica");
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

        setSize(700, 800);
        setLocationRelativeTo(null);
    }

    private void configurarRuleta() {
        // ✅ Orden auténtico de la ruleta americana (sentido horario desde 0)
        String[] ordenRuleta = {
            "0", "28", "9", "26", "30", "11", "7", "20", "32", "17",
            "5", "22", "34", "15", "3", "24", "36", "13", "1", "00",
            "27", "10", "25", "29", "12", "8", "19", "31", "18", "6",
            "21", "33", "16", "4", "23", "35", "14", "2"
        };

        // Verificar que son 38 números
        if (ordenRuleta.length != 38) {
            throw new IllegalStateException("La ruleta debe tener 38 números");
        }

        // Copiar al arreglo principal
        System.arraycopy(ordenRuleta, 0, numeros, 0, 38);

        // ✅ Colores: rojos y negros según ruleta americana
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

        double anguloFinalBola = RAND.nextDouble() * 360;
        int vueltasBola = 5 + RAND.nextInt(4);
        double anguloTotalBola = vueltasBola * 360 + anguloFinalBola;

        int vueltasRuleta = 4 + RAND.nextInt(3);
        double anguloTotalRuleta = vueltasRuleta * 360;

        anguloBola = 0;
        anguloVisualRuleta = 0;
        fase = 0;

        timer = new Timer(25, e -> {
            if (fase == 0) {
                anguloBola -= 18;
                anguloVisualRuleta += 12;

                if (anguloVisualRuleta >= anguloTotalRuleta) {
                    fase = 1;
                    radioActual = radioOrbita;
                }
            } else if (fase == 1) {
                radioActual -= 10;
                if (radioActual <= 30) {
                    radioActual = 30;
                    timer.stop();

                    double anguloRelativo = (anguloBola - anguloVisualRuleta) % 360;
                    if (anguloRelativo < 0) anguloRelativo += 360;

                    double anguloAjustado = (anguloRelativo + 90) % 360;
                    double anguloPorSector = 360.0 / 38;
                    int sectorIndex = (int) (anguloAjustado / anguloPorSector) % 38;

                    resultadoLabel.setText("¡Ganaste: " + numeros[sectorIndex] + "!");
                    girarButton.setEnabled(true);
                    girando = false;
                }
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
            int tamaño = Math.min(ancho, alto) - 140;
            int centroX = ancho / 2;
            int centroY = alto / 2;
            radioOrbita = tamaño / 2 - 10;

            double anguloPorSector = 360.0 / 38;
            for (int i = 0; i < 38; i++) {
                double anguloInicio = i * anguloPorSector - 90 + anguloVisualRuleta;
                g2d.setColor(colores[i]);
                g2d.fillArc(centroX - tamaño/2, centroY - tamaño/2, tamaño, tamaño,
                           (int)anguloInicio, (int)anguloPorSector);
                g2d.setColor(Color.WHITE);
                g2d.setStroke(new BasicStroke(1));
                g2d.drawArc(centroX - tamaño/2, centroY - tamaño/2, tamaño, tamaño,
                           (int)anguloInicio, (int)anguloPorSector);

                double anguloMitad = Math.toRadians(anguloInicio + anguloPorSector / 2);
                int radioTexto = (int)(tamaño * 0.38);
                int textoX = (int)(centroX + radioTexto * Math.cos(anguloMitad));
                int textoY = (int)(centroY - radioTexto * Math.sin(anguloMitad));

                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 12));
                FontMetrics fm = g2d.getFontMetrics();
                String texto = numeros[i];
                int w = fm.stringWidth(texto);
                g2d.drawString(texto, textoX - w/2, textoY + fm.getAscent()/2);
            }

            g2d.setColor(Color.DARK_GRAY);
            g2d.fillOval(centroX - 30, centroY - 30, 60, 60);
            g2d.setColor(Color.YELLOW);
            g2d.setStroke(new BasicStroke(3));
            g2d.drawOval(centroX - 30, centroY - 30, 60, 60);

            if (girando) {
                double anguloDibujo = anguloBola - 90;
                double r = (fase == 1) ? radioActual : radioOrbita;
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