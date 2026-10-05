package co.edu.uptc.gramatica.service;

import co.edu.uptc.gramatica.model.Gramatica;
import co.edu.uptc.gramatica.model.NodoArbol;
import co.edu.uptc.gramatica.model.Produccion;
import co.edu.uptc.gramatica.model.ResultadoValidacion;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

/**
 * Motor lógico de derivación: decide si una palabra {@code w} pertenece a L(G) y, si pertenece,
 * construye su árbol de derivación particular.
 *
 * <h2>Algoritmo</h2>
 * <p>Se hace una <b>búsqueda en anchura (BFS)</b> sobre las <b>derivaciones por la izquierda</b>
 * que parten del símbolo inicial S:</p>
 * <ol>
 *   <li>La cola empieza con la forma sentencial {@code S}.</li>
 *   <li>Se saca una forma de la cola. Si es igual a {@code w}, la palabra pertenece.</li>
 *   <li>Si no, se busca el no terminal de más a la izquierda y se reemplaza por el lado
 *       derecho de cada una de sus producciones. Cada forma nueva que pasa la poda entra a la cola.</li>
 *   <li>Si la cola se vacía, no existe derivación y la palabra no pertenece.</li>
 * </ol>
 * <p>Al usar BFS, la derivación encontrada es una de las más cortas.</p>
 *
 * <h2>Poda</h2>
 * <p>Para no explorar formas que nunca pueden llegar a {@code w}, se descarta una forma sentencial si:</p>
 * <ul>
 *   <li>Los terminales anteriores al primer no terminal no coinciden con el inicio de {@code w}.
 *       En una derivación por la izquierda ese prefijo ya no puede cambiar.</li>
 *   <li>Tiene más terminales que {@code |w|}. Los terminales nunca desaparecen.</li>
 *   <li>La gramática no tiene producciones λ y la forma es más larga que {@code |w|}.
 *       Sin λ, una forma nunca se acorta.</li>
 *   <li>Ya fue visitada antes.</li>
 * </ul>
 * <p>Con producciones λ una forma puede crecer en no terminales sin límite
 * (por ejemplo {@code S → SS | λ}). Por eso se fija un máximo de {@value #MAX_ESTADOS}
 * formas exploradas para garantizar que el algoritmo termine.</p>
 */
public class MotorDerivacion {

    /** Máximo de formas sentenciales que se exploran antes de detener la búsqueda. */
    public static final int MAX_ESTADOS = 30_000;

    /** Separador que se usa al mostrar una derivación paso a paso. */
    public static final String SIMBOLO_DERIVA = " ⇒ ";

    /**
     * Estado de la búsqueda: una forma sentencial y cómo se llegó a ella.
     *
     * @param forma      forma sentencial actual
     * @param padre      estado anterior ({@code null} para el estado inicial S)
     * @param produccion producción aplicada al padre para obtener esta forma ({@code null} en S)
     */
    private record Estado(String forma, Estado padre, Produccion produccion) {
    }

    /**
     * Valida si una palabra pertenece al lenguaje generado por la gramática.
     *
     * @param gramatica gramática ya validada
     * @param palabra   palabra a validar; la cadena vacía representa λ
     * @return el resultado: si pertenece, con los pasos de la derivación y el árbol particular
     */
    public ResultadoValidacion validar(Gramatica gramatica, String palabra) {
        // Si la palabra usa un símbolo que no es terminal, no puede pertenecer al lenguaje
        for (char simbolo : palabra.toCharArray()) {
            if (!gramatica.esTerminal(simbolo)) {
                return ResultadoValidacion.noPertenece("El símbolo '" + simbolo
                        + "' no pertenece a Σ_T, así que la palabra no puede generarse.");
            }
        }

        boolean hayLambda = gramatica.tieneProduccionesLambda();
        Queue<Estado> cola = new ArrayDeque<>();
        Set<String> visitadas = new HashSet<>();

        String formaInicial = String.valueOf(gramatica.simboloInicial());
        cola.add(new Estado(formaInicial, null, null));
        visitadas.add(formaInicial);

        while (!cola.isEmpty()) {
            Estado actual = cola.poll();

            // ¿Se llegó a la palabra buscada?
            if (actual.forma().equals(palabra)) {
                List<Estado> camino = reconstruirCamino(actual);
                return new ResultadoValidacion(true,
                        "Se encontró una derivación por la izquierda de " + (camino.size() - 1) + " pasos.",
                        obtenerPasos(camino),
                        construirArbolParticular(gramatica, camino));
            }

            int posicion = gramatica.posicionNoTerminalIzquierdo(actual.forma());
            if (posicion < 0) {
                continue; // Solo terminales y distinta de la palabra: no se puede seguir derivando
            }

            // Expandir el no terminal de más a la izquierda con cada una de sus producciones
            char noTerminal = actual.forma().charAt(posicion);
            for (Produccion produccion : gramatica.produccionesDe(noTerminal)) {
                String nuevaForma = actual.forma().substring(0, posicion)
                        + produccion.derecha()
                        + actual.forma().substring(posicion + 1);

                if (puedeGenerar(gramatica, nuevaForma, palabra, hayLambda) && visitadas.add(nuevaForma)) {
                    if (visitadas.size() > MAX_ESTADOS) {
                        return ResultadoValidacion.noPertenece("No se encontró derivación tras explorar "
                                + MAX_ESTADOS + " formas sentenciales (límite de búsqueda).");
                    }
                    cola.add(new Estado(nuevaForma, actual, produccion));
                }
            }
        }

        return ResultadoValidacion.noPertenece("No existe ninguna derivación de S que genere la palabra.");
    }

