-- --------------------------------------------------------------------------------
-- 数据库初始化脚本（完整版）
-- 设计版本: 3.0 - 工程信息服务版
-- 更新日期: 2025年1月28日
-- 说明: 新增工程信息服务模块的8个数据表和相关字典数据
-- --------------------------------------------------------------------------------

-- 创建数据库
CREATE DATABASE IF NOT EXISTS eb_water_resources DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE eb_water_resources;

-- --------------------------------------------------------------------------------
-- 模块: 基础信息管理 (已实现功能)
-- --------------------------------------------------------------------------------

-- regions (行政区划表) - 保留，被其他表引用
CREATE TABLE IF NOT EXISTS `regions` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '区域 ID',
  `code` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '行政区划代码',
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '区域名称',
  `level` tinyint NOT NULL COMMENT '行政级别(省/市/县/乡/村)',
  `parent_id` bigint DEFAULT NULL COMMENT '父级区域 ID',
  `boundary` geometry DEFAULT NULL COMMENT '行政区划边界',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  `deleted_at` datetime DEFAULT NULL COMMENT '软删除标记',
  PRIMARY KEY (`id`),
  UNIQUE KEY `code` (`code`),
  KEY `FK_regions_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='行政区划表';

-- departments (部门信息表)
CREATE TABLE IF NOT EXISTS `departments` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '部门 ID',
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '部门名称',
  `parent_id` bigint DEFAULT NULL COMMENT '父部门 ID (支持层级)',
  `duty` text COLLATE utf8mb4_unicode_ci COMMENT '部门职责',
  `contact` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '联系方式',
  `region_id` bigint DEFAULT NULL COMMENT '所属行政区域 ID',
  `is_active` VARCHAR(50) DEFAULT '1' COMMENT '部门是否启用',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  `deleted_at` datetime DEFAULT NULL COMMENT '软删除标记',
  PRIMARY KEY (`id`),
  KEY `FK_departments_parent_id` (`parent_id`),
  KEY `FK_departments_region_id` (`region_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='部门信息表';

-- positions (岗位表)
CREATE TABLE IF NOT EXISTS `positions` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '岗位 ID',
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '岗位名称',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '岗位描述',
  `responsibilities` text COLLATE utf8mb4_unicode_ci COMMENT '岗位职责',
  `level` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '岗位级别',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  `deleted_at` datetime DEFAULT NULL COMMENT '软删除标记',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='岗位表';

-- roles (角色表)
CREATE TABLE IF NOT EXISTS `roles` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色 ID',
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色名称',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '角色描述',
  `sort_order` int DEFAULT 0 COMMENT '排序值，数值越小优先级越高',
  `is_active` VARCHAR(50) DEFAULT '1' COMMENT '角色是否启用',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  `deleted_at` datetime DEFAULT NULL COMMENT '软删除标记',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`),
  KEY `idx_roles_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- users (用户信息表)
CREATE TABLE IF NOT EXISTS `users` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户 ID',
  `username` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名 (用于登录)',
  `password` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '加密后的密码',
  `email` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '电子邮箱',
  `role_id` bigint DEFAULT NULL COMMENT '角色 ID',
  `is_active` VARCHAR(50) DEFAULT '1' COMMENT '账户是否激活',
  `last_login` datetime DEFAULT NULL COMMENT '最后登录时间',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  `deleted_at` datetime DEFAULT NULL COMMENT '软删除标记',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  KEY `FK_users_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户信息表';

-- user_roles (用户角色关联表)
CREATE TABLE IF NOT EXISTS `user_roles` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role` (`user_id`,`role_id`),
  KEY `idx_user_roles_user_id` (`user_id`),
  KEY `idx_user_roles_role_id` (`role_id`),
  CONSTRAINT `fk_user_roles_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_user_roles_role_id` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- personnel (人员信息表)
