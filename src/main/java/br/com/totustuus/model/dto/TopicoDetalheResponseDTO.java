package br.com.totustuus.model.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import br.com.totustuus.model.StatusTopico;
import br.com.totustuus.model.Topico;

public class TopicoDetalheResponseDTO {

	private Long id;
	private String titulo;
	private String mensagem;
	private LocalDateTime dataCriacao;
	private StatusTopico status;
	private String usuario;
	private List<RespostaResponseDTO> respostas;

	public TopicoDetalheResponseDTO(Topico topico) {
		id = topico.getId();
		titulo = topico.getTitulo();
		mensagem = topico.getMensagem();
		dataCriacao = topico.getDataCriacao();
		status = topico.getStatus();
		usuario = topico.getAutor().getNome();
		respostas = new ArrayList<RespostaResponseDTO>();
		
		// Convertendo uma lista de Resposta em RespostaResponseDTO
		respostas.addAll(
				topico.getRespostas().stream().map(r -> new RespostaResponseDTO(r)).collect(Collectors.toList()));
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

	public StatusTopico getStatus() {
		return status;
	}

	public String getUsuario() {
		return usuario;
	}

	public List<RespostaResponseDTO> getRespostas() {
		return respostas;
	}

}
