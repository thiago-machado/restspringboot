package br.com.totustuus.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.totustuus.model.Topico;

/**
 * Normalmente, as classes que são gerenciadas pelo Spring tem que ter um @.
 * Esse por ser interface não precisa. O Spring já encontra a classe
 * automaticamente.
 * 
 * <br />
 * 
 * JpaRepository<{Entity}, {ID}> = precisamos definir qual classe será
 * gerenciada e qual o tipo de atributo define o ID.
 * 
 * <br />
 * <br />
 * 
 * Ao extender de JpaRepository, ganhamos todos os métodos padrões de um DAO,
 * como: <br />
 * <br />
 * 
 * <ul>
 * <li>findAll()</li>
 * <li>findById()</li>
 * <li>save()</li>
 * <li>delete()</li>
 * <li>Entre muitos outros...</li>
 * </ul>
 * 
 * @author thiago.machado
 *
 */
public interface TopicoRepository extends JpaRepository<Topico, Long> {

	/*
	 * O SpringData tem um padrão de nomenclatura. Se você seguir esse padrão, ele
	 * consegue gerar a query para você automaticamente. O padrão de nomenclatura
	 * seria findby e o nome do atributo na entidade. Por exemplo, imagine que quero
	 * filtrar pelo título, o nome do método seria findByTitulo(nomeCurso).
	 */
	List<Topico> findByTitulo(String titulo);

	/*
	 * Como eu faço para filtrar não por um atributo da entidade, mas por um
	 * atributo que é de um relacionamento que está na minha entidade?
	 * 
	 * O Spring também consegue filtrar isso. Só precisamos mudar o padrão para
	 * findbyCurso_Nome. Curso é a entidade de relacionamento, Nome é o atributo
	 * dentro dessa entidade de relacionamento. Dessa maneira, ele vai conseguir
	 * encontrar.
	 * 
	 * É necessário o "_" entre a entidade e o atributo.
	 * 
	 */
	List<Topico> findByCurso_Nome(String cursoNome);

	@Query("SELECT t FROM Topico t WHERE t.curso.nome = :cursoNome")
	Page<Topico> selecionarTopicosPeloNomeCurso(@Param("cursoNome") String cursoNome, Pageable pageable);

}
