package br.com.totustuus.controller;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.totustuus.model.Topico;
import br.com.totustuus.model.dto.TopicoAtualizarRequestDTO;
import br.com.totustuus.model.dto.TopicoDetalheResponseDTO;
import br.com.totustuus.model.dto.TopicoRequestDTO;
import br.com.totustuus.model.dto.TopicoResponseDTO;
import br.com.totustuus.repository.CursoRepository;
import br.com.totustuus.repository.TopicoRepository;

/**
 * Anotar a classe com @RestController significa que a classe já assume que todo
 * método possuirá a anotação @ResponseBody.
 * 
 * Com isso, não é mais preciso anotar os métodos nesse controller
 * com @ResponseBody.
 * 
 * @author thiago.machado
 *
 */
@RestController
@RequestMapping("/topicos")
public class TopicoController {

	/*
	 * Essa interface possui os métodos necessários quanto a entidade Topico.
	 * Lembrar do Repository e do @DAO no Android.
	 */
	@Autowired
	private TopicoRepository topicoRepository;

	@Autowired
	private CursoRepository cursoRepository;

	/**
	 * Da forma como estpa, vamos perceber que por padrão o Spring pega o retorno e
	 * devolve ela em um formato JSON. Na verdade, não é o Spring que faz isso. O
	 * Spring usa uma biblioteca chamada Jackson.
	 * 
	 * É o Jackson que faz a conversão de Java para JSON. O Spring usa o Jackson por
	 * debaixo dos panos. Ele pegou a lista que foi devolvida, passou ela para o
	 * Jackson, o Jackson converteu para JSON, e ele devolveu como uma string.
	 * 
	 * A anotação @GetMapping diz que esse método será chamado via GET. O mapeamento
	 * está na classe.
	 */
	// Ex.: http://localhost:8080/topicos
	// Ex.: http://localhost:8080/topicos?cursoNome=Spring+Boot
	@GetMapping
	public List<TopicoResponseDTO> lista(String cursoNome) {
		List<Topico> topicos = null;

		if (cursoNome == null)
			topicos = topicoRepository.findAll();
		else
			// topicos = topicoRepository.findByCurso_Nome(cursoNome);
			topicos = topicoRepository.selecionarTopicosPeloNomeCurso(cursoNome);

		return TopicoResponseDTO.converter(topicos);
	}

	// Ex.: http://localhost:8080/topicos/titulo?titulo=D%C3%BAvida
	@RequestMapping("/titulo")
	public List<TopicoResponseDTO> listaTopicoPorNome(String titulo) {
		List<Topico> topicos = topicoRepository.findByTitulo(titulo);
		return TopicoResponseDTO.converter(topicos);
	}

	/**
	 * A anotação @PostMapping diz que será uma requisição via POST. O mapeamento
	 * está na classe.
	 * 
	 * A anotação @RequestBody diz que iremos receber esse objeto no corpo da
	 * requisição.
	 * 
	 * Quando estou cadastrando uma informação, postando uma nova informação no
	 * sistema, o ideal seria devolver outro código da família de sucesso, que é o
	 * código 201, de quando tenho uma requisição processada com sucesso, mas um
	 * novo recurso foi criado no servidor.
	 * 
	 * O Spring tem uma classe chamada ResponseEntity. Esse generic é o tipo de
	 * objeto que vou devolver no corpo da resposta, que no caso, seria o tópico.
	 * 
	 * O Jackson possui a inteligência de converter um JSON para um objeto de
	 * TopicoRequestDTO.
	 * 
	 * A anotação @Valid servirá para ler as anotações do Bean Validation dentro do
	 * próprio TopicoRequestDTO
	 * 
	 * 
	 * Segundo o Spring Data a ideia é que todo método que tiver uma operação de
	 * escrita, ou seja, salvar, alterar e excluir, deveríamos colocar
	 * o @Transactional.
	 */
	@PostMapping
	@Transactional
	public ResponseEntity<TopicoResponseDTO> cadastrar(@RequestBody @Valid TopicoRequestDTO topicoRequestDTO,
			UriComponentsBuilder uriBuilder) {
		Topico topico = topicoRequestDTO.converter(cursoRepository);
		topicoRepository.save(topico);

		/*
		 * Criando um URI para a resposna.
		 * 
		 * Em path() vem o caminho que leva até o recurso.
		 * 
		 * buildAndExpand() recebe o ID do novo recurso.
		 * 
		 */
		URI uri = uriBuilder.path("/topicos/{id}").buildAndExpand(topico.getId()).toUri();

		/*
		 * A classe ResponseEntity tem alguns métodos estáticos, para você criá-lo. Só
		 * que esse método created() recebe um parâmetro, um tal de URI. Isso acontece
		 * porque toda vez que devolvo 201 para o cliente, além de devolver o código,
		 * tenho que devolver mais duas coisas.
		 * 
		 * Uma delas é um cabeçalho http, chamado location, com a url desse novo recurso
		 * que acabou de ser criado. E a segunda coisa é que no corpo da resposta eu
		 * tenho que devolver uma representação desse recurso que acabei de criar.
		 * 
		 * Esse uri que ele recebe é a classe uri do Java mesmo. Mas na hora em que crio
		 * um objeto uri, tenho que passar o caminho dessa uri. No nosso caso, tenho que
		 * passar a uri completa. Para não ter que fazer isso, até porque quando eu
		 * colocar o sistema em produção não vai ser mais localhost:8080, o Spring vai
		 * nos ajudar novamente.
		 * 
		 * Nesse método cadastrar(), estou recebendo o TopicoRequestDTO. Posso colocar
		 * depois uma classe do Spring chamada UriCompoentsBuilder. É só declarar isso
		 * como parâmetro que o Spring vai injetar no método.
		 */
		return ResponseEntity.created(uri).body(new TopicoResponseDTO(topico));
	}

