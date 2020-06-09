package br.com.totustuus.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.totustuus.model.Curso;

public interface CursoRepository extends JpaRepository<Curso, Long> {

	Curso findByNome(String cursoNome);

}
