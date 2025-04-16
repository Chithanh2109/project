package com.skincare.controller;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.skincare.model.Product;
import com.skincare.service.ProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public String listProducts(Model model) {
        model.addAttribute("products", productService.getAllActiveProducts());
        return "products/list";
    }

    @GetMapping("/{id}")
    public String productDetails(@PathVariable Long id, Model model) {
        return productService.getProductById(id)
                .map(product -> {
                    model.addAttribute("product", product);
                    return "products/details";
                })
                .orElse("redirect:/products");
    }

    @GetMapping("/category/{category}")
    public String productsByCategory(@PathVariable String category, Model model) {
        model.addAttribute("products", productService.getProductsByCategory(category));
        model.addAttribute("category", category);
        return "products/by-category";
    }

    @GetMapping("/brand/{brand}")
    public String productsByBrand(@PathVariable String brand, Model model) {
        model.addAttribute("products", productService.getProductsByBrand(brand));
        model.addAttribute("brand", brand);
        return "products/by-brand";
    }

    @GetMapping("/api")
    @ResponseBody
    public ResponseEntity<Page<Product>> getAllProducts(Pageable pageable) {
        return ResponseEntity.ok(productService.findAll(pageable));
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public ResponseEntity<Product> createProduct(@Valid @RequestBody Product product) {
        return ResponseEntity.ok(productService.save(product));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @Valid @RequestBody Product product) {
        return ResponseEntity.ok(productService.update(id, product));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<Product>> getProductsByCategory(@PathVariable Long categoryId, Pageable pageable) {
        return ResponseEntity.ok(productService.findByCategory(categoryId, pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Product>> searchProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            Pageable pageable) {
        return ResponseEntity.ok(productService.search(keyword, categoryId, minPrice, maxPrice, pageable));
    }

    @PatchMapping("/{id}/stock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Product> updateStock(
            @PathVariable Long id,
            @RequestParam Integer quantity) {
        return ResponseEntity.ok(productService.updateStock(id, quantity));
    }
} 