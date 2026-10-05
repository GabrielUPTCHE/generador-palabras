package co.edu.uptc.gramatica.service;

import co.edu.uptc.gramatica.model.Gramatica;
import co.edu.uptc.gramatica.model.Produccion;

import java.util.ArrayList;
import java.util.List;

/**
 * Verifica que una gramática cumpla las reglas exigidas por el ejercicio y sea coherente.
 *
 * <p>Reglas revisadas:</p>
 * <ol>
 *   <li>Al menos {@value #MIN_TERMINALES} terminales, {@value #MIN_NO_TERMINALES} no terminales y
 *       {@value #MIN_PRODUCCIONES} producciones.</li>
 *   <li>Los terminales son letras minúsculas o dígitos, y los no terminales son letras mayúsculas.
 *       Así ambos conjuntos siempre son disjuntos.</li>
 *   <li>El símbolo inicial pertenece a Σ_NT.</li>
 *   <li>Cada producción tiene un no terminal declarado a la izquierda y solo símbolos
 *       declarados a la derecha.</li>
 * </ol>
 */
public class ValidadorGramatica {

    /** Cantidad mínima de símbolos terminales. */
    public static final int MIN_TERMINALES = 2;
    /** Cantidad mínima de símbolos no terminales. */
    public static final int MIN_NO_TERMINALES = 3;
    /** Cantidad mínima de producciones. */
    public static final int MIN_PRODUCCIONES = 3;

    /**
     * Revisa todas las reglas y reporta juntos todos los errores encontrados.
     *
     * @param gramatica gramática a revisar
     * @throws IllegalArgumentException con la lista de errores, si hay al menos uno
     */
    public void validar(Gramatica gramatica) {
        List<String> errores = new ArrayList<>();

        // 1. Cantidades mínimas
        if (gramatica.terminales().size() < MIN_TERMINALES) {
            errores.add("Se requieren al menos " + MIN_TERMINALES + " símbolos terminales.");
        }
        if (gramatica.noTerminales().size() < MIN_NO_TERMINALES) {
            errores.add("Se requieren al menos " + MIN_NO_TERMINALES + " símbolos no terminales.");
        }
        if (gramatica.producciones().size() < MIN_PRODUCCIONES) {
            errores.add("Se requieren al menos " + MIN_PRODUCCIONES + " producciones.");
        }

        // 2. Forma de los símbolos
        for (char t : gramatica.terminales()) {
            if (!Character.isLowerCase(t) && !Character.isDigit(t)) {
                errores.add("El terminal '" + t + "' debe ser una letra minúscula o un dígito.");
            }
        }
        for (char nt : gramatica.noTerminales()) {
            if (!Character.isUpperCase(nt)) {
                errores.add("El no terminal '" + nt + "' debe ser una letra mayúscula.");
            }
        }

        // 3. Símbolo inicial
        if (!gramatica.esNoTerminal(gramatica.simboloInicial())) {
            errores.add("El símbolo inicial '" + gramatica.simboloInicial()
                    + "' no está en los no terminales.");
        }

        // 4. Producciones con símbolos declarados
        for (Produccion p : gramatica.producciones()) {
            if (!gramatica.esNoTerminal(p.izquierda())) {
                errores.add("En '" + p + "', '" + p.izquierda() + "' no es un no terminal declarado.");
            }
            for (char simbolo : p.derecha().toCharArray()) {
                if (!gramatica.esTerminal(simbolo) && !gramatica.esNoTerminal(simbolo)) {
                    errores.add("En '" + p + "', el símbolo '" + simbolo + "' no está declarado.");
                }
            }
        }

        if (!errores.isEmpty()) {
            throw new IllegalArgumentException(String.join("\n", errores));
        }
    }
}