    /**
     * Aplica las reglas de poda: indica si la forma sentencial todavía podría derivar la palabra.
     *
     * @param gramatica gramática
     * @param forma     forma sentencial candidata
     * @param palabra   palabra buscada
     * @param hayLambda si la gramática tiene producciones λ
     * @return {@code false} si es seguro que la forma no puede llegar a la palabra
     */
    private boolean puedeGenerar(Gramatica gramatica, String forma, String palabra, boolean hayLambda) {
        // Sin λ, las formas sentenciales nunca se acortan
        if (!hayLambda && forma.length() > palabra.length()) {
            return false;
        }

        int terminales = 0;
        boolean enPrefijo = true;
        for (int i = 0; i < forma.length(); i++) {
            char simbolo = forma.charAt(i);
            if (gramatica.esNoTerminal(simbolo)) {
                enPrefijo = false;
            } else {
                terminales++;
                // El prefijo de terminales debe coincidir con el inicio de la palabra
                if (enPrefijo && (i >= palabra.length() || palabra.charAt(i) != simbolo)) {
                    return false;
                }
            }
        }
        // Los terminales nunca desaparecen, así que no pueden superar la longitud de la palabra
        return terminales <= palabra.length();
    }

    /**
     * Recorre los padres desde el estado final hasta S para obtener la derivación completa.
     *
     * @param estadoFinal estado cuya forma es la palabra buscada
     * @return estados en orden, desde S hasta la palabra
     */
    private List<Estado> reconstruirCamino(Estado estadoFinal) {
        List<Estado> camino = new ArrayList<>();
        for (Estado e = estadoFinal; e != null; e = e.padre()) {
            camino.add(e);
        }
        Collections.reverse(camino);
        return camino;
    }

    /**
     * Convierte el camino en la lista de formas sentenciales para mostrarla (la forma vacía se muestra como λ).
     *
     * @param camino estados desde S hasta la palabra
     * @return formas sentenciales en orden
     */
    private List<String> obtenerPasos(List<Estado> camino) {
        List<String> pasos = new ArrayList<>();
        for (Estado estado : camino) {
            pasos.add(estado.forma().isEmpty() ? Produccion.LAMBDA : estado.forma());
        }
        return pasos;
    }

    /**
     * Construye el árbol particular repitiendo la derivación encontrada.
     *
     * <p>Se mantiene la lista de hojas del árbol de izquierda a derecha. En cada paso, la hoja no
     * terminal de más a la izquierda (la que la derivación por la izquierda reemplazó) recibe como
     * hijos los símbolos del lado derecho de la producción, o una hoja λ.</p>
     *
     * @param gramatica gramática
     * @param camino    estados desde S hasta la palabra
     * @return raíz del árbol de derivación particular
     */
    private NodoArbol construirArbolParticular(Gramatica gramatica, List<Estado> camino) {
        NodoArbol raiz = new NodoArbol(String.valueOf(gramatica.simboloInicial()), NodoArbol.Tipo.NO_TERMINAL);
        List<NodoArbol> hojas = new ArrayList<>();
        hojas.add(raiz);

        // El primer estado es S (sin producción); los demás indican qué producción se aplicó
        for (Estado estado : camino.subList(1, camino.size())) {
            int indice = indiceHojaNoTerminalIzquierda(hojas);
            NodoArbol nodo = hojas.remove(indice);

            List<NodoArbol> nuevosHijos = new ArrayList<>();
            if (estado.produccion().esLambda()) {
                nuevosHijos.add(new NodoArbol(Produccion.LAMBDA, NodoArbol.Tipo.LAMBDA));
            } else {
                for (char simbolo : estado.produccion().derecha().toCharArray()) {
                    NodoArbol.Tipo tipo = gramatica.esNoTerminal(simbolo)
                            ? NodoArbol.Tipo.NO_TERMINAL
                            : NodoArbol.Tipo.TERMINAL;
                    nuevosHijos.add(new NodoArbol(String.valueOf(simbolo), tipo));
                }
            }
            nuevosHijos.forEach(nodo::agregarHijo);
            hojas.addAll(indice, nuevosHijos);
        }
        return raiz;
    }

    /**
     * Busca, entre las hojas actuales, la primera que es un no terminal.
     *
     * @param hojas hojas del árbol, de izquierda a derecha
     * @return índice de la hoja no terminal de más a la izquierda
     */
    private int indiceHojaNoTerminalIzquierda(List<NodoArbol> hojas) {
        for (int i = 0; i < hojas.size(); i++) {
            if (hojas.get(i).getTipo() == NodoArbol.Tipo.NO_TERMINAL) {
                return i;
            }
        }
        throw new IllegalStateException("La derivación no corresponde al árbol construido.");
    }
}
