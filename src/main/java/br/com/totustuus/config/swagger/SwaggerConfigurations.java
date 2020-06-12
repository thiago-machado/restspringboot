package br.com.totustuus.config.swagger;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import br.com.totustuus.model.Usuario;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * Para permitir acessar a URL do Swagger, é necessário habilitar o acesso 
 * na classe SecurityConfigurarion no método configure(WebSecurity).
 * 
 * Para acessar o swagger no browser: http://localhost:8080/swagger-ui.html
 * @author thiago.machado
 *
 */
@Configuration
public class SwaggerConfigurations {

	/**
	 * Dentro do método tenho que instanciar esse objeto docket e 
	 * setar todas as informações que o SpringFox Swagger precisa 
	 * para configurar nosso projeto.
	 * @return
	 */
	@Bean
	public Docket forumAPI() {
		return new Docket(DocumentationType.SWAGGER_2) // Tipo da Documentação
				.select()
				.apis(RequestHandlerSelectors.basePackage("br.com.totustuus")) // Dizer qual o pacote raiz que ele vai ler para montar a documentação
				.paths(PathSelectors.ant("/**")) // Pode ler todos os endereços
				.build()
				.ignoredParameterTypes(Usuario.class) // Como nossa classe Usuario possui atributos relacionados ao login, senha e perfis de acesso, não é recomendado que essas informações sejam expostas na documentação do Swagger.
				// Permitindo ao Swagger que mais um parâmetro seja acrescentado na exibição de cada endpoint
				.globalOperationParameters(
                        Arrays.asList(
                                new ParameterBuilder()
                                    .name("Authorization") // Nome do parâmetro
                                    .description("Header para Token JWT") // Descrição
                                    .modelRef(new ModelRef("string")) // Tipo da informação
                                    .parameterType("header") // Tipo do parâmetro
                                    .required(false) // Não é obrigatório (pois tem endpoint que não é necessário)
                                    .build()));
	}
}
