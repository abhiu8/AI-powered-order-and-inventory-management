package com.abhishek.orderinventory.order;

import com.abhishek.orderinventory.product.Product;
import com.abhishek.orderinventory.product.ProductRepository;
import com.abhishek.orderinventory.shared.BadRequestException;
import com.abhishek.orderinventory.shared.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {
    private final CustomerOrderRepository orderRepository;
    private final ProductRepository productRepository;
    public OrderService(CustomerOrderRepository orderRepository, ProductRepository productRepository) { this.orderRepository = orderRepository; this.productRepository = productRepository; }

    @Transactional(readOnly = true)
    public List<CustomerOrder> findAll() { return orderRepository.findAll(); }

    @Transactional(readOnly = true)
    public CustomerOrder findById(Long id) { return orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order not found with id " + id)); }

    @Transactional
    public CustomerOrder create(CreateOrderRequest request) {
        CustomerOrder order = new CustomerOrder(request.customerName(), request.customerEmail());
        BigDecimal total = BigDecimal.ZERO;
        for (OrderItemRequest requestedItem : request.items()) {
            Product product = productRepository.findById(requestedItem.productId()).orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + requestedItem.productId()));
            int quantity = requestedItem.quantity();
            if (product.getStockQuantity() < quantity) throw new BadRequestException("Not enough stock for product '" + product.getName() + "'. Available: " + product.getStockQuantity());
            product.setStockQuantity(product.getStockQuantity() - quantity);
            BigDecimal lineTotal = product.getPrice().multiply(BigDecimal.valueOf(quantity));
            total = total.add(lineTotal);
            order.addItem(new OrderItem(product.getId(), product.getName(), quantity, product.getPrice()));
        }
        order.setTotalAmount(total);
        productRepository.flush();
        return orderRepository.save(order);
    }

    @Transactional
    public CustomerOrder cancel(Long id) {
        CustomerOrder order = findById(id);
        if (order.getStatus() == OrderStatus.CANCELLED) throw new BadRequestException("Order " + id + " is already cancelled");
        for (OrderItem item : order.getItems()) {
            Product product = productRepository.findById(item.getProductId()).orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + item.getProductId()));
            product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
        }
        order.setStatus(OrderStatus.CANCELLED);
        return orderRepository.save(order);
    }
}
