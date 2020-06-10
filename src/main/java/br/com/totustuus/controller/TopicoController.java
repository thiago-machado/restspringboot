package br.com.totustuus.controller;

import java.net.URI;
import java.util.Optional;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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
	 * Agora é possível fazer paginação usando Pageable. Precisamos somente criar
	 * uma instância do mesmo usando:
	 * 
	 * PageRequest.of(numeroDaPagina, quantidadeDeRegistros)
	 * 
	 * O Spring é inteligente o suficiente para entender que ao passar esse
	 * parâmetro aos nossos métodos, significa que queremos fazer paginação. Não
	 * precisamos ferver a cabeça com lógica.
	 * 
	 * Podemos também fazer ordenação de registros utilizando mais dois parâmetros
	 * em PageRequest: PageRequest.of(numeroDaPagina, quantidadeDeRegistros,
	 * direcaoDaOrdenacao, campoParaOrdenacao)
	 * 
	 * Parâmetros "cursoNome" e "ordenacao" não são obrigatórios, mas "pagina" e
	 * "quantidade" são!
	 * 
	 * O método findAll() aceita receber como parâmetro um Pageable. Contudo, o
	 * retorno será um Page. Pois será enviado ao usuário um JSON com os registros e
	 * com informações sobre número total de registros, número total de páginas e
	 * etc.
	 * 
	 */
	// http://localhost:8080/topicos?pagina=0&quantidade=1
	@GetMapping
	public Page<TopicoResponseDTO> lista(@RequestParam(required = false, value = "cursoNome") String cursoNome,
			@RequestParam("pagina") int pagina, @RequestParam("quantidade") int quantidade,
			@RequestParam(required = false, value = "ordenacao") String ordenacao) {

		Pageable pageable = null;

		// Direction.ASC ordena de modo crescente os registros
		if (ordenacao != null)
			pageable = PageRequest.of(pagina, quantidade, Direction.ASC, ordenacao);
		else
			pageable = PageRequest.of(pagina, quantidade);

		Page<Topico> topicos = null;

		if (cursoNome == null)
			topicos = topicoRepository.findAll(pageable);
		else
			topicos = topicoRepository.selecionarTopicosPeloNomeCurso(cursoNome, pageable);

		return TopicoResponseDTO.converter(topicos);
	}

	/**
	 * Outra forma de usar uma requisição GET com paginação.
	 * 
	 * Existe uma forma mais simples no Spring, onde recebemos um objeto de Pageable
	 * no método.
	 * 
	 * Através desse parâmetro podemos definir alguns valores padrões através da
	 * anotação @PageableDefault. Com essa anotação, podemos definir o campo de
	 * ordenação padrão, a direção, número da página e quantidade de itens por
	 * página.
	 * 
	 * 
	 * IMPORTANTE: Para o Spring conseguir pegar os parâmetros de paginação e
	 * ordenação da requisição, ele precisa que o módulo esteja habilitado no
	 * projeto, que é o módulo que faz esse suporte de pegar as coisas da web e
	 * passar para o Spring Data. Esse módulo não vem habilitado no projeto por
	 * padrão. Para habilitarmos isso, assim como quase todos os módulos que vamos
	 * utilizar do Spring Boot (que não vem habilitado por padrão), temos que ir até
	 * aquela classe main.
	 * 
	 * Então, é necessário anotar a classe SpringbootRestApplication com a
	 * anotação @EnableSpringDataWebSupport.
	 * 
	 * Com essa anotação habilitamos esse suporte, para o Spring pegar da
	 * requisição, dos parâmetros da url os campos, as informações de paginação e
	 * ordenação, e repassar isso para o Spring data.
	 * 
	 * -- SOBRE O CACHE --
	 * 
	 * Em cima do método, temos que colocar a anotação @Cacheable, para falar para o
	 * Spring guardar o retorno desse método em cache. Só cuidado na hora de fazer o
	 * import, porque existe a mesma anotação no pacote da JPA. O que vamos utilizar
	 * no curso é do org.springframework.
	 * 
	 * Essa anotação tem um atributo que precisamos preencher. Um chamado value, em
	 * que temos que passar uma string que vai ser o identificador único desse
	 * cache. Na nossa aplicação, posso ter vários métodos anotados com @Cacheable,
	 * e o Spring precisa saber como ele vai diferenciar um do outro. Ele faz isso
	 * utilizando o id único.
	 * 
	 * Esse cache é bem inteligente em gravar as diversas formas de consulta. Por
	 * exemplo: se fizer uma requisição sem parâmetros e outra com parâmetros,
	 * teremos dois caches guardados.
	 * 
	 * 
	 * NOTA: é importante utilizar esse recurso de Cache nos métodos menos
	 * requisitados. Métodos mais requisitados podem comprometer o uso da aplicação,
	 * já que sempre precisaremos invalidar o cache por causa das alterações. Logo,
	 * seria interessante, por exemplo, usar chace em uma tabela de País, Estado,
	 * por exemplo.
	 * 
	 * @param cursoNome
	 * @param pageable
	 * @return
	 */

	// http://localhost:8080/topicos/paginaNoParametro?page=0&size=3&sort=id,asc
	// http://localhost:8080/topicos/paginaNoParametro (por padrão, os parâmetros
	// serão: page=0, size=10, sorte=id,desc)

	@RequestMapping(value = "/paginaNoParametro", method = RequestMethod.GET)
	@Cacheable(value = "listaDeTopicosPorPagina")
	public Page<TopicoResponseDTO> listaComPaginacaoNoParametro(
			@RequestParam(required = false, value = "cursoNome") String cursoNome,
			@PageableDefault(sort = "id", direction = Direction.DESC, page = 0, size = 10) Pageable pageable) {

		Page<Topico> topicos = null;

		if (cursoNome == null)
			topicos = topicoRepository.findAll(pageable);
		else
			topicos = topicoRepository.selecionarTopicosPeloNomeCurso(cursoNome, pageable);

		return TopicoResponseDTO.converter(topicos);
	}

	// Ex.: http://localhost:8080/topicos/titulo?titulo=D%C3%BAvida
	/*
	 * @RequestMapping("/titulo") public List<TopicoResponseDTO>
	 * listaTopicoPorNome(String titulo) { List<Topico> topicos =
	 * topicoRepository.findByTitulo(titulo); return
	 * TopicoResponseDTO.converter(topicos); }
	 */

	/*
	 * A anotação @CacheEvict serve para apagar todas as entradas de um determindo
	 * cache. No caso, vamos apagar todos os caches de "listaDeTopicosPorPagina". O
	 * parâmetro allEntries também se faz necessário, já que teremos vários caches
	 * de mesmo nome, mas com conteúdos diferentes.
	 * 
	 * Essa anotação deve ser utilizados nos métodos: cadastra, edição e remoção.
	 * 
	 * Dessa forma, as seleções (GET) sempre irão buscar os dados atualizados.
	 */
	@PostMapping
	@Transactional
	@CacheEvict(value = "listaDeTopicosPorPagina", allEntries = true)
	public ResponseEntity<TopicoResponseDTO> cadastrar(@RequestBody @Valid TopicoRequestDTO topicoRequestDTO,
			UriComponentsBuilder uriBuilder) {

		Topico topico = topicoRequestDTO.converter(cursoRepository);
		topicoRepository.save(topico);

		URI uri = uriBuilder.path("/topicos/{id}").buildAndExpand(topico.getId()).toUri();

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
	@CacheEvict(value = "listaDeTopicosPorPagina", allEntries = true)
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
	@CacheEvict(value = "listaDeTopicosPorPagina", allEntries = true)
	public ResponseEntity<?> remover(@PathVariable("id") Long id) {

		Optional<Topico> optional = topicoRepository.findById(id);
		if (optional.isPresent()) { // Verifica antes se o registro existe para poder ser excluído
			topicoRepository.deleteById(id);
			return ResponseEntity.ok().build();
		}

		return ResponseEntity.notFound().build(); // Retorna um 404
	}
}
