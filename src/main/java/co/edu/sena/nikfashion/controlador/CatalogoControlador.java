package co.edu.sena.nikfashion.controlador;

import co.edu.sena.nikfashion.servicio.ProductoServicio;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controlador del catalogo publico: lo que ve el cliente de la tienda.
 *
 * @author Equipo NikFashion - SENA ADSO
 */
@Controller
public class CatalogoControlador {

    private final ProductoServicio productoServicio;

    public CatalogoControlador(ProductoServicio productoServicio) {
        this.productoServicio = productoServicio;
    }

    /**
     * GET / → muestra los zapatos disponibles, opcionalmente filtrados por categoria.
     *
     * @param categoria filtro opcional que llega en la URL (?categoria=Running)
     * @param modelo    datos que se envian a la plantilla Thymeleaf
     * @return nombre de la vista catalogo.html
     */
    @GetMapping("/")
    public String verCatalogo(@RequestParam(required = false) String categoria, Model modelo) {
        modelo.addAttribute("productos", productoServicio.listarDisponibles(categoria));
        modelo.addAttribute("categorias", ProductoServicio.CATEGORIAS);
        modelo.addAttribute("categoriaSeleccionada", categoria);
        return "catalogo";
    }
}
