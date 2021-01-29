package com.lvldungeons.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.lvldungeons.model.entity.User;
import com.lvldungeons.model.repo.UserRepository;
import com.lvldungeons.service.jwtSecurity.AuthJWT;

@Service
public class UserService {

	// Repositorios
	@Autowired
	private UserRepository userRepo;

	// Servicios
	@Autowired
	private UpdateService updateService;

	private ObjectMapper mapper = new ObjectMapper();

	// Autenticacion de usuario, devolviendo jwt creado a partir de un usuario.
	public JsonNode autenticaUsuario(String username, String password) {
		JsonNode jwt = null;
		try {
			jwt = mapper.readTree(new String("{}"));
			User usuarioAutenticado = userRepo.findByUsernameAndPassword(username, password);
			((ObjectNode) jwt).put("jwt", AuthJWT.generarJWTDesdeId(usuarioAutenticado));
			((ObjectNode) jwt).put("id", usuarioAutenticado.getId());
		} catch (Exception e) {
			return null;
		}
		return jwt;
	}

	// Crear un nuevo user
	public User saveEntity(User sent) {
		if (userRepo.findByEmail(sent.getEmail()) != null) {
			sent = null;
		} else if (userRepo.findByUsername(sent.getUsername()) != null) {
			sent = null;
		} else {
			userRepo.save(sent);
		}
		return sent;
	}

	// Devuelve los datos del usuario autenticado
	public User datosAutenticado(HttpServletRequest request, long id) {
		long idRequest = AuthJWT.getIdUserDesdeRequest(request);
		User user;

		if (idRequest == id) {
			user = userRepo.findById(id).get();
		} else {
			user = null;
		}

		return user;
	}

	// Get de todos los users
	public List<User> getEntity() {
		return (List<User>) userRepo.findAll();
	}

	// Get de un user por id
	public User getEntityById(Long id) {
		return userRepo.findById(id).get();
	}

	// Actualziar un user
	public User updateEntity(Long id, User sent) {
		User us = userRepo.findById(id).get();

		if (us != null) {
			updateService.updateUser(us, sent);
			userRepo.save(us);
		}
		return us;
	}

	// Actualziar un user autenticado
	public User updateEntityAut(HttpServletRequest request, User sent) {
		long id = AuthJWT.getIdUserDesdeRequest(request);
		User us = userRepo.findById(id).get();

		if (us != null) {
			updateService.updateUser(us, sent);
			userRepo.save(us);
		}
		return us;
	}

	// Borrar un user
	public void deleteEntity(Long id) {
		userRepo.deleteById(id);
	}
}
