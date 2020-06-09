package br.com.totustuus.model.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import br.com.totustuus.model.Topico;

/**
 * POJO resnposável por enviar as informações solicitadas.
 * 
 * Classe que representa os dados que queremos devolver nos endpoints.
 * 
 * E é uma classe de valor, que possui somente os atributos que devolveremos.
 * Geralmente, o pessoal utiliza o padrão DTO, Data Transfer Object, ou o padrão
 * VO, Value Object, para esse tipo de classe.
 */
public class TopicoResponseDTO {

	private Long id;
	private String titulo;
	private String mensagem;
	private LocalDateTime dataCriacao;

	public TopicoResponseDTO(Topico topico) {
		id = topico.getId();
		titulo = topico.getTitulo();
		mensagem = topico.getMensagem();
		dataCriacao = topico.getDataCriacao();
	}

	public Long getId() {
		return id;
	}

	public String getTitulo() {
		return titulo;
	}

	public String getMensagem() {
		return mensagem;
	}

	public LocalDateTime getDataCriacao() {
		return dataCriacao;
	}

	/*
	 * Convertendo uma lista de Topico para TopicoDTO
	 */
	public static List<TopicoResponseDTO> converter(List<Topico> topicoLista) {
		return topicoLista.stream().map((topico) -> new TopicoResponseDTO(topico)).collect(Collectors.toList());
	}

}
