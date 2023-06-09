package com.driesvl.eurder.order.service;

import com.driesvl.eurder.item.repository.ItemRepository;
import com.driesvl.eurder.item.repository.domain.Item;
import com.driesvl.eurder.order.repository.OrderRepository;
import com.driesvl.eurder.order.repository.domain.ItemGroup;
import com.driesvl.eurder.order.repository.domain.Order;
import com.driesvl.eurder.order.api.dto.CreateOrderDTO;
import com.driesvl.eurder.order.api.dto.OrderDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class OrderService {
    private final ItemRepository itemRepository;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Autowired
    public OrderService(ItemRepository itemRepository, OrderRepository orderRepository, OrderMapper orderMapper) {
        this.itemRepository = itemRepository;
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
    }

    public OrderDTO addOrder(UUID userId, CreateOrderDTO createOrderDTO) {
        Order order = createOrder(userId, createOrderDTO);
        orderRepository.addOrder(order);
        takeItemsFromStock(order);
        return orderMapper.toResultDTO(order, calculateOrderTotalPrice(order));
    }

    private Order createOrder(UUID userId, CreateOrderDTO createOrderDTO) {
        return new Order(userId, createOrderDTO.items()
                .entrySet()
                .stream()
                .map(entry -> createItemGroup(entry.getKey(), entry.getValue()))
                .toList());
    }

    private void takeItemsFromStock(Order order) {
        order.getItemGroups()
            .forEach(itemGroup -> itemRepository.reduceItemAmount(itemGroup.getItemId(), itemGroup.getAmount()));
    }

    private ItemGroup createItemGroup(UUID itemId, int amountOrdered) {
        Item item = itemRepository.getItem(itemId);
        return new ItemGroup(itemId, item.getName(), item.getPrice(), amountOrdered, calculateShippingDate(item, amountOrdered));
    }

    private LocalDate calculateShippingDate(Item item, int amountOrdered) {
        return (item.getAmount() >= amountOrdered)?
                (LocalDate.now().plusDays(1)):
                (LocalDate.now().plusDays(7));
    }

    private double calculateOrderTotalPrice(Order order) {
        return order.getItemGroups()
                .stream()
                .mapToDouble(group -> group.getPriceAtTimeOfPurchase() * group.getAmount())
                .sum();
    }
}
