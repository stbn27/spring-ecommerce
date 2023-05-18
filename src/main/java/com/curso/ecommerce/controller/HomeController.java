package com.curso.ecommerce.controller;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.curso.ecommerce.service.IUsuarioService;
import com.curso.ecommerce.service.ProductoService;
import com.curso.ecommerce.model.Producto;
import com.curso.ecommerce.model.Usuario;
import com.curso.ecommerce.model.DetalleOrden;
import com.curso.ecommerce.model.Orden;

@Controller
@RequestMapping("/")
public class HomeController {

	private final Logger log = LoggerFactory.getLogger(HomeController.class);

	@Autowired
	private ProductoService productoService;
	@Autowired
	private IUsuarioService usuarioService;
	
	//Para almacenar los detalles de la orden:
	List<DetalleOrden> detalles = new ArrayList<DetalleOrden>();	
	//Datos de la orden:
	Orden orden = new Orden();
	

	// Vista principal - Home
	@GetMapping("")
	public String home(Model model) {
		model.addAttribute("productos", productoService.FindAll());
		return "usuario/home";
	}

	// Vista detalles del producto:
	@GetMapping("/detallesProducto/{id}")
	public String ProductoHome(@PathVariable Integer id, Model model) {

		log.info("Id producto enviado como parametro {}", id);

		Producto producto = new Producto();
		Optional<Producto> productoOptional = productoService.get(id);
		producto = productoOptional.get();
		model.addAttribute("producto", producto);

		return "usuario/productohome";
	}

	@PostMapping("/cart")
	public String addCart(@RequestParam Integer id, @RequestParam Integer cantidad, Model model) {
		
		DetalleOrden detalle = new DetalleOrden();
		Producto producto = new Producto();
		double sumaTotal = 0;
		
		Optional<Producto> optionalProducto = productoService.get(id);
		log.info("Producto añadido: {}", optionalProducto.get());
		log.info("Cantidad: {}", cantidad);
		
		producto = optionalProducto.get();
		
		detalle.setNombre(producto.getNombre());
		detalle.setCantidad(cantidad);
		detalle.setPrecio(producto.getPrecio());
		detalle.setTotal(producto.getPrecio() *  cantidad);
		detalle.setProducto(producto);
		
		//Validacion para que el producto no se añada dos veces:
		Integer idProducto = producto.getId();
		boolean ingresado = detalles.stream().anyMatch(p -> p.getProducto().getId() == idProducto);
		
		if(!ingresado) {
			detalles.add(detalle);			
		}
				
		sumaTotal = detalles.stream().mapToDouble(dt -> dt.getTotal()).sum();
		orden.setTotal(sumaTotal);
		
		model.addAttribute("cart", detalles);
		model.addAttribute("orden", orden);
		
		return "usuario/carrito";
	}
	
	//Eliminar un producto del carrito:
	@GetMapping("/delete/cart/{id}")
	public String deleteProducto(@PathVariable Integer id, Model model) {
		
		List<DetalleOrden> ordenNueva = new ArrayList<>();
		
		for(DetalleOrden detalleOrden : detalles) {
			if(detalleOrden.getProducto().getId() != id) {
				ordenNueva.add(detalleOrden);
			}
		}
		//Nueva lista con los productos restantes
		detalles = ordenNueva;
		
		double sumaTotal = 0;
		
		sumaTotal = detalles.stream().mapToDouble(dt -> dt.getTotal()).sum();
		orden.setTotal(sumaTotal);
		
		model.addAttribute("cart", detalles);
		model.addAttribute("orden", orden);
		
		return "usuario/carrito";
	}
	
	@GetMapping("/getCart")
	public String getCart(Model model) {
		
		model.addAttribute("cart", detalles);
		model.addAttribute("orden", orden);
		
		return "usuario/carrito";
	}
	
	//Detalles de la orden
	@GetMapping("/orden")
	public String orden(Model model) {
		
		Usuario usuario = usuarioService.findById(1).get();
		
		model.addAttribute("cart", detalles);
		model.addAttribute("orden", orden);
		model.addAttribute("usuario", usuario);
		
		return "usuario/resumenorden";
	}
}