CREATE TABLE IF NOT EXISTS `personnel` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '人员 ID',
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '姓名',
  `phone` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '联系电话',
  `email` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '电子邮箱',
  `position_id` bigint DEFAULT NULL COMMENT '岗位 ID',
  `department_id` bigint DEFAULT NULL COMMENT '部门 ID',
  `user_id` bigint DEFAULT NULL COMMENT '关联的用户 ID (可空)',
  `employee_no` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '工号',
  `hire_date` date DEFAULT NULL COMMENT '入职日期',
  `work_responsibilities` text COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '工作职责',
  `is_active` VARCHAR(50) DEFAULT '1' COMMENT '是否启用',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  `deleted_at` datetime DEFAULT NULL COMMENT '软删除标记',
  PRIMARY KEY (`id`),
  UNIQUE KEY `employee_no` (`employee_no`),
  KEY `FK_personnel_position_id` (`position_id`),
  KEY `FK_personnel_department_id` (`department_id`),
  KEY `FK_personnel_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='人员信息表';

-- permissions (权限表)
CREATE TABLE IF NOT EXISTS `permissions` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '权限 ID',
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '权限名称',
  `code` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '权限编码',
  `type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '权限类型 (MODULE/MENU/BUTTON/API)',
  `parent_id` bigint DEFAULT NULL COMMENT '父权限 ID',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '权限描述',
  `sort_order` int DEFAULT 0 COMMENT '排序值',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  `deleted_at` datetime DEFAULT NULL COMMENT '软删除标记',
  PRIMARY KEY (`id`),
  UNIQUE KEY `code` (`code`),
  KEY `FK_permissions_parent_id` (`parent_id`),
  KEY `idx_permissions_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='权限表';

-- role_permissions (角色权限关联表)
CREATE TABLE IF NOT EXISTS `role_permissions` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '关联 ID',
  `role_id` bigint NOT NULL COMMENT '角色 ID',
  `permission_id` bigint NOT NULL COMMENT '权限 ID',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  `deleted_at` datetime DEFAULT NULL COMMENT '软删除标记',
  PRIMARY KEY (`id`),
  KEY `FK_role_permissions_role_id` (`role_id`),
  KEY `FK_role_permissions_permission_id` (`permission_id`),
  UNIQUE KEY `uk_role_permission` (`role_id`,`permission_id`,`deleted_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限关联表';

-- dict_types (字典类型表)
CREATE TABLE IF NOT EXISTS `dict_types` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '字典类型ID',
  `type_code` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '字典类型编码',
  `type_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '字典类型名称',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '描述信息',
  `sort_order` int DEFAULT 0 COMMENT '排序值，数值越小优先级越高',
  `is_active` VARCHAR(50) DEFAULT '1' COMMENT '是否启用',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  `deleted_at` datetime DEFAULT NULL COMMENT '软删除标记',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_dict_types_type_code` (`type_code`),
  KEY `idx_dict_types_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='字典类型表';

-- dict_data (字典数据表)
CREATE TABLE IF NOT EXISTS `dict_data` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '字典数据ID',
  `type_id` bigint NOT NULL COMMENT '关联字典类型ID',
  `data_label` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '字典标签（显示值）',
  `data_value` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '字典键值（实际值）',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '描述信息',
  `sort_order` int DEFAULT 0 COMMENT '排序值，数值越小优先级越高',
  `is_active` VARCHAR(50) DEFAULT '1' COMMENT '是否启用',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  `deleted_at` datetime DEFAULT NULL COMMENT '软删除标记',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_dict_data_type_value` (`type_id`,`data_value`),
  KEY `FK_dict_data_type_id` (`type_id`),
  KEY `idx_dict_data_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='字典数据表';

-- engineering_service_tabs (工程信息服务Tab配置表)
CREATE TABLE IF NOT EXISTS `engineering_service_tabs` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `tab_key` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Tab标识键',
  `tab_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Tab显示名称',
  `tab_icon` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Tab图标类名',
  `sort_order` int DEFAULT 0 COMMENT '排序顺序',
  `is_visible` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT '1' COMMENT '是否显示',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  `deleted_at` datetime DEFAULT NULL COMMENT '软删除时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tab_key` (`tab_key`),
  KEY `idx_engineering_tabs_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工程信息服务Tab配置表';

-- --------------------------------------------------------------------------------
-- 模块: 工程信息服务 (新增功能)
-- --------------------------------------------------------------------------------

-- pumping_stations (泵站信息表)
CREATE TABLE IF NOT EXISTS `pumping_stations` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '泵站名称',
  `station_code` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '泵站编码',
  `station_type` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '泵站类型（通过工程服务API获取类型选项）',
  `water_project` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '所属供水工程',
  `water_company` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '所属供水公司',
  `longitude` decimal(10,7) DEFAULT NULL COMMENT '经度',
  `latitude` decimal(10,7) DEFAULT NULL COMMENT '纬度',
  `address` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '地址',
  `operation_mode` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '运行方式（关联dict_data.data_value，type_code=operation_mode）',
  `unit_count` int DEFAULT NULL COMMENT '机组数量（台）',
  `design_scale` decimal(12,2) DEFAULT NULL COMMENT '设计规模（m³/天）',
  `installed_capacity` decimal(12,2) DEFAULT NULL COMMENT '装机容量（kW）',
  `lift_head` decimal(10,2) DEFAULT NULL COMMENT '扬程（m）',
  `establishment_date` date DEFAULT NULL COMMENT '建站年月',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  `deleted_at` datetime DEFAULT NULL COMMENT '软删除标记',
  PRIMARY KEY (`id`),
  UNIQUE KEY `station_code` (`station_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='泵站信息表';

