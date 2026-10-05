package co.edu.uptc.gramatica.service;

import co.edu.uptc.gramatica.model.Gramatica;
import co.edu.uptc.gramatica.model.Produccion;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Convierte el texto que el usuario escribe en la interfaz en un objeto {@link Gramatica}.
 *
 * <p>Formato esperado:</p>
 * <ul>
 *   <li>Terminales y no terminales: símbolos de un carácter separados por comas o espacios
 *       (por ejemplo {@code a, b} y {@code S, A, B}).</li>
 *   <li>Símbolo inicial: un único carácter (por ejemplo {@code S}).</li>
 *   <li>Producciones: una línea por no terminal, con alternativas separadas por {@code |}
 *       (por ejemplo {@code S -> aA | bB}). Se acepta {@code ->} o {@code →}. La palabra vacía
 *       se escribe {@code λ}.</li>
 * </ul>
 *
 * <p>Esta clase solo revisa el <b>formato</b> del texto. Las reglas de la gramática
 * (cantidades mínimas, símbolos declarados, etc.) las revisa {@link ValidadorGramatica}.</p>
 */
public class LectorGramatica {

    /**
     * Construye una gramática a partir de los textos ingresados.
     *
     * @param textoTerminales    símbolos terminales, por ejemplo {@code "a, b"}
     * @param textoNoTerminales  símbolos no terminales, por ejemplo {@code "S, A, B"}
     * @param textoInicial       símbolo inicial, por ejemplo {@code "S"}
     * @param textoProducciones  producciones, una línea por no terminal
     * @return la gramática leída (aún sin validar)
     * @throws IllegalArgumentException si algún texto no respeta el formato
     */
    public Gramatica leer(String textoTerminales, String textoNoTerminales,
                          String textoInicial, String textoProducciones) {
        Set<Character> terminales = leerSimbolos(textoTerminales, "terminales");
        Set<Character> noTerminales = leerSimbolos(textoNoTerminales, "no terminales");
        char inicial = leerSimboloInicial(textoInicial);
        List<Produccion> producciones = leerProducciones(textoProducciones);
        return new Gramatica(terminales, noTerminales, inicial, producciones);
    }

    /**
     * Lee una lista de símbolos de un carácter separados por comas o espacios.
     *
     * @param texto  texto ingresado
     * @param nombre nombre del conjunto, usado en los mensajes de error
     * @return conjunto de símbolos, en el orden ingresado y sin repetidos
     * @throws IllegalArgumentException si algún símbolo tiene más de un carácter
     */
    private Set<Character> leerSimbolos(String texto, String nombre) {
        Set<Character> simbolos = new LinkedHashSet<>();
        for (String token : texto.trim().split("[,\\s]+")) {
            if (token.isEmpty()) {
                continue;
            }
            if (token.length() != 1) {
                throw new IllegalArgumentException("En " + nombre + ", el símbolo '" + token
                        + "' debe tener un solo carácter.");
            }
            simbolos.add(token.charAt(0));
        }
        return simbolos;
    }

    /**
     * Lee el símbolo inicial.
     *
     * @param texto texto ingresado
     * @return el símbolo inicial
     * @throws IllegalArgumentException si el texto no es exactamente un carácter
     */
    private char leerSimboloInicial(String texto) {
        String limpio = texto.trim();
        if (limpio.length() != 1) {
            throw new IllegalArgumentException("El símbolo inicial debe ser un único carácter.");
        }
        return limpio.charAt(0);
    }

    /**
     * Lee las producciones. Cada línea no vacía tiene la forma {@code A -> α1 | α2 | ...}
     * y genera una producción por cada alternativa.
     *
     * @param texto texto ingresado, con una línea por no terminal
     * @return lista de producciones sin repetidos, en el orden ingresado
     * @throws IllegalArgumentException si alguna línea no respeta el formato
     */
    private List<Produccion> leerProducciones(String texto) {
        Set<Produccion> producciones = new LinkedHashSet<>();
        String[] lineas = texto.split("\\R");
        for (int i = 0; i < lineas.length; i++) {
            String linea = lineas[i].trim();
            if (linea.isEmpty()) {
                continue;
            }
            int numeroLinea = i + 1;

            // Separar lado izquierdo y lado derecho por la flecha
            String[] partes = linea.replace("→", "->").split("->", -1);
            if (partes.length != 2) {
                throw new IllegalArgumentException("Línea " + numeroLinea
                        + ": debe tener la forma 'A -> α | β'.");
            }
            String izquierda = partes[0].trim();
            if (izquierda.length() != 1) {
                throw new IllegalArgumentException("Línea " + numeroLinea
                        + ": el lado izquierdo debe ser un único no terminal.");
            }

            // Cada alternativa separada por '|' es una producción
            for (String alternativa : partes[1].split("\\|", -1)) {
                String derecha = alternativa.replaceAll("\\s+", "");
                if (derecha.isEmpty()) {
                    throw new IllegalArgumentException("Línea " + numeroLinea
                            + ": hay una alternativa vacía. Use " + Produccion.LAMBDA
                            + " para la palabra vacía.");
                }
                // λ representa la cadena vacía
                derecha = derecha.replace(Produccion.LAMBDA, "");
                producciones.add(new Produccion(izquierda.charAt(0), derecha));
            }
        }
        return new ArrayList<>(producciones);
    }
}
