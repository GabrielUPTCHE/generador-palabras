package co.edu.uptc.gramatica;

import co.edu.uptc.gramatica.controller.ControladorGramatica;
import co.edu.uptc.gramatica.service.GeneradorArbolGeneral;
import co.edu.uptc.gramatica.service.LectorGramatica;
import co.edu.uptc.gramatica.service.MotorDerivacion;
import co.edu.uptc.gramatica.service.ValidadorGramatica;
import co.edu.uptc.gramatica.view.VentanaPrincipal;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Punto de entrada de la aplicación.
 *
 * <p>Crea la vista, los servicios y el controlador que los conecta (patrón MVC), y
 * muestra la ventana en el hilo de eventos de Swing.</p>
 */
public class Main {

    /**
     * Inicia la aplicación.
     *
     * @param args no se usan
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            usarAparienciaDelSistema();
            VentanaPrincipal vista = new VentanaPrincipal();
            new ControladorGramatica(vista, new LectorGramatica(), new ValidadorGramatica(),
                    new MotorDerivacion(), new GeneradorArbolGeneral());
            vista.setVisible(true);
        });
    }

    /**
     * Aplica la apariencia del sistema operativo. Si no se puede, se mantiene la de Swing por defecto.
     */
    private static void usarAparienciaDelSistema() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Se continúa con la apariencia por defecto
        }
    }
}
