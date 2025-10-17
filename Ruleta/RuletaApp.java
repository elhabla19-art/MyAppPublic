import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class RuletaApp extends JFrame {
    private String[] opciones = {"Premio 1", "Premio 2", "Premio 3", "Premio 4", "Jackpot!"};
    private JLabel resultadoLabel;
    private JButton girarButton;
    private Timer timer;
    private int pasosAnimacion = 0;
    private final int TOTAL_PASOS = 30; // duración de la animación

    public RuletaApp() {
        setTitle("Ruleta de la Suerte");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Etiqueta para mostrar la opción seleccionada
        resultadoLabel = new JLabel("¡Haz clic en 'Girar'!", SwingConstants.CENTER);
        resultadoLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(resultadoLabel, BorderLayout.CENTER);

        // Botón para girar
        girarButton = new JButton("Girar");
        girarButton.setFont(new Font("Arial", Font.PLAIN, 18));
        girarButton.addActionListener(new GirarListener());
        add(girarButton, BorderLayout.SOUTH);

        setSize(500, 300);
        setLocationRelativeTo(null); // centrar ventana
    }

    private class GirarListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (timer != null && timer.isRunning()) return; // evitar múltiples giros

            girarButton.setEnabled(false);
            pasosAnimacion = 0;

            timer = new Timer(100, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    // Mostrar opción aleatoria durante la animación
                    String opcionTemporal = opciones[new Random().nextInt(opciones.length)];
                    resultadoLabel.setText("¿? " + opcionTemporal + " ?");
                    pasosAnimacion++;

                    if (pasosAnimacion >= TOTAL_PASOS) {
                        timer.stop();
                        // Elegir resultado final
                        String resultadoFinal = opciones[new Random().nextInt(opciones.length)];
                        resultadoLabel.setText("¡Ganaste: " + resultadoFinal + "!");
                        girarButton.setEnabled(true);
                    }
                }
            });
            timer.start();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new RuletaApp().setVisible(true);
        });
    }
}