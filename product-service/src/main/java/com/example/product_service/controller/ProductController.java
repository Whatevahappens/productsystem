package com.example.product_service.controller;

import com.example.product_service.dto.ProductDTO;
import com.example.product_service.model.OrderItem;
import com.example.product_service.service.ProductService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;



/// 20:00
@Controller
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping("/")
    public String home(Model model, HttpSession session) {
        model.addAttribute("products", productService.getAllProducts());
        initializeCartIfEmpty(session);
        return "index";
    }

    @GetMapping("/products")
    public String listProducts(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "products";
    }

    @GetMapping("/products/add")
    public String showAddProductForm(Model model) {
        model.addAttribute("product", new ProductDTO());
        return "add-product";
    }

    @PostMapping("/products/add")
    public String addProduct(@ModelAttribute ProductDTO productDTO) {
        productService.createProduct(productDTO);
        return "redirect:/products";
    }

    @GetMapping("/products/edit/{id}")
    public String showEditProductForm(@PathVariable String id, Model model) {
        ProductDTO productDTO = productService.getProductById(id);
        model.addAttribute("product", productDTO);
        return "edit-product";
    }

    @PostMapping("/products/edit/{id}")
    public String updateProduct(@PathVariable String id, @ModelAttribute ProductDTO productDTO) {
        productService.updateProduct(id, productDTO);
        return "redirect:/products";
    }

    @GetMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable String id) {
        productService.deleteProduct(id);
        return "redirect:/products";
    }

    @PostMapping("/cart/add/{id}")
    public String addToCart(@PathVariable String id, @RequestParam int quantity, HttpSession session) {
        ProductDTO product = productService.getProductById(id);
        if (product != null) {
            List<OrderItem> cart = (List<OrderItem>) session.getAttribute("cart");
            if (cart == null) {
                cart = new ArrayList<>();
            }

            // Check if product already in cart
            boolean found = false;
            for (OrderItem item : cart) {
                if (item.getProductId().equals(id)) {
                    item.setQuantity(item.getQuantity() + quantity);
                    found = true;
                    break;
                }
            }

            if (!found) {
                OrderItem newItem = new OrderItem();
                newItem.setProductId(id);
                newItem.setProductName(product.getName());
                newItem.setQuantity(quantity);
                newItem.setPrice(product.getPrice());
                cart.add(newItem);
            }

            session.setAttribute("cart", cart);
        }
        return "redirect:/";
    }

    @GetMapping("/cart")
    public String viewCart(Model model, HttpSession session) {
        initializeCartIfEmpty(session);
        model.addAttribute("cart", session.getAttribute("cart"));
        return "cart";
    }

    @GetMapping("/cart/remove/{index}")
    public String removeFromCart(@PathVariable int index, HttpSession session) {
        List<OrderItem> cart = (List<OrderItem>) session.getAttribute("cart");
        if (cart != null && index >= 0 && index < cart.size()) {
            cart.remove(index);
            session.setAttribute("cart", cart);
        }
        return "redirect:/cart";
    }

    @GetMapping("/checkout")
    public String checkout(Model model, HttpSession session) {
        initializeCartIfEmpty(session);
        model.addAttribute("cart", session.getAttribute("cart"));
        return "checkout";
    }

    @PostMapping("/checkout")
    public String processCheckout(HttpSession session) {
        // In a real application, you would process the order here
        session.removeAttribute("cart");
        return "redirect:/?orderSuccess";
    }

    private void initializeCartIfEmpty(HttpSession session) {
        if (session.getAttribute("cart") == null) {
            session.setAttribute("cart", new ArrayList<OrderItem>());
        }
    }
}