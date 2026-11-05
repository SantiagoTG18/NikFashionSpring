package co.edu.sena.nikfashion.repositorio;

import co.edu.sena.nikfashion.modelo.Producto;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio de productos.
 *
 * <p>Al extender {@link JpaRepository}, Spring Data genera automaticamente
 * los metodos CRUD: {@code save}, {@code findAll}, {@code findById},
 * {@code deleteById}, etc. Los metodos declarados aqui se convierten en
 * consultas SQL a partir de su nombre (query methods).</p>
 *
 * @author Santiago Torres García
 */
@Repository
public interface ProductoRepositorio extends JpaRepository<Producto, Integer> {

    /** SELECT * FROM producto WHERE nombre LIKE %texto% ORDER BY nombre */
    List<Producto> findByNombreContainingIgnoreCaseOrderByNombreAsc(String texto);

    /** SELECT * FROM producto WHERE categoria = ? ORDER BY nombre */
    List<Producto> findByCategoriaOrderByNombreAsc(String categoria);

    /** Productos con unidades disponibles, para el catalogo publico. */
    List<Producto> findByStockGreaterThanOrderByNombreAsc(Integer stock);

    /** Verifica si ya existe la referencia (para registrar). */
    boolean existsByReferenciaIgnoreCase(String referencia);

    /** Verifica si otra fila distinta ya usa la referencia (para actualizar). */
    boolean existsByReferenciaIgnoreCaseAndIdProductoNot(String referencia, Integer idProducto);
}
