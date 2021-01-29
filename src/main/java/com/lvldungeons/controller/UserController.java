package com.lvldungeons.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.lvldungeons.model.entity.User;
import com.lvldungeons.service.UserService;
import com.lvldungeons.service.Error.ManejoErrores;


@RestController
@RequestMapping(path = "user")
public class UserController {

	@Autowired 
	private UserService userService; 
	
	@Autowired 
	private ManejoErrores errorService;

	/**
	 * Puede devolver un JSON con un error y un mensaje si el login no es correcto.
	 * 
	 * @param username String
	 * @param password String
	 * @return Token de sesion.
	 */
	@GetMapping("")
	public ResponseEntity<?> login(@RequestParam String username, @RequestParam String password){
		ResponseEntity<?> response;
		
		if (username.equals("") || password.equals("")) {
			response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorService.generarError(1));
		
		} else {
			JsonNode jwt = userService.autenticaUsuario(username, password);
			if (jwt == null) {			
				response = ResponseEntity.status(HttpStatus.ACCEPTED).body(errorService.generarError(3));	
			} else {
				response = ResponseEntity.status(HttpStatus.ACCEPTED).body(jwt);	
			}
		}
		return response;
	}
	
	/**
	 * Devuelve el usuario recien creado, o un codigo de error y un mensaje.
	 * 
	 * @param user User
	 * @return User.
	 */
	@PostMapping("")
	public ResponseEntity<?> register(@RequestBody User user) {
		ResponseEntity<?> response;
		
		if (user.getUsername().isBlank() || user.getPassword().isBlank() || user.getEmail().isBlank()) {
			response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorService.generarError(1));			
		} else {			
			User postUser = userService.saveEntity(user);
			if (postUser == null) {
				response = ResponseEntity.status(HttpStatus.ACCEPTED).body(errorService.generarError(2));
			} 
			else {
				response = ResponseEntity.status(HttpStatus.ACCEPTED).body(postUser);	
			}
		}

		return response;
	}
	
	/**
	 * Te devuelve un usuario que use un parametro Authorization correcto en la peticion
	 * 
	 * @param user User
	 * @return User.
	 */
	@GetMapping("/{id}")
	public ResponseEntity<?> getUserWithToken(HttpServletRequest request, @PathVariable long id) {
		ResponseEntity<?> response = null;
		User user = userService.datosAutenticado(request, id);
		if(request != null && user != null) {
			response = ResponseEntity.status(HttpStatus.ACCEPTED).body(user);
		}else {
			ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorService.generarError(1));
		}
		return response;
	}
	
	
	/*
	 *    METODOS DEPRECATED
	 *    
	 *    Aun no cumplen ninguna funcion o solo tienen sentido para realizar pruebas en el entorno de desarrollo.
	 */
	
	// Obtener todos los usuarios
	@GetMapping("all")
	public ResponseEntity<?> getAllUsers() {
		ResponseEntity<?> response;
		response = ResponseEntity.status(HttpStatus.OK).body(userService.getEntity());
		return response;
	}
	
	// Actualizar un usuario
	@PutMapping("{id}")
	public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User user) {
		ResponseEntity<?> response;
		response = ResponseEntity.status(HttpStatus.OK).body(userService.updateEntity(id, user));
		
		return response;
	}
	
	@PutMapping("")
	public ResponseEntity<?> updateUserAut(HttpServletRequest request, @RequestBody User user) {
		ResponseEntity<?> response;
		response = ResponseEntity.status(HttpStatus.OK).body(userService.updateEntityAut(request, user));
		return response;
	}
	
	
	// Borrar un usuario
	@DeleteMapping("{id}")
	public ResponseEntity<?> deleteUser(@PathVariable Long id) {
		ResponseEntity<?> response;
		userService.deleteEntity(id);
		response = ResponseEntity.status(HttpStatus.OK).body("Se ha eliminado la entidad: " + id.toString());
		
		return response;
	}
}
