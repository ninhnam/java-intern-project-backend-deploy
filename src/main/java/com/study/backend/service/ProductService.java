package com.study.backend.service;

import com.study.backend.entity.Product;
import com.study.backend.user.User;
import com.study.backend.exception.NotFoundException;
import com.study.backend.repository.ProductRepository;
import com.study.backend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;

    public Product createProduct(Product product, Long user_id) {
        User user = userRepository.findById(user_id).orElseThrow(() -> new EntityNotFoundException("User with id " + user_id + " not found"));;
        product.setUser(user);
        return productRepository.save(product);
    }

    public Product updateProduct(Long id, @Valid Product product) {
        Product existingProduct = getProductById(id);
        if (existingProduct == null) {
            throw new NotFoundException("Product with id: " + id + " not existing");
        }
        // Perform any necessary validation
        existingProduct.setName(product.getName());
        existingProduct.setPrice(product.getPrice());
        // Perform any necessary validation
        productRepository.save(existingProduct);
        return existingProduct;
    }

    public List<Product> getAllProduct() {
        return productRepository.findAll();
    }

    public List<Product> getProductsByUserId(Long userId) {
        return productRepository.findByUserId(userId);
    }

    public void deleteProductById(Long id) {
        Product existingProduct = getProductById(id);
        if (existingProduct == null) {
            throw new NotFoundException("Product with id: " + id + " not existing");
        }
        productRepository.deleteById(id);
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }
}
