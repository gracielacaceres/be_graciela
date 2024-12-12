package pe.edu.vallegrande.barberia_macha.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.vallegrande.barberia_macha.model.Categoria;
import pe.edu.vallegrande.barberia_macha.service.CategoriaService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    @GetMapping
    public List<Categoria> getAllCategorias() {
        return categoriaService.listarCategorias();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Categoria> getCategoriaById(@PathVariable Long id) {
        return categoriaService.obtenerCategoria(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/estado/activo")
    public List<Categoria> listarCategoriasPorEstadoActivo() {
        return categoriaService.listarCategoriasPorEstado("A");
    }

    @GetMapping("/estado/inactivo")
    public List<Categoria> listarCategoriasPorEstadoInactivo() {
        return categoriaService.listarCategoriasPorEstado("I");
    }

    @PostMapping
    public Categoria createCategoria(@RequestBody Categoria categoria) {
        return categoriaService.agregarCategoria(categoria);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Categoria> updateCategoria(@PathVariable Long id, @RequestBody Categoria categoriaDetails) {
        Categoria updatedCategoria = categoriaService.editarCategoria(id, categoriaDetails);
        if (updatedCategoria != null) {
            return ResponseEntity.ok(updatedCategoria);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/eliminar/{id}")
    public ResponseEntity<Void> deleteCategoria(@PathVariable Long id) {
        categoriaService.eliminarCategoria(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/restaurar/{id}")
    public ResponseEntity<Categoria> restaurarCategoria(@PathVariable Long id) {
        Categoria restoredCategoria = categoriaService.restaurarCategoria(id);
        if (restoredCategoria != null) {
            return ResponseEntity.ok(restoredCategoria);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/check-nombre/{nombre}")
    public ResponseEntity<Boolean> checkNombreExists(@PathVariable String nombre) {
        boolean exists = categoriaService.checkNombreExists(nombre);
        return ResponseEntity.ok(exists);
    }




}