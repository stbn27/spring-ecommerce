package com.curso.ecommerce.service;

import java.util.Optional;

import com.curso.ecommerce.model.Producto;

public interface ProductoService {
	
	public Producto Save(Producto producto);
	public Optional<Producto> get(Integer id);
	public void Update(Producto producto);
	public void Delete(Integer id);
}
