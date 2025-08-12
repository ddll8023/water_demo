-- --------------------------------------------------------------------------------
-- 初始化数据
-- --------------------------------------------------------------------------------
USE eb_water_resources;
-- 插入正确的角色（权限级别，按权限大小排序）
INSERT INTO roles (name, description, sort_order, is_active, created_at, updated_at)
VALUES
    ('超级管理员', '拥有系统所有权限，包括系统配置和用户管理', 10, TRUE, NOW(), NOW()),
    ('业务管理员', '拥有业务管理权限，不能修改系统核心配置', 20, TRUE, NOW(), NOW()),
    ('只读用户', '仅有数据查看权限，不能进行任何修改操作', 40, TRUE, NOW(), NOW());

-- 插入岗位数据（职务职责，不关联角色）
INSERT INTO positions (name, description, responsibilities, level, created_at, updated_at)
VALUES
    ('系统管理员', '负责系统维护、配置管理和技术支持', '系统运维、技术支持、配置管理、故障处理', '高级', NOW(), NOW()),
    ('业务管理员', '负责业务流程管理、监督和协调工作', '业务流程设计、团队协调、工作监督、决策支持', '中级', NOW(), NOW()),
    ('监测员', '负责监测数据采集、分析和异常处理', '数据采集、监测分析、异常识别、报告编制', '初级', NOW(), NOW()),
    ('巡检员', '负责现场设备巡检、维护和问题上报', '设备巡检、故障发现、维护记录、问题上报', '初级', NOW(), NOW()),
    ('数据分析员', '负责数据统计分析和报表生成', '数据统计、趋势分析、报表制作、决策支持', '中级', NOW(), NOW());

-- 插入初始用户 (密码: admin123)
INSERT INTO users (username, password_hash, email, role_id, is_active, created_at, updated_at)
SELECT 'admin', '$2a$10$WLeiMBhn4Xyv.NkIh6ITb.d486nM1x22N0CfGxOswB85jqw7AawVq', 'admin@example.com',
       (SELECT id FROM roles WHERE name = '超级管理员'),
       TRUE, NOW(), NOW();


-- 插入简化权限体系（按重要性排序）
INSERT INTO permissions (name, code, type, description, sort_order, created_at, updated_at)
VALUES
    ('系统管理', 'system:manage', 'MODULE', '系统管理权限（包含用户、角色、权限管理）', 10, NOW(), NOW()),
    ('业务管理', 'business:manage', 'MODULE', '业务管理权限（包含业务数据的增删改查）', 20, NOW(), NOW()),
    ('业务操作', 'business:operate', 'MODULE', '业务操作权限（可进行数据录入和处理）', 25, NOW(), NOW()),
    ('数据查看', 'data:view', 'MODULE', '数据查看权限（只读访问权限）', 30, NOW(), NOW());

-- 为超级管理员角色分配所有权限
INSERT INTO role_permissions (role_id, permission_id, created_at, updated_at)
SELECT
    r.id,
    p.id,
    NOW(),
    NOW()
FROM
    roles r,
    permissions p
WHERE
    r.name = '超级管理员'
  AND p.code IN (
                 'system:manage',
                 'business:manage',
                 'business:operate',
                 'data:view'
    );


-- 为业务管理员角色分配权限（业务管理 + 业务操作 + 数据查看）
INSERT INTO role_permissions (role_id, permission_id, created_at, updated_at)
SELECT
    r.id,
    p.id,
    NOW(),
    NOW()
FROM
    roles r,
    permissions p
WHERE
    r.name = '业务管理员'
  AND p.code IN (
                 'business:manage',
                 'business:operate',
                 'data:view'
    );

-- 为只读用户角色分配权限（仅数据查看）
INSERT INTO role_permissions (role_id, permission_id, created_at, updated_at)
SELECT
    (SELECT id FROM roles WHERE name = '只读用户' LIMIT 1),
    (SELECT id FROM permissions WHERE code = 'data:view' LIMIT 1),
    NOW(),
    NOW();

