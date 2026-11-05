package co.edu.sena.nikfashion.excepcion;

/**
 * Se lanza cuando se busca un producto por ID y no existe en la base de datos.
 *
 * @author Santiago Torres García
 */
public class ProductoNoEncontradoException extends RuntimeException {

    public ProductoNoEncontradoException(Integer idProducto) {
        super("No existe un producto con ID " + idProducto);
    }
}
