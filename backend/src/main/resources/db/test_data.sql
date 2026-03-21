-- ============================================================
-- 仪表盘测试数据 - 最近7天的用户和订单数据
-- 执行此脚本前请确保已执行 init.sql
-- ============================================================

USE `user_management`;

-- 清理之前的测试数据（保留admin等基础用户）
DELETE FROM `t_user` WHERE `username` LIKE 'test_user_%';
DELETE FROM `t_order` WHERE `order_no` LIKE 'TEST%';

-- ============================================================
-- 插入最近7天的测试用户
-- 密码均为: password123 -> $2a$10$Qm4.QozGlzkmy.7cmwJ8xuMal22vlBR92NvyOMYsI49FERDTzXys.
-- ============================================================

-- 6天前 - 2个用户
INSERT INTO `t_user` (`username`, `password`, `real_name`, `email`, `phone`, `status`, `create_time`) VALUES
('test_user_d6_1', '$2a$10$Qm4.QozGlzkmy.7cmwJ8xuMal22vlBR92NvyOMYsI49FERDTzXys.', '测试用户D6-1', 'test_d6_1@example.com', '13900000001', 1, DATE_SUB(CURDATE(), INTERVAL 6 DAY)),
('test_user_d6_2', '$2a$10$Qm4.QozGlzkmy.7cmwJ8xuMal22vlBR92NvyOMYsI49FERDTzXys.', '测试用户D6-2', 'test_d6_2@example.com', '13900000002', 1, DATE_SUB(CURDATE(), INTERVAL 6 DAY));

-- 5天前 - 3个用户
INSERT INTO `t_user` (`username`, `password`, `real_name`, `email`, `phone`, `status`, `create_time`) VALUES
('test_user_d5_1', '$2a$10$Qm4.QozGlzkmy.7cmwJ8xuMal22vlBR92NvyOMYsI49FERDTzXys.', '测试用户D5-1', 'test_d5_1@example.com', '13900000003', 1, DATE_SUB(CURDATE(), INTERVAL 5 DAY)),
('test_user_d5_2', '$2a$10$Qm4.QozGlzkmy.7cmwJ8xuMal22vlBR92NvyOMYsI49FERDTzXys.', '测试用户D5-2', 'test_d5_2@example.com', '13900000004', 1, DATE_SUB(CURDATE(), INTERVAL 5 DAY)),
('test_user_d5_3', '$2a$10$Qm4.QozGlzkmy.7cmwJ8xuMal22vlBR92NvyOMYsI49FERDTzXys.', '测试用户D5-3', 'test_d5_3@example.com', '13900000005', 1, DATE_SUB(CURDATE(), INTERVAL 5 DAY));

-- 4天前 - 1个用户
INSERT INTO `t_user` (`username`, `password`, `real_name`, `email`, `phone`, `status`, `create_time`) VALUES
('test_user_d4_1', '$2a$10$Qm4.QozGlzkmy.7cmwJ8xuMal22vlBR92NvyOMYsI49FERDTzXys.', '测试用户D4-1', 'test_d4_1@example.com', '13900000006', 1, DATE_SUB(CURDATE(), INTERVAL 4 DAY));

-- 3天前 - 4个用户
INSERT INTO `t_user` (`username`, `password`, `real_name`, `email`, `phone`, `status`, `create_time`) VALUES
('test_user_d3_1', '$2a$10$Qm4.QozGlzkmy.7cmwJ8xuMal22vlBR92NvyOMYsI49FERDTzXys.', '测试用户D3-1', 'test_d3_1@example.com', '13900000007', 1, DATE_SUB(CURDATE(), INTERVAL 3 DAY)),
('test_user_d3_2', '$2a$10$Qm4.QozGlzkmy.7cmwJ8xuMal22vlBR92NvyOMYsI49FERDTzXys.', '测试用户D3-2', 'test_d3_2@example.com', '13900000008', 1, DATE_SUB(CURDATE(), INTERVAL 3 DAY)),
('test_user_d3_3', '$2a$10$Qm4.QozGlzkmy.7cmwJ8xuMal22vlBR92NvyOMYsI49FERDTzXys.', '测试用户D3-3', 'test_d3_3@example.com', '13900000009', 1, DATE_SUB(CURDATE(), INTERVAL 3 DAY)),
('test_user_d3_4', '$2a$10$Qm4.QozGlzkmy.7cmwJ8xuMal22vlBR92NvyOMYsI49FERDTzXys.', '测试用户D3-4', 'test_d3_4@example.com', '13900000010', 1, DATE_SUB(CURDATE(), INTERVAL 3 DAY));

