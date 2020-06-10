package br.com.totustuus.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Tenho que habilitar a parte do Spring security. Para fazer isso, fazemos na
 * própria classe. Existe uma anotação chamada @EnableWebSecurity. Como essa é
 * uma classe que tem configurações, precisamos colocar a
 * anotação @Configuration. O Spring vai carregar e ler as configurações que
 * estiverem dentro dessa classe.
 * 
 * Além disso, vamos ter que herdar essa classe de outra classe do Spring
 * chamada WebSecurityConfigurerAdapter. Essa classe tem alguns métodos para
 * fazer as configurações que vamos sobrescrever posteriormente.
 * 
 * Em resumo: Nós colocamos a dependência do Spring security no projeto, criamos
 * essa classe, anotada com @EnableWebSecurity, com @Configuration. Depois,
 * vamos colocar as configurações de segurança. Por enquanto está vazio, mas só
 * de ter feiro isso, habilitamos a parte de segurança. Por padrão, o Spring
 * bloqueia todo acesso à nossa API. Tudo está restrito até que eu faça a
 * configuração e libere o que precisa ser liberado.
 * 
 * @author thiago.machado
 *
 */
@EnableWebSecurity
@Configuration
public class SecurityConfigurations extends WebSecurityConfigurerAdapter {

	@Autowired
	private AutenticacaoService autenticacaoService;

	/**
	 * Configurações de autenticação (login).
	 * 
	 * Vamos usar o método userDetailsService do AuthenticationManagerBuilder.
	 * 
	 * Esse método pede uma classe que implemente UserDetailsService.
	 * 
	 * Essa classe foi criada e se chama AutenticacaoService. A mesma está sendo
	 * injetada logo acima.
	 * 
	 * Acessar a classe AutenticacaoService para mais detalhes.
	 * 
	 * Além disso, estamos usando o método passwordEncoder, passando uma instância
	 * de BCryptPasswordEncoder. O que essa linha faz é encriptar a senha digitada
	 * pelo usuário usando BCrypt. Obviamente, a senha na base de dados deve estar
	 * cifrada em BCrypt também (ver arquivo data.sql).
	 */
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(autenticacaoService).passwordEncoder(new BCryptPasswordEncoder());
	}

	/**
	 * Configuração de autorização (quem tem acesso a determinada URL)
	 */
	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {

		/*
		 * Liberando acesso aos endpoints via GET: "/topicos" e "/topicos/*".
		 * 
		 * Com isso, conseguimos chamar os métodos GET para listagem geral e individual
		 * dos tópicos sem precisa de login, ou algo do tipo.
		 * 
		 * Para as outras URLS, é preciso autenticação: anyRequest().authenticated()
		 * 
		 * Para isso, um formulário será aberto: and().formLogin().
		 * 
		 * É importante também termos classes que herdem de UserDetails e
		 * GrantedAuthority.
		 * 
		 * Para isso, vamos utilizar o model Usuario e Perfil. (ver essas classes).
		 * 
		 * Essas duas classes citadas serão utilizadas para autenticação.
		 * 
		 */
		httpSecurity.authorizeRequests().antMatchers(HttpMethod.GET, "/topicos").permitAll()
				.antMatchers(HttpMethod.GET, "/topicos/*").permitAll().anyRequest().authenticated().and().formLogin();
	}

	/**
	 * Configurações de recursos estáticos (CSS, JS, Imagens).
	 * 
	 * Normalmente, ignoramos os bloqueios desses recursos.
	 * 
	 * Nesse projeto, não vamos utilizar!!!
	 */
	@Override
	public void configure(WebSecurity web) throws Exception {

	}
}
