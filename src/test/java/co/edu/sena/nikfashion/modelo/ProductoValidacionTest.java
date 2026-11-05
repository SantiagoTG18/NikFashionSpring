package co.edu.sena.nikfashion.modelo;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Pruebas unitarias de las validaciones de la entidad Producto.
 * No necesitan base de datos ni servidor: se ejecutan con "mvn test".
 *
 * @author Equipo NikFashion - SENA ADSO
 */
@DisplayName("Validaciones de la entidad Producto")
class ProductoValidacionTest {

    private static Validator validador;

    @BeforeAll
    static void crearValidador() {
        validador = Validation.buildDefaultValidatorFactory().getValidator();
    }

    /** Crea un producto con todos los datos correctos. */
    private Producto productoValido() {
        Producto producto = new Producto();
        producto.setReferencia("NK-AF1-010");
        producto.setNombre("Nike Air Force 1 '07");
        producto.setCategoria("Casual");
        producto.setColor("Blanco");
        producto.setTalla(new BigDecimal("40.5"));
        producto.setPrecio(new BigDecimal("549900"));
        producto.setStock(10);
        producto.setFechaLanzamiento(LocalDate.of(2007, 1, 15));
        return producto;
    }

    /** Devuelve los nombres de los campos que tienen errores. */
    private Set<String> camposConError(Producto producto) {
        return validador.validate(producto).stream()
                .map(ConstraintViolation::getPropertyPath)
                .map(Object::toString)
                .collect(Collectors.toSet());
    }

    @Test
    @DisplayName("Un producto con datos correctos no tiene errores")
    void productoValidoNoTieneErrores() {
        assertThat(validador.validate(productoValido())).isEmpty();
    }

    @Test
    @DisplayName("Campos obligatorios vacios")
    void camposObligatorios() {
        Producto producto = new Producto();
        assertThat(camposConError(producto)).containsExactlyInAnyOrder(
                "referencia", "nombre", "categoria", "color", "talla", "precio", "stock", "fechaLanzamiento");
    }

    // ---------- Textos y caracteres especiales ----------

    @ParameterizedTest(name = "Referencia invalida: \"{0}\"")
    @ValueSource(strings = {"NK#AF1", "NK AF1 01", "NK@001", "NK-ÑÑ-01", "<script>"})
    void referenciaConCaracteresEspeciales(String referencia) {
        Producto producto = productoValido();
        producto.setReferencia(referencia);
        assertThat(camposConError(producto)).contains("referencia");
    }

    @ParameterizedTest(name = "Nombre invalido: \"{0}\"")
    @ValueSource(strings = {"Nike <script>", "Nike $$$", "Nike @Max", "Nike; DROP TABLE"})
    void nombreConCaracteresEspeciales(String nombre) {
        Producto producto = productoValido();
        producto.setNombre(nombre);
        assertThat(camposConError(producto)).contains("nombre");
    }

    @ParameterizedTest(name = "Color invalido: \"{0}\"")
    @ValueSource(strings = {"Blanco123", "Rojo!", "Az-ul"})
    void colorSoloLetras(String color) {
        Producto producto = productoValido();
        producto.setColor(color);
        assertThat(camposConError(producto)).contains("color");
    }

    @Test
    @DisplayName("Nombre y color aceptan tildes y enie")
    void aceptaTildes() {
        Producto producto = productoValido();
        producto.setNombre("Nike Niño Edición Ñandú");
        producto.setColor("Azul Marino Cálido");
        assertThat(validador.validate(producto)).isEmpty();
    }

    // ---------- Longitudes ----------

    @Test
    @DisplayName("Longitudes minimas")
    void longitudesMinimas() {
        Producto producto = productoValido();
        producto.setReferencia("NK1");
        producto.setNombre("Ni");
        producto.setColor("Az");
        assertThat(camposConError(producto)).contains("referencia", "nombre", "color");
    }

    @Test
    @DisplayName("Longitudes maximas")
    void longitudesMaximas() {
        Producto producto = productoValido();
        producto.setReferencia("N".repeat(21));
        producto.setNombre("N".repeat(101));
        producto.setColor("A".repeat(31));
        assertThat(camposConError(producto)).contains("referencia", "nombre", "color");
    }

    // ---------- Numeros ----------

    @ParameterizedTest(name = "Talla fuera de rango: {0}")
    @ValueSource(strings = {"29.5", "50.5", "0", "-40"})
    void tallaFueraDeRango(String talla) {
        Producto producto = productoValido();
        producto.setTalla(new BigDecimal(talla));
        assertThat(camposConError(producto)).contains("talla");
    }

    @ParameterizedTest(name = "Precio invalido: {0}")
    @ValueSource(strings = {"0", "-5000", "549900.50", "20000001"})
    void precioInvalido(String precio) {
        Producto producto = productoValido();
        producto.setPrecio(new BigDecimal(precio));
        assertThat(camposConError(producto)).contains("precio");
    }

    @Test
    @DisplayName("Stock negativo o mayor a 9999")
    void stockFueraDeRango() {
        Producto producto = productoValido();
        producto.setStock(-1);
        assertThat(camposConError(producto)).contains("stock");
        producto.setStock(10000);
        assertThat(camposConError(producto)).contains("stock");
    }

    @Test
    @DisplayName("Valores limite validos: talla 30 y 50, stock 0 y 9999")
    void valoresLimite() {
        Producto producto = productoValido();
        producto.setTalla(new BigDecimal("30"));
        producto.setStock(0);
        assertThat(validador.validate(producto)).isEmpty();
        producto.setTalla(new BigDecimal("50"));
        producto.setStock(9999);
        assertThat(validador.validate(producto)).isEmpty();
    }

    // ---------- Fechas ----------

    @Test
    @DisplayName("La fecha de lanzamiento no puede ser futura")
    void fechaFutura() {
        Producto producto = productoValido();
        producto.setFechaLanzamiento(LocalDate.now().plusDays(1));
        assertThat(camposConError(producto)).contains("fechaLanzamiento");
    }

    @Test
    @DisplayName("La fecha de hoy es valida")
    void fechaHoy() {
        Producto producto = productoValido();
        producto.setFechaLanzamiento(LocalDate.now());
        assertThat(validador.validate(producto)).isEmpty();
    }

    @Test
    @DisplayName("Fecha anterior a la fundacion de Nike (25/01/1964)")
    void fechaAnteriorAFundacion() {
        Producto producto = productoValido();
        producto.setFechaLanzamiento(LocalDate.of(1964, 1, 24));
        assertThat(producto.esFechaAnteriorAFundacion()).isTrue();
        producto.setFechaLanzamiento(LocalDate.of(1964, 1, 25));
        assertThat(producto.esFechaAnteriorAFundacion()).isFalse();
    }

    // ---------- Metodos de negocio ----------

    @Test
    @DisplayName("Stock bajo cuando hay 5 unidades o menos")
    void stockBajo() {
        Producto producto = productoValido();
        producto.setStock(5);
        assertThat(producto.tieneStockBajo()).isTrue();
        producto.setStock(6);
        assertThat(producto.tieneStockBajo()).isFalse();
        producto.setStock(0);
        assertThat(producto.estaDisponible()).isFalse();
    }
}
