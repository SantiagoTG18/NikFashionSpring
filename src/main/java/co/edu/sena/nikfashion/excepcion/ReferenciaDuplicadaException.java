package co.edu.sena.nikfashion.excepcion;

/**
 * Se lanza cuando se intenta guardar un producto con una referencia
 * que ya pertenece a otro producto.
 *
 * @author Santiago Torres García
 */
public class ReferenciaDuplicadaException extends RuntimeException {

    public ReferenciaDuplicadaException(String referencia) {
        super("Ya existe un producto con la referencia " + referencia);
    }
}
