# NikFashion Spring – Catálogo e Inventario con Spring Boot

Evidencia **GA7-220501096-AA3-EV01 – Codificación de módulos del software stand-alone, web y móvil**
SENA – Análisis y Desarrollo de Software

Módulo **web** de la mini tienda de calzado Nike **NikFashion**, construido con el framework **Spring Boot**. Tiene dos partes:

1. **Catálogo público** (`/`): el cliente ve los zapatos disponibles en tarjetas y puede filtrarlos por categoría.
2. **Panel de administración** (`/productos`): el administrador registra, consulta, edita y elimina productos.

## Framework y tecnologías
| Tecnología | Uso en el proyecto |
|------------|--------------------|
| **Spring Boot 3.3** | Framework principal: configuración automática y servidor Tomcat embebido |
| **Spring MVC** | Controladores web (`@Controller`, `@GetMapping`, `@PostMapping`) |
| **Spring Data JPA + Hibernate** | Acceso a datos: el CRUD se genera a partir de la interfaz `ProductoRepositorio` |
| **Bean Validation** | Validaciones con anotaciones (`@NotBlank`, `@DecimalMin`, `@Min`) |
| **Thymeleaf** | Plantillas HTML (vistas) |
| MySQL / MariaDB (XAMPP) | Base de datos |
| Java 17, Maven, Git | Lenguaje, gestor de dependencias y control de versiones |

## Arquitectura por capas
```
Navegador
   │  GET / POST
   ▼
Controlador  (CatalogoControlador, ProductoControlador)   → recibe peticiones y elige la vista
   │
   ▼
Servicio     (ProductoServicio / ProductoServicioImpl)    → reglas de negocio
   │
   ▼
Repositorio  (ProductoRepositorio extends JpaRepository)  → acceso a datos (Spring Data JPA)
   │
   ▼
MySQL  (tabla producto)  ◄──►  Modelo / Entidad (Producto)
```

```
NikFashionSpring/
├── pom.xml
├── database/nikfashion.sql
├── capturas/
└── src/main/
    ├── java/co/edu/sena/nikfashion/
    │   ├── NikFashionApplication.java          -> clase principal
    │   ├── modelo/Producto.java                -> entidad JPA con validaciones
    │   ├── repositorio/ProductoRepositorio.java
    │   ├── servicio/ProductoServicio.java
    │   ├── servicio/ProductoServicioImpl.java
    │   ├── controlador/CatalogoControlador.java
    │   ├── controlador/ProductoControlador.java
    │   └── excepcion/ (ProductoNoEncontradoException, ReferenciaDuplicadaException)
    └── resources/
        ├── application.properties              -> configuracion (BD, puerto, JPA)
        ├── static/css/estilos.css
        └── templates/
            ├── fragmentos/plantilla.html       -> encabezado, pie y alertas reutilizables
            ├── catalogo.html
            └── productos/lista.html, formulario.html
```

## Rutas del módulo
| Método | Ruta | Acción |
|--------|------|--------|
| GET  | `/` | Catálogo de productos disponibles |
| GET  | `/?categoria=Running` | Catálogo filtrado por categoría |
| GET  | `/productos` | Listado de administración |
| GET  | `/productos?buscar=air` | Búsqueda por nombre |
| GET  | `/productos/nuevo` | Formulario de registro |
| GET  | `/productos/{id}/editar` | Formulario de edición |
| POST | `/productos/guardar` | Inserta o actualiza |
| POST | `/productos/{id}/eliminar` | Elimina |

## Reglas de negocio
- La referencia es única y se guarda en mayúsculas.
- Talla entre 30 y 50, precio mayor a cero y stock no negativo.
- El catálogo solo muestra productos con stock mayor a 0.
- Los productos con 5 unidades o menos se marcan como **"Últimas unidades"** en el catálogo y en rojo en el panel.

## Estándares de codificación
- **Paquetes:** minúsculas por capa → `co.edu.sena.nikfashion.servicio`
- **Clases:** PascalCase con sufijo de su capa → `ProductoControlador`, `ProductoServicioImpl`, `ProductoRepositorio`
- **Métodos:** camelCase con verbo → `listarDisponibles()`, `guardarProducto()`, `tieneStockBajo()`
- **Variables:** camelCase descriptivo → `productoServicio`, `categoriaSeleccionada`
- **Constantes:** MAYÚSCULAS → `VISTA_FORMULARIO`, `CATEGORIAS`
- **Comentarios:** Javadoc en todas las clases y métodos públicos, y comentarios en las plantillas HTML.
- Inyección de dependencias por constructor y excepciones propias para los errores de negocio.

## Pruebas realizadas
| # | Prueba | Resultado |
|---|--------|-----------|
| 1 | Abrir catálogo | ✔ 5 tarjetas de producto |
| 2 | Filtrar catálogo por "Running" | ✔ Solo Nike Pegasus 41 |
| 3 | Buscar "air" en administración | ✔ 2 productos |
| 4 | Registrar Nike Air Jordan 1 Mid | ✔ "Producto registrado correctamente." |
| 5 | Editar color, talla y precio | ✔ "Producto actualizado correctamente." (verificado en la BD) |
| 6 | Enviar formulario vacío / valores inválidos | ✔ 7 mensajes de error bajo cada campo |
| 7 | Registrar referencia repetida (nk-af1-001) | ✔ "Ya existe un producto con la referencia NK-AF1-001" |
| 8 | Eliminar producto | ✔ "Producto eliminado correctamente." (ya no está en la BD) |
| 9 | Editar un ID inexistente (999) | ✔ "No existe un producto con ID 999" |

## Cómo ejecutarlo
1. Encender **MySQL** en XAMPP y ejecutar `database/nikfashion.sql` en phpMyAdmin.
2. Abrir la carpeta en **NetBeans** o IntelliJ como proyecto Maven y ejecutar `NikFashionApplication.java`.
   - Por consola: `mvn spring-boot:run`
3. Abrir **http://localhost:8081/**

## Autores
- Diego Alfonso Arenas Uscategui
- Miller Leandro Portilla Chamorro
- Santiago Torres García
- Brallan Enrique Ariza Fandiño
