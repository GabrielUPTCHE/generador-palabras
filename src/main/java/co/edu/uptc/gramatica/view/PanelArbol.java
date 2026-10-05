package co.edu.uptc.gramatica.view;

import co.edu.uptc.gramatica.model.NodoArbol;

import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.HashMap;
import java.util.Map;

/**
 * Panel que dibuja un árbol ({@link NodoArbol}). Se usa tanto para el árbol particular
 * como para el general, y debe colocarse dentro de un {@code JScrollPane}.
 *
 * <p>Distribución de los nodos:</p>
 * <ul>
 *   <li>Cada nivel del árbol ocupa una fila.</li>
 *   <li>Las hojas se colocan de izquierda a derecha, una a continuación de otra.</li>
 *   <li>Cada nodo interno se centra sobre sus hijos.</li>
 * </ul>
 */
public class PanelArbol extends JPanel {

    private static final Font FUENTE_NODO = new Font(Font.DIALOG, Font.BOLD, 15);
    private static final Font FUENTE_ARISTA = new Font(Font.DIALOG, Font.PLAIN, 11);
    private static final Font FUENTE_MENSAJE = new Font(Font.DIALOG, Font.ITALIC, 15);

    private static final int MARGEN = 30;
    private static final int ALTO_NODO = 30;
    private static final int SEPARACION_HORIZONTAL = 18;
    private static final int SEPARACION_VERTICAL = 80;

    private static final Color COLOR_NO_TERMINAL = new Color(186, 214, 255);
    private static final Color COLOR_TERMINAL = new Color(190, 235, 190);
    private static final Color COLOR_LAMBDA = new Color(220, 220, 220);

    private NodoArbol raiz;
    private String mensaje = "";

    /** Centro horizontal (x) de cada nodo, calculado en {@link #calcularPosiciones}. */
    private final Map<NodoArbol, Integer> posicionX = new HashMap<>();
    /** Siguiente x libre mientras se ubican los nodos. */
    private int siguienteX;

    /**
     * Crea un panel vacío con fondo blanco.
     */
    public PanelArbol() {
        setBackground(Color.WHITE);
    }

    /**
     * Cambia el árbol a dibujar y recalcula el tamaño del panel.
     *
     * @param raiz    raíz del árbol, o {@code null} para no dibujar ninguno
     * @param mensaje texto que se muestra si no hay árbol
     */
    public void mostrarArbol(NodoArbol raiz, String mensaje) {
        this.raiz = raiz;
        this.mensaje = mensaje;
        posicionX.clear();

        if (raiz != null) {
            siguienteX = MARGEN;
            int profundidadMaxima = calcularPosiciones(raiz, 0);
            int ancho = siguienteX + MARGEN;
            int alto = MARGEN * 2 + profundidadMaxima * SEPARACION_VERTICAL + ALTO_NODO;
            setPreferredSize(new Dimension(ancho, alto));
        } else {
            setPreferredSize(new Dimension(400, 200));
        }
        revalidate();
        repaint();
    }

    /**
     * Calcula recursivamente la posición horizontal de cada nodo: las hojas toman el siguiente
     * espacio libre y los nodos internos se centran sobre sus hijos.
     *
     * @param nodo  nodo a ubicar
     * @param nivel nivel del nodo (0 para la raíz)
     * @return el nivel más profundo alcanzado dentro del subárbol
     */
    private int calcularPosiciones(NodoArbol nodo, int nivel) {
        int ancho = anchoNodo(nodo);
        int nivelMaximo = nivel;

        if (nodo.esHoja()) {
            posicionX.put(nodo, siguienteX + ancho / 2);
            siguienteX += ancho + SEPARACION_HORIZONTAL;
            return nivel;
        }

        for (NodoArbol hijo : nodo.getHijos()) {
            nivelMaximo = Math.max(nivelMaximo, calcularPosiciones(hijo, nivel + 1));
        }
        int primero = posicionX.get(nodo.getHijos().get(0));
        int ultimo = posicionX.get(nodo.getHijos().get(nodo.getHijos().size() - 1));
        int centro = (primero + ultimo) / 2;
        posicionX.put(nodo, centro);

        // Si el padre es más ancho que sus hijos, reservar su espacio para no tapar al vecino
        siguienteX = Math.max(siguienteX, centro + ancho / 2 + SEPARACION_HORIZONTAL);
        return nivelMaximo;
    }