-- 2天前 - 2个用户
INSERT INTO `t_user` (`username`, `password`, `real_name`, `email`, `phone`, `status`, `create_time`) VALUES
('test_user_d2_1', '$2a$10$Qm4.QozGlzkmy.7cmwJ8xuMal22vlBR92NvyOMYsI49FERDTzXys.', '测试用户D2-1', 'test_d2_1@example.com', '13900000011', 1, DATE_SUB(CURDATE(), INTERVAL 2 DAY)),
('test_user_d2_2', '$2a$10$Qm4.QozGlzkmy.7cmwJ8xuMal22vlBR92NvyOMYsI49FERDTzXys.', '测试用户D2-2', 'test_d2_2@example.com', '13900000012', 1, DATE_SUB(CURDATE(), INTERVAL 2 DAY));

-- 昨天 - 3个用户
INSERT INTO `t_user` (`username`, `password`, `real_name`, `email`, `phone`, `status`, `create_time`) VALUES
('test_user_d1_1', '$2a$10$Qm4.QozGlzkmy.7cmwJ8xuMal22vlBR92NvyOMYsI49FERDTzXys.', '测试用户D1-1', 'test_d1_1@example.com', '13900000013', 1, DATE_SUB(CURDATE(), INTERVAL 1 DAY)),
('test_user_d1_2', '$2a$10$Qm4.QozGlzkmy.7cmwJ8xuMal22vlBR92NvyOMYsI49FERDTzXys.', '测试用户D1-2', 'test_d1_2@example.com', '13900000014', 1, DATE_SUB(CURDATE(), INTERVAL 1 DAY)),
('test_user_d1_3', '$2a$10$Qm4.QozGlzkmy.7cmwJ8xuMal22vlBR92NvyOMYsI49FERDTzXys.', '测试用户D1-3', 'test_d1_3@example.com', '13900000015', 1, DATE_SUB(CURDATE(), INTERVAL 1 DAY));

-- 今天 - 2个用户
INSERT INTO `t_user` (`username`, `password`, `real_name`, `email`, `phone`, `status`, `create_time`) VALUES
('test_user_d0_1', '$2a$10$Qm4.QozGlzkmy.7cmwJ8xuMal22vlBR92NvyOMYsI49FERDTzXys.', '测试用户D0-1', 'test_d0_1@example.com', '13900000016', 1, CURDATE()),
('test_user_d0_2', '$2a$10$Qm4.QozGlzkmy.7cmwJ8xuMal22vlBR92NvyOMYsI49FERDTzXys.', '测试用户D0-2', 'test_d0_2@example.com', '13900000017', 1, CURDATE());

-- ============================================================
-- 插入最近7天的测试订单
-- ============================================================

-- 6天前 - 3个订单
INSERT INTO `t_order` (`order_no`, `user_id`, `product_name`, `amount`, `status`, `create_time`) VALUES
('TEST_ORD_D6_001', 1, '智能手表', 1299.00, 3, DATE_SUB(CURDATE(), INTERVAL 6 DAY)),
('TEST_ORD_D6_002', 2, '蓝牙音箱', 399.00, 2, DATE_SUB(CURDATE(), INTERVAL 6 DAY)),
('TEST_ORD_D6_003', 3, '移动电源', 159.00, 1, DATE_SUB(CURDATE(), INTERVAL 6 DAY));

-- 5天前 - 5个订单
INSERT INTO `t_order` (`order_no`, `user_id`, `product_name`, `amount`, `status`, `create_time`) VALUES
('TEST_ORD_D5_001', 1, '机械键盘', 599.00, 3, DATE_SUB(CURDATE(), INTERVAL 5 DAY)),
('TEST_ORD_D5_002', 2, '游戏鼠标', 299.00, 3, DATE_SUB(CURDATE(), INTERVAL 5 DAY)),
('TEST_ORD_D5_003', 3, '显示器支架', 189.00, 2, DATE_SUB(CURDATE(), INTERVAL 5 DAY)),
('TEST_ORD_D5_004', 4, 'USB扩展坞', 259.00, 1, DATE_SUB(CURDATE(), INTERVAL 5 DAY)),
('TEST_ORD_D5_005', 1, '桌面收纳盒', 89.00, 3, DATE_SUB(CURDATE(), INTERVAL 5 DAY));

