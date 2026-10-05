package co.edu.uptc.gramatica.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Nodo de un árbol de derivación. Se usa para los dos árboles:
 * <ul>
 *   <li><b>Árbol particular:</b> cada nodo es un símbolo (terminal, no terminal o λ).</li>
 *   <li><b>Árbol general:</b> cada nodo es una forma sentencial (por ejemplo {@code aA}), y
 *       la arista que llega a él indica la producción aplicada.</li>
 * </ul>
 */
public class NodoArbol {

    /** Clasificación del nodo; la vista la usa para elegir el color. */
    public enum Tipo {
        /** Contiene al menos un no terminal (todavía se puede derivar). */
        NO_TERMINAL,
        /** Solo contiene terminales. */
        TERMINAL,
        /** Representa la palabra vacía λ. */
        LAMBDA
    }

    private final String etiqueta;
    private final Tipo tipo;
    private final String etiquetaArista;
    private final List<NodoArbol> hijos = new ArrayList<>();

    /**
     * Crea un nodo sin etiqueta de arista (se usa en el árbol particular).
     *
     * @param etiqueta texto que se muestra dentro del nodo
     * @param tipo     clasificación del nodo
     */
    public NodoArbol(String etiqueta, Tipo tipo) {
        this(etiqueta, tipo, null);
    }

    /**
     * Crea un nodo con una etiqueta en la arista que lo une con su padre (se usa en el árbol general).
     *
     * @param etiqueta       texto que se muestra dentro del nodo
     * @param tipo           clasificación del nodo
     * @param etiquetaArista texto de la arista entrante (la producción aplicada), o {@code null}
     */
    public NodoArbol(String etiqueta, Tipo tipo, String etiquetaArista) {
        this.etiqueta = etiqueta;
        this.tipo = tipo;
        this.etiquetaArista = etiquetaArista;
    }

    /**
     * Agrega un hijo al final de la lista de hijos.
     *
     * @param hijo nodo hijo
     */
    public void agregarHijo(NodoArbol hijo) {
        hijos.add(hijo);
    }

    /** @return texto que se muestra dentro del nodo */
    public String getEtiqueta() {
        return etiqueta;
    }

    /** @return clasificación del nodo */
    public Tipo getTipo() {
        return tipo;
    }

    /** @return texto de la arista que llega desde el padre, o {@code null} si no tiene */
    public String getEtiquetaArista() {
        return etiquetaArista;
    }

    /** @return lista de hijos (solo lectura), de izquierda a derecha */
    public List<NodoArbol> getHijos() {
        return Collections.unmodifiableList(hijos);
    }

    /** @return {@code true} si el nodo no tiene hijos */
    public boolean esHoja() {
        return hijos.isEmpty();
    }
}
