package pe.edu.vallegrande.barberia_macha.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.barberia_macha.model.Producto;
import pe.edu.vallegrande.barberia_macha.repository.ProductoRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    // Listar todos los productos
    public List<Producto> listarProductos() {
        return productoRepository.findAll();
    }

    // Listar productos por estado
    public List<Producto> listarProductosPorEstado(int estado) {
        return productoRepository.findByEstado(estado);
    }

    public Producto agregarProducto(Producto producto) {
        // Validaciones básicas antes de guardar
        if (producto.getNombre() == null || producto.getNombre().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }
        if (producto.getPrecio() == null || producto.getPrecio() <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a 0.");
        }
        if (producto.getCategoria() == null || producto.getCategoria().getIdCategoria() == null) {
            throw new IllegalArgumentException("La categoría es obligatoria.");
        }
        return productoRepository.save(producto);
    }

    public Optional<Producto> obtenerProducto(Long id) {
        return productoRepository.findById(id);
    }

    public Producto editarProducto(Long id, Producto productoActualizado) {
        if (productoRepository.existsById(id)) {
            // Asegura que el ID no cambie y establece los valores actualizados
            productoActualizado.setIdProducto(id);

            // Validaciones adicionales al editar
            if (productoActualizado.getNombre() == null || productoActualizado.getNombre().isEmpty()) {
                throw new IllegalArgumentException("El nombre es obligatorio.");
            }
            if (productoActualizado.getPrecio() == null || productoActualizado.getPrecio() <= 0) {
                throw new IllegalArgumentException("El precio debe ser mayor a 0.");
            }
            if (productoActualizado.getCategoria() == null || productoActualizado.getCategoria().getIdCategoria() == null) {
                throw new IllegalArgumentException("La categoría es obligatoria.");
            }

            return productoRepository.save(productoActualizado);
        }
        return null;
    }

    public Producto eliminarProducto(Long id) {
        Optional<Producto> optionalProducto = productoRepository.findById(id);
        if (optionalProducto.isPresent()) {
            Producto producto = optionalProducto.get();
            producto.setEstado(0); // Desactiva el producto sin eliminarlo físicamente
            return productoRepository.save(producto);
        }
        return null;
    }

    public Producto restaurarProducto(Long id) {
        Optional<Producto> optionalProducto = productoRepository.findById(id);
        if (optionalProducto.isPresent()) {
            Producto producto = optionalProducto.get();
            producto.setEstado(1); // Restaura el estado activo del producto
            return productoRepository.save(producto);
        }
        return null;
    }

    public boolean checkNombreExists(String nombre) {
        return productoRepository.existsByNombre(nombre);
    }


}