package co.edu.uptc.gramatica.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Gramática libre de contexto G = ({Σ_T}, {Σ_NT}, S, P).
 *
 * <p>Es inmutable: los conjuntos y la lista recibidos se copian al construirla.
 * El orden en que el usuario ingresó los símbolos y producciones se conserva.</p>
 *
 * @param terminales     conjunto de símbolos terminales Σ_T
 * @param noTerminales   conjunto de símbolos no terminales Σ_NT
 * @param simboloInicial símbolo inicial S (debe pertenecer a Σ_NT)
 * @param producciones   conjunto de producciones P
 */
public record Gramatica(Set<Character> terminales,
                        Set<Character> noTerminales,
                        char simboloInicial,
                        List<Produccion> producciones) {

    /**
     * Crea la gramática copiando las colecciones recibidas para que no puedan modificarse desde fuera.
     */
    public Gramatica {
        terminales = Collections.unmodifiableSet(new LinkedHashSet<>(terminales));
        noTerminales = Collections.unmodifiableSet(new LinkedHashSet<>(noTerminales));
        producciones = List.copyOf(producciones);
    }

    /**
     * Indica si un símbolo es terminal en esta gramática.
     *
     * @param simbolo símbolo a consultar
     * @return {@code true} si el símbolo pertenece a Σ_T
     */
    public boolean esTerminal(char simbolo) {
        return terminales.contains(simbolo);
    }

    /**
     * Indica si un símbolo es no terminal en esta gramática.
     *
     * @param simbolo símbolo a consultar
     * @return {@code true} si el símbolo pertenece a Σ_NT
     */
    public boolean esNoTerminal(char simbolo) {
        return noTerminales.contains(simbolo);
    }

    /**
     * Obtiene las producciones cuyo lado izquierdo es el no terminal indicado,
     * en el mismo orden en que fueron ingresadas.
     *
     * @param noTerminal no terminal del lado izquierdo
     * @return lista (posiblemente vacía) de producciones de ese no terminal
     */
    public List<Produccion> produccionesDe(char noTerminal) {
        List<Produccion> resultado = new ArrayList<>();
        for (Produccion produccion : producciones) {
            if (produccion.izquierda() == noTerminal) {
                resultado.add(produccion);
            }
        }
        return resultado;
    }

    /**
     * Busca el no terminal de más a la izquierda en una forma sentencial.
     * Es el símbolo que se reemplaza en una derivación por la izquierda.
     *
     * @param forma forma sentencial, por ejemplo {@code "aAbB"}
     * @return índice del primer no terminal, o {@code -1} si la forma solo tiene terminales
     */
    public int posicionNoTerminalIzquierdo(String forma) {
        for (int i = 0; i < forma.length(); i++) {
            if (esNoTerminal(forma.charAt(i))) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Indica si la gramática tiene al menos una producción λ.
     *
     * @return {@code true} si existe alguna producción de la forma {@code A → λ}
     */
    public boolean tieneProduccionesLambda() {
        for (Produccion produccion : producciones) {
            if (produccion.esLambda()) {
                return true;
            }
        }
        return false;
    }
}
