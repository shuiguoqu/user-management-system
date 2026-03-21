-- ============================================================
-- 用户管理系统 - 数据库初始化脚本
-- 字符集: utf8mb4 | 排序规则: utf8mb4_unicode_ci
-- ============================================================

CREATE DATABASE IF NOT EXISTS `user_management`
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE `user_management`;

-- ============================================================
-- 用户表
-- ============================================================
CREATE TABLE IF NOT EXISTS `t_user` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `username`    VARCHAR(50)  NOT NULL COMMENT '用户名（登录用）',
    `password`    VARCHAR(255) NOT NULL COMMENT '密码',
    `real_name`   VARCHAR(100) DEFAULT NULL COMMENT '真实姓名',
    `email`       VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `phone`       VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
    `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `deleted`     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-正常，1-已删除',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ============================================================
-- 订单表
-- ============================================================
CREATE TABLE IF NOT EXISTS `t_order` (
    `id`           BIGINT         NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `order_no`     VARCHAR(64)    NOT NULL COMMENT '订单编号',
    `user_id`      BIGINT         NOT NULL COMMENT '关联用户ID',
    `product_name` VARCHAR(200)   NOT NULL COMMENT '商品名称',
    `amount`       DECIMAL(10, 2) NOT NULL DEFAULT 0.00 COMMENT '订单金额',
    `status`       TINYINT        NOT NULL DEFAULT 0 COMMENT '状态：0-待支付，1-已支付，2-已发货，3-已完成，4-已取消',
    `deleted`      TINYINT        NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-正常，1-已删除',
    `create_time`  DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';

-- ============================================================
-- 密码已采用 BCrypt 哈希存储
-- admin123    -> $2a$10$Qm4.QozGlzkmy.7cmwJ8xuMal22vlBR92NvyOMYsI49FERDTzXys.
-- password123 -> $2a$10$Qm4.QozGlzkmy.7cmwJ8xuMal22vlBR92NvyOMYsI49FERDTzXys.
INSERT INTO `t_user` (`username`, `password`, `real_name`, `email`, `phone`, `status`) VALUES
('admin',    '$2a$10$Qm4.QozGlzkmy.7cmwJ8xuMal22vlBR92NvyOMYsI49FERDTzXys.', '系统管理员', 'admin@example.com',    '13800000001', 1),
('zhangsan', '$2a$10$Qm4.QozGlzkmy.7cmwJ8xuMal22vlBR92NvyOMYsI49FERDTzXys.', '张三',       'zhangsan@example.com', '13800000002', 1),
('lisi',     '$2a$10$Qm4.QozGlzkmy.7cmwJ8xuMal22vlBR92NvyOMYsI49FERDTzXys.', '李四',       'lisi@example.com',     '13800000003', 1),
('wangwu',   '$2a$10$Qm4.QozGlzkmy.7cmwJ8xuMal22vlBR92NvyOMYsI49FERDTzXys.', '王五',       'wangwu@example.com',   '13800000004', 1),
('zhaoliu',  '$2a$10$Qm4.QozGlzkmy.7cmwJ8xuMal22vlBR92NvyOMYsI49FERDTzXys.', '赵六',       'zhaoliu@example.com',  '13800000005', 0)
ON DUPLICATE KEY UPDATE `username` = VALUES(`username`);

INSERT INTO `t_order` (`order_no`, `user_id`, `product_name`, `amount`, `status`) VALUES
('ORD20260001', 1, '燕麦拿铁咖啡套餐',     128.00, 3),
('ORD20260002', 1, '办公桌面收纳套装',     259.90, 1),
('ORD20260003', 2, '无线蓝牙降噪耳机',     899.00, 2),
('ORD20260004', 2, '手工陶瓷马克杯',        68.50, 3),
('ORD20260005', 3, '北欧风格台灯',         199.00, 0),
('ORD20260006', 3, '有机棉质四件套',       459.00, 1),
('ORD20260007', 4, '智能体脂秤',           149.00, 3),
('ORD20260008', 4, '原木书架落地款',       1280.00, 4),
('ORD20260009', 5, '便携式咖啡研磨器',      329.00, 0),
('ORD20260010', 1, '天然乳胶枕头',         268.00, 2)
ON DUPLICATE KEY UPDATE `order_no` = VALUES(`order_no`);

-- ============================================================
-- 仪表盘测试数据 - 最近7天的用户和订单数据
-- ============================================================

-- 清理旧的测试数据（保留原始5个用户和10个订单）
DELETE FROM `t_order` WHERE `id` > 10;
DELETE FROM `t_user` WHERE `id` > 5;

-- 添加最近7天的测试用户（每天2-3个新用户）
INSERT INTO `t_user` (`id`, `username`, `password`, `real_name`, `email`, `phone`, `status`, `create_time`) VALUES
(6,  'testuser1',  '$2a$10$Qm4.QozGlzkmy.7cmwJ8xuMal22vlBR92NvyOMYsI49FERDTzXys.', '测试用户1',  'test1@example.com',  '13900000001', 1, DATE_SUB(CURDATE(), INTERVAL 6 DAY)),
(7,  'testuser2',  '$2a$10$Qm4.QozGlzkmy.7cmwJ8xuMal22vlBR92NvyOMYsI49FERDTzXys.', '测试用户2',  'test2@example.com',  '13900000002', 1, DATE_SUB(CURDATE(), INTERVAL 6 DAY)),
(8,  'testuser3',  '$2a$10$Qm4.QozGlzkmy.7cmwJ8xuMal22vlBR92NvyOMYsI49FERDTzXys.', '测试用户3',  'test3@example.com',  '13900000003', 1, DATE_SUB(CURDATE(), INTERVAL 5 DAY)),
(9,  'testuser4',  '$2a$10$Qm4.QozGlzkmy.7cmwJ8xuMal22vlBR92NvyOMYsI49FERDTzXys.', '测试用户4',  'test4@example.com',  '13900000004', 1, DATE_SUB(CURDATE(), INTERVAL 5 DAY)),
(10, 'testuser5',  '$2a$10$Qm4.QozGlzkmy.7cmwJ8xuMal22vlBR92NvyOMYsI49FERDTzXys.', '测试用户5',  'test5@example.com',  '13900000005', 1, DATE_SUB(CURDATE(), INTERVAL 4 DAY)),
(11, 'testuser6',  '$2a$10$Qm4.QozGlzkmy.7cmwJ8xuMal22vlBR92NvyOMYsI49FERDTzXys.', '测试用户6',  'test6@example.com',  '13900000006', 1, DATE_SUB(CURDATE(), INTERVAL 4 DAY)),
(12, 'testuser7',  '$2a$10$Qm4.QozGlzkmy.7cmwJ8xuMal22vlBR92NvyOMYsI49FERDTzXys.', '测试用户7',  'test7@example.com',  '13900000007', 1, DATE_SUB(CURDATE(), INTERVAL 3 DAY)),
(13, 'testuser8',  '$2a$10$Qm4.QozGlzkmy.7cmwJ8xuMal22vlBR92NvyOMYsI49FERDTzXys.', '测试用户8',  'test8@example.com',  '13900000008', 1, DATE_SUB(CURDATE(), INTERVAL 3 DAY)),
(14, 'testuser9',  '$2a$10$Qm4.QozGlzkmy.7cmwJ8xuMal22vlBR92NvyOMYsI49FERDTzXys.', '测试用户9',  'test9@example.com',  '13900000009', 1, DATE_SUB(CURDATE(), INTERVAL 2 DAY)),
(15, 'testuser10', '$2a$10$Qm4.QozGlzkmy.7cmwJ8xuMal22vlBR92NvyOMYsI49FERDTzXys.', '测试用户10', 'test10@example.com', '13900000010', 1, DATE_SUB(CURDATE(), INTERVAL 2 DAY)),
(16, 'testuser11', '$2a$10$Qm4.QozGlzkmy.7cmwJ8xuMal22vlBR92NvyOMYsI49FERDTzXys.', '测试用户11', 'test11@example.com', '13900000011', 1, DATE_SUB(CURDATE(), INTERVAL 1 DAY)),
(17, 'testuser12', '$2a$10$Qm4.QozGlzkmy.7cmwJ8xuMal22vlBR92NvyOMYsI49FERDTzXys.', '测试用户12', 'test12@example.com', '13900000012', 1, DATE_SUB(CURDATE(), INTERVAL 1 DAY)),
(18, 'testuser13', '$2a$10$Qm4.QozGlzkmy.7cmwJ8xuMal22vlBR92NvyOMYsI49FERDTzXys.', '测试用户13', 'test13@example.com', '13900000013', 1, DATE_SUB(CURDATE(), INTERVAL 0 DAY)),
(19, 'testuser14', '$2a$10$Qm4.QozGlzkmy.7cmwJ8xuMal22vlBR92NvyOMYsI49FERDTzXys.', '测试用户14', 'test14@example.com', '13900000014', 1, DATE_SUB(CURDATE(), INTERVAL 0 DAY)),
(20, 'testuser15', '$2a$10$Qm4.QozGlzkmy.7cmwJ8xuMal22vlBR92NvyOMYsI49FERDTzXys.', '测试用户15', 'test15@example.com', '13900000015', 1, DATE_SUB(CURDATE(), INTERVAL 0 DAY))
ON DUPLICATE KEY UPDATE `username` = VALUES(`username`);

-- 添加最近7天的测试订单（每天3-5个新订单）
INSERT INTO `t_order` (`id`, `order_no`, `user_id`, `product_name`, `amount`, `status`, `create_time`) VALUES
(11, 'ORD20260011', 1, '智能手环Pro',        299.00,  1, DATE_SUB(CURDATE(), INTERVAL 6 DAY)),
(12, 'ORD20260012', 2, '机械键盘青轴',       459.00,  2, DATE_SUB(CURDATE(), INTERVAL 6 DAY)),
(13, 'ORD20260013', 3, '显示器支架',         189.00,  3, DATE_SUB(CURDATE(), INTERVAL 6 DAY)),
(14, 'ORD20260014', 1, 'Type-C扩展坞',       159.00,  1, DATE_SUB(CURDATE(), INTERVAL 5 DAY)),
(15, 'ORD20260015', 2, '无线鼠标',           89.00,   2, DATE_SUB(CURDATE(), INTERVAL 5 DAY)),
(16, 'ORD20260016', 3, '笔记本支架铝合金',    129.00,  3, DATE_SUB(CURDATE(), INTERVAL 5 DAY)),
(17, 'ORD20260017', 4, 'USB-C数据线套装',    49.00,   1, DATE_SUB(CURDATE(), INTERVAL 4 DAY)),
(18, 'ORD20260018', 1, '桌面收纳盒',         39.90,   2, DATE_SUB(CURDATE(), INTERVAL 4 DAY)),
(19, 'ORD20260019', 2, '手机快充头',         79.00,   3, DATE_SUB(CURDATE(), INTERVAL 4 DAY)),
(20, 'ORD20260020', 3, '蓝牙耳机',          199.00,  1, DATE_SUB(CURDATE(), INTERVAL 4 DAY)),
(21, 'ORD20260021', 4, '护眼台灯',          249.00,  2, DATE_SUB(CURDATE(), INTERVAL 3 DAY)),
(22, 'ORD20260022', 1, '保温杯',            69.00,   3, DATE_SUB(CURDATE(), INTERVAL 3 DAY)),
(23, 'ORD20260023', 2, '抱枕靠垫',          35.00,   1, DATE_SUB(CURDATE(), INTERVAL 3 DAY)),
(24, 'ORD20260024', 3, '桌面绿植',          25.00,   2, DATE_SUB(CURDATE(), INTERVAL 3 DAY)),
(25, 'ORD20260025', 4, '鼠标垫大号',        19.90,   3, DATE_SUB(CURDATE(), INTERVAL 2 DAY)),
(26, 'ORD20260026', 1, '手机支架',          15.00,   1, DATE_SUB(CURDATE(), INTERVAL 2 DAY)),
(27, 'ORD20260027', 2, '数据线收纳包',      29.00,   2, DATE_SUB(CURDATE(), INTERVAL 2 DAY)),
(28, 'ORD20260028', 3, '屏幕清洁套装',      18.50,   3, DATE_SUB(CURDATE(), INTERVAL 2 DAY)),
(29, 'ORD20260029', 4, '便签纸',            9.90,    1, DATE_SUB(CURDATE(), INTERVAL 1 DAY)),
(30, 'ORD20260030', 1, '签字笔套装',        22.00,   2, DATE_SUB(CURDATE(), INTERVAL 1 DAY)),
(31, 'ORD20260031', 2, '文件夹',            12.00,   3, DATE_SUB(CURDATE(), INTERVAL 1 DAY)),
(32, 'ORD20260032', 3, '计算器',            45.00,   1, DATE_SUB(CURDATE(), INTERVAL 1 DAY)),
(33, 'ORD20260033', 4, '订书机',            16.00,   2, DATE_SUB(CURDATE(), INTERVAL 0 DAY)),
(34, 'ORD20260034', 1, '剪刀',              8.50,    3, DATE_SUB(CURDATE(), INTERVAL 0 DAY)),
(35, 'ORD20260035', 2, '胶带',              5.00,    1, DATE_SUB(CURDATE(), INTERVAL 0 DAY)),
(36, 'ORD20260036', 3, '回形针',            3.00,    2, DATE_SUB(CURDATE(), INTERVAL 0 DAY)),
(37, 'ORD20260037', 4, '便利贴',            6.50,    3, DATE_SUB(CURDATE(), INTERVAL 0 DAY))
ON DUPLICATE KEY UPDATE `order_no` = VALUES(`order_no`);