-- 4天前 - 2个订单
INSERT INTO `t_order` (`order_no`, `user_id`, `product_name`, `amount`, `status`, `create_time`) VALUES
('TEST_ORD_D4_001', 2, '无线充电器', 129.00, 2, DATE_SUB(CURDATE(), INTERVAL 4 DAY)),
('TEST_ORD_D4_002', 3, '手机壳套装', 49.00, 3, DATE_SUB(CURDATE(), INTERVAL 4 DAY));

-- 3天前 - 6个订单
INSERT INTO `t_order` (`order_no`, `user_id`, `product_name`, `amount`, `status`, `create_time`) VALUES
('TEST_ORD_D3_001', 1, '降噪耳机', 899.00, 3, DATE_SUB(CURDATE(), INTERVAL 3 DAY)),
('TEST_ORD_D3_002', 2, '平板支架', 159.00, 3, DATE_SUB(CURDATE(), INTERVAL 3 DAY)),
('TEST_ORD_D3_003', 3, '护眼台灯', 269.00, 2, DATE_SUB(CURDATE(), INTERVAL 3 DAY)),
('TEST_ORD_D3_004', 4, '桌面风扇', 99.00, 1, DATE_SUB(CURDATE(), INTERVAL 3 DAY)),
('TEST_ORD_D3_005', 1, '数据线套装', 39.00, 3, DATE_SUB(CURDATE(), INTERVAL 3 DAY)),
('TEST_ORD_D3_006', 2, '鼠标垫', 59.00, 3, DATE_SUB(CURDATE(), INTERVAL 3 DAY));

-- 2天前 - 4个订单
INSERT INTO `t_order` (`order_no`, `user_id`, `product_name`, `amount`, `status`, `create_time`) VALUES
('TEST_ORD_D2_001', 1, '咖啡机', 1599.00, 2, DATE_SUB(CURDATE(), INTERVAL 2 DAY)),
('TEST_ORD_D2_002', 2, '咖啡豆礼盒', 199.00, 1, DATE_SUB(CURDATE(), INTERVAL 2 DAY)),
('TEST_ORD_D2_003', 3, '保温杯', 89.00, 3, DATE_SUB(CURDATE(), INTERVAL 2 DAY)),
('TEST_ORD_D2_004', 4, '茶具套装', 359.00, 2, DATE_SUB(CURDATE(), INTERVAL 2 DAY));

-- 昨天 - 3个订单
INSERT INTO `t_order` (`order_no`, `user_id`, `product_name`, `amount`, `status`, `create_time`) VALUES
('TEST_ORD_D1_001', 1, '运动手环', 249.00, 1, DATE_SUB(CURDATE(), INTERVAL 1 DAY)),
('TEST_ORD_D1_002', 2, '瑜伽垫', 99.00, 2, DATE_SUB(CURDATE(), INTERVAL 1 DAY)),
('TEST_ORD_D1_003', 3, '跳绳', 39.00, 3, DATE_SUB(CURDATE(), INTERVAL 1 DAY));

-- 今天 - 2个订单
INSERT INTO `t_order` (`order_no`, `user_id`, `product_name`, `amount`, `status`, `create_time`) VALUES
('TEST_ORD_D0_001', 1, '办公椅', 899.00, 0, CURDATE()),
('TEST_ORD_D0_002', 2, '脚踏板', 129.00, 0, CURDATE());

-- ============================================================
-- 数据统计说明
-- ============================================================
-- 用户趋势: [2, 3, 1, 4, 2, 3, 2] (从6天前到今天)
-- 订单趋势: [3, 5, 2, 6, 4, 3, 2] (从6天前到今天)
-- 总新增用户: 17人
-- 总新增订单: 25个
-- 订单总金额: 9,890.00元
