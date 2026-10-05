package co.edu.uptc.gramatica.controller;

import co.edu.uptc.gramatica.model.Gramatica;
import co.edu.uptc.gramatica.model.NodoArbol;
import co.edu.uptc.gramatica.model.Produccion;
import co.edu.uptc.gramatica.model.ResultadoValidacion;
import co.edu.uptc.gramatica.service.GeneradorArbolGeneral;
import co.edu.uptc.gramatica.service.LectorGramatica;
import co.edu.uptc.gramatica.service.MotorDerivacion;
import co.edu.uptc.gramatica.service.ValidadorGramatica;
import co.edu.uptc.gramatica.view.PanelEntrada;
import co.edu.uptc.gramatica.view.VentanaPrincipal;

/**
 * Controlador de la aplicación: conecta la vista con los servicios.
 *
 * <p>Cuando el usuario presiona "Validar palabra", el controlador sigue este flujo:</p>
 * <ol>
 *   <li>Lee los textos de la vista y construye la {@link Gramatica} con {@link LectorGramatica}.</li>
 *   <li>Revisa la gramática con {@link ValidadorGramatica}.</li>
 *   <li>Valida la palabra con {@link MotorDerivacion}, que da la pertenencia y el árbol particular.</li>
 *   <li>Genera el árbol general con {@link GeneradorArbolGeneral}.</li>
 *   <li>Envía los resultados a la vista, o un mensaje si hubo un error de entrada.</li>
 * </ol>
 */
public class ControladorGramatica {

    private final VentanaPrincipal vista;
    private final LectorGramatica lector;
    private final ValidadorGramatica validador;
    private final MotorDerivacion motor;
    private final GeneradorArbolGeneral generadorArbolGeneral;

    /**
     * Crea el controlador y lo registra como oyente del botón "Validar palabra".
     *
     * @param vista                 ventana principal
     * @param lector                servicio que interpreta el texto de la gramática
     * @param validador             servicio que revisa las reglas de la gramática
     * @param motor                 motor de derivación
     * @param generadorArbolGeneral servicio que construye el árbol general
     */
    public ControladorGramatica(VentanaPrincipal vista, LectorGramatica lector, ValidadorGramatica validador,
                                MotorDerivacion motor, GeneradorArbolGeneral generadorArbolGeneral) {
        this.vista = vista;
        this.lector = lector;
        this.validador = validador;
        this.motor = motor;
        this.generadorArbolGeneral = generadorArbolGeneral;
        vista.getPanelEntrada().alPresionarValidar(e -> procesar());
    }

    /**
     * Ejecuta el flujo completo: leer, validar la gramática, validar la palabra y mostrar los árboles.
     * Los errores de entrada se muestran al usuario sin cerrar la aplicación.
     */
    private void procesar() {
        PanelEntrada entrada = vista.getPanelEntrada();
        try {
            Gramatica gramatica = lector.leer(entrada.getTerminales(), entrada.getNoTerminales(),
                    entrada.getSimboloInicial(), entrada.getProducciones());
            validador.validar(gramatica);

            // La palabra vacía se puede escribir como "λ" o dejar el campo en blanco
            String palabra = entrada.getPalabra().replace(Produccion.LAMBDA, "");
            String palabraMostrada = palabra.isEmpty() ? Produccion.LAMBDA : palabra;

            ResultadoValidacion resultado = motor.validar(gramatica, palabra);
            NodoArbol arbolGeneral = generadorArbolGeneral.generar(gramatica, entrada.getProfundidad());

            vista.mostrarResultado(palabraMostrada, resultado);
            vista.mostrarArbolGeneral(arbolGeneral);
        } catch (IllegalArgumentException ex) {
            vista.mostrarError(ex.getMessage());
        }
    }
}
