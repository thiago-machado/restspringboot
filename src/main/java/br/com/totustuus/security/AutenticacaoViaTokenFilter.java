package br.com.totustuus.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import br.com.totustuus.model.Usuario;
import br.com.totustuus.repository.UsuarioRepository;

/**
 * Antes de executar qualquer lógica de autenticação do usuário, é necessário
 * ter esse filtro antes de tal Controller.
 * 
 * Como estamos trabalhando com uma requisição STATELESS, mesmo que o usuário
 * tenha feito login em /auth, o mesmo precisa ser verificado a cada
 * resquisição. Por isso esse filtro estende de OncePerRequestFilter (filtrar
 * uma vez por request).
 * 
 * Para que esse filtro funcione, é necessário registrá-lo na classe
 * SecurityConfigurations, dentro do método configure(). Localizar
 * addFilterBefore() para entender como ele foi registrado.
 * 
 * NOTA: é importante informar a não utilização da validação do Token através da
 * classe AutenticacaoService: a classe AutenticacaoService deve ser utilizada
 * apenas na lógica de autenticação via username/password, para a geração do
 * token.
 * 
 * @author thiago.machado
 *
 */
public class AutenticacaoViaTokenFilter extends OncePerRequestFilter {

	private static final String AUTENTICACAO_TIPO = "Bearer ";
	private static final String HEADER_AUTORIZATION = "Authorization";

	/*
	 * Não dá para injetar esses atributos (TokenService e UsuarioRepository) nessa
	 * classe. Por isso, vamos recebê-los via construtor.
	 * 
	 * Isso ocorre pois essa classe não é um Bean gerenciado pelo Spring.
	 * 
	 * Quem vai injetar esse atributo para nós será a classe que o instanciará. No
	 * caso, SecurityConfigurations.
	 */
	private TokenService tokenService;

	private UsuarioRepository usuarioRepository;

	public AutenticacaoViaTokenFilter(TokenService tokenService, UsuarioRepository usuarioRepository) {
		this.tokenService = tokenService;
		this.usuarioRepository = usuarioRepository;
	}

	/**
	 * Nesse método que estamos sobrescrevendo, temos que colocar nossa lógica para
	 * pegar o token do cabeçalho, verificar se está ok, autenticar no Spring e
	 * chamar filterChain.doFilter no final.
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String token = pegarToken(request);

		if (tokenService.isTokenValido(token)) {
			autenticarUsuario(token);
		}

		/*
		 * Caso o usuário esteja autenticado, os métodos privados deverão ser
		 * executados: inserir/editar/remover.
		 * 
		 * Caso a autenticação falhe, ou o token seja inválido, o Spring retornará 403.
		 */
		filterChain.doFilter(request, response);
	}

	/**
	 * Esse método autentica o usuário.
	 * 
	 * Como o token está válido, agora é só questão de "forçar" a autenticação.
	 * 
	 * Antes de tudo, vamos pegar o ID do usuário que consta no token.
	 * 
	 * Em seguida, criamos o objeto Usuario através do ID.
	 * 
	 * Em seguida, criamos uma instância de UsernamePasswordAuthenticationToken, que
	 * recebe: o Usuario, Senha e Perfis de Autorização.
	 * 
	 * A senha não precisamos passar, já que o token já está válido (por isso o
	 * null).
	 * 
	 * Por fim, forçamos a autenticação através do SecurityContextHolder.
	 * 
	 * @param token
	 */
	private void autenticarUsuario(String token) {
		// usuario, senha, perfil de acesso
		Long id = tokenService.getUsuarioID(token);

		// Não podemos usar o usuarioRepository.getOne(), pois os perfis não serão
		// selecionados pelo Hibernate
		Usuario usuario = usuarioRepository.findById(id).get();

		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(usuario, null,
				usuario.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authentication); // Força a autenticação
	}

	/**
	 * Pegando o token do Request
	 * 
	 */
	private String pegarToken(HttpServletRequest request) {

		String token = request.getHeader(HEADER_AUTORIZATION);

		if (token == null || token.trim().isEmpty() || !token.startsWith(AUTENTICACAO_TIPO)) {
			return null;
		}

		return token.substring(AUTENTICACAO_TIPO.length(), token.length());
	}

}
