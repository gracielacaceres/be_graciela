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
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public Venta actualizarVenta(Long id, Venta ventaUpdated) {
        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));

        // Actualizar los datos de la venta
        venta.setUsuario(ventaUpdated.getUsuario());
        venta.setFechaVenta(ventaUpdated.getFechaVenta());
        venta.setEstado(ventaUpdated.getEstado());

        // Actualizar los detalles de la venta
        actualizarDetallesVenta(venta, ventaUpdated.getDetalles());

        // Calcular el total de la venta
        calcularMontoTotalVenta(venta);

        // Guardar la venta actualizada
        return ventaRepository.save(venta);
    }


     private void actualizarDetallesVenta(Venta venta, List<DetalleVenta> updatedDetails) {
        // Mapa de detalles actuales para búsqueda rápida por ID
        Map<Long, DetalleVenta> currentDetailsMap = venta.getDetalles().stream()
                .collect(Collectors.toMap(DetalleVenta::getIdDetalleVenta, detalle -> detalle));

        for (DetalleVenta detalle : updatedDetails) {
            if (detalle.getIdDetalleVenta() == null) {
                // Nuevo detalle, agregarlo a la venta
                detalle.setVenta(venta);
                detalleVentaRepository.save(detalle);
            } else if (currentDetailsMap.containsKey(detalle.getIdDetalleVenta())) {
                // Detalle existente, actualizar
                DetalleVenta existingDetail = currentDetailsMap.get(detalle.getIdDetalleVenta());
                existingDetail.setProducto(detalle.getProducto());
                existingDetail.setCantidad(detalle.getCantidad());
                existingDetail.setPrecioUnitario(detalle.getPrecioUnitario());
                existingDetail.setSubtotal(detalle.getSubtotal());
                detalleVentaRepository.save(existingDetail);
            }
        }

        // Eliminar detalles que ya no están presentes
        venta.getDetalles().removeIf(detalle -> !updatedDetails.stream()
                .map(DetalleVenta::getIdDetalleVenta)
                .collect(Collectors.toList())
                .contains(detalle.getIdDetalleVenta()));
    }




    private void calcularMontoTotalVenta(Venta venta) {
        double total = 0.0;
        for (DetalleVenta detalle : venta.getDetalles()) {
            Producto producto = productoRepository.findById(detalle.getProducto().getIdProducto())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
            double subtotal = detalle.getCantidad() * detalle.getPrecioUnitario();
            detalle.setSubtotal(subtotal);
            total += subtotal;
        }
        venta.setMontoTotal(total);
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