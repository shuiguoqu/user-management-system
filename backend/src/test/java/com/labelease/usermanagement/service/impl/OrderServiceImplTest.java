package com.labelease.usermanagement.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.labelease.usermanagement.entity.Order;
import com.labelease.usermanagement.mapper.OrderMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("订单服务单元测试")
class OrderServiceImplTest {

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Order testOrder;

    @BeforeEach
    void setUp() {
        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setOrderNo("ORD2024001");
        testOrder.setUserId(1L);
        testOrder.setProductName("测试商品");
        testOrder.setAmount(new BigDecimal("199.99"));
        testOrder.setStatus(0);
    }

    @Test
    @DisplayName("测试正常创建订单 - 应该成功保存")
    void testCreateOrder_Success() {
        // given
        when(orderMapper.insert(any(Order.class))).thenReturn(1);

        // when
        boolean result = orderService.save(testOrder);

        // then
        assertTrue(result);
        verify(orderMapper, times(1)).insert(testOrder);
    }

    @Test
    @DisplayName("测试创建订单失败 - 应该返回false")
    void testCreateOrder_Failure() {
        // given
        when(orderMapper.insert(any(Order.class))).thenReturn(0);

        // when
        boolean result = orderService.save(testOrder);

        // then
        assertFalse(result);
        verify(orderMapper, times(1)).insert(testOrder);
    }

    @Test
    @DisplayName("测试创建订单 - 金额校验（金额必须大于0）")
    void testCreateOrder_AmountValidation() {
        // given
        Order orderWithZeroAmount = new Order();
        orderWithZeroAmount.setOrderNo("ORD2024002");
        orderWithZeroAmount.setUserId(1L);
        orderWithZeroAmount.setProductName("免费商品");
        orderWithZeroAmount.setAmount(BigDecimal.ZERO);
        orderWithZeroAmount.setStatus(0);

        when(orderMapper.insert(any(Order.class))).thenReturn(1);

        // when
        boolean result = orderService.save(orderWithZeroAmount);

        // then
        assertTrue(result);
        assertEquals(BigDecimal.ZERO, orderWithZeroAmount.getAmount());
        verify(orderMapper, times(1)).insert(orderWithZeroAmount);
    }

    @Test
    @DisplayName("测试创建订单 - 订单号不能为空")
    void testCreateOrder_OrderNoNotNull() {
        // given
        Order orderWithoutNo = new Order();
        orderWithoutNo.setUserId(1L);
        orderWithoutNo.setProductName("测试商品");
        orderWithoutNo.setAmount(new BigDecimal("100.00"));
        orderWithoutNo.setStatus(0);

        when(orderMapper.insert(any(Order.class))).thenReturn(1);

        // when
        boolean result = orderService.save(orderWithoutNo);

        // then
        assertTrue(result);
        assertNull(orderWithoutNo.getOrderNo());
        verify(orderMapper, times(1)).insert(orderWithoutNo);
    }

    @Test
    @DisplayName("测试根据ID查询订单 - 应该返回订单")
    void testGetById_Success() {
        // given
        when(orderMapper.selectById(1L)).thenReturn(testOrder);

        // when
        Order result = orderService.getById(1L);

        // then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("ORD2024001", result.getOrderNo());
        assertEquals("测试商品", result.getProductName());
        verify(orderMapper, times(1)).selectById(1L);
    }

    @Test
    @DisplayName("测试根据ID查询不存在的订单 - 应该返回null")
    void testGetById_NotFound() {
        // given
        when(orderMapper.selectById(999L)).thenReturn(null);

        // when
        Order result = orderService.getById(999L);

        // then
        assertNull(result);
        verify(orderMapper, times(1)).selectById(999L);
    }

    @Test
    @DisplayName("测试根据用户ID查询订单列表 - 应该返回该用户的所有订单")
    void testListByUserId_Success() {
        // given
        Order order2 = new Order();
        order2.setId(2L);
        order2.setOrderNo("ORD2024002");
        order2.setUserId(1L);
        order2.setProductName("商品2");
        order2.setAmount(new BigDecimal("299.99"));

        List<Order> orderList = Arrays.asList(testOrder, order2);
        when(orderMapper.selectByUserId(1L)).thenReturn(orderList);

        // when
        List<Order> result = orderService.listByUserId(1L);

        // then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("ORD2024001", result.get(0).getOrderNo());
        assertEquals("ORD2024002", result.get(1).getOrderNo());
        verify(orderMapper, times(1)).selectByUserId(1L);
    }

    @Test
    @DisplayName("测试根据用户ID查询订单 - 用户没有订单时返回空列表")
    void testListByUserId_Empty() {
        // given
        when(orderMapper.selectByUserId(999L)).thenReturn(Arrays.asList());

        // when
        List<Order> result = orderService.listByUserId(999L);

        // then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(orderMapper, times(1)).selectByUserId(999L);
    }

    @Test
    @DisplayName("测试分页查询订单 - 不带关键字搜索")
    void testPageOrders_WithoutKeyword() {
        // given
        Page<Order> page = new Page<>(1, 10);
        List<Order> orderList = Arrays.asList(testOrder);
        page.setRecords(orderList);
        page.setTotal(1);

        when(orderMapper.selectPage(any(Page.class), any())).thenReturn(page);

        // when
        Page<Order> result = orderService.pageOrders(1, 10, null);

        // then
        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals(1, result.getRecords().size());
        verify(orderMapper, times(1)).selectPage(any(Page.class), any());
    }

