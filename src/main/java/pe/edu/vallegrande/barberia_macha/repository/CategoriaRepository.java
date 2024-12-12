package pe.edu.vallegrande.barberia_macha.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.vallegrande.barberia_macha.model.Categoria;

import java.util.List;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    List<Categoria> findByEstado(String estado);


    boolean existsByNombre(String nombre);
}
