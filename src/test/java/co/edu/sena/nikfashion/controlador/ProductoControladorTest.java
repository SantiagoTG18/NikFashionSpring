package co.edu.sena.nikfashion.controlador;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import co.edu.sena.nikfashion.excepcion.ProductoNoEncontradoException;
import co.edu.sena.nikfashion.excepcion.ReferenciaDuplicadaException;
import co.edu.sena.nikfashion.modelo.Producto;
import co.edu.sena.nikfashion.servicio.ProductoServicio;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

/**
 * Pruebas del controlador de productos con MockMvc.
 * El servicio se simula con Mockito, por lo que no se necesita base de datos.
 *
 * @author Santiago Torres García
 */
@WebMvcTest(ProductoControlador.class)
@DisplayName("Controlador de productos (CRUD)")
class ProductoControladorTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductoServicio productoServicio;

    /** Arma una peticion POST con un formulario valido. */
    private MockHttpServletRequestBuilder formularioValido() {
        return formularioConFecha("2023-05-10");
    }

    /** Formulario valido cambiando solo la fecha de lanzamiento. */
    private MockHttpServletRequestBuilder formularioConFecha(String fecha) {
        return post("/productos/guardar")
                .param("referencia", "NK-JRD-006")
                .param("nombre", "Nike Air Jordan 1 Mid")
                .param("categoria", "Basket")
                .param("color", "Negro")
                .param("talla", "42")
                .param("precio", "789900")
                .param("stock", "3")
                .param("fechaLanzamiento", fecha);
    }

    /** Producto de ejemplo con todos sus datos. */
    private Producto productoEjemplo() {
        Producto producto = new Producto();
        producto.setIdProducto(1);
        producto.setReferencia("NK-AF1-001");
        producto.setNombre("Nike Air Force 1");
        producto.setCategoria("Casual");
        producto.setColor("Blanco");
        producto.setTalla(new java.math.BigDecimal("40"));
        producto.setPrecio(new java.math.BigDecimal("549900"));
        producto.setStock(12);
        producto.setFechaLanzamiento(java.time.LocalDate.of(2007, 1, 15));
        return producto;
    }

    @Test
    @DisplayName("HU-03: GET /productos muestra el inventario")
    void listarProductos() throws Exception {
        when(productoServicio.buscarPorNombre(null)).thenReturn(List.of(productoEjemplo()));
        mockMvc.perform(get("/productos"))
                .andExpect(status().isOk())
                .andExpect(view().name("productos/lista"))
                .andExpect(model().attributeExists("productos"));
    }

    @Test
    @DisplayName("HU-04: GET /productos?buscar=air busca por nombre")
    void buscarProductos() throws Exception {
        mockMvc.perform(get("/productos").param("buscar", "air"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("buscar", "air"));
        verify(productoServicio).buscarPorNombre("air");
    }

    @Test
    @DisplayName("HU-05: GET /productos/nuevo muestra el formulario vacio")
    void formularioNuevo() throws Exception {
        mockMvc.perform(get("/productos/nuevo"))
                .andExpect(status().isOk())
                .andExpect(view().name("productos/formulario"));
    }

    @Test
    @DisplayName("HU-05: POST valido registra y redirige con mensaje de exito")
    void registrarProductoValido() throws Exception {
        mockMvc.perform(formularioValido())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/productos"))
                .andExpect(flash().attribute("exito", "Producto registrado correctamente."));
        verify(productoServicio).guardar(any(Producto.class));
    }

    @Test
    @DisplayName("HU-05: POST con datos invalidos vuelve al formulario y no guarda")
    void registrarProductoInvalido() throws Exception {
        mockMvc.perform(post("/productos/guardar")
                        .param("referencia", "NK#1")
                        .param("nombre", "")
                        .param("talla", "abc")
                        .param("precio", "-5")
                        .param("stock", "")
                        .param("fechaLanzamiento", "2099-01-01"))
                .andExpect(status().isOk())
                .andExpect(view().name("productos/formulario"))
                .andExpect(model().attributeHasFieldErrors("producto",
                        "referencia", "nombre", "categoria", "color", "talla", "precio", "stock", "fechaLanzamiento"));
        verify(productoServicio, never()).guardar(any());
    }

    @Test
    @DisplayName("Fecha anterior a 1964 se rechaza")
    void fechaAnteriorAFundacion() throws Exception {
        mockMvc.perform(formularioConFecha("1950-06-01"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrorCode("producto", "fechaLanzamiento", "anteriorFundacion"));
    }

    @Test
    @DisplayName("Referencia duplicada muestra el error en el formulario")
    void referenciaDuplicada() throws Exception {
        when(productoServicio.guardar(any())).thenThrow(new ReferenciaDuplicadaException("NK-JRD-006"));
        mockMvc.perform(formularioValido())
                .andExpect(status().isOk())
                .andExpect(view().name("productos/formulario"))
                .andExpect(model().attributeHasFieldErrorCode("producto", "referencia", "duplicada"));
    }

    @Test
    @DisplayName("HU-06: POST con ID actualiza y muestra mensaje")
    void actualizarProducto() throws Exception {
        mockMvc.perform(formularioValido().param("idProducto", "3"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("exito", "Producto actualizado correctamente."));
    }

    @Test
    @DisplayName("HU-06: editar un ID inexistente redirige con error")
    void editarInexistente() throws Exception {
        when(productoServicio.buscarPorId(999)).thenThrow(new ProductoNoEncontradoException(999));
        mockMvc.perform(get("/productos/999/editar"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("error", "No existe un producto con ID 999"));
    }

    @Test
    @DisplayName("HU-07: POST eliminar borra y muestra mensaje")
    void eliminarProducto() throws Exception {
        mockMvc.perform(post("/productos/3/eliminar"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("exito", "Producto eliminado correctamente."));
        verify(productoServicio).eliminar(3);
    }

    @Test
    @DisplayName("HU-07: eliminar un ID inexistente muestra error")
    void eliminarInexistente() throws Exception {
        doThrow(new ProductoNoEncontradoException(999)).when(productoServicio).eliminar(999);
        mockMvc.perform(post("/productos/999/eliminar"))
                .andExpect(flash().attribute("error", "No existe un producto con ID 999"));
    }
}
