package co.edu.uptc.gramatica.model;

import java.util.List;

/**
 * Resultado de validar una palabra contra una gramática.
 *
 * @param pertenece        {@code true} si la palabra pertenece a L(G)
 * @param mensaje          explicación breve del resultado, para mostrar al usuario
 * @param pasos            formas sentenciales de la derivación por la izquierda, desde S hasta la
 *                         palabra (por ejemplo {@code [S, aA, aaA, aab]}); vacía si no pertenece
 * @param arbolParticular  raíz del árbol de derivación particular, o {@code null} si no pertenece
 */
public record ResultadoValidacion(boolean pertenece,
                                  String mensaje,
                                  List<String> pasos,
                                  NodoArbol arbolParticular) {

    /**
     * Crea el resultado copiando la lista de pasos.
     */
    public ResultadoValidacion {
        pasos = List.copyOf(pasos);
    }

    /**
     * Crea un resultado negativo (la palabra no pertenece al lenguaje).
     *
     * @param mensaje motivo por el cual no pertenece
     * @return resultado sin pasos ni árbol
     */
    public static ResultadoValidacion noPertenece(String mensaje) {
        return new ResultadoValidacion(false, mensaje, List.of(), null);
    }
}
