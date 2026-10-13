package com.abhishek.orderinventory.product;

import com.abhishek.orderinventory.shared.ConflictException;
import com.abhishek.orderinventory.shared.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ProductService {
    private final ProductRepository repository;
    public ProductService(ProductRepository repository) { this.repository = repository; }

    @Transactional(readOnly = true)
    public List<Product> findAll() { return repository.findAll(); }

    @Transactional(readOnly = true)
    public Product findById(Long id) { return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + id)); }

    @Transactional
    public Product create(ProductRequest request) {
        if (repository.existsBySku(request.sku())) throw new ConflictException("A product with SKU '" + request.sku() + "' already exists");
        return repository.save(new Product(request.name(), request.sku(), request.category(), request.price(), request.stockQuantity()));
    }

    @Transactional
    public Product update(Long id, ProductRequest request) {
        Product product = findById(id);
        repository.findBySku(request.sku()).filter(existing -> !existing.getId().equals(id)).ifPresent(existing -> { throw new ConflictException("A product with SKU '" + request.sku() + "' already exists"); });
        product.setName(request.name()); product.setSku(request.sku()); product.setCategory(request.category());
        product.setPrice(request.price()); product.setStockQuantity(request.stockQuantity());
        return repository.save(product);
    }

    @Transactional
    public void delete(Long id) { repository.delete(findById(id)); }
}
