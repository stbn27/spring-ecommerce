package com.curso.ecommerce.controller;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.curso.ecommerce.model.Producto;
import com.curso.ecommerce.model.Usuario;
import com.curso.ecommerce.service.ProductoService;


@Controller
@RequestMapping("/productos")
public class ProductoController {
	
	private final Logger LOGGER = LoggerFactory.getLogger(ProductoController.class);
	
	@Autowired
	private ProductoService productoService;
	
	//Vista de productos -- Lista los productos
	@GetMapping(path = {"","/"})
	public String Product(Model model) {
		model.addAttribute("productos", productoService.FindAll());
		return "productos/productos";
	}
	
	//Vista de añadir productos
	@GetMapping(path = {"/create"})
	public String Create() {
		return "productos/create";
	}
	
	//Boton GUARDAR - Guardar informacion en la base de datos. ->Redirecciona a la vista productos nuevamente.
	@PostMapping("/save")
	public String Save(Producto producto) {
		LOGGER.info("Este es el objeto producto: {}", producto);
		
		Usuario u = new Usuario(1, "", "", "", "", "", "", "");
		producto.setUsuario(u);
		
		productoService.Save(producto);
		return "redirect:/productos";
	}
}