-- 为admin用户分配超级管理员角色
INSERT INTO user_roles (user_id, role_id, created_at)
SELECT
    (SELECT id FROM users WHERE username = 'admin' LIMIT 1),
    (SELECT id FROM roles WHERE name = '超级管理员' LIMIT 1),
    NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM user_roles
    WHERE user_id = (SELECT id FROM users WHERE username = 'admin' LIMIT 1)
      AND role_id = (SELECT id FROM roles WHERE name = '超级管理员' LIMIT 1)
);

-- --------------------------------------------------------------------------------
-- 工程信息服务模块示例数据
-- --------------------------------------------------------------------------------

-- 泵站信息示例数据
INSERT INTO pumping_stations (name, station_code, station_type, water_project, water_company, longitude, latitude, address, operation_mode, unit_count, design_scale, installed_capacity, lift_head, establishment_date, created_at, updated_at)
VALUES
('加压泵站3', '24043426', 'pumping_station', '鄂北水资源供水工程', '暂无', 113.522685, 31.853906, '湖北省随州市曾都区万店镇', '1_main_1_backup', 2, 300, 300, 300, '2023-12-01', NOW(), NOW()),
('加压泵站4', '24043424', 'pumping_station', '鄂北水资源供水工程', '暂无', 113.539961, 31.908334, '湖北省随州市曾都区万店镇', '1_main_1_backup', 2, 400, 400, 400, '2023-12-01', NOW(), NOW()),
('加压泵站5', '24043418', 'pumping_station', '鄂北水资源供水工程', '暂无', 113.527801, 31.854328, '湖北省随州市曾都区万店镇', '1_main_1_backup', 2, 500, 500, 500, '2023-12-01', NOW(), NOW()),
('加压泵站1', '24043419', 'pumping_station', '鄂北水资源供水工程', '打开', 113.486306, 31.883631, '湖北省随州市曾都区万店镇', '1_main_1_backup', 2, 100, 100, 100, '2023-11-01', NOW(), NOW()),
('加压泵站2', '24043422', 'pumping_station', '鄂北水资源供水工程', '打开', 113.512786, 31.883631, '湖北省随州市曾都区万店镇', '1_main_1_backup', 2, 100, 100, 100, '2023-11-01', NOW(), NOW());

-- 监测站点示例数据
INSERT INTO monitoring_stations (name, station_code, water_system_name, river_name, monitoring_item_code, admin_region_code, longitude, latitude, establishment_date, remark, created_at, updated_at)
VALUES
    ('流量站1', '00000001111112', NULL, NULL, 'Q', NULL, 113.492078, 31.882447, '2024-04-01', '暂无', NOW(), NOW()),
    ('流量站2', '00000002', NULL, NULL, 'Q', NULL, 113.495254, 31.880916, '2024-04-01', '暂无', NOW(), NOW()),
    ('流量站3', '00000003', NULL, NULL, 'Q', NULL, 113.486218, 31.884864, '2024-04-01', '暂无', NOW(), NOW()),
    ('流量站4', '00000004', NULL, NULL, 'Q', NULL, 113.52604, 31.856767, '2024-04-01', '暂无', NOW(), NOW()),
    ('流量站5', '00000005', NULL, NULL, 'Q', NULL, 113.45938, 31.807912, '2024-04-01', '暂无', NOW(), NOW()),
    ('加压视频1', '24043419', NULL, NULL, 'V', NULL, 113.486306, 31.883631, '2024-04-01', '暂无', NOW(), NOW()),
    ('加压视频4', '24043424', NULL, NULL, 'V', NULL, 113.539961, 31.908334, '2024-04-01', '暂无', NOW(), NOW()),
    ('加压视频5', '24043418', NULL, NULL, 'V', NULL, 113.527891, 31.854328, '2024-04-01', '暂无', NOW(), NOW()),
    ('加压视频2', '24043422', NULL, NULL, 'V', NULL, 113.512786, 31.860796, '2024-04-01', '暂无', NOW(), NOW()),
    ('加压视频3', '24043426', NULL, NULL, 'V', NULL, 113.522555, 31.854036, '2024-04-01', '暂无', NOW(), NOW());

