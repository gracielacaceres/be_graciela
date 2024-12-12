package pe.edu.vallegrande.barberia_macha.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.barberia_macha.model.DetalleVenta;
import pe.edu.vallegrande.barberia_macha.model.Producto;
import pe.edu.vallegrande.barberia_macha.model.Usuario;
import pe.edu.vallegrande.barberia_macha.model.Venta;
import pe.edu.vallegrande.barberia_macha.repository.DetalleVentaRepository;
import pe.edu.vallegrande.barberia_macha.repository.ProductoRepository;
import pe.edu.vallegrande.barberia_macha.repository.UsuarioRepository;
import pe.edu.vallegrande.barberia_macha.repository.VentaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class VentaService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private DetalleVentaRepository detalleVentaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Transactional
    public Venta registrarVenta(Long idUsuario, List<DetalleVenta> detalles) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Venta venta = new Venta();
        venta.setFechaVenta(LocalDate.now());
        venta.setUsuario(usuario);
        venta.setEstado("A");

        double montoTotal = 0.0;

        for (DetalleVenta detalle : detalles) {
            Producto producto = productoRepository.findById(detalle.getProducto().getIdProducto())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            if (producto.getStock() < detalle.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para el producto: " + producto.getNombre());
            }

            producto.setStock(producto.getStock() - detalle.getCantidad());
            productoRepository.save(producto);

            detalle.setVenta(venta);
            montoTotal += producto.getPrecio() * detalle.getCantidad();
        }

        venta.setMontoTotal(montoTotal);
        venta = ventaRepository.save(venta);

        for (DetalleVenta detalle : detalles) {
            detalle.setVenta(venta);
            detalleVentaRepository.save(detalle);
        }

        return venta;
    }

    public List<Venta> listarVentas() {
        return ventaRepository.findAll();
    }

    public Optional<Venta> listarVentaPorId(Long id) {
        return ventaRepository.findById(id);
    }

    @Transactional
    public Venta editarVenta(Long id, Venta venta) {
        Venta ventaExistente = ventaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));

        ventaExistente.setUsuario(venta.getUsuario());
        ventaExistente.setFechaVenta(venta.getFechaVenta());
        ventaExistente.setMontoTotal(venta.getMontoTotal());
        ventaExistente.setDetalles(venta.getDetalles());

        return ventaRepository.save(ventaExistente);
    }

    @Transactional
    public Venta eliminarVenta(Long id) {
        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));
        venta.setEstado("I");
        return ventaRepository.save(venta);
    }



    @Transactional
    public Venta restaurarVenta(Long id) {
        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));
        venta.setEstado("A");
        return ventaRepository.save(venta);
    }

    public List<Venta> listarVentasPorEstado(String estado) {
        return ventaRepository.findByEstado(estado);
    }


}