package co.edu.sena.nikfashion.servicio;

import co.edu.sena.nikfashion.modelo.Producto;
import java.util.List;

/**
 * Capa de servicio: contiene las reglas de negocio del modulo de productos.
 * Los controladores usan esta interfaz y no acceden directo al repositorio.
 *
 * @author Santiago Torres García
 */
public interface ProductoServicio {

    /** Categorias de calzado que maneja la tienda. */
    List<String> CATEGORIAS = List.of("Casual", "Running", "Futbol", "Basket", "Training");

    List<Producto> listarTodos();

    List<Producto> listarDisponibles(String categoria);

    List<Producto> buscarPorNombre(String texto);

    Producto buscarPorId(Integer idProducto);

    Producto guardar(Producto producto);

    void eliminar(Integer idProducto);
}