    /**
     * Ancho del óvalo de un nodo, según el largo de su etiqueta (y de la etiqueta de su arista).
     *
     * @param nodo nodo
     * @return ancho en píxeles
     */
    private int anchoNodo(NodoArbol nodo) {
        int anchoTexto = getFontMetrics(FUENTE_NODO).stringWidth(nodo.getEtiqueta()) + 24;
        if (nodo.getEtiquetaArista() != null) {
            anchoTexto = Math.max(anchoTexto, getFontMetrics(FUENTE_ARISTA).stringWidth(nodo.getEtiquetaArista()));
        }
        return Math.max(ALTO_NODO, anchoTexto);
    }

    /**
     * Dibuja el árbol o, si no hay, el mensaje.
     *
     * @param g contexto gráfico de Swing
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        if (raiz == null) {
            g2.setFont(FUENTE_MENSAJE);
            g2.setColor(Color.GRAY);
            g2.drawString(mensaje, MARGEN, MARGEN + 20);
            return;
        }
        dibujarAristas(g2, raiz, 0);
        dibujarNodos(g2, raiz, 0);
    }

    /**
     * Dibuja las líneas de cada nodo a sus hijos y, si existe, la etiqueta de la arista.
     *
     * @param g2    contexto gráfico
     * @param nodo  nodo actual
     * @param nivel nivel del nodo actual
     */
    private void dibujarAristas(Graphics2D g2, NodoArbol nodo, int nivel) {
        int x = posicionX.get(nodo);
        int y = coordenadaY(nivel);
        for (NodoArbol hijo : nodo.getHijos()) {
            int xHijo = posicionX.get(hijo);
            int yHijo = coordenadaY(nivel + 1);
            g2.setColor(Color.DARK_GRAY);
            g2.setStroke(new BasicStroke(1.4f));
            g2.drawLine(x, y + ALTO_NODO, xHijo, yHijo);

            if (hijo.getEtiquetaArista() != null) {
                g2.setFont(FUENTE_ARISTA);
                FontMetrics fm = g2.getFontMetrics();
                String texto = hijo.getEtiquetaArista();
                int xTexto = xHijo - fm.stringWidth(texto) / 2;
                int yTexto = yHijo - 6;
                // Fondo blanco para que la línea no tape el texto
                g2.setColor(getBackground());
                g2.fillRect(xTexto - 2, yTexto - fm.getAscent(), fm.stringWidth(texto) + 4, fm.getHeight());
                g2.setColor(new Color(150, 60, 60));
                g2.drawString(texto, xTexto, yTexto);
            }
            dibujarAristas(g2, hijo, nivel + 1);
        }
    }

    /**
     * Dibuja el óvalo y la etiqueta de cada nodo, coloreado según su tipo.
     *
     * @param g2    contexto gráfico
     * @param nodo  nodo actual
     * @param nivel nivel del nodo actual
     */
    private void dibujarNodos(Graphics2D g2, NodoArbol nodo, int nivel) {
        int ancho = anchoNodo(nodo);
        int x = posicionX.get(nodo) - ancho / 2;
        int y = coordenadaY(nivel);

        g2.setColor(colorDe(nodo.getTipo()));
        g2.fillRoundRect(x, y, ancho, ALTO_NODO, ALTO_NODO, ALTO_NODO);
        g2.setColor(Color.DARK_GRAY);
        g2.setStroke(new BasicStroke(1.4f));
        g2.drawRoundRect(x, y, ancho, ALTO_NODO, ALTO_NODO, ALTO_NODO);

        g2.setFont(FUENTE_NODO);
        g2.setColor(Color.BLACK);
        FontMetrics fm = g2.getFontMetrics();
        String texto = nodo.getEtiqueta();
        g2.drawString(texto, posicionX.get(nodo) - fm.stringWidth(texto) / 2,
                y + (ALTO_NODO + fm.getAscent() - fm.getDescent()) / 2);

        for (NodoArbol hijo : nodo.getHijos()) {
            dibujarNodos(g2, hijo, nivel + 1);
        }
    }

    /**
     * Coordenada vertical (borde superior) de los nodos de un nivel.
     *
     * @param nivel nivel del árbol (0 para la raíz)
     * @return y en píxeles
     */
    private int coordenadaY(int nivel) {
        return MARGEN + nivel * SEPARACION_VERTICAL;
    }

    /**
     * Color de relleno según el tipo de nodo.
     *
     * @param tipo tipo del nodo
     * @return color de relleno
     */
    private Color colorDe(NodoArbol.Tipo tipo) {
        return switch (tipo) {
            case NO_TERMINAL -> COLOR_NO_TERMINAL;
            case TERMINAL -> COLOR_TERMINAL;
            case LAMBDA -> COLOR_LAMBDA;
        };
    }
}