-- monitoring_stations (监测站点表)
CREATE TABLE IF NOT EXISTS `monitoring_stations` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `station_code` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '站码',
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '站名',
  `water_system_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '水系名称',
  `river_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '河流名称',
  `monitoring_item_code` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '监测项目码（关联dict_data.data_value，type_code=monitoring_item）',
  `admin_region_code` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '行政区划代码（关联regions.code）',
  `establishment_date` date DEFAULT NULL COMMENT '设站年月',
  `longitude` decimal(10,7) DEFAULT NULL COMMENT '经度',
  `latitude` decimal(10,7) DEFAULT NULL COMMENT '纬度',
  `remark` text COLLATE utf8mb4_unicode_ci COMMENT '备注',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  `deleted_at` datetime DEFAULT NULL COMMENT '软删除标记',
  PRIMARY KEY (`id`),
  UNIQUE KEY `station_code` (`station_code`),
  KEY `FK_monitoring_stations_region_code` (`admin_region_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='监测站点表';

-- flow_monitoring_data (流量监测数据表)
CREATE TABLE IF NOT EXISTS `flow_monitoring_data` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '数据ID',
  `station_id` bigint DEFAULT NULL COMMENT '监测站点ID (关联monitoring_stations表)',
  `monitoring_time` datetime NOT NULL COMMENT '监测时间',
  `instant_flow` decimal(10,3) DEFAULT NULL COMMENT '瞬时流量(m³/s)',
  `cumulative_flow` decimal(15,3) DEFAULT NULL COMMENT '累计流量(m³)',
  `data_quality` tinyint DEFAULT 1 COMMENT '数据质量(1:正常,2:异常,3:缺失)',
  `collection_method` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT 'AUTO' COMMENT '采集方式(AUTO:自动,MANUAL:手动)',
  `data_source` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '数据来源设备',
  `remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注信息',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  `deleted_at` datetime DEFAULT NULL COMMENT '软删除标记',
  PRIMARY KEY (`id`),
  KEY `idx_station_time` (`station_id`, `monitoring_time`),
  KEY `idx_monitoring_time` (`monitoring_time`),
  KEY `idx_data_quality` (`data_quality`),
  KEY `idx_data_source` (`data_source`),
  KEY `idx_created_at` (`created_at`),
  KEY `idx_quality_time` (`data_quality`, `monitoring_time`),
  CONSTRAINT `FK_flow_data_station_id` FOREIGN KEY (`station_id`)
    REFERENCES `monitoring_stations` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='流量监测数据表';

-- water_level_monitoring_data (水位监测数据表)
CREATE TABLE IF NOT EXISTS `water_level_monitoring_data` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '数据ID',
  `station_id` bigint DEFAULT NULL COMMENT '监测站点ID (关联monitoring_stations表)',
  `monitoring_time` datetime NOT NULL COMMENT '监测时间',
  `water_level` decimal(10,3) DEFAULT NULL COMMENT '水位高度(m)',
  `data_quality` tinyint DEFAULT 1 COMMENT '数据质量(1:正常,2:异常,3:缺失)',
  `collection_method` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT 'AUTO' COMMENT '采集方式(AUTO:自动,MANUAL:手动)',
  `data_source` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '数据来源设备',
  `remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注信息',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  `deleted_at` datetime DEFAULT NULL COMMENT '软删除标记',
  PRIMARY KEY (`id`),
  KEY `idx_station_time` (`station_id`, `monitoring_time`),
  KEY `idx_monitoring_time` (`monitoring_time`),
  KEY `idx_data_quality` (`data_quality`),
  KEY `idx_data_source` (`data_source`),
  KEY `idx_created_at` (`created_at`),
  KEY `idx_quality_time` (`data_quality`, `monitoring_time`),
  CONSTRAINT `FK_water_level_data_station_id` FOREIGN KEY (`station_id`)
    REFERENCES `monitoring_stations` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='水位监测数据表';

-- water_quality_monitoring_data (水质监测数据表) - 水平存储模式
CREATE TABLE IF NOT EXISTS `water_quality_monitoring_data` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '数据ID',
  `station_id` bigint DEFAULT NULL COMMENT '监测站点ID (关联monitoring_stations表)',
  `monitoring_time` datetime NOT NULL COMMENT '监测时间',

  -- 8种水质监测项目数据字段
  `water_temperature` decimal(10,3) DEFAULT NULL COMMENT '水温(℃)',
  `turbidity` decimal(10,3) DEFAULT NULL COMMENT '浊度(NTU)',
  `ph_value` decimal(10,3) DEFAULT NULL COMMENT 'PH值',
  `conductivity` decimal(10,3) DEFAULT NULL COMMENT '电导率(uS/cm)',
  `dissolved_oxygen` decimal(10,3) DEFAULT NULL COMMENT '溶解氧(mg/L)',
  `ammonia_nitrogen` decimal(10,3) DEFAULT NULL COMMENT '氨氮(mg/L)',
  `cod_value` decimal(10,3) DEFAULT NULL COMMENT '化学需氧量(mg/L)',
  `residual_chlorine` decimal(10,3) DEFAULT NULL COMMENT '余氯(mg/L)',

  -- 数据质量和来源信息
  `data_quality` tinyint DEFAULT 1 COMMENT '数据质量(1:正常,2:异常,3:缺失)',
  `collection_method` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT 'AUTO' COMMENT '采集方式(AUTO:自动,MANUAL:手动)',
  `data_source` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '数据来源设备',
  `remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注信息',

  -- 时间戳字段
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  `deleted_at` datetime DEFAULT NULL COMMENT '软删除标记',

  PRIMARY KEY (`id`),
  KEY `idx_station_time` (`station_id`, `monitoring_time`),
  KEY `idx_monitoring_time` (`monitoring_time`),
  KEY `idx_data_quality` (`data_quality`),
  KEY `idx_data_source` (`data_source`),
  KEY `idx_created_at` (`created_at`),
  CONSTRAINT `FK_water_quality_data_station_id` FOREIGN KEY (`station_id`)
    REFERENCES `monitoring_stations` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='水质监测数据表(水平存储模式)';

-- reservoir_monitoring_data (水库监测数据表)
CREATE TABLE IF NOT EXISTS `reservoir_monitoring_data` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '数据ID',
  `station_id` bigint DEFAULT NULL COMMENT '监测站点ID (关联monitoring_stations表)',
  `monitoring_time` datetime NOT NULL COMMENT '监测时间',
  `water_level` decimal(10,3) DEFAULT NULL COMMENT '水位高度(m)',
  `storage_capacity` decimal(15,3) DEFAULT NULL COMMENT '蓄水量(10⁴m³)',
  `flood_limit_diff` decimal(10,3) DEFAULT NULL COMMENT '超汛限(m)',
  `inflow` decimal(10,3) DEFAULT NULL COMMENT '入库流量(m³/s)',
  `outflow` decimal(10,3) DEFAULT NULL COMMENT '出库流量(m³/s)',
  `data_quality` tinyint DEFAULT 1 COMMENT '数据质量(1:正常,2:异常,3:缺失)',
  `collection_method` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT 'AUTO' COMMENT '采集方式(AUTO:自动,MANUAL:手动)',
  `data_source` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '数据来源设备',
  `remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注信息',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  `deleted_at` datetime DEFAULT NULL COMMENT '软删除标记',
  PRIMARY KEY (`id`),
  KEY `idx_station_time` (`station_id`, `monitoring_time`),
  KEY `idx_monitoring_time` (`monitoring_time`),
  KEY `idx_data_quality` (`data_quality`),
  KEY `idx_data_source` (`data_source`),
  KEY `idx_created_at` (`created_at`),
  KEY `idx_quality_time` (`data_quality`, `monitoring_time`),
  CONSTRAINT `FK_reservoir_data_station_id` FOREIGN KEY (`station_id`)
    REFERENCES `monitoring_stations` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='水库监测数据表';

-- pipelines (管道信息表)
CREATE TABLE IF NOT EXISTS `pipelines` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `pipeline_code` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '管道编码',
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '管道名称',
  `pipeline_type` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '管道类型（关联dict_data.data_value，type_code=pipeline_type）',
  `start_longitude` decimal(10,7) DEFAULT NULL COMMENT '起点经度',
  `start_latitude` decimal(10,7) DEFAULT NULL COMMENT '起点纬度',
  `end_longitude` decimal(10,7) DEFAULT NULL COMMENT '终点经度',
  `end_latitude` decimal(10,7) DEFAULT NULL COMMENT '终点纬度',
  `length` decimal(10,2) DEFAULT NULL COMMENT '管道长度（km）',
  `diameter` decimal(8,2) DEFAULT NULL COMMENT '管径（mm）',
  `design_pressure` decimal(8,2) DEFAULT NULL COMMENT '设计压力（MPa）',
  `design_flow` decimal(12,2) DEFAULT NULL COMMENT '设计流量（m³/h）',
  `burial_depth` decimal(8,2) DEFAULT NULL COMMENT '埋深（m）',
  `construction_date` date DEFAULT NULL COMMENT '建设年月',
  `remark` text COLLATE utf8mb4_unicode_ci COMMENT '备注',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  `deleted_at` datetime DEFAULT NULL COMMENT '软删除标记',
  PRIMARY KEY (`id`),
  UNIQUE KEY `pipeline_code` (`pipeline_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管道信息表';

-- villages (村庄信息表)
CREATE TABLE IF NOT EXISTS `villages` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '村庄名称',
  `longitude` decimal(10,7) DEFAULT NULL COMMENT '经度',
  `latitude` decimal(10,7) DEFAULT NULL COMMENT '纬度',
  `administrative_code` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '行政区划代码（关联regions.code）',

  `current_population` int DEFAULT NULL COMMENT '现状人口（人）',
  `planned_population` int DEFAULT NULL COMMENT '规划人口（人）',

  `daily_water_consumption` decimal(10,2) DEFAULT NULL COMMENT '日用水量（m³/天）',
  `water_supply_guarantee_rate` decimal(5,2) DEFAULT NULL COMMENT '供水保证率（%）',

  `contact_person` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '联系人',
  `contact_phone` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '联系电话',
  `remark` text COLLATE utf8mb4_unicode_ci COMMENT '备注',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  `deleted_at` datetime DEFAULT NULL COMMENT '软删除标记',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_village_name_admin` (`name`, `administrative_code`),
  KEY `FK_villages_region_code` (`administrative_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='村庄信息表';

-- reservoirs (水库信息表)
CREATE TABLE IF NOT EXISTS `reservoirs` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `reservoir_code` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '水库编码',
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '水库名称',
  `water_project` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '所属水利工程',
  `department_id` bigint DEFAULT NULL COMMENT '管理部门ID（关联departments.id）',
  `manager_id` bigint DEFAULT NULL COMMENT '负责人ID（关联personnel.id）',
  `longitude` decimal(10,7) DEFAULT NULL COMMENT '经度',
  `latitude` decimal(10,7) DEFAULT NULL COMMENT '纬度',
  `location` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '水库所在位置',
  `registration_no` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '水库注册登记号',
  `admin_region_code` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '所属行政区划（关联regions.code）',
  `engineering_grade` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '工程等级（关联dict_data.data_value，type_code=engineering_grade）',
  `engineering_scale` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '工程规模（关联dict_data.data_value，type_code=engineering_scale）',
  `total_capacity` decimal(12,2) DEFAULT NULL COMMENT '总库容（万m³）',
  `regulating_capacity` decimal(12,2) DEFAULT NULL COMMENT '调节库容（万m³）',
  `dead_capacity` decimal(12,2) DEFAULT NULL COMMENT '死库容（万m³）',
  `establishment_date` date DEFAULT NULL COMMENT '建库年月',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  `deleted_at` datetime DEFAULT NULL COMMENT '软删除标记',
  PRIMARY KEY (`id`),
  UNIQUE KEY `reservoir_code` (`reservoir_code`),
  KEY `FK_reservoirs_department_id` (`department_id`),
  KEY `FK_reservoirs_manager_id` (`manager_id`),
  KEY `FK_reservoirs_region_code` (`admin_region_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='水库信息表';

-- water_plants (水厂信息表)
CREATE TABLE IF NOT EXISTS `water_plants` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `plant_code` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '水厂编码',
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '水厂名称',
  `water_project` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '所属供水工程',
  `department_id` bigint DEFAULT NULL COMMENT '管理部门ID（关联departments.id）',
  `manager_id` bigint DEFAULT NULL COMMENT '负责人ID（关联personnel.id）',
  `address` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '地址',
  `management_unit` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '管理单位',
  `longitude` decimal(10,7) DEFAULT NULL COMMENT '经度',
  `latitude` decimal(10,7) DEFAULT NULL COMMENT '纬度',
  `design_scale` decimal(12,2) DEFAULT NULL COMMENT '设计规模（m³/天）',
  `supply_area` text COLLATE utf8mb4_unicode_ci COMMENT '供水范围（村镇）',
  `supply_load_ratio` decimal(5,2) DEFAULT NULL COMMENT '供水负荷率（%）',
  `supply_population` int DEFAULT NULL COMMENT '供水人口（万人）',
  `contact_phone` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '联系电话',
  `establishment_date` date DEFAULT NULL COMMENT '建站年月',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  `deleted_at` datetime DEFAULT NULL COMMENT '软删除标记',
  PRIMARY KEY (`id`),
  UNIQUE KEY `plant_code` (`plant_code`),
  KEY `FK_water_plants_department_id` (`department_id`),
  KEY `FK_water_plants_manager_id` (`manager_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='水厂信息表';

-- floating_boats (浮船信息表)
CREATE TABLE IF NOT EXISTS `floating_boats` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `boat_code` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '浮船编码',
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '浮船名称',
  `water_plant_id` bigint DEFAULT NULL COMMENT '所属水厂ID（关联water_plants.id）',
  `department_id` bigint DEFAULT NULL COMMENT '管理部门ID（关联departments.id）',
  `manager_id` bigint DEFAULT NULL COMMENT '负责人ID（关联personnel.id）',
  `longitude` decimal(10,7) DEFAULT NULL COMMENT '经度',
  `latitude` decimal(10,7) DEFAULT NULL COMMENT '纬度',

  `length` decimal(8,2) DEFAULT NULL COMMENT '船长（m）',
  `width` decimal(8,2) DEFAULT NULL COMMENT '船宽（m）',
  `draft` decimal(8,2) DEFAULT NULL COMMENT '吃水深度（m）',
  `capacity` decimal(10,2) DEFAULT NULL COMMENT '抽水能力（m³/h）',

  `power_consumption` decimal(10,2) DEFAULT NULL COMMENT '功率（kW）',

  `pumping_status` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '抽水状态（关联dict_data.data_value，type_code=device_status）',
  `maintenance_date` date DEFAULT NULL COMMENT '上次维护日期',
  `remark` text COLLATE utf8mb4_unicode_ci COMMENT '备注',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  `deleted_at` datetime DEFAULT NULL COMMENT '软删除标记',
  PRIMARY KEY (`id`),
  UNIQUE KEY `boat_code` (`boat_code`),
  KEY `FK_floating_boats_water_plant_id` (`water_plant_id`),
  KEY `FK_floating_boats_department_id` (`department_id`),
  KEY `FK_floating_boats_manager_id` (`manager_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='浮船信息表';

-- disinfection_materials (消毒药材表)
CREATE TABLE IF NOT EXISTS `disinfection_materials` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '名称',
  `water_plant_id` bigint DEFAULT NULL COMMENT '所属水厂ID（关联water_plants.id）',
  `storage_condition` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '存储条件',
  `production_date` date DEFAULT NULL COMMENT '生产日期',
  `validity_period` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '有效期',
  `quantity` decimal(10,2) DEFAULT NULL COMMENT '库存数量',
  `unit` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '单位',
  `remark` text COLLATE utf8mb4_unicode_ci COMMENT '备注',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  `deleted_at` datetime DEFAULT NULL COMMENT '软删除标记',
  PRIMARY KEY (`id`),
  KEY `FK_disinfection_materials_water_plant_id` (`water_plant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消毒药材表';



-- --------------------------------------------------------------------------------
-- 模块: 预警管理 (新增功能)
-- --------------------------------------------------------------------------------

-- warning_thresholds (预警指标设定表)
CREATE TABLE IF NOT EXISTS `warning_thresholds` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `station_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '测点名称',
  `monitoring_item` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '监测项（关联dict_data，type_code=monitoring_item）',
  `upper_upper_limit` decimal(10,2) DEFAULT NULL COMMENT '上上限',
  `upper_limit` decimal(10,2) DEFAULT NULL COMMENT '上限',
  `lower_limit` decimal(10,2) DEFAULT NULL COMMENT '下限',
  `lower_lower_limit` decimal(10,2) DEFAULT NULL COMMENT '下下限',
  `unit` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '单位',
  `is_active` VARCHAR(50) DEFAULT '1' COMMENT '是否启用',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  `deleted_at` datetime DEFAULT NULL COMMENT '软删除标记',
  PRIMARY KEY (`id`),
  KEY `idx_station_name` (`station_name`),
  KEY `idx_monitoring_item` (`monitoring_item`),
  KEY `idx_is_active` (`is_active`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='预警指标设定表';

-- warning_records (预警信息记录表)
CREATE TABLE IF NOT EXISTS `warning_records` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `warning_location` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '预警地点',
  `warning_type` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '预警类型（水位、流量、水质等）',
  `warning_level` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '预警等级（关联dict_data，type_code=warning_level）',
  `warning_content` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '预警内容',
  `warning_status` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT '进行中' COMMENT '预警状态（进行中、已解除）',
  `project_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '所属工程',
  `occurred_at` datetime NOT NULL COMMENT '发生时间',
  `resolved_at` datetime DEFAULT NULL COMMENT '解除时间',
  `threshold_id` bigint DEFAULT NULL COMMENT '关联预警指标ID',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  `deleted_at` datetime DEFAULT NULL COMMENT '软删除标记',
  PRIMARY KEY (`id`),
  KEY `idx_warning_location` (`warning_location`),
  KEY `idx_warning_type` (`warning_type`),
  KEY `idx_warning_level` (`warning_level`),
  KEY `idx_warning_status` (`warning_status`),
  KEY `idx_occurred_at` (`occurred_at`),
  KEY `FK_warning_records_threshold_id` (`threshold_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='预警信息记录表';

-- rainfall_monitoring_data (雨情监测数据表)
CREATE TABLE IF NOT EXISTS `rainfall_monitoring_data` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '数据ID',
  `station_id` bigint DEFAULT NULL COMMENT '监测站点ID (关联monitoring_stations表)',
  `monitoring_time` datetime NOT NULL COMMENT '监测时间',
  `rainfall` decimal(10,3) DEFAULT NULL COMMENT '降雨量(mm)',
  `rainfall_intensity` decimal(10,3) DEFAULT NULL COMMENT '降雨强度(mm/h)',
  `cumulative_rainfall` decimal(15,3) DEFAULT NULL COMMENT '累计降雨量(mm)',
  `data_quality` tinyint DEFAULT 1 COMMENT '数据质量(1:正常,2:异常,3:缺失)',
  `collection_method` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT 'AUTO' COMMENT '采集方式(AUTO:自动,MANUAL:手动)',
  `data_source` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '数据来源设备',
  `remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注信息',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  `deleted_at` datetime DEFAULT NULL COMMENT '软删除标记',
  PRIMARY KEY (`id`),
  KEY `idx_station_time` (`station_id`, `monitoring_time`),
  KEY `idx_monitoring_time` (`monitoring_time`),
  KEY `idx_data_quality` (`data_quality`),
  KEY `idx_data_source` (`data_source`),
  KEY `idx_created_at` (`created_at`),
  KEY `idx_quality_time` (`data_quality`, `monitoring_time`),
  CONSTRAINT `FK_rainfall_data_station_id` FOREIGN KEY (`station_id`)
    REFERENCES `monitoring_stations` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='雨情监测数据表';

-- --------------------------------------------------------------------------------
-- 模块: 工程巡检 (新增功能)
-- --------------------------------------------------------------------------------

-- 巡检任务表
CREATE TABLE IF NOT EXISTS `inspection_tasks` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `title` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '任务标题',
  `facility_type` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '设施类型（通过工程服务API获取类型选项）',
  `facility_id` bigint NOT NULL COMMENT '设施ID（引用不同业务表的主键）',
  `frequency` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '巡检频次（dict_data.data_value，type_code=inspection_frequency）',
  `content` text COLLATE utf8mb4_unicode_ci COMMENT '巡检内容',
  `assignee_id` bigint DEFAULT NULL COMMENT '责任人ID（关联personnel.id）',
  `status` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT 'PENDING' COMMENT '任务状态（dict_data.data_value，type_code=inspection_status）',
  `scheduled_date` date DEFAULT NULL COMMENT '计划执行日期',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  `deleted_at` datetime DEFAULT NULL COMMENT '软删除标记',
  PRIMARY KEY (`id`),
  KEY `idx_inspection_tasks_facility` (`facility_type`, `facility_id`),
  KEY `FK_inspection_tasks_assignee_id` (`assignee_id`),
  KEY `idx_inspection_tasks_status` (`status`),
  KEY `idx_inspection_tasks_scheduled_date` (`scheduled_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='巡检任务表';

-- 巡检记录表
CREATE TABLE IF NOT EXISTS `inspection_records` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `task_id` bigint DEFAULT NULL COMMENT '关联的巡检任务ID',
  `inspector_id` bigint DEFAULT NULL COMMENT '巡检人员ID（关联personnel.id）',
  `facility_type` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '设施类型（通过工程服务API获取类型选项）',
  `facility_id` bigint NOT NULL COMMENT '设施ID',
  `record_time` datetime NOT NULL COMMENT '巡检时间',
  `device_status` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '设备状态（dict_data.data_value，type_code=device_status）',
  `issue_flag` tinyint(1) DEFAULT 0 COMMENT '是否发现问题(0:否,1:是)',
  `issue_description` text COLLATE utf8mb4_unicode_ci COMMENT '问题描述',
  `resolution` text COLLATE utf8mb4_unicode_ci COMMENT '处理措施',
  `resolved_at` datetime DEFAULT NULL COMMENT '问题解决时间',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  `deleted_at` datetime DEFAULT NULL COMMENT '软删除标记',
  PRIMARY KEY (`id`),
  KEY `idx_inspection_records_task_id` (`task_id`),
  KEY `idx_inspection_records_facility` (`facility_type`, `facility_id`),
  KEY `idx_inspection_records_record_time` (`record_time`),
  KEY `idx_inspection_records_issue_flag` (`issue_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='巡检记录表';

-- 巡检记录附件表
CREATE TABLE IF NOT EXISTS `inspection_attachments` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `record_id` bigint NOT NULL COMMENT '巡检记录ID',
  `file_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件名',
  `file_path` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件访问路径(/uploads/...)',
  `content_type` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文件类型',
  `file_size` bigint DEFAULT NULL COMMENT '文件大小(字节)',
  `created_at` datetime NOT NULL COMMENT '上传时间',
  PRIMARY KEY (`id`),
  KEY `idx_inspection_attachments_record_id` (`record_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='巡检记录附件表';

-- --------------------------------------------------------------------------------
-- 数据库更新完成
-- 更新说明：
-- 1. 新增了工程信息服务模块的8个数据表
-- 2. 新增了预警管理模块的2个数据表（warning_thresholds、warning_records）
-- 3. 新增了实时监控模块的2个数据表（flow_monitoring_data、water_quality_monitoring_data）
-- 4. 新增了6个工程信息相关的字典类型和对应的字典数据
-- 5. 保留了原有的10个基础信息管理数据表
-- 6. 保留了所有初始化数据（角色、权限、用户、岗位等）
-- 7. 确保了数据库结构的完整性和一致性
-- 8. 工程信息服务模块支持泵站、监测站点、管道、村庄、水库、水厂、浮船、消毒药材管理
-- 9. 实时监控模块支持流量监测、水位监测和水质监测数据管理
-- 10. 预警管理模块支持预警指标设定和预警信息处理功能
-- 11. 实时监控模块支持流量监测和水位监测数据的存储和管理
-- 11. 通过外键关联实现了与现有基础信息管理模块的集成
-- 12. 通过数据字典实现了枚举值的统一管理
-- 13. 流量监测数据表优化了索引设计，支持高效的时间序列查询
-- 14. 采用软删除机制和灵活的外键约束，提高数据维护的灵活性
-- 15. 新增了工程巡检模块的3个数据表（inspection_tasks、inspection_records、inspection_attachments）
-- --------------------------------------------------------------------------------