-- 管道信息示例数据（与前端显示数据一致）
INSERT INTO pipelines (pipeline_code, name, pipeline_type, start_longitude, start_latitude, end_longitude, end_latitude, length, diameter, design_pressure, design_flow, burial_depth, operation_status, construction_date, remark, created_at, updated_at)
VALUES
('PL001', '干管1', 'main_pipeline', 112.1234, 31.6789, 112.2234, 31.7789, 5.0, 1200, 1.0, 10000, 2.0, 'normal', '2023-01-01', '供水干管', NOW(), NOW()),
('PL002', '干管2', 'main_pipeline', 112.2234, 31.7789, 112.3234, 31.8789, 5.0, 1200,  1.0, 10000, 2.0, 'normal', '2023-01-01', '供水干管', NOW(), NOW()),
('PL003', '干管3', 'main_pipeline', 112.3234, 31.8789, 112.4234, 31.9789, 5.0, 1200,  1.0, 10000, 2.0, 'normal', '2023-01-01', '供水干管', NOW(), NOW()),
('PL004', '支管1', 'branch_pipeline', 112.1234, 31.6789, 112.1334, 31.6889, 2.0, 600, 0.6, 5000, 1.5, 'normal', '2023-01-01', '供水支管', NOW(), NOW()),
('PL005', '支管2', 'branch_pipeline', 112.2234, 31.7789, 112.2334, 31.7889, 2.0, 600, 0.6, 5000, 1.5, 'normal', '2023-01-01', '供水支管', NOW(), NOW()),
('PL006', '支管3', 'branch_pipeline', 112.3234, 31.8789, 112.3334, 31.8889, 2.0, 600, 0.6, 5000, 1.5, 'normal', '2023-01-01', '供水支管', NOW(), NOW()),
('PL007', '支管4', 'branch_pipeline', 112.4234, 31.9789, 112.4334, 31.9889, 2.0, 600,  0.6, 5000, 1.5, 'normal', '2023-01-01', '供水支管', NOW(), NOW()),
('PL008', '支管5', 'branch_pipeline', 112.1334, 31.6889, 112.1434, 31.6989, 2.0, 600, 0.6, 5000, 1.5, 'normal', '2023-01-01', '供水支管', NOW(), NOW()),
('PL009', '支管6', 'branch_pipeline', 112.2334, 31.7889, 112.2434, 31.7989, 2.0, 600,  0.6, 5000, 1.5, 'normal', '2023-01-01', '干管2的下段', NOW(), NOW()),
('PL010', '支管7', 'branch_pipeline', 112.3334, 31.8889, 112.3434, 31.8989, 2.0, 600, 0.6, 5000, 1.5, 'normal', '2023-01-01', '经过加压泵站5', NOW(), NOW());



-- 水库信息示例数据
INSERT INTO reservoirs (reservoir_code, name, water_project, longitude, latitude, location, registration_no, admin_region_code, engineering_grade, engineering_scale, total_capacity, regulating_capacity, dead_capacity, created_at, updated_at)
VALUES
('24063432', '两河口水库', '鄂北水资源供水工程', 113.497584, 31.883842, '两河口位置', 'SK6589746', '曾都区万店镇', 'V', 'small_1', 50, 60, 50, NOW(), NOW());

-- 水厂信息示例数据
-- 首先确保有部门和人员数据
INSERT INTO departments (name, duty, contact, region_id, is_active, created_at, updated_at)
SELECT '测试部门', '负责水务管理工作', '027-12345678', 1, TRUE, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM departments WHERE name = '测试部门');

INSERT INTO personnel (full_name, phone, email, position_id, department_id, employee_no, status, hire_date, created_at, updated_at)
SELECT '测试1', '13545627895', 'user1@example.com',
       (SELECT id FROM positions WHERE name = '业务管理员' LIMIT 1),
       (SELECT id FROM departments WHERE name = '测试部门' LIMIT 1),
       'EMP002', '在职', '2020-01-01', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM personnel WHERE employee_no = 'EMP002');

