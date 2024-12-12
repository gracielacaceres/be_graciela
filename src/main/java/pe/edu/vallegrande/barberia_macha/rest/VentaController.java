package pe.edu.vallegrande.barberia_macha.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.vallegrande.barberia_macha.model.Venta;
import pe.edu.vallegrande.barberia_macha.service.VentaService;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/ventas")
public class VentaController {
    @Autowired
    private VentaService ventaService;

    @PostMapping("/registrar")
    public ResponseEntity<Venta> registrarVenta(@RequestBody Venta venta) {
        if (venta.getUsuario() == null || venta.getUsuario().getIdUsuario() == null) {
            return ResponseEntity.badRequest().body(null);
        }
        Venta nuevaVenta = ventaService.registrarVenta(venta.getUsuario().getIdUsuario(), venta.getDetalles());
        return ResponseEntity.ok(nuevaVenta);
    }

    @GetMapping
    public List<Venta> listarVentas() {
        return ventaService.listarVentas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Venta> listarVentaPorId(@PathVariable Long id) {
        return ventaService.listarVentaPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    @GetMapping("/estado/activo")
    public List<Venta> listarVentasPorEstadoActivo() {
        return ventaService.listarVentasPorEstado("A");
    }


    @GetMapping("/estado/inactivo")
    public List<Venta> listarVentasPorEstadoInactivo() {
        return ventaService.listarVentasPorEstado("I");
    }


    @PutMapping("/editar/{id}")
    public ResponseEntity<Venta> editarVenta(@PathVariable Long id, @RequestBody Venta venta) {
        Venta ventaEditada = ventaService.editarVenta(id, venta);
        return ResponseEntity.ok(ventaEditada);
    }

    @PutMapping("/eliminar/{id}")
    public ResponseEntity<Venta> eliminarVenta(@PathVariable Long id) {
        Venta ventaEliminada = ventaService.eliminarVenta(id);
        return ResponseEntity.ok(ventaEliminada);
    }


    @PutMapping("/restaurar/{id}")
    public ResponseEntity<Venta> restaurarVenta(@PathVariable Long id) {
        Venta ventaRestaurada = ventaService.restaurarVenta(id);
        return ResponseEntity.ok(ventaRestaurada);
    }

}