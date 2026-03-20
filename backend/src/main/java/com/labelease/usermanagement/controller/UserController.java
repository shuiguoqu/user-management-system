package com.labelease.usermanagement.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.labelease.usermanagement.common.Result;
import com.labelease.usermanagement.entity.Order;
import com.labelease.usermanagement.entity.User;
import com.labelease.usermanagement.service.OrderService;
import com.labelease.usermanagement.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户管理控制器 - CRUD + 分页查询 + 关联订单查询
 */
@Tag(name = "用户管理", description = "用户的增删改查与关联查询")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final OrderService orderService;

    @Operation(summary = "分页查询用户列表")
    @GetMapping
    public Result<Page<User>> list(
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") int current,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "搜索关键词") @RequestParam(required = false) String keyword) {
        // 参数校验：页码和每页条数不能小于1
        if (current < 1) {
            current = 1;
        }
        if (size < 1) {
            size = 10;
        }
        // 限制最大每页条数，防止查询过多数据
        if (size > 100) {
            size = 100;
        }
        return Result.success(userService.pageUsers(current, size, keyword));
    }

    @Operation(summary = "根据ID查询用户")
    @GetMapping("/{id}")
    public Result<User> getById(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user == null) {
            return Result.error(404, "用户不存在");
        }
        return Result.success(user);
    }

    @Operation(summary = "新增用户")
    @PostMapping
    public Result<User> create(@Valid @RequestBody User user) {
        userService.save(user);
        return Result.success("创建成功", user);
    }

    @Operation(summary = "更新用户")
    @PutMapping("/{id}")
    public Result<User> update(@PathVariable Long id, @Valid @RequestBody User user) {
        user.setId(id);
        boolean updated = userService.updateById(user);
        if (!updated) {
            return Result.error(404, "用户不存在或更新失败");
        }
        return Result.success("更新成功", user);
    }

    @Operation(summary = "删除用户（逻辑删除）")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        // 先检查用户是否存在
        User user = userService.getById(id);
        if (user == null) {
            return Result.error(404, "用户不存在");
        }
        // 先逻辑删除该用户的所有订单
        orderService.removeByUserId(id);
        // 再逻辑删除用户
        userService.removeById(id);
        return Result.success("删除成功", null);
    }

    @Operation(summary = "查询用户详情及关联订单")
    @GetMapping("/{id}/orders")
    public Result<Map<String, Object>> getUserWithOrders(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user == null) {
            return Result.error(404, "用户不存在");
        }
        List<Order> orders = orderService.listByUserId(id);

        Map<String, Object> result = new HashMap<>();
        result.put("user", user);
        result.put("orders", orders);
        return Result.success(result);
    }
}
