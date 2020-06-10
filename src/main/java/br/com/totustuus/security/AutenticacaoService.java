package br.com.totustuus.security;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.com.totustuus.model.Usuario;
import br.com.totustuus.repository.UsuarioRepository;

/**
 * Essa classe é um serviço de autenticação do Spring.
 * 
 * Essa classe deve implementar UserDetailService para que o Spring possa
 * encontrá-la e saber o que deve fazer.
 * 
 * @author thiago.machado
 *
 */
@Service
public class AutenticacaoService implements UserDetailsService {

	@Autowired
	private UsuarioRepository UsuarioRepository;

	/*
	 * Quando passamos essa classe como parâmetro em userDetailsService() da classe
	 * AuthenticationManagerBuilder (consultar classe SecurityConfigurations, método
	 * configure()), o Spring saberá que esse método deverá ser chamado para validar
	 * o usuário que está tentando se autenticar.
	 * 
	 * Esse método apenas nos traz o username e não o pass (senha). Isso corre pois,
	 * o Spring, precisa apenas saber se existe o usuário com esse e-mail no
	 * sistema. Se existir, devemos retornar o mesmo. A senha ele mesmo irá validar!
	 * 
	 * Caso o usuário não exista, devemos lançar uma exceção!
	 * 
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<Usuario> usuario = UsuarioRepository.findByEmail(username);

		if (usuario.isPresent()) {
			return usuario.get();
		}

		throw new UsernameNotFoundException("Dados inválidos.");
	}

}
