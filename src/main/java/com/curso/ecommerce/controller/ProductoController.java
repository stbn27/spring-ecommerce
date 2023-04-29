package com.curso.ecommerce.controller;

import java.util.Optional;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
	
	//Modificar informacion de un producto:
	@GetMapping(path = "/edit/{id}")
	public String Edit(@PathVariable Integer id, Model model) {
		
		Producto producto = new Producto();
		Optional<Producto> optionalProducto = productoService.get(id);
		producto = optionalProducto.get();
		LOGGER.info("Producto buscado: {}", producto);
		model.addAttribute("producto", producto);
		return "productos/edit";
	}
	
	@PostMapping("/update")
	public String Update(Producto producto) {
		productoService.Update(producto);
		return "redirect:/productos";
	}
	
	@GetMapping("/delete/{id}")
	public String DeleteProduct(@PathVariable Integer id) {
		productoService.Delete(id);
		return "redirect:/productos";
	}
}