	/**
	 * Retorna os detalhes de um tópico.
	 * 
	 * Estamos associando o {id} com o @PathVariable("id").
	 * 
	 * Interessante observar que o Spring irá trazer todos os seus relacionamentos
	 * quando fizermos um getOne(id), ou findById(id). Ou seja, teremos acesso as
	 * respostas e ao autor.
	 * 
	 * <br />
	 * <br />
	 * Diferença entre getOne e findById:
	 * <ul>
	 * <li>getOne - retorna o tipo esperado. Caso não encontre o registro pelo ID,
	 * cai na exceção</li>
	 * <li>findById - retorna um Optional. Método isPresent() do Optional irá dizer
	 * se encontrou, ou não, um registro</li>
	 * </ul>
	 * 
	 * Pelo visto, não é carregamento LAZY.
	 * 
	 * @param id
	 */
	// Ex.: http://localhost:8080/topicos/4
	@GetMapping("/{id}")
	public ResponseEntity<TopicoDetalheResponseDTO> detalhar(@PathVariable("id") Long id) {
		Optional<Topico> optional = topicoRepository.findById(id);
		if (optional.isPresent()) { // Verifica antes se o registro existe para poder ser detalhá-lo
			return ResponseEntity.ok(new TopicoDetalheResponseDTO(optional.get()));
		}

		return ResponseEntity.notFound().build(); // Retorna um 404
	}

	/**
	 * Faz a atualização de um tópico.
	 * 
	 * @param id
	 * @return
	 */
	// Ex.: http://localhost:8080/topicos/4
	@PutMapping("/{id}")
	@Transactional
	public ResponseEntity<TopicoResponseDTO> atualizar(@PathVariable("id") Long id,
			@RequestBody @Valid TopicoAtualizarRequestDTO topicoAtualizarRequestDTO) {

		/*
		 * Para atualizar no banco de dados, não precisamos chamar nenhum método do
		 * repository, porque a partir do momento em que carreguei do banco de dados
		 * pelo id, pela JPA ele já está sendo gerenciado. Qualquer atributo que eu
		 * setar, cada método roda dentro de uma transação, então eu vou carregar o
		 * tópico do banco de dados.
		 * 
		 * No final do método ele vai comitar a transação, a JPA vai detectar que foram
		 * alterados os atributos e ela vai disparar o update no banco de dados
		 * automaticamente.
		 * 
		 * Contudo, para que tudo isso funcione, é necessário a anotação @Transactional,
		 * que irá dizer a JPA para realizar o commit das alteração no final da
		 * execução!
		 */

		Optional<Topico> optional = topicoRepository.findById(id);
		if (optional.isPresent()) { // Verifica antes se o registro existe para poder alterá-lo
			Topico topico = topicoAtualizarRequestDTO.atualizar(id, topicoRepository);

			/*
			 * Retornando um ok (status 200) com o tópico atualizado
			 */
			return ResponseEntity.ok(new TopicoResponseDTO(topico));
		}

		return ResponseEntity.notFound().build(); // Retorna um 404
	}

	/**
	 * Remove o tópico por id
	 */
	// Ex.: http://localhost:8080/topicos/4
	@DeleteMapping("/{id}")
	@Transactional
	public ResponseEntity remover(@PathVariable("id") Long id) {
		
		Optional<Topico> optional = topicoRepository.findById(id);
		if (optional.isPresent()) { // Verifica antes se o registro existe para poder ser excluído
			topicoRepository.deleteById(id);
			return ResponseEntity.ok().build();
		}

		return ResponseEntity.notFound().build(); // Retorna um 404
	}
}