-- 插入水厂示例数据
INSERT INTO water_plants (plant_code, name, water_project, department_id, manager_id, address, management_unit, longitude, latitude, design_scale, supply_area, supply_load_ratio, supply_population, contact_phone, establishment_date, created_at, updated_at)
SELECT '987654321', '水厂', '鄂北水资源供水工程',
       (SELECT id FROM departments WHERE name = '测试部门' LIMIT 1),
       (SELECT id FROM personnel WHERE employee_no = 'EMP002' LIMIT 1),
       '湖北省随州市', '随州市', 113.495722, 31.884381, 10.00, '曾都区', 12.00, 15, '13545627895', '2022-01-01', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM water_plants WHERE plant_code = '987654321');

-- 浮船信息示例数据（只保留用例数据）
INSERT INTO floating_boats (name, longitude, latitude, capacity, power_consumption, pumping_status, remark, created_at, updated_at)
VALUES
('浮船', 113.500215, 31.883597, 200, 300, 'normal', '用例示例浮船数据', NOW(), NOW());

-- 消毒药材示例数据
INSERT INTO disinfection_materials (name, water_plant_id, storage_condition, production_date, validity_period, quantity, unit, remark, created_at, updated_at)
VALUES
('氯气', 1, '干燥', '2024-08-13', '6个月', 0, '', '', NOW(), NOW()),
('氯气', 1, '干燥', '2020-09-10', '12个月', 0, '', '备注', NOW(), NOW());

-- --------------------------------------------------------------------------------
-- 村庄信息示例数据（新增）
-- --------------------------------------------------------------------------------
INSERT INTO villages (name, longitude, latitude, administrative_code, current_population, remark, created_at, updated_at)
VALUES
('沈家村', 113.471431, 31.917701, '420100', 1538, '安陆1组点', NOW(), NOW()),
('高家村', 113.447044, 31.892954, '420100', 1400, '安陆2组点', NOW(), NOW()),
('石家村委会', 113.474955, 31.873261, '420300', 500, '安陆10组点', NOW(), NOW()),
('红云村村委会', 113.481735, 31.876475, '420300', 400, '安陆13组点', NOW(), NOW()),
('楼市村', 113.495888, 31.868739, '420100', 2227, '安陆11组点', NOW(), NOW()),
('万店镇委会1', 113.406974, 31.860813, '420100', 1407, '千里岗点', NOW(), NOW()),
('万店镇委会2', 113.504905, 31.849186, '420300', 1466, '安陆3组点', NOW(), NOW()),
('万店镇委会3', 113.512751, 31.844108, '420300', 3258, '安陆6组点', NOW(), NOW()),
('黄水寺村委1', 113.533971, 31.846165, '420100', 900, '安陆8组点', NOW(), NOW()),
('黄水寺村委2', 113.530054, 31.854106, '420100', 700, '安陆7组点', NOW(), NOW());



