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
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

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
    @Size(max = 20, message = "La referencia admite maximo 20 caracteres")
    @Column(nullable = false, unique = true, length = 20)
    private String referencia;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre admite maximo 100 caracteres")
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
    @Size(max = 30, message = "El color admite maximo 30 caracteres")
    @Column(nullable = false, length = 30)
    private String color;

    /** Se usa BigDecimal para manejar dinero sin errores de redondeo. */
    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "1", message = "El precio debe ser mayor a cero")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal precio;

    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    @Column(nullable = false)
    private Integer stock;

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

    public BigDecimal getPrecio() {
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
}
