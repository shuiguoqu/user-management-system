package com.labelease.usermanagement.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.labelease.usermanagement.entity.Order;
import com.labelease.usermanagement.mapper.OrderMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderService 单元测试")
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
        testOrder.setOrderNo("ORD202401010001");
        testOrder.setUserId(1L);
        testOrder.setProductName("测试商品");
        testOrder.setAmount(new BigDecimal("99.99"));
        testOrder.setStatus(0);
        testOrder.setDeleted(0);
        testOrder.setCreateTime(LocalDateTime.now());
        testOrder.setUpdateTime(LocalDateTime.now());
    }

    @Nested
    @DisplayName("订单创建测试")
    class CreateOrderTests {

        @Test
        @DisplayName("正常创建订单 - 成功保存")
        void createOrder_Success() {
            when(orderMapper.insert(any(Order.class))).thenReturn(1);

            boolean result = orderService.save(testOrder);

            assertTrue(result);
            verify(orderMapper, times(1)).insert(any(Order.class));
        }

        @Test
        @DisplayName("创建订单 - 金额为零")
        void createOrder_ZeroAmount() {
            testOrder.setAmount(BigDecimal.ZERO);
            when(orderMapper.insert(any(Order.class))).thenReturn(1);

            boolean result = orderService.save(testOrder);

            assertTrue(result);
            assertEquals(BigDecimal.ZERO, testOrder.getAmount());
        }

        @Test
        @DisplayName("创建订单 - 金额为负数时仍可保存（业务层不做校验）")
        void createOrder_NegativeAmount() {
            testOrder.setAmount(new BigDecimal("-10.00"));
            when(orderMapper.insert(any(Order.class))).thenReturn(1);

            boolean result = orderService.save(testOrder);

            assertTrue(result);
            assertEquals(new BigDecimal("-10.00"), testOrder.getAmount());
        }

        @Test
        @DisplayName("创建订单 - 大金额订单")
        void createOrder_LargeAmount() {
            testOrder.setAmount(new BigDecimal("999999.99"));
            when(orderMapper.insert(any(Order.class))).thenReturn(1);

            boolean result = orderService.save(testOrder);

            assertTrue(result);
            assertEquals(new BigDecimal("999999.99"), testOrder.getAmount());
        }

        @Test
        @DisplayName("创建订单 - 初始状态为待支付")
        void createOrder_InitialStatus() {
            testOrder.setStatus(0);
            when(orderMapper.insert(any(Order.class))).thenReturn(1);

            boolean result = orderService.save(testOrder);

            assertTrue(result);
            assertEquals(0, testOrder.getStatus());
        }
    }

    @Nested
    @DisplayName("订单查询测试")
    class QueryOrderTests {

        @Test
        @DisplayName("根据用户ID查询订单 - 用户有订单")
        void listByUserId_UserHasOrders() {
            Order order2 = new Order();
            order2.setId(2L);
            order2.setOrderNo("ORD202401010002");
            order2.setUserId(1L);

            when(orderMapper.selectByUserId(1L)).thenReturn(Arrays.asList(testOrder, order2));

            List<Order> result = orderService.listByUserId(1L);

            assertNotNull(result);
            assertEquals(2, result.size());
            result.forEach(o -> assertEquals(1L, o.getUserId()));
            verify(orderMapper, times(1)).selectByUserId(1L);
        }

        @Test
        @DisplayName("根据用户ID查询订单 - 用户无订单")
        void listByUserId_UserHasNoOrders() {
            when(orderMapper.selectByUserId(999L)).thenReturn(new ArrayList<>());

            List<Order> result = orderService.listByUserId(999L);

            assertNotNull(result);
            assertTrue(result.isEmpty());
            verify(orderMapper, times(1)).selectByUserId(999L);
        }

        @Test
        @DisplayName("分页查询订单 - 不带关键字")
        void pageOrders_WithoutKeyword() {
            Page<Order> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Arrays.asList(testOrder));
            mockPage.setTotal(1);

            when(orderMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(mockPage);

            Page<Order> result = orderService.pageOrders(1, 10, null);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
            assertEquals(1, result.getRecords().size());
            verify(orderMapper, times(1)).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("分页查询订单 - 按订单编号搜索")
        void pageOrders_SearchByOrderNo() {
            Page<Order> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Arrays.asList(testOrder));
            mockPage.setTotal(1);

            when(orderMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(mockPage);

            Page<Order> result = orderService.pageOrders(1, 10, "ORD202401010001");

            assertNotNull(result);
            assertEquals(1, result.getTotal());
            verify(orderMapper, times(1)).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("分页查询订单 - 按商品名称搜索")
        void pageOrders_SearchByProductName() {
            Page<Order> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Arrays.asList(testOrder));
            mockPage.setTotal(1);

            when(orderMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(mockPage);

            Page<Order> result = orderService.pageOrders(1, 10, "测试商品");

            assertNotNull(result);
            assertEquals(1, result.getTotal());
            verify(orderMapper, times(1)).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("分页查询订单 - 空关键字")
        void pageOrders_EmptyKeyword() {
            Page<Order> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Arrays.asList(testOrder));
            mockPage.setTotal(1);

            when(orderMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(mockPage);

            Page<Order> result = orderService.pageOrders(1, 10, "");

            assertNotNull(result);
            verify(orderMapper, times(1)).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("根据ID查询订单 - 订单存在")
        void getById_OrderExists() {
            when(orderMapper.selectById(1L)).thenReturn(testOrder);

            Order result = orderService.getById(1L);

            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals("ORD202401010001", result.getOrderNo());
            verify(orderMapper, times(1)).selectById(1L);
        }

        @Test
        @DisplayName("根据ID查询订单 - 订单不存在")
        void getById_OrderNotExists() {
            when(orderMapper.selectById(999L)).thenReturn(null);

            Order result = orderService.getById(999L);

            assertNull(result);
            verify(orderMapper, times(1)).selectById(999L);
        }
    }

    @Nested
    @DisplayName("订单更新测试")
    class UpdateOrderTests {

        @Test
        @DisplayName("正常更新订单 - 更新状态")
        void updateOrder_Status_Success() {
            testOrder.setStatus(1);
            when(orderMapper.updateById(any(Order.class))).thenReturn(1);

            boolean result = orderService.updateById(testOrder);

            assertTrue(result);
            assertEquals(1, testOrder.getStatus());
            verify(orderMapper, times(1)).updateById(any(Order.class));
        }

        @Test
        @DisplayName("正常更新订单 - 更新金额")
        void updateOrder_Amount_Success() {
            testOrder.setAmount(new BigDecimal("199.99"));
            when(orderMapper.updateById(any(Order.class))).thenReturn(1);

            boolean result = orderService.updateById(testOrder);

            assertTrue(result);
            assertEquals(new BigDecimal("199.99"), testOrder.getAmount());
        }

        @Test
        @DisplayName("更新不存在的订单 - 返回失败")
        void updateOrder_NotExist_ReturnsFalse() {
            Order nonExistentOrder = new Order();
            nonExistentOrder.setId(999L);
            nonExistentOrder.setOrderNo("ORD999");

            when(orderMapper.updateById(any(Order.class))).thenReturn(0);

            boolean result = orderService.updateById(nonExistentOrder);

            assertFalse(result);
            verify(orderMapper, times(1)).updateById(any(Order.class));
        }

        @Test
        @DisplayName("订单状态流转 - 待支付到已支付")
        void updateOrder_StatusTransition_ToPaid() {
            testOrder.setStatus(0);
            testOrder.setStatus(1);
            when(orderMapper.updateById(any(Order.class))).thenReturn(1);

            boolean result = orderService.updateById(testOrder);

            assertTrue(result);
            assertEquals(1, testOrder.getStatus());
        }

        @Test
        @DisplayName("订单状态流转 - 已支付到已发货")
        void updateOrder_StatusTransition_ToShipped() {
            testOrder.setStatus(1);
            testOrder.setStatus(2);
            when(orderMapper.updateById(any(Order.class))).thenReturn(1);

            boolean result = orderService.updateById(testOrder);

            assertTrue(result);
            assertEquals(2, testOrder.getStatus());
        }

        @Test
        @DisplayName("订单状态流转 - 已发货到已完成")
        void updateOrder_StatusTransition_ToCompleted() {
            testOrder.setStatus(2);
            testOrder.setStatus(3);
            when(orderMapper.updateById(any(Order.class))).thenReturn(1);

            boolean result = orderService.updateById(testOrder);

            assertTrue(result);
            assertEquals(3, testOrder.getStatus());
        }

        @Test
        @DisplayName("订单状态流转 - 取消订单")
        void updateOrder_StatusTransition_ToCancelled() {
            testOrder.setStatus(4);
            when(orderMapper.updateById(any(Order.class))).thenReturn(1);

            boolean result = orderService.updateById(testOrder);

            assertTrue(result);
            assertEquals(4, testOrder.getStatus());
        }
    }

    @Nested
    @DisplayName("订单删除测试")
    class DeleteOrderTests {

        @Test
        @DisplayName("正常删除订单 - 单个删除成功")
        void deleteOrder_Success() {
            when(orderMapper.deleteById(1L)).thenReturn(1);

            boolean result = orderService.removeById(1L);

            assertTrue(result);
            verify(orderMapper, times(1)).deleteById(1L);
        }

        @Test
        @DisplayName("删除不存在的订单 - 返回失败")
        void deleteOrder_NotExist_ReturnsFalse() {
            when(orderMapper.deleteById(999L)).thenReturn(0);

            boolean result = orderService.removeById(999L);

            assertFalse(result);
            verify(orderMapper, times(1)).deleteById(999L);
        }

        @Test
        @DisplayName("批量删除订单 - 成功删除多个")
        void deleteOrders_Batch_Success() {
            List<Long> ids = Arrays.asList(1L, 2L, 3L);
            when(orderMapper.deleteBatchIds(ids)).thenReturn(3);

            boolean result = orderService.removeByIds(ids);

            assertTrue(result);
            verify(orderMapper, times(1)).deleteBatchIds(ids);
        }

        @Test
        @DisplayName("批量删除订单 - 部分不存在")
        void deleteOrders_Batch_PartialNotExist() {
            List<Long> ids = Arrays.asList(1L, 2L, 999L);
            when(orderMapper.deleteBatchIds(ids)).thenReturn(2);

            boolean result = orderService.removeByIds(ids);

            assertFalse(result);
            verify(orderMapper, times(1)).deleteBatchIds(ids);
        }

        @Test
        @DisplayName("批量删除订单 - 空列表")
        void deleteOrders_Batch_EmptyList() {
            List<Long> ids = new ArrayList<>();
            when(orderMapper.deleteBatchIds(ids)).thenReturn(0);

            boolean result = orderService.removeByIds(ids);

            assertFalse(result);
        }
    }

    @Nested
    @DisplayName("查询所有订单测试")
    class ListOrdersTests {

        @Test
        @DisplayName("查询所有订单 - 返回列表")
        void listAll_Success() {
            Order order2 = new Order();
            order2.setId(2L);
            order2.setOrderNo("ORD202401010002");

            when(orderMapper.selectList(any(LambdaQueryWrapper.class)))
                    .thenReturn(Arrays.asList(testOrder, order2));

            List<Order> result = orderService.list();

            assertNotNull(result);
            assertEquals(2, result.size());
            verify(orderMapper, times(1)).selectList(any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("查询所有订单 - 空列表")
        void listAll_Empty() {
            when(orderMapper.selectList(any(LambdaQueryWrapper.class)))
                    .thenReturn(new ArrayList<>());

            List<Order> result = orderService.list();

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }
}
