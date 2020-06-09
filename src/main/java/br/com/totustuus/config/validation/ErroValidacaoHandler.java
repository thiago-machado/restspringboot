package br.com.totustuus.config.validation;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import br.com.totustuus.model.dto.ErroRequestDTO;

/**
 * Essa classe é um interceptador. Toda vez que acontecer uma exception, em
 * qualquer método, o Spring automaticamente vai chamar esse interceptador, onde
 * fazemos o tratamento apropriado.
 * 
 * Esse interceptador é chamado de Controller Advice.
 * 
 * Agora precisamos dizer para o Spring que essa classe é um controller advice.
 * Existe a anotação @RestControllerAdvice, porque estamos usando REST
 * controllers. Agora, precisamos ensinar para o Spring que esse controller
 * advice vai fazer tratamentos de erros, para quando tiver uma exceção.
 * 
 * Obs.: não é necessário sinalizar o Controller sobre esse Advice! O Spring já
 * saberá que quando ocorrer uma exceção do tipo
 * MethodArgumentNotValidException, ele cairá no método desse interceptador.
 */
@RestControllerAdvice
public class ErroValidacaoHandler {

	/*
	 * Essa classe te ajuda a pegar mensagens de erro de acordo com o idioma que o
	 * cliente requisitar.
	 */
	@Autowired
	private MessageSource messageSource;

	/*
	 * Precisamos dizer para o Spring que esse método deve ser chamado quando houver
	 * uma exceção dentro de algum controller. Para falar isso para o Spring, em
	 * cima do método vamos colocar uma anotação.
	 * 
	 * Temos que passar para o parâmetro que tipo de exceção que quando acontecer
	 * dentro do controller o Spring vai direcionar para o método.
	 * 
	 * No nosso caso, é exceção de validação do Bean Validation. Quando dá um erro
	 * de validação, que exceção o Spring lança? Uma exception chamada
	 * MethodArgumentNotValidException. Ou seja, passamos o nome da classe no
	 * parâmetro. Agora o Spring sabe que se acontecer essa execption em qualquer
	 * REST controller, ele vai cair nesse método.
	 * 
	 * A anotação @ResponseStatus define o código de status da requisição. No caso,
	 * como é uma exceção, sempre retornará 400.
	 */
	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public List<ErroRequestDTO> handle(MethodArgumentNotValidException exception) {

		List<ErroRequestDTO> erroRequestLista = new ArrayList<ErroRequestDTO>();
		List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();

		/*
		 * Iterando a lista de erros.
		 * 
		 * Para cada erro, cria-se um objeto de ErroRequestDTO e atribui a lista.
		 * 
		 * A mensagem de erro passa antes por messageSource, que fará a
		 * internacionalização.
		 * 
		 * Por fim, retornamos nossa lista de erros.
		 */
		fieldErrors.stream().forEach((erro) -> {
			ErroRequestDTO erroRequestDTO = new ErroRequestDTO(erro.getField(),
					messageSource.getMessage(erro, LocaleContextHolder.getLocale()));
			erroRequestLista.add(erroRequestDTO);
		});

		return erroRequestLista;
	}
}
