package br.com.totustuus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

/**
 * 
 * Para receber os parâmetros de ordenação e paginação diretamente nos métodos
 * do controller, devemos habilitar o módulo SpringDataWebSupport, adicionando a
 * anotação @EnableSpringDataWebSupport.
 * 
 * Acessar o método listaComPaginacaoNoParametro() da classe TopicoController
 * para entender mais sobre a anotação @EnableSpringDataWebSupport.
 * 
 * @EnableCaching habilita o uso de cache na aplicação. Precisa inserir a dependência no pom.
 * 
 * @author thiago.machado
 *
 */
@SpringBootApplication
@EnableSpringDataWebSupport
@EnableCaching
public class SpringbootRestApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootRestApplication.class, args);
	}

}
