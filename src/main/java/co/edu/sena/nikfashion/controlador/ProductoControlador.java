package co.edu.sena.nikfashion.controlador;

import co.edu.sena.nikfashion.excepcion.ProductoNoEncontradoException;
import co.edu.sena.nikfashion.excepcion.ReferenciaDuplicadaException;
import co.edu.sena.nikfashion.modelo.Producto;
import co.edu.sena.nikfashion.servicio.ProductoServicio;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador del panel de administracion de productos (CRUD).
 *
 * <p>Todas las rutas empiezan por {@code /productos}. Las consultas usan
 * GET y las operaciones que modifican datos usan POST.</p>
 *
 * @author Equipo NikFashion - SENA ADSO
 */
@Controller
@RequestMapping("/productos")
public class ProductoControlador {

    private static final String VISTA_LISTA = "productos/lista";
    private static final String VISTA_FORMULARIO = "productos/formulario";
    private static final String REDIRECCION_LISTA = "redirect:/productos";

    private final ProductoServicio productoServicio;

    public ProductoControlador(ProductoServicio productoServicio) {
        this.productoServicio = productoServicio;
    }

    /** Envia la lista de categorias a todas las vistas de este controlador. */
    @ModelAttribute("categorias")
    public java.util.List<String> cargarCategorias() {
        return ProductoServicio.CATEGORIAS;
    }

    // ===================== CONSULTAR =====================

    /** GET /productos?buscar=texto → listado con busqueda opcional por nombre. */
    @GetMapping
    public String listarProductos(@RequestParam(required = false) String buscar, Model modelo) {
        modelo.addAttribute("productos", productoServicio.buscarPorNombre(buscar));
        modelo.addAttribute("buscar", buscar);
        return VISTA_LISTA;
    }

    // ===================== INSERTAR =====================

    /** GET /productos/nuevo → formulario vacio. */
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model modelo) {
        modelo.addAttribute("producto", new Producto());
        return VISTA_FORMULARIO;
    }

    // ===================== ACTUALIZAR =====================

    /** GET /productos/{id}/editar → formulario con los datos actuales. */
    @GetMapping("/{idProducto}/editar")
    public String mostrarFormularioEditar(@PathVariable Integer idProducto, Model modelo,
                                          RedirectAttributes redireccion) {
        try {
            modelo.addAttribute("producto", productoServicio.buscarPorId(idProducto));
            return VISTA_FORMULARIO;
        } catch (ProductoNoEncontradoException e) {
            redireccion.addFlashAttribute("error", e.getMessage());
            return REDIRECCION_LISTA;
        }
    }

    // ===================== GUARDAR (insertar o actualizar) =====================

    /**
     * POST /productos/guardar → recibe el formulario.
     *
     * <p>{@code @Valid} ejecuta las validaciones de la entidad y deja los
     * errores en {@code BindingResult}. Si hay errores se vuelve a mostrar
     * el formulario con los mensajes.</p>
     */
    @PostMapping("/guardar")
    public String guardarProducto(@Valid @ModelAttribute("producto") Producto producto,
                                  BindingResult resultado,
                                  RedirectAttributes redireccion) {
        if (resultado.hasErrors()) {
            return VISTA_FORMULARIO;
        }

        boolean esNuevo = producto.getIdProducto() == null;
        try {
            productoServicio.guardar(producto);
        } catch (ReferenciaDuplicadaException e) {
            // Se marca el error sobre el campo para mostrarlo debajo del input
            resultado.rejectValue("referencia", "duplicada", e.getMessage());
            return VISTA_FORMULARIO;
        }

        // Flash attribute: el mensaje sobrevive a la redireccion (patron PRG)
        redireccion.addFlashAttribute("exito",
                "Producto " + (esNuevo ? "registrado" : "actualizado") + " correctamente.");
        return REDIRECCION_LISTA;
    }

    // ===================== ELIMINAR =====================

    /** POST /productos/{id}/eliminar → borra el producto. */
    @PostMapping("/{idProducto}/eliminar")
    public String eliminarProducto(@PathVariable Integer idProducto, RedirectAttributes redireccion) {
        try {
            productoServicio.eliminar(idProducto);
            redireccion.addFlashAttribute("exito", "Producto eliminado correctamente.");
        } catch (ProductoNoEncontradoException e) {
            redireccion.addFlashAttribute("error", e.getMessage());
        }
        return REDIRECCION_LISTA;
    }
}
