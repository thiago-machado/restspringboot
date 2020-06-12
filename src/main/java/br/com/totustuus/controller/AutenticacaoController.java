package br.com.totustuus.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.totustuus.model.dto.LoginRequestDTO;
import br.com.totustuus.model.dto.TokenDTO;
import br.com.totustuus.security.TokenService;

/**
 * Controller responsável pelo login
 * 
 * @author thiago.machado
 *
 */
@RestController
@RequestMapping("/auth")
public class AutenticacaoController {

	/*
	 * Essa injeção de dependência apenas é possível pois sibrevemos o método
	 * authenticationManager() da classe SecurityConfigurations e anotamos esse
	 * método sobrecrito com um @Override.
	 */
	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private TokenService tokenService;

	/**
	 * Esse método ficará responsável pela autenticação. Iremos receber e-mail e
	 * senha que serão armazenados em LoginRequestDTO.
	 * 
	 * Precisamos, à partir desse objeto, criar uma instância de
	 * UsernamePasswordAuthenticationToken.
	 * 
	 * Com esse objeto criado, chamamos o método authenticate() de
	 * AuthenticationManager. Quando for executado essa linha, automaicamente o
	 * Spring vai entrar no método loadUserByUsername() da classe
	 * AutenticacaoService e fará as validações que já implemetamos.
	 * 
	 * @param loginRequestDTO
	 * @return
	 */
	// http://localhost:8080/auth
	@PostMapping
	public ResponseEntity<TokenDTO> autenticar(@RequestBody @Valid LoginRequestDTO loginRequestDTO) {

		UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = loginRequestDTO.converter();

		try {
			Authentication authenticate = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
			String token = tokenService.gerarToken(authenticate);

			/*
			 * Além do token, também precisamos dizer para o cliente qual o tipo de
			 * autenticação e como ele vai fazer a autenticação nas próximas requisições.
			 * 
			 * No HTTP existe uma parte que fala sobre autenticação. Há vários métodos. Um
			 * deles é o Bearer. Junto com o token, vamos mandar para ele o tipo de
			 * autenticação que ele tem que fazer nas próximas requisições.
			 * 
			 * Bearer é um dos mecanismos de autenticação utilizados no protocolo HTTP, tal
			 * como o Basic e o Digest.
			 */

			return ResponseEntity.ok(new TokenDTO(token, "Bearer"));
		} catch (AuthenticationException ex) {
			return ResponseEntity.badRequest().build();
		}
	}
}