-- 插入预警指标设定数据（用户提供的10条记录）
INSERT INTO warning_thresholds (station_name, monitoring_item, upper_upper_limit, upper_limit, lower_limit, lower_lower_limit, unit, is_active, created_at, updated_at)
VALUES
('两河口水库', 'H', 150.00, 120.00, 80.00, 60.00, 'm', 1, NOW(), NOW()),
('流量站1', 'Q', 100.00, 50.00, -50.00, -100.00, 'm³/s', 1, NOW(), NOW()),
('水厂', 'WT', 40.00, 30.00, 20.00, 10.00, '°', 1, NOW(), NOW()),
('水厂', 'TU', 30.00, 20.00, 10.00, 7.50, 'NTU', 1, NOW(), NOW()),
('水厂', 'PH', 10.00, 8.00, 6.00, 4.00, '无', 1, NOW(), NOW()),
('水厂', 'EC', 30.00, 20.00, 10.00, 7.50, 'uS/cm', 1, NOW(), NOW()),
('水厂', 'DO', 30.00, 20.00, 10.00, 7.50, 'mg/L', 1, NOW(), NOW()),
('水厂', 'AN', 30.00, 20.00, 10.00, 7.50, 'mg/L', 1, NOW(), NOW()),
('水厂', 'COD', 30.00, 20.00, 10.00, 7.50, 'mg/L', 1, NOW(), NOW()),
('水厂', 'RC', 100.00, 10.00, 0.00, 0.00, 'mg/L', 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE updated_at = NOW();

-- 插入预警记录示例数据（基于用户提供的预警信息处理表格）
INSERT INTO warning_records (warning_location, warning_type, warning_level, warning_content, warning_status, project_name, occurred_at, resolved_at, threshold_id, created_at, updated_at)
VALUES
('两河口水库', '水位', '4', '水位超过上限4.60米', '已解除', '鄂北水资源供水工程', '2025-02-25 08:50:00', '2025-02-25 15:32:13', NULL, NOW(), NOW()),
('两河口水库', '水位', '1', '水位高于上限9.60米', '已解除', '鄂北水资源供水工程', '2025-02-25 06:50:00', '2025-02-25 08:43:42', NULL, NOW(), NOW()),
('两河口水库', '水位', '1', '水位高于上限9.60米', '已解除', '鄂北水资源供水工程', '2025-02-25 05:45:00', '2025-02-25 08:43:42', NULL, NOW(), NOW()),
('两河口水库', '水位', '1', '水位高于上限9.60米', '已解除', '鄂北水资源供水工程', '2025-02-25 04:40:00', '2025-02-25 08:43:43', NULL, NOW(), NOW()),
('两河口水库', '水位', '1', '水位高于上限9.60米', '已解除', '鄂北水资源供水工程', '2025-02-25 03:35:00', '2025-02-25 08:43:43', NULL, NOW(), NOW())
ON DUPLICATE KEY UPDATE updated_at = NOW();



-- --------------------------------------------------------------------------------
-- 水库监测数据模块示例数据
-- --------------------------------------------------------------------------------

-- 添加水库监测站点示例数据
INSERT INTO monitoring_stations (name, station_code, water_system_name, river_name, monitoring_item_code, admin_region_code, longitude, latitude, establishment_date, remark, created_at, updated_at)
SELECT '杨树堰水库-雨水情监测', '24083437', '长江水系', '汉江', 'H', NULL, 113.495722, 31.884381, '2024-04-01', '水库水位监测站点', NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM monitoring_stations WHERE name = '杨树堰水库-雨水情监测'
);

-- 添加水库监测数据示例数据
INSERT INTO reservoir_monitoring_data (station_id, monitoring_time, water_level, storage_capacity, flood_limit_diff, inflow, outflow, data_quality, collection_method, data_source, remark, created_at, updated_at)
VALUES
((SELECT id FROM monitoring_stations WHERE name = '杨树堰水库-雨水情监测' LIMIT 1), '2025-07-05 15:00:00', 125.68, 2568.5, -1.2, 12.5, 8.3, 1, 'AUTO', 'ULTRASONIC', NULL, NOW(), NOW()),
((SELECT id FROM monitoring_stations WHERE name = '杨树堰水库-雨水情监测' LIMIT 1), '2025-07-05 16:00:00', 125.72, 2570.2, -1.16, 13.2, 8.5, 1, 'AUTO', 'ULTRASONIC', NULL, NOW(), NOW()),
((SELECT id FROM monitoring_stations WHERE name = '杨树堰水库-雨水情监测' LIMIT 1), '2025-07-05 17:00:00', 125.80, 2573.6, -1.08, 15.6, 8.7, 1, 'AUTO', 'ULTRASONIC', NULL, NOW(), NOW()),
((SELECT id FROM monitoring_stations WHERE name = '杨树堰水库-雨水情监测' LIMIT 1), '2025-07-05 18:00:00', 125.92, 2578.3, -0.96, 18.2, 9.0, 1, 'AUTO', 'ULTRASONIC', NULL, NOW(), NOW()),
((SELECT id FROM monitoring_stations WHERE name = '杨树堰水库-雨水情监测' LIMIT 1), '2025-07-05 19:00:00', 126.05, 2583.1, -0.83, 21.5, 9.2, 1, 'AUTO', 'ULTRASONIC', NULL, NOW(), NOW());

