-- init.sql
SET NAMES utf8mb4;
CREATE DATABASE IF NOT EXISTS `elderly_care` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `elderly_care`;

-- 1. 用户表
CREATE TABLE `sys_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `real_name` varchar(50) DEFAULT NULL COMMENT '真实姓名',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `role` varchar(20) NOT NULL COMMENT '角色 (SENIOR, CHILD, CARER, ORG_ADMIN, PLATFORM_ADMIN)',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像',
  `points` int(11) DEFAULT '0' COMMENT '积分',
  `status` varchar(20) DEFAULT 'NORMAL' COMMENT '状态 (PENDING, NORMAL, BLACKLIST)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 这里默认写入明文密码或简单密文，可以结合具体框架调整 (比如123456存为 {noop}123456 或者 bcrypt hash)
-- 这里统一按前端发送明文过来处理即可（实际应用中需加密，但为了简化开发先存明文或通用密文）
INSERT INTO `sys_user` (`username`, `password`, `real_name`, `role`, `status`) VALUES
('admin', '123456', '平台超级管理员', 'PLATFORM_ADMIN', 'NORMAL'),
('org1', '123456', '红星养老机构', 'ORG_ADMIN', 'PENDING'),
('carer1', '123456', '王阿姨', 'CARER', 'NORMAL'),
('senior1', '123456', '李爷爷', 'SENIOR', 'NORMAL'),
('child1', '123456', '李小明', 'CHILD', 'NORMAL');

-- 8. 系统日志表
CREATE TABLE `sys_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) DEFAULT NULL COMMENT '操作人ID',
  `username` varchar(50) DEFAULT NULL COMMENT '操作人用户名',
  `action` varchar(100) NOT NULL COMMENT '操作动作',
  `method` varchar(255) DEFAULT NULL COMMENT '请求方法',
  `params` text DEFAULT NULL COMMENT '请求参数',
  `ip` varchar(50) DEFAULT NULL COMMENT 'IP地址',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- 9. 投诉建议表
CREATE TABLE `act_complaint` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `complainant_id` bigint(20) NOT NULL COMMENT '投诉人ID',
  `target_id` bigint(20) DEFAULT NULL COMMENT '投诉对象ID (比如订单ID或护理员ID)',
  `target_type` varchar(50) DEFAULT NULL COMMENT '投诉对象类型',
  `title` varchar(100) NOT NULL COMMENT '投诉标题',
  `content` text NOT NULL COMMENT '详细内容',
  `images` text DEFAULT NULL COMMENT '证据图片(逗号分隔)',
  `status` varchar(20) DEFAULT 'PENDING' COMMENT '状态 (PENDING, RESOLVED)',
  `result` text DEFAULT NULL COMMENT '处理结果',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='投诉建议表';

-- 2. 服务类型表
CREATE TABLE `sys_service_type` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL COMMENT '服务名称',
  `description` varchar(255) DEFAULT NULL COMMENT '服务描述',
  `base_price` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '基础价格',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务类型表';

INSERT INTO `sys_service_type` (`name`, `description`, `base_price`) VALUES 
('日常保洁', '2小时家庭卫生清洁', 100.00), 
('助浴服务', '专业人员上门协助洗浴', 150.00), 
('健康体检', '基础生命体征测量记录', 50.00);

