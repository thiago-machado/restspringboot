package br.com.totustuus.security;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import br.com.totustuus.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class TokenService {

	/*
	 * Esses dois atributos estão em application.properties!
	 * 
	 * @Value("${chave}") = significa que estamos buscando por uma chave 
	 * dentro do arquivo application.properties.
	 */
	
	@Value("${forum.jwt.expiration}")
	private String expiracao;
	
	@Value("${forum.jwt.secret}")
	private String segredo;
	
	public String gerarToken(Authentication authenticate) {
		
		Date dataExpiracao = new Date(new Date().getTime() + Long.valueOf(expiracao));
		
		// Pegando o usuário de Authenticate
		Usuario usuario = (Usuario) authenticate.getPrincipal();
		
		/**
		 * setIssuer = quem é que está gerando o token. Vou colocar que foi 
		 * a "API do Fórum", porque aí o cliente consegue identificar 
		 * quem foi que fez a geração.
		 * 
		 * setSubject = o usuário logado. No caso, vamos usar o ID
		 * 
		 * setIssuedAt = data de geração do token
		 * 
		 * setExpiration = data de expiração do token
		 * 
		 * signWith = pela especificação JSON webtoken, o token tem que ser 
		 * criptografado. Preciso dizer para ele quem é o algoritmo de criptografia 
		 * e a senha da minha aplicação, que é usada para fazer a assinatura e 
		 * gerar o REST da criptografia do token. 
		 * É aqui que vou injetar o secret, que está no 
		 * application.properties.
		 * 
		 * compact = gera a String
		 */
		return Jwts.builder()
			.setIssuer("API de fórum")
			.setSubject(usuario.getId().toString())
			.setIssuedAt(new Date())
			.setExpiration(dataExpiracao)
			.signWith(SignatureAlgorithm.HS256, segredo)
			.compact();
	}

	/**
	 * Verifica se o token é válido.
	 * 
	 * Usando o Jwts para verificar se o token é válido.
	 * 
	 * parse() = é o método que tem a lógica para fazer o passe 
	 * de um token. Você passa para ele um token, ele vai descriptografar 
	 * e verificar se está ok.
	 * 
	 * setSigningKey() = recebe a secret que utilizamos para gerar o token
	 * 
	 * parseClaimsJws() = recebe o token.  Esse método devolve o Jws claims, 
	 * que é um objeto onde consigo recuperar o token e as informações que 
	 * setei dentro do token. Mas quando eu fizer essa chamada, se o 
	 * token estiver válido, ele devolve o objeto. Se estiver inválido, ou nulo, 
	 * ele joga uma exception.
	 * 
	 * 
	 * @param token
	 * @return
	 */
	public boolean isTokenValido(String token) {
		
		try {
			Jwts.parser().setSigningKey(segredo).parseClaimsJws(token);
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return false;
	}

	public Long getUsuarioID(String token) {
		Claims claims = Jwts.parser().setSigningKey(segredo).parseClaimsJws(token).getBody();
		return Long.valueOf(claims.getSubject());
	}

}
