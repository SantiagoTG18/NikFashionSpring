package co.edu.sena.nikfashion.servicio;

import co.edu.sena.nikfashion.excepcion.ProductoNoEncontradoException;
import co.edu.sena.nikfashion.excepcion.ReferenciaDuplicadaException;
import co.edu.sena.nikfashion.modelo.Producto;
import co.edu.sena.nikfashion.repositorio.ProductoRepositorio;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementacion de las reglas de negocio de productos.
 *
 * <p>{@code @Service} registra la clase en el contenedor de Spring y
 * {@code @Transactional} hace que cada operacion se ejecute dentro de
 * una transaccion de base de datos.</p>
 *
 * @author Equipo NikFashion - SENA ADSO
 */
@Service
@Transactional
public class ProductoServicioImpl implements ProductoServicio {

    private final ProductoRepositorio productoRepositorio;

    /**
     * Inyeccion de dependencias por constructor: Spring entrega
     * automaticamente la implementacion del repositorio.
     */
    public ProductoServicioImpl(ProductoRepositorio productoRepositorio) {
        this.productoRepositorio = productoRepositorio;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> listarTodos() {
        return productoRepositorio.findAll(Sort.by("idProducto"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> listarDisponibles(String categoria) {
        // Sin categoria: todos los productos con stock
        if (categoria == null || categoria.isBlank()) {
            return productoRepositorio.findByStockGreaterThanOrderByNombreAsc(0);
        }
        // Con categoria: se filtra y se excluyen los agotados
        return productoRepositorio.findByCategoriaOrderByNombreAsc(categoria).stream()
                .filter(Producto::estaDisponible)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> buscarPorNombre(String texto) {
        if (texto == null || texto.isBlank()) {
            return listarTodos();
        }
        return productoRepositorio.findByNombreContainingIgnoreCaseOrderByNombreAsc(texto.trim());
    }

    @Override
    @Transactional(readOnly = true)
    public Producto buscarPorId(Integer idProducto) {
        return productoRepositorio.findById(idProducto)
                .orElseThrow(() -> new ProductoNoEncontradoException(idProducto));
    }

    @Override
    public Producto guardar(Producto producto) {
        // Regla de negocio: la referencia se guarda en mayusculas y sin espacios
        producto.setReferencia(producto.getReferencia().trim().toUpperCase());

        // Regla de negocio: la referencia no se puede repetir
        boolean esNuevo = producto.getIdProducto() == null;
        boolean referenciaRepetida = esNuevo
                ? productoRepositorio.existsByReferenciaIgnoreCase(producto.getReferencia())
                : productoRepositorio.existsByReferenciaIgnoreCaseAndIdProductoNot(
                        producto.getReferencia(), producto.getIdProducto());
        if (referenciaRepetida) {
            throw new ReferenciaDuplicadaException(producto.getReferencia());
        }

        // Al actualizar, se confirma que el producto exista
        if (!esNuevo && !productoRepositorio.existsById(producto.getIdProducto())) {
            throw new ProductoNoEncontradoException(producto.getIdProducto());
        }

        // save() hace INSERT si el ID es nulo o UPDATE si ya existe
        return productoRepositorio.save(producto);
    }

    @Override
    public void eliminar(Integer idProducto) {
        if (!productoRepositorio.existsById(idProducto)) {
            throw new ProductoNoEncontradoException(idProducto);
        }
        productoRepositorio.deleteById(idProducto);
    }
}
