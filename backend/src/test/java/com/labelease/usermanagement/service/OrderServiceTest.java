package com.labelease.usermanagement.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.labelease.usermanagement.entity.Order;
import com.labelease.usermanagement.mapper.OrderMapper;
import com.labelease.usermanagement.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * OrderService 单元测试
 */
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void testCreateOrder_Success() {
        Order order = new Order();
        order.setOrderNo("ORD2024001");
        order.setUserId(1L);
        order.setProductName("测试商品");
        order.setAmount(new BigDecimal("99.99"));
        order.setStatus(0);

        when(orderMapper.insert(any(Order.class))).thenReturn(1);

        boolean result = orderService.save(order);

        assertTrue(result);
        verify(orderMapper, times(1)).insert(any(Order.class));
    }

    @Test
    void testCreateOrder_AmountValidation() {
        Order order = new Order();
        order.setOrderNo("ORD2024002");
        order.setUserId(1L);
        order.setProductName("测试商品");
        order.setAmount(new BigDecimal("-10.00"));
        order.setStatus(0);

        when(orderMapper.insert(any(Order.class))).thenThrow(new IllegalArgumentException("订单金额不能为负数"));

        assertThrows(IllegalArgumentException.class, () -> orderService.save(order));
        verify(orderMapper, times(1)).insert(any(Order.class));
    }

    @Test
    void testListByUserId_Found() {
        Long userId = 1L;
        Order order1 = new Order();
        order1.setId(1L);
        order1.setOrderNo("ORD2024001");
        order1.setUserId(userId);

        Order order2 = new Order();
        order2.setId(2L);
        order2.setOrderNo("ORD2024002");
        order2.setUserId(userId);

        List<Order> expectedOrders = Arrays.asList(order1, order2);

        when(orderMapper.selectByUserId(userId)).thenReturn(expectedOrders);

        List<Order> result = orderService.listByUserId(userId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(userId, result.get(0).getUserId());
        verify(orderMapper, times(1)).selectByUserId(userId);
    }

    @Test
    void testListByUserId_Empty() {
        Long userId = 999L;

        when(orderMapper.selectByUserId(userId)).thenReturn(Arrays.asList());

        List<Order> result = orderService.listByUserId(userId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(orderMapper, times(1)).selectByUserId(userId);
    }

    @Test
    void testPageOrders_WithoutKeyword() {
        int current = 1;
        int size = 10;
        Page<Order> expectedPage = new Page<>(current, size);
        expectedPage.setRecords(Arrays.asList(new Order(), new Order()));
        expectedPage.setTotal(2);

        when(orderMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(expectedPage);

        Page<Order> result = orderService.pageOrders(current, size, null);

        assertNotNull(result);
        assertEquals(2, result.getRecords().size());
        assertEquals(2, result.getTotal());
        verify(orderMapper, times(1)).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
    }

    @Test
    void testPageOrders_WithKeyword() {
        int current = 1;
        int size = 10;
        String keyword = "ORD2024";
        Page<Order> expectedPage = new Page<>(current, size);
        expectedPage.setRecords(Arrays.asList(new Order()));
        expectedPage.setTotal(1);

        when(orderMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(expectedPage);

        Page<Order> result = orderService.pageOrders(current, size, keyword);

        assertNotNull(result);
        assertEquals(1, result.getRecords().size());
        assertEquals(1, result.getTotal());
        verify(orderMapper, times(1)).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
    }

    @Test
    void testUpdateOrder_Success() {
        Order updateOrder = new Order();
        updateOrder.setId(1L);
        updateOrder.setStatus(1);

        when(orderMapper.updateById(any(Order.class))).thenReturn(1);

        boolean result = orderService.updateById(updateOrder);

        assertTrue(result);
        verify(orderMapper, times(1)).updateById(any(Order.class));
    }

    @Test
    void testUpdateOrder_NotExists() {
        Order updateOrder = new Order();
        updateOrder.setId(999L);
        updateOrder.setStatus(1);

        when(orderMapper.updateById(any(Order.class))).thenReturn(0);

        boolean result = orderService.updateById(updateOrder);

        assertFalse(result);
        verify(orderMapper, times(1)).updateById(any(Order.class));
    }

    @Test
    void testDeleteOrder_Success() {
        Long orderId = 1L;

        when(orderMapper.deleteById(orderId)).thenReturn(1);

        boolean result = orderService.removeById(orderId);

        assertTrue(result);
        verify(orderMapper, times(1)).deleteById(orderId);
    }

    @Test
    void testBatchDeleteOrders_Success() {
        List<Long> orderIds = Arrays.asList(1L, 2L, 3L);

        when(orderMapper.deleteBatchIds(orderIds)).thenReturn(3);

        boolean result = orderService.removeByIds(orderIds);

        assertTrue(result);
        verify(orderMapper, times(1)).deleteBatchIds(orderIds);
    }
}
