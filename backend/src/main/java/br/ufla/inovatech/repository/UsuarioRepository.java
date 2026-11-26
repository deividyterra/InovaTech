package br.ufla.inovatech.repository;

import br.ufla.inovatech.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Usuario findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Integer id);

    boolean existsByCpfAndIdNot(String cpf, Integer id);

    long countByPerfil(String perfil);
}