-- 3. 服务订单表
CREATE TABLE `act_service_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `senior_id` bigint(20) NOT NULL COMMENT '老人ID',
  `child_id` bigint(20) DEFAULT NULL COMMENT '代下单子女ID(可选)',
  `service_type_id` bigint(20) NOT NULL COMMENT '服务类型',
  `carer_id` bigint(20) DEFAULT NULL COMMENT '接单/派单护理员ID',
  `status` varchar(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态 (PENDING, ASSIGNED, IN_PROGRESS, COMPLETED, CANCELLED)',
  `appointment_time` datetime DEFAULT NULL COMMENT '预约时间',
  `sign_in_time` datetime DEFAULT NULL COMMENT '签到时间',
  `sign_out_time` datetime DEFAULT NULL COMMENT '签退时间',
  `price` decimal(10,2) DEFAULT '0.00' COMMENT '最终价格',
  `remark` varchar(255) DEFAULT NULL COMMENT '下单备注',
  `evaluate_score` int(11) DEFAULT NULL COMMENT '老人评价(1-5星)',
  `evaluate_text` varchar(500) DEFAULT NULL COMMENT '评价文字',
  `evaluate_images` text DEFAULT NULL COMMENT '评价图片(逗号分隔)',
  `carer_evaluate_score` int(11) DEFAULT NULL COMMENT '护理员对客户评价(1-5星)',
  `carer_evaluate_text` varchar(500) DEFAULT NULL COMMENT '护理员评价文字',
  `carer_evaluate_images` text DEFAULT NULL COMMENT '护理员评价图片(逗号分隔)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务预约订单表';

-- 4. 健康记录表
CREATE TABLE `health_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `senior_id` bigint(20) NOT NULL COMMENT '老人ID',
  `record_type` varchar(20) NOT NULL COMMENT '记录类型 (BLOOD_PRESSURE, BLOOD_SUGAR, BLOOD_LIPID)',
  `value1` varchar(50) DEFAULT NULL COMMENT '测量值1 (如收缩压/空腹血糖)',
  `value2` varchar(50) DEFAULT NULL COMMENT '测量值2 (如舒张压/餐后血糖)',
  `measured_by` bigint(20) DEFAULT NULL COMMENT '测量人(自己或护理员)',
  `measure_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '测量时间',
  `is_abnormal` tinyint(1) DEFAULT '0' COMMENT '是否异常(1是 0否)',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `measure_stage` varchar(20) DEFAULT NULL COMMENT '测量阶段 (BEFORE_SERVICE, AFTER_SERVICE)',
  `related_order_id` bigint(20) DEFAULT NULL COMMENT '关联工单ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='健康数据跟踪表';

-- 11. 积分商城表
CREATE TABLE `sys_points_mall` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL COMMENT '商品名称',
  `points_required` int(11) NOT NULL COMMENT '所需积分',
  `stock` int(11) NOT NULL DEFAULT '0' COMMENT '库存',
  `image_url` varchar(255) DEFAULT NULL COMMENT '商品图片',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='积分商城商品表';

INSERT INTO `sys_points_mall` (`name`, `points_required`, `stock`, `image_url`) VALUES
('保温杯', 500, 10, '/mall/thermos.png'),
('按摩仪', 2000, 5, '/mall/massager.png'),
('超市购物券', 1000, 20, '/mall/voucher.png');

-- 12. 积分兑换记录表
CREATE TABLE `act_points_exchange_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `senior_id` bigint(20) NOT NULL COMMENT '老人ID',
  `mall_id` bigint(20) NOT NULL COMMENT '商品ID',
  `points_cost` int(11) NOT NULL COMMENT '消耗积分',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='积分兑换记录表';

-- 13. 心情树洞表
CREATE TABLE `act_tree_hole` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `author_id` bigint(20) NOT NULL COMMENT '作者ID',
  `nickname` varchar(50) DEFAULT '匿名' COMMENT '匿名昵称',
  `content` text NOT NULL COMMENT '内容',
  `likes` int(11) DEFAULT '0' COMMENT '点赞数',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='心情树洞表';

-- 14. 树洞评论表
CREATE TABLE `act_tree_hole_comment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `tree_hole_id` bigint(20) NOT NULL COMMENT '树洞ID',
  `author_id` bigint(20) NOT NULL COMMENT '评论人ID',
  `nickname` varchar(50) DEFAULT '匿名' COMMENT '匿名昵称',
  `content` text NOT NULL COMMENT '内容',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='树洞评论表';

-- 5. 社区活动表
CREATE TABLE `act_activity` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `title` varchar(100) NOT NULL COMMENT '活动标题',
  `content` text COMMENT '活动内容',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `address` varchar(100) DEFAULT NULL COMMENT '活动地点',
  `reward_points` int(11) DEFAULT '0' COMMENT '签到奖励积分',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='社区活动表';

-- 6. 活动报名记录表
CREATE TABLE `act_activity_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `activity_id` bigint(20) NOT NULL COMMENT '活动ID',
  `senior_id` bigint(20) NOT NULL COMMENT '老人ID',
  `status` varchar(20) DEFAULT 'REGISTERED' COMMENT '状态 (REGISTERED 报名, ATTENDED 签到)',
  `sign_in_time` datetime DEFAULT NULL COMMENT '签到时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='活动报名记录表';

-- 7. 健康科普文章表
CREATE TABLE `act_article` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `title` varchar(200) NOT NULL COMMENT '文章标题',
  `content` text NOT NULL COMMENT '富文本内容',
  `type` varchar(20) DEFAULT 'GENERAL' COMMENT '类型 (GENERAL, SENIOR, CHILD)',
  `cover_image` varchar(255) DEFAULT NULL COMMENT '封面图片URL',
  `views` int(11) DEFAULT '0' COMMENT '阅读数',
  `likes` int(11) DEFAULT '0' COMMENT '点赞数',
  `publish_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  `schedule_time` datetime DEFAULT NULL COMMENT '定时发布时间',
  `target_role` varchar(20) DEFAULT NULL COMMENT '定向角色',
  `is_pushed` tinyint(1) DEFAULT '0' COMMENT '是否已推送',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='健康科普文章表';

-- 10. 文章互动表
CREATE TABLE `act_article_interaction` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `article_id` bigint(20) NOT NULL COMMENT '文章ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `is_read` tinyint(1) DEFAULT '0' COMMENT '是否已读',
  `is_liked` tinyint(1) DEFAULT '0' COMMENT '是否点赞',
  `is_bookmarked` tinyint(1) DEFAULT '0' COMMENT '是否收藏',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章互动表';
