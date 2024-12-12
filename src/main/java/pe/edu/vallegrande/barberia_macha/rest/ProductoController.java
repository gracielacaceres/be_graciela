    package pe.edu.vallegrande.barberia_macha.rest;

    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;
    import pe.edu.vallegrande.barberia_macha.model.Producto;
    import pe.edu.vallegrande.barberia_macha.service.ProductoService;

    import java.util.List;
    import java.util.Optional;

    @CrossOrigin(origins = "*")
    @RestController
    @RequestMapping("/api/productos")
    public class ProductoController {

        @Autowired
        private ProductoService productoService;

        @GetMapping
        public List<Producto> getAllProductos() {
            return productoService.listarProductos();
        }

        @GetMapping("/{id}")
        public ResponseEntity<Producto> getProductoById(@PathVariable Long id) {
            return productoService.obtenerProducto(id)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        }

        @GetMapping("/estado/activo")
        public List<Producto> listarProductosActivos() {
            return productoService.listarProductosPorEstado(1); // Assuming 1 is the active state
        }

        @GetMapping("/estado/inactivo")
        public List<Producto> listarProductosInactivos() {
            return productoService.listarProductosPorEstado(0); // Assuming 0 is the inactive state
        }

        @PostMapping
        public Producto createProducto(@RequestBody Producto producto) {
            return productoService.agregarProducto(producto);
        }

        @PutMapping("/{id}")
        public ResponseEntity<Producto> updateProducto(@PathVariable Long id, @RequestBody Producto productoDetails) {
            Producto updatedProducto = productoService.editarProducto(id, productoDetails);
            if (updatedProducto != null) {
                return ResponseEntity.ok(updatedProducto);
            } else {
                return ResponseEntity.notFound().build();
            }
        }

        @PutMapping("/eliminar/{id}")
        public ResponseEntity<Producto> eliminarProducto(@PathVariable Long id) {
            Producto productoEliminado = productoService.eliminarProducto(id);
            if (productoEliminado != null) {
                return ResponseEntity.ok(productoEliminado);
            } else {
                return ResponseEntity.notFound().build();
            }
        }

        @PutMapping("/restaurar/{id}")
        public ResponseEntity<Producto> restaurarProducto(@PathVariable Long id) {
            Producto productoRestaurado = productoService.restaurarProducto(id);
            if (productoRestaurado != null) {
                return ResponseEntity.ok(productoRestaurado);
            } else {
                return ResponseEntity.notFound().build();
            }
        }

        @GetMapping("/check-nombre/{nombre}")
        public ResponseEntity<Boolean> checkNombreExists(@PathVariable String nombre) {
            boolean exists = productoService.checkNombreExists(nombre);
            return new ResponseEntity<>(exists, HttpStatus.OK);
        }
    }