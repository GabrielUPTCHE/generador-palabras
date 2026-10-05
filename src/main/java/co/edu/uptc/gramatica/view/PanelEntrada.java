package co.edu.uptc.gramatica.view;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;

/**
 * Panel donde el usuario ingresa la gramática G = ({Σ_T}, {Σ_NT}, S, P), la palabra a validar
 * y la profundidad del árbol general.
 *
 * <p>Solo contiene componentes gráficos. La lógica la ejecuta el controlador, que se
 * registra con {@link #alPresionarValidar(ActionListener)}.</p>
 */
public class PanelEntrada extends JPanel {

    private static final Font FUENTE_TEXTO = new Font(Font.MONOSPACED, Font.PLAIN, 15);

    private final JTextField campoTerminales = new JTextField("a, b");
    private final JTextField campoNoTerminales = new JTextField("S, A, B");
    private final JTextField campoInicial = new JTextField("S");
    private final JTextArea areaProducciones = new JTextArea("S -> aA | bB\nA -> aA | b\nB -> bB | a", 8, 20);
    private final JButton botonLambda = new JButton("Insertar λ");
    private final JTextField campoPalabra = new JTextField("aab");
    private final JSpinner spinnerProfundidad = new JSpinner(new SpinnerNumberModel(3, 1, 6, 1));
    private final JButton botonValidar = new JButton("Validar palabra");

    /**
     * Crea el panel con todos los campos de entrada, precargados con una gramática de ejemplo.
     */
    public PanelEntrada() {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createTitledBorder("Gramática G = ({Σ_T}, {Σ_NT}, S, P)"));

        for (JTextField campo : new JTextField[]{campoTerminales, campoNoTerminales, campoInicial, campoPalabra}) {
            campo.setFont(FUENTE_TEXTO);
        }
        areaProducciones.setFont(FUENTE_TEXTO);

        int fila = 0;
        agregarFila(fila++, "Terminales Σ_T (mín. 2):", campoTerminales);
        agregarFila(fila++, "No terminales Σ_NT (mín. 3):", campoNoTerminales);
        agregarFila(fila++, "Símbolo inicial S:", campoInicial);
        agregarFila(fila++, "Producciones P (mín. 3), ej. S -> aA | λ:", new JScrollPane(areaProducciones));
        agregarFila(fila++, null, botonLambda);
        agregarFila(fila++, "Palabra a validar (vacía = λ):", campoPalabra);
        agregarFila(fila++, "Profundidad del árbol general:", spinnerProfundidad);
        agregarFila(fila, null, botonValidar);

        // Inserta λ donde está el cursor en el área de producciones
        botonLambda.addActionListener(e -> {
            areaProducciones.replaceSelection("λ");
            areaProducciones.requestFocusInWindow();
        });
    }

    /**
     * Agrega una etiqueta (opcional) y debajo su componente, ocupando todo el ancho.
     *
     * @param fila       número de fila lógica
     * @param etiqueta   texto de la etiqueta, o {@code null} si no lleva
     * @param componente componente de entrada
     */
    private void agregarFila(int fila, String etiqueta, java.awt.Component componente) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.insets = new Insets(4, 6, 4, 6);

        if (etiqueta != null) {
            c.gridy = fila * 2;
            add(new JLabel(etiqueta), c);
        }
        c.gridy = fila * 2 + 1;
        if (componente instanceof JScrollPane) {
            c.fill = GridBagConstraints.BOTH;
            c.weighty = 1;
        }
        add(componente, c);
    }

    /**
     * Registra la acción que se ejecuta al presionar "Validar palabra".
     *
     * @param accion acción a ejecutar (la define el controlador)
     */
    public void alPresionarValidar(ActionListener accion) {
        botonValidar.addActionListener(accion);
    }

    /** @return texto ingresado para los terminales */
    public String getTerminales() {
        return campoTerminales.getText();
    }

    /** @return texto ingresado para los no terminales */
    public String getNoTerminales() {
        return campoNoTerminales.getText();
    }

    /** @return texto ingresado para el símbolo inicial */
    public String getSimboloInicial() {
        return campoInicial.getText();
    }

    /** @return texto ingresado para las producciones */
    public String getProducciones() {
        return areaProducciones.getText();
    }

    /** @return palabra ingresada, sin espacios al inicio ni al final */
    public String getPalabra() {
        return campoPalabra.getText().trim();
    }

    /** @return profundidad elegida para el árbol general */
    public int getProfundidad() {
        return (Integer) spinnerProfundidad.getValue();
    }
}
