package br.com.totustuus.model.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import br.com.totustuus.model.Topico;
import br.com.totustuus.repository.CursoRepository;

/**
 * POJO resnposável por armazenar as informações recebidas.
 */
public class TopicoRequestDTO {

	/*
	 * Anotações do Bean Validation.
	 * 
	 * Existe anotação para decimais, inteiros e etc.
	 * 
	 * Podemos até mesmo criar nossas próprias anotações de validação.
	 * 
	 * Mas não basta somente anotar os atributos com as anotações, é encessário que
	 * esse objeto seja anotado com @Valid aonde ele esteja sendo recebido. (ver
	 * método cadastrar em TopicoController)
	 */
	@NotNull
	@NotEmpty
	@Length(min = 5, max = 50)
	private String titulo;

	@NotNull
	@NotEmpty
	@Length(min = 15, max = 255)
	private String mensagem;

	@NotNull
	@NotEmpty
	@Length(min = 5, max = 15)
	private String cursoNome;

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}

	public String getCursoNome() {
		return cursoNome;
	}

	public void setCursoNome(String cursoNome) {
		this.cursoNome = cursoNome;
	}

	public Topico converter(CursoRepository cursoRepository) {

		Topico topico = new Topico();
		topico.setTitulo(titulo);
		topico.setMensagem(mensagem);
		topico.setCurso(cursoRepository.findByNome(cursoNome));

		return topico;
	}

}
