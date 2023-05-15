package com.study.backend.controller;

import com.study.backend.entity.Product;
import com.study.backend.exception.BadRequestException;
import com.study.backend.exception.ForbiddenException;
import com.study.backend.exception.NotFoundException;
import com.study.backend.request.ResponseData;
import com.study.backend.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ResponseData<String>> handleBadRequestException(BadRequestException ex) {
        ResponseData<String> response = new ResponseData<>("Error", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ResponseData<String>> handleNotFoundException(NotFoundException ex) {
        ResponseData<String> response = new ResponseData<>("Error", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ResponseData<String>> handleForbiddenException(NotFoundException ex) {
        ResponseData<String> response = new ResponseData<>("Error", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }


    @GetMapping
    public ResponseEntity<ResponseData<List<Product>>> getAllProduct() {
        List<Product> products = productService.getAllProduct();
        ResponseData<List<Product>> response = new ResponseData<>("Success", products);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}")
    public ResponseEntity<ResponseData<Product>> createProduct(@PathVariable Long id, @RequestBody Product product) {
        Product createdProduct = productService.createProduct(product, id);
        ResponseData<Product> response = new ResponseData<>("Create success", createdProduct);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ResponseData<List<Product>>> getProductsByUserId(@PathVariable Long userId) {
        List<Product> products = productService.getProductsByUserId(userId);
        ResponseData<List<Product>> response = new ResponseData<>("Success", products);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseData<Product>> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        Product productUpdate = productService.updateProduct(id, product);
        ResponseData<Product> response = new ResponseData<>("Edit success", productUpdate);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseData<String>> deleteProductById(@PathVariable Long id) {
        productService.deleteProductById(id);
        ResponseData<String> response = new ResponseData<>("Delete success", "Delete product with id = " + id);
        return ResponseEntity.ok(response);
    }
}