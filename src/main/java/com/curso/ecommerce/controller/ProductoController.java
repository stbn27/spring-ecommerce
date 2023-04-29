package com.curso.ecommerce.controller;

import java.io.IOException;
import java.util.Optional;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.curso.ecommerce.model.Producto;
import com.curso.ecommerce.model.Usuario;
import com.curso.ecommerce.service.ProductoService;
import com.curso.ecommerce.service.UploadFileService;


@Controller
@RequestMapping("/productos")
public class ProductoController {
	
	private final Logger LOGGER = LoggerFactory.getLogger(ProductoController.class);
	
	@Autowired
	private ProductoService productoService;
	
	@Autowired
	private UploadFileService upload;
	
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
	public String Save(Producto producto, @RequestParam("img") MultipartFile file) throws IOException {
		LOGGER.info("Este es el objeto producto: {}", producto);
		
		Usuario u = new Usuario(1, "", "", "", "", "", "", "");
		producto.setUsuario(u);
		
		//Logica para subir la imagen al servidor:
		if(producto.getId() == null) {	//Cuando se crea un  producto
			String nameImg = upload.SaveImage(file);
			producto.setImagen(nameImg);
		} else {
			if(file.isEmpty()) {	//Edita el producto pero no modifica la imagen
				Producto p = new Producto();
				p = productoService.get(producto.getId()).get();
				producto.setImagen(p.getImagen());
			} else {
				String nameImg = upload.SaveImage(file);
				producto.setImagen(nameImg);
			}
		}
		
		
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
	
	//Actualiza la informacion de un producto en la base datos.
	@PostMapping("/update")
	public String Update(Producto producto, @RequestParam("img") MultipartFile file) throws IOException {
		
		Producto p = new Producto();
		p = productoService.get(producto.getId()).get();
		
		if(file.isEmpty()) {	//Edita el producto pero no modifica la imagen
			producto.setImagen(p.getImagen());
		} else {
			//Eliminar cuando no sea la imagen por defecto
			if(!p.getImagen().equals("default.jpg")) {
				upload.DeleteImage(p.getImagen());
			}
			
			String nameImg = upload.SaveImage(file);
			producto.setImagen(nameImg);
		}
		producto.setUsuario(p.getUsuario());
		productoService.Update(producto);
		
		return "redirect:/productos";
	}
	
	@GetMapping("/delete/{id}")
	public String DeleteProduct(@PathVariable Integer id) {
		
		Producto p = new Producto();
		p = productoService.get(id).get();
		//Eliminar cuando no sea la imagen por defecto
		if(!p.getImagen().equals("default.jpg")) {
			upload.DeleteImage(p.getImagen());
		}
		
		productoService.Delete(id);
		return "redirect:/productos";
	}
}
