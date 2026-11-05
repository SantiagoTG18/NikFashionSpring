package co.edu.sena.nikfashion.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * Entidad JPA que representa un producto (par de zapatos) de NikFashion.
 *
 * <p>Cada atributo se mapea a una columna de la tabla {@code producto}.
 * Las anotaciones de validacion se revisan automaticamente cuando el
 * formulario llega al controlador con {@code @Valid}.</p>
 *
 * @author Equipo NikFashion - SENA ADSO
 */
@Entity
@Table(name = "producto")
public class Producto {

    /** Llave primaria autoincremental. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Integer idProducto;

    /** Codigo unico del producto, ej. NK-AF1-001. */
    @NotBlank(message = "La referencia es obligatoria")
    @Size(min = 5, max = 20, message = "La referencia debe tener entre 5 y 20 caracteres")
    @Pattern(regexp = "^[A-Za-z0-9-]*$",
            message = "La referencia solo admite letras, numeros y guiones (sin espacios ni caracteres especiales)")
    @Column(nullable = false, unique = true, length = 20)
    private String referencia;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    @Pattern(regexp = "^[\\p{L}0-9 .'-]*$",
            message = "El nombre solo admite letras, numeros, espacios y los signos . ' -")
    @Column(nullable = false, length = 100)
    private String nombre;

    @NotBlank(message = "Seleccione una categoria")
    @Column(nullable = false, length = 50)
    private String categoria;

    @NotNull(message = "La talla es obligatoria")
    @DecimalMin(value = "30", message = "La talla minima es 30")
    @DecimalMax(value = "50", message = "La talla maxima es 50")
    @Column(nullable = false, precision = 3, scale = 1)
    private BigDecimal talla;

    @NotBlank(message = "El color es obligatorio")
    @Size(min = 3, max = 30, message = "El color debe tener entre 3 y 30 caracteres")
    @Pattern(regexp = "^[\\p{L} ]*$", message = "El color solo admite letras y espacios")
    @Column(nullable = false, length = 30)
    private String color;

    /** Se usa BigDecimal para manejar dinero sin errores de redondeo. */
    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "1", message = "El precio debe ser mayor a cero")
    @DecimalMax(value = "20000000", message = "El precio maximo es $20.000.000")
    @Digits(integer = 8, fraction = 0, message = "El precio debe ser un valor entero en pesos, sin decimales")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal precio;

    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    @Max(value = 9999, message = "El stock maximo es 9999 unidades")
    @Column(nullable = false)
    private Integer stock;

    /** Fecha en que el modelo salio al mercado. */
    @NotNull(message = "La fecha de lanzamiento es obligatoria")
    @PastOrPresent(message = "La fecha de lanzamiento no puede ser una fecha futura")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "fecha_lanzamiento")
    private LocalDate fechaLanzamiento;

    /** Fecha de fundacion de Nike: ningun modelo puede ser anterior. */
    public static final LocalDate FECHA_FUNDACION_NIKE = LocalDate.of(1964, 1, 25);

    /** Constructor vacio requerido por JPA. */
    public Producto() {
    }

    // ---------- Metodos de negocio ----------

    /**
     * Indica si quedan pocas unidades para alertar al administrador.
     *
     * @return true si el stock es 5 o menos
     */
    public boolean tieneStockBajo() {
        return stock != null && stock <= 5;
    }

    /**
     * Indica si el producto se puede vender.
     *
     * @return true si hay al menos una unidad
     */
    public boolean estaDisponible() {
        return stock != null && stock > 0;
    }

    /**
     * Regla de negocio: la fecha de lanzamiento no puede ser anterior
     * a la fundacion de Nike (25/01/1964).
     *
     * @return true si la fecha es anterior a la fundacion
     */
    public boolean esFechaAnteriorAFundacion() {
        return fechaLanzamiento != null && fechaLanzamiento.isBefore(FECHA_FUNDACION_NIKE);
    }

    // ---------- Getters y setters ----------

    public Integer getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public BigDecimal getTalla() {
        return talla;
    }

    public void setTalla(BigDecimal talla) {
        this.talla = talla;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    /**
     * Devuelve el precio sin decimales en cero (599900.00 → 599900)
     * para mostrarlo limpio en los formularios.
     */
    public BigDecimal getPrecio() {
        if (precio != null && precio.scale() > 0 && precio.stripTrailingZeros().scale() <= 0) {
            return precio.setScale(0);
        }
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public LocalDate getFechaLanzamiento() {
        return fechaLanzamiento;
    }

    public void setFechaLanzamiento(LocalDate fechaLanzamiento) {
        this.fechaLanzamiento = fechaLanzamiento;
    }
}