-- 添加雨情监测站点
INSERT INTO monitoring_stations (name, station_code, water_system_name, river_name, monitoring_item_code, admin_region_code, longitude, latitude, establishment_date, remark, created_at, updated_at)
SELECT '杨树堰水库-雨情监测 (新站)', '24083438', '长江水系', '汉江', 'R', NULL, 113.497584, 31.883842, '2024-05-01', '雨情监测站点', NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM monitoring_stations WHERE name = '杨树堰水库-雨情监测 (新站)'
);

-- 添加雨情监测数据示例
INSERT INTO rainfall_monitoring_data (station_id, monitoring_time, rainfall, rainfall_intensity, cumulative_rainfall, data_quality, collection_method, data_source, remark, created_at, updated_at)
VALUES
((SELECT id FROM monitoring_stations WHERE name = '杨树堰水库-雨情监测 (新站)' LIMIT 1), '2025-07-05 15:00:00', 2.5, 2.5, 2.5, 1, 'AUTO', 'ULTRASONIC', NULL, NOW(), NOW()),
((SELECT id FROM monitoring_stations WHERE name = '杨树堰水库-雨情监测 (新站)' LIMIT 1), '2025-07-05 16:00:00', 3.8, 3.8, 6.3, 1, 'AUTO', 'ULTRASONIC', NULL, NOW(), NOW()),
((SELECT id FROM monitoring_stations WHERE name = '杨树堰水库-雨情监测 (新站)' LIMIT 1), '2025-07-05 17:00:00', 5.2, 5.2, 11.5, 1, 'AUTO', 'ULTRASONIC', NULL, NOW(), NOW()),
((SELECT id FROM monitoring_stations WHERE name = '杨树堰水库-雨情监测 (新站)' LIMIT 1), '2025-07-05 18:00:00', 6.8, 6.8, 18.3, 1, 'AUTO', 'ULTRASONIC', NULL, NOW(), NOW()),
((SELECT id FROM monitoring_stations WHERE name = '杨树堰水库-雨情监测 (新站)' LIMIT 1), '2025-07-05 19:00:00', 8.5, 8.5, 26.8, 1, 'AUTO', 'ULTRASONIC', NULL, NOW(), NOW()),
((SELECT id FROM monitoring_stations WHERE name = '杨树堰水库-雨情监测 (新站)' LIMIT 1), '2025-07-05 20:00:00', 7.2, 7.2, 34.0, 1, 'AUTO', 'ULTRASONIC', NULL, NOW(), NOW()),
((SELECT id FROM monitoring_stations WHERE name = '杨树堰水库-雨情监测 (新站)' LIMIT 1), '2025-07-05 21:00:00', 5.8, 5.8, 39.8, 1, 'AUTO', 'ULTRASONIC', NULL, NOW(), NOW()),
((SELECT id FROM monitoring_stations WHERE name = '杨树堰水库-雨情监测 (新站)' LIMIT 1), '2025-07-05 22:00:00', 4.3, 4.3, 44.1, 1, 'AUTO', 'ULTRASONIC', NULL, NOW(), NOW()),
((SELECT id FROM monitoring_stations WHERE name = '杨树堰水库-雨情监测 (新站)' LIMIT 1), '2025-07-05 23:00:00', 3.5, 3.5, 47.6, 1, 'AUTO', 'ULTRASONIC', NULL, NOW(), NOW()),
((SELECT id FROM monitoring_stations WHERE name = '杨树堰水库-雨情监测 (新站)' LIMIT 1), '2025-07-06 00:00:00', 2.8, 2.8, 50.4, 1, 'AUTO', 'ULTRASONIC', NULL, NOW(), NOW()),
((SELECT id FROM monitoring_stations WHERE name = '杨树堰水库-雨情监测 (新站)' LIMIT 1), '2025-07-06 01:00:00', 1.5, 1.5, 51.9, 1, 'AUTO', 'ULTRASONIC', NULL, NOW(), NOW()),
((SELECT id FROM monitoring_stations WHERE name = '杨树堰水库-雨情监测 (新站)' LIMIT 1), '2025-07-06 02:00:00', 0.8, 0.8, 52.7, 1, 'AUTO', 'ULTRASONIC', NULL, NOW(), NOW());
