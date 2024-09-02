package com.rensystem.apirest.apirest.Controllers;

import com.rensystem.apirest.apirest.Entities.Product;
import com.rensystem.apirest.apirest.Repositories.ProductRepository;
import com.rensystem.apirest.apirest.dto.ProductDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    // Convierte un Product en un ProductDTO
    private ProductDTO convertToDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setNombre(product.getNombre());
        dto.setPrecio(product.getPrecio());
        return dto;
    }

    // Convierte un ProductDTO en un Product
    private Product convertToEntity(ProductDTO dto) {
        Product product = new Product();
        product.setId(dto.getId());
        product.setNombre(dto.getNombre());
        product.setPrecio(dto.getPrecio());
        return product;
    }

    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        //List<Product> products = productRepository.findAll();
        List<Product> products = productRepository.findAllByOrderByOrdenAsc();
        List<ProductDTO> productDTOs = products.stream().map(this::convertToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(productDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductByID(@PathVariable Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontró el producto con el ID: " + id));
        return ResponseEntity.ok(convertToDTO(product));
    }

    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@RequestBody ProductDTO productDTO) {
        Product product = convertToEntity(productDTO);
        Product createdProduct = productRepository.save(product);
        return ResponseEntity.status(201).body(convertToDTO(createdProduct)); // 201 Created
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id, @RequestBody ProductDTO productDTO) {
        Product productFinded = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontró el producto con el ID: " + id));

        productFinded.setNombre(productDTO.getNombre());
        productFinded.setPrecio(productDTO.getPrecio());

        Product updatedProduct = productRepository.save(productFinded);
        return ResponseEntity.ok(convertToDTO(updatedProduct));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        Product productFinded = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontró el producto con el ID: " + id));

        productRepository.delete(productFinded);

        return ResponseEntity.ok().body("{\"message\": \"El producto con el ID: " + id + " fue eliminado correctamente\"}");
    }

    @PutMapping("/order")
    public ResponseEntity<?> updateProductOrder(@RequestBody List<Long> productIds) {
        List<Product> products = productRepository.findAllById(productIds);

        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            product.setOrden(i); // Asigna el nuevo orden basado en la posición en la lista
        }

        productRepository.saveAll(products);

        return ResponseEntity.ok().build();
    }

    /*@PutMapping("/order")
    public ResponseEntity<List<ProductDTO>> updateProductOrder(@RequestBody List<ProductDTO> productDTOs) {
        List<Product> products = productDTOs.stream()
                .map(this::convertToEntity)
                .collect(Collectors.toList());

        productRepository.saveAll(products);

        List<ProductDTO> updatedProducts = products.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(updatedProducts);
    }*/


}