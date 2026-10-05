package co.edu.uptc.gramatica.service;

import co.edu.uptc.gramatica.model.Gramatica;
import co.edu.uptc.gramatica.model.NodoArbol;
import co.edu.uptc.gramatica.model.Produccion;

import java.util.ArrayList;
import java.util.List;

/**
 * Construye el árbol de derivación general de una gramática.
 *
 * <p>El árbol general muestra todas las formas sentenciales que se pueden obtener desde S con
 * derivaciones por la izquierda, hasta una profundidad N:</p>
 * <ul>
 *   <li>La raíz es el símbolo inicial S.</li>
 *   <li>Cada hijo es la forma que resulta de aplicar una producción al no terminal de más a la
 *       izquierda del padre. La arista muestra la producción aplicada.</li>
 *   <li>Las formas que solo tienen terminales (palabras del lenguaje) son hojas.</li>
 * </ul>
 *
 * <p>El árbol se construye nivel por nivel. Si un nivel completo haría superar
 * {@value #MAX_NODOS} nodos, ese nivel no se agrega, para que el dibujo siga siendo legible.</p>
 */
public class GeneradorArbolGeneral {

    /** Máximo de nodos del árbol general. */
    public static final int MAX_NODOS = 1000;

    /**
     * Par nodo-forma usado mientras se construye un nivel del árbol.
     *
     * @param nodo  nodo del árbol
     * @param forma forma sentencial que representa el nodo (sin reemplazar λ)
     */
    private record NodoForma(NodoArbol nodo, String forma) {
    }

    /**
     * Genera el árbol general hasta la profundidad indicada.
     *
     * @param gramatica   gramática ya validada
     * @param profundidad cantidad máxima de pasos de derivación (niveles debajo de la raíz)
     * @return raíz del árbol general
     */
    public NodoArbol generar(Gramatica gramatica, int profundidad) {
        String formaInicial = String.valueOf(gramatica.simboloInicial());
        NodoArbol raiz = new NodoArbol(formaInicial, NodoArbol.Tipo.NO_TERMINAL);

        List<NodoForma> nivelActual = List.of(new NodoForma(raiz, formaInicial));
        int totalNodos = 1;

        for (int nivel = 1; nivel <= profundidad; nivel++) {
            // Calcular todos los hijos del nivel antes de agregarlos, para poder respetar MAX_NODOS
            List<NodoForma> padres = new ArrayList<>();
            List<NodoForma> siguienteNivel = new ArrayList<>();

            for (NodoForma padre : nivelActual) {
                int posicion = gramatica.posicionNoTerminalIzquierdo(padre.forma());
                if (posicion < 0) {
                    continue; // Solo terminales: es una hoja
                }
                char noTerminal = padre.forma().charAt(posicion);
                for (Produccion produccion : gramatica.produccionesDe(noTerminal)) {
                    String nuevaForma = padre.forma().substring(0, posicion)
                            + produccion.derecha()
                            + padre.forma().substring(posicion + 1);
                    NodoArbol hijo = new NodoArbol(etiquetaDe(nuevaForma),
                            tipoDe(gramatica, nuevaForma), produccion.toString());
                    padres.add(padre);
                    siguienteNivel.add(new NodoForma(hijo, nuevaForma));
                }
            }

            if (siguienteNivel.isEmpty() || totalNodos + siguienteNivel.size() > MAX_NODOS) {
                break;
            }
            for (int i = 0; i < siguienteNivel.size(); i++) {
                padres.get(i).nodo().agregarHijo(siguienteNivel.get(i).nodo());
            }
            totalNodos += siguienteNivel.size();
            nivelActual = siguienteNivel;
        }
        return raiz;
    }

    /**
     * Texto que se muestra para una forma sentencial (la forma vacía se muestra como λ).
     *
     * @param forma forma sentencial
     * @return etiqueta del nodo
     */
    private String etiquetaDe(String forma) {
        return forma.isEmpty() ? Produccion.LAMBDA : forma;
    }

    /**
     * Clasifica una forma sentencial para colorear el nodo.
     *
     * @param gramatica gramática
     * @param forma     forma sentencial
     * @return {@code LAMBDA} si es vacía, {@code NO_TERMINAL} si tiene algún no terminal,
     *         {@code TERMINAL} en otro caso
     */
    private NodoArbol.Tipo tipoDe(Gramatica gramatica, String forma) {
        if (forma.isEmpty()) {
            return NodoArbol.Tipo.LAMBDA;
        }
        return gramatica.posicionNoTerminalIzquierdo(forma) >= 0
                ? NodoArbol.Tipo.NO_TERMINAL
                : NodoArbol.Tipo.TERMINAL;
    }
}
