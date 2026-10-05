package co.edu.uptc.gramatica.view;

import co.edu.uptc.gramatica.model.NodoArbol;
import co.edu.uptc.gramatica.model.ResultadoValidacion;
import co.edu.uptc.gramatica.service.MotorDerivacion;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

/**
 * Ventana principal de la aplicación.
 *
 * <p>A la izquierda está el {@link PanelEntrada}. A la derecha se muestra:</p>
 * <ul>
 *   <li>el veredicto de pertenencia,</li>
 *   <li>una pestaña con los pasos de la derivación y el árbol particular,</li>
 *   <li>una pestaña con el árbol general.</li>
 * </ul>
 */
public class VentanaPrincipal extends JFrame {

    private static final Color COLOR_PERTENECE = new Color(0, 130, 0);
    private static final Color COLOR_NO_PERTENECE = new Color(190, 0, 0);

    private final PanelEntrada panelEntrada = new PanelEntrada();
    private final JLabel etiquetaVeredicto = new JLabel("Ingrese la gramática y la palabra, luego presione \"Validar palabra\".");
    private final JLabel etiquetaMensaje = new JLabel(" ");
    private final JTextArea areaPasos = new JTextArea(3, 40);
    private final PanelArbol panelArbolParticular = new PanelArbol();
    private final PanelArbol panelArbolGeneral = new PanelArbol();

    /**
     * Crea y organiza todos los componentes de la ventana.
     */
    public VentanaPrincipal() {
        super("Máquina de Gramáticas - Árboles de derivación");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Encabezado con el veredicto de pertenencia
        etiquetaVeredicto.setFont(new Font(Font.DIALOG, Font.BOLD, 20));
        etiquetaMensaje.setFont(new Font(Font.DIALOG, Font.PLAIN, 13));
        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        encabezado.add(etiquetaVeredicto, BorderLayout.NORTH);
        encabezado.add(etiquetaMensaje, BorderLayout.SOUTH);

        // Pestaña 1: pasos de la derivación + árbol particular
        areaPasos.setEditable(false);
        areaPasos.setLineWrap(true);
        areaPasos.setWrapStyleWord(true);
        areaPasos.setFont(new Font(Font.DIALOG, Font.PLAIN, 15));
        JScrollPane scrollPasos = new JScrollPane(areaPasos);
        scrollPasos.setBorder(BorderFactory.createTitledBorder("Derivación por la izquierda"));
        JPanel pestanaParticular = new JPanel(new BorderLayout());
        pestanaParticular.add(scrollPasos, BorderLayout.NORTH);
        pestanaParticular.add(new JScrollPane(panelArbolParticular), BorderLayout.CENTER);

        JTabbedPane pestanas = new JTabbedPane();
        pestanas.addTab("Árbol de derivación particular", pestanaParticular);
        pestanas.addTab("Árbol de derivación general", new JScrollPane(panelArbolGeneral));

        JPanel panelResultados = new JPanel(new BorderLayout());
        panelResultados.add(encabezado, BorderLayout.NORTH);
        panelResultados.add(pestanas, BorderLayout.CENTER);

        JSplitPane division = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelEntrada, panelResultados);
        division.setDividerLocation(340);
        add(division);

        panelArbolParticular.mostrarArbol(null, "Aún no se ha validado ninguna palabra.");
        panelArbolGeneral.mostrarArbol(null, "Aún no se ha generado el árbol general.");

        setSize(1200, 720);
        setLocationRelativeTo(null);
    }

    /** @return el panel de entrada de datos */
    public PanelEntrada getPanelEntrada() {
        return panelEntrada;
    }

    /**
     * Muestra el veredicto de pertenencia, los pasos de la derivación y el árbol particular.
     *
     * @param palabraMostrada palabra validada, tal como se muestra al usuario (λ si es vacía)
     * @param resultado       resultado entregado por el motor de derivación
     */
    public void mostrarResultado(String palabraMostrada, ResultadoValidacion resultado) {
        if (resultado.pertenece()) {
            etiquetaVeredicto.setText("La palabra \"" + palabraMostrada + "\" PERTENECE a L(G)");
            etiquetaVeredicto.setForeground(COLOR_PERTENECE);
            areaPasos.setText(String.join(MotorDerivacion.SIMBOLO_DERIVA, resultado.pasos()));
            panelArbolParticular.mostrarArbol(resultado.arbolParticular(), "");
        } else {
            etiquetaVeredicto.setText("La palabra \"" + palabraMostrada + "\" NO PERTENECE a L(G)");
            etiquetaVeredicto.setForeground(COLOR_NO_PERTENECE);
            areaPasos.setText("");
            panelArbolParticular.mostrarArbol(null,
                    "No hay árbol particular: la palabra no pertenece al lenguaje.");
        }
        etiquetaMensaje.setText(resultado.mensaje());
    }

    /**
     * Muestra el árbol de derivación general.
     *
     * @param raiz raíz del árbol general
     */
    public void mostrarArbolGeneral(NodoArbol raiz) {
        panelArbolGeneral.mostrarArbol(raiz, "");
    }

    /**
     * Muestra un mensaje de error en un cuadro de diálogo.
     *
     * @param mensaje descripción del error
     */
    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Gramática no válida", JOptionPane.ERROR_MESSAGE);
    }
}
