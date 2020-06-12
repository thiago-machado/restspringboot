package br.com.totustuus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

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
 * @EnableSwagger2 habilita o Swagger no projeto. Precisa inserir as dependências no pom.
 * Além disso, foi preciso criar a classe SwaggerConfigurations e habilitar o acesso ao swagger no 
 * SecurityConfigurations.
 * 
 * @author thiago.machado
 *
 */
@SpringBootApplication
@EnableSpringDataWebSupport
@EnableCaching
@EnableSwagger2
public class SpringbootRestApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootRestApplication.class, args);
	}

}