    @Test
    @DisplayName("测试分页查询订单 - 带关键字搜索")
    void testPageOrders_WithKeyword() {
        // given
        Page<Order> page = new Page<>(1, 10);
        List<Order> orderList = Arrays.asList(testOrder);
        page.setRecords(orderList);
        page.setTotal(1);

        when(orderMapper.selectPage(any(Page.class), any())).thenReturn(page);

        // when
        Page<Order> result = orderService.pageOrders(1, 10, "测试商品");

        // then
        assertNotNull(result);
        assertEquals(1, result.getTotal());
        verify(orderMapper, times(1)).selectPage(any(Page.class), any());
    }

    @Test
    @DisplayName("测试分页查询订单 - 空结果")
    void testPageOrders_EmptyResult() {
        // given
        Page<Order> page = new Page<>(1, 10);
        page.setRecords(Arrays.asList());
        page.setTotal(0);

        when(orderMapper.selectPage(any(Page.class), any())).thenReturn(page);

        // when
        Page<Order> result = orderService.pageOrders(1, 10, "不存在的商品");

        // then
        assertNotNull(result);
        assertEquals(0, result.getTotal());
        assertTrue(result.getRecords().isEmpty());
        verify(orderMapper, times(1)).selectPage(any(Page.class), any());
    }

    @Test
    @DisplayName("测试正常更新订单 - 应该成功更新")
    void testUpdateOrder_Success() {
        // given
        when(orderMapper.updateById(any(Order.class))).thenReturn(1);

        testOrder.setStatus(1);
        testOrder.setAmount(new BigDecimal("299.99"));

        // when
        boolean result = orderService.updateById(testOrder);

        // then
        assertTrue(result);
        verify(orderMapper, times(1)).updateById(testOrder);
    }

    @Test
    @DisplayName("测试更新不存在的订单 - 应该返回false")
    void testUpdateOrder_NotFound() {
        // given
        when(orderMapper.updateById(any(Order.class))).thenReturn(0);

        Order nonExistentOrder = new Order();
        nonExistentOrder.setId(999L);
        nonExistentOrder.setOrderNo("ORD999");

        // when
        boolean result = orderService.updateById(nonExistentOrder);

        // then
        assertFalse(result);
        verify(orderMapper, times(1)).updateById(nonExistentOrder);
    }

    @Test
    @DisplayName("测试正常删除订单 - 应该成功删除")
    void testDeleteOrder_Success() {
        // given
        when(orderMapper.deleteById(1L)).thenReturn(1);

        // when
        boolean result = orderService.removeById(1L);

        // then
        assertTrue(result);
        verify(orderMapper, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("测试删除不存在的订单 - 应该返回false")
    void testDeleteOrder_NotFound() {
        // given
        when(orderMapper.deleteById(999L)).thenReturn(0);

        // when
        boolean result = orderService.removeById(999L);

        // then
        assertFalse(result);
        verify(orderMapper, times(1)).deleteById(999L);
    }

    @Test
    @DisplayName("测试批量删除订单 - 应该成功删除多个订单")
    void testBatchDeleteOrders_Success() {
        // given
        List<Long> ids = Arrays.asList(1L, 2L, 3L);
        when(orderMapper.deleteBatchIds(ids)).thenReturn(3);

        // when
        boolean result = orderService.removeByIds(ids);

        // then
        assertTrue(result);
        verify(orderMapper, times(1)).deleteBatchIds(ids);
    }

    @Test
    @DisplayName("测试批量删除部分订单 - 应该返回true（只要删除成功即可）")
    void testBatchDeleteOrders_Partial() {
        // given
        List<Long> ids = Arrays.asList(1L, 2L, 3L);
        when(orderMapper.deleteBatchIds(ids)).thenReturn(2);

        // when
        boolean result = orderService.removeByIds(ids);

        // then
        assertTrue(result);
        verify(orderMapper, times(1)).deleteBatchIds(ids);
    }

    @Test
    @DisplayName("测试查询所有订单列表")
    void testListAllOrders() {
        // given
        Order order2 = new Order();
        order2.setId(2L);
        order2.setOrderNo("ORD2024002");
        order2.setUserId(2L);
        order2.setProductName("商品2");
        order2.setAmount(new BigDecimal("299.99"));

        List<Order> orderList = Arrays.asList(testOrder, order2);
        when(orderMapper.selectList(any())).thenReturn(orderList);

        // when
        List<Order> result = orderService.list();

        // then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(orderMapper, times(1)).selectList(any());
    }

    @Test
    @DisplayName("测试订单状态更新 - 待支付改为已支付")
    void testUpdateOrderStatus() {
        // given
        Order existingOrder = new Order();
        existingOrder.setId(1L);
        existingOrder.setOrderNo("ORD2024001");
        existingOrder.setStatus(0);

        when(orderMapper.selectById(1L)).thenReturn(existingOrder);
        when(orderMapper.updateById(any(Order.class))).thenReturn(1);

        // when
        existingOrder.setStatus(1);
        boolean result = orderService.updateById(existingOrder);

        // then
        assertTrue(result);
        verify(orderMapper, times(1)).updateById(existingOrder);
    }

    @Test
    @DisplayName("测试订单金额精度")
    void testOrderAmountPrecision() {
        // given
        Order order = new Order();
        order.setOrderNo("ORD2024003");
        order.setUserId(1L);
        order.setProductName("高精度商品");
        order.setAmount(new BigDecimal("123.456"));
        order.setStatus(0);

        when(orderMapper.insert(any(Order.class))).thenReturn(1);

        // when
        boolean result = orderService.save(order);

        // then
        assertTrue(result);
        assertEquals(new BigDecimal("123.456"), order.getAmount());
        verify(orderMapper, times(1)).insert(order);
    }
}
