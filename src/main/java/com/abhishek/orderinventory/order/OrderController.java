package com.abhishek.orderinventory.order;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService service;
    public OrderController(OrderService service) { this.service = service; }
    @GetMapping public List<CustomerOrder> getAll() { return service.findAll(); }
    @GetMapping("/{id}") public CustomerOrder getById(@PathVariable Long id) { return service.findById(id); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public CustomerOrder create(@Valid @RequestBody CreateOrderRequest request) { return service.create(request); }
    @PostMapping("/{id}/cancel") public CustomerOrder cancel(@PathVariable Long id) { return service.cancel(id); }
}
