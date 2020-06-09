package br.com.totustuus.model.dto;

import java.time.LocalDateTime;

import br.com.totustuus.model.Resposta;

public class RespostaResponseDTO {

	private Long id;
	private String mensagem;
	private LocalDateTime dataCriacao;
	private String autor;

	public RespostaResponseDTO(Resposta resposta) {
		id = resposta.getId();
		mensagem = resposta.getMensagem();
		dataCriacao = resposta.getDataCriacao();
		autor = resposta.getAutor().getNome();
	}

	public Long getId() {
		return id;
	}

	public String getMensagem() {
		return mensagem;
	}

	public LocalDateTime getDataCriacao() {
		return dataCriacao;
	}

	public String getAutor() {
		return autor;
	}

}
