package br.com.totustuus.model.dto;

public class ErroRequestDTO {

	private String campo;
	private String erro;

	public ErroRequestDTO(String campo, String erro) {
		this.campo = campo;
		this.erro = erro;
	}

	public String getCampo() {
		return campo;
	}

	public String getErro() {
		return erro;
	}

}
