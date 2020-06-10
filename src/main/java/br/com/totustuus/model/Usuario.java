package br.com.totustuus.model;

import java.util.Collection;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * UserDetails é a interface para dizer que essa é a classe que tem detalhes de
 * um usuário.
 * 
 * Vamos precisar implementar alguns métodos (consultar abaixo).
 * 
 */
@Entity
public class Usuario implements UserDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String nome;
	private String email;
	private String senha;

	/*
	 * Para o Spring security, além de ter uma classe usuário, precisa ter uma
	 * classe também que representa o perfil do usuário. Qual o perfil relacionado
	 * com as permissões de acesso dele.
	 * 
	 * Um usuário pode ter mais de um perfil e o mesmo perfil pode estar atrelado a
	 * mais de um usuário.
	 * 
	 * Por padrão, na JPA, todo relacionamento que é ManyToMany, se eu carregar do
	 * banco de dados, ele não carrega a lista, porque é lazy, só que eu vou colocar
	 * o fetch para ser Eager, porque quando eu carregar o usuário já carrego a
	 * lista de perfis, porque vou precisar dos perfis de acesso do usuário.
	 */
	@ManyToMany(fetch = FetchType.EAGER)
	private List<Perfil> perfis;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Usuario other = (Usuario) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	// MÉTODOS HERDADOS DE UserDetails

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return perfis;
	}

	@Override
	public String getPassword() {
		return senha;
	}

	@Override
	public String getUsername() {
		return email;
	}

	// Esses outros métodos não vamos usar, por isso, retornamos true em tudo
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

}
