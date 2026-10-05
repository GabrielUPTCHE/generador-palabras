package co.edu.uptc.gramatica.model;

/**
 * Regla de producción de una gramática libre de contexto, de la forma {@code A → α}.
 *
 * <p>El lado izquierdo es un único no terminal. El lado derecho es una cadena de
 * terminales y no terminales, cada uno de un carácter. La cadena vacía
 * {@code ""} representa λ (la palabra vacía).</p>
 *
 * @param izquierda no terminal del lado izquierdo (por ejemplo {@code 'S'})
 * @param derecha   cadena del lado derecho (por ejemplo {@code "aA"}); {@code ""} equivale a λ
 */
public record Produccion(char izquierda, String derecha) {

    /** Símbolo usado para mostrar la palabra vacía. */
    public static final String LAMBDA = "λ";

    /**
     * Indica si la producción es una producción λ ({@code A → λ}).
     *
     * @return {@code true} si el lado derecho es la cadena vacía
     */
    public boolean esLambda() {
        return derecha.isEmpty();
    }

    /**
     * Representación legible de la producción, por ejemplo {@code S → aA} o {@code B → λ}.
     *
     * @return la producción como texto
     */
    @Override
    public String toString() {
        return izquierda + " → " + (esLambda() ? LAMBDA : derecha);
    }
}
