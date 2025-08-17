-- --------------------------------------------------------------------------------
-- 字典数据初始化
-- --------------------------------------------------------------------------------
USE eb_water_resources;

-- --------------------------------------------------------------------------------
-- 字典管理初始化数据
-- --------------------------------------------------------------------------------

-- 插入字典类型数据
INSERT INTO dict_types (type_code, type_name, description, sort_order, is_active, created_at, updated_at)
SELECT 'user_status', '用户状态', '用户账户的启用禁用状态', 10, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM dict_types WHERE type_code = 'user_status');



INSERT INTO dict_types (type_code, type_name, description, sort_order, is_active, created_at, updated_at)
SELECT 'device_status', '设备状态', '设备运行状态分类', 30, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM dict_types WHERE type_code = 'device_status');

INSERT INTO dict_types (type_code, type_name, description, sort_order, is_active, created_at, updated_at)
SELECT 'warning_level', '预警级别', '系统预警等级分类', 40, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM dict_types WHERE type_code = 'warning_level');

INSERT INTO dict_types (type_code, type_name, description, sort_order, is_active, created_at, updated_at)
SELECT 'warning_type', '预警类型', '预警类型分类', 41, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM dict_types WHERE type_code = 'warning_type');

INSERT INTO dict_types (type_code, type_name, description, sort_order, is_active, created_at, updated_at)
SELECT 'department_status', '部门状态', '部门启用禁用状态', 60, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM dict_types WHERE type_code = 'department_status');

INSERT INTO dict_types (type_code, type_name, description, sort_order, is_active, created_at, updated_at)
SELECT 'data_source', '数据来源', '数据采集来源分类', 70, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM dict_types WHERE type_code = 'data_source');

-- 工程信息服务模块字典类型
INSERT INTO dict_types (type_code, type_name, description, sort_order, is_active, created_at, updated_at)
SELECT 'facility_type', '设施类型', '水利工程设施类型分类', 80, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM dict_types WHERE type_code = 'facility_type');

INSERT INTO dict_types (type_code, type_name, description, sort_order, is_active, created_at, updated_at)
SELECT 'operation_mode', '运行方式', '设备运行方式分类', 90, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM dict_types WHERE type_code = 'operation_mode');

INSERT INTO dict_types (type_code, type_name, description, sort_order, is_active, created_at, updated_at)
SELECT 'engineering_grade', '工程等级', '水利工程等级分类', 100, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM dict_types WHERE type_code = 'engineering_grade');

INSERT INTO dict_types (type_code, type_name, description, sort_order, is_active, created_at, updated_at)
SELECT 'engineering_scale', '工程规模', '水利工程规模分类', 110, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM dict_types WHERE type_code = 'engineering_scale');

INSERT INTO dict_types (type_code, type_name, description, sort_order, is_active, created_at, updated_at)
SELECT 'pipeline_type', '管道类型', '供水管道类型分类', 120, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM dict_types WHERE type_code = 'pipeline_type');

INSERT INTO dict_types (type_code, type_name, description, sort_order, is_active, created_at, updated_at)
SELECT 'monitoring_item', '监测项目', '监测站点项目类型', 130, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM dict_types WHERE type_code = 'monitoring_item');

INSERT INTO dict_types (type_code, type_name, description, sort_order, is_active, created_at, updated_at)
SELECT 'warning_status', '预警状态', '预警信息状态分类', 150, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM dict_types WHERE type_code = 'warning_status');

INSERT INTO dict_types (type_code, type_name, description, sort_order, is_active, created_at, updated_at)
SELECT 'data_quality', '数据质量', '监测数据质量状态分类', 160, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM dict_types WHERE type_code = 'data_quality');

INSERT INTO dict_types (type_code, type_name, description, sort_order, is_active, created_at, updated_at)
SELECT 'collection_method', '采集方式', '数据采集方式分类', 170, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM dict_types WHERE type_code = 'collection_method');

INSERT INTO dict_types (type_code, type_name, description, sort_order, is_active, created_at, updated_at)
SELECT 'data_source_type', '数据来源类型', '监测数据来源设备类型分类', 180, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM dict_types WHERE type_code = 'data_source_type');

INSERT INTO dict_types (type_code, type_name, description, sort_order, is_active, created_at, updated_at)
SELECT 'operation_status', '运行状态', '设备运行状态分类', 220, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM dict_types WHERE type_code = 'operation_status');

-- 新增字典类型 - 管道相关
INSERT INTO dict_types (type_code, type_name, description, sort_order, is_active, created_at, updated_at)
SELECT 'pipeline_material', '管道材质', '管道材质类型分类', 230, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM dict_types WHERE type_code = 'pipeline_material');

-- 插入字典数据 - 用户状态
INSERT INTO dict_data (type_id, data_label, data_value, description, sort_order, is_active, created_at, updated_at)
SELECT
    (SELECT id FROM dict_types WHERE type_code = 'user_status' LIMIT 1),
    '启用', '1', '用户账户启用状态', 10, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM dict_data dd
    WHERE dd.type_id = (SELECT id FROM dict_types WHERE type_code = 'user_status' LIMIT 1)
    AND dd.data_value = '1'
);

INSERT INTO dict_data (type_id, data_label, data_value, description, sort_order, is_active, created_at, updated_at)
SELECT
    (SELECT id FROM dict_types WHERE type_code = 'user_status' LIMIT 1),
    '禁用', '0', '用户账户禁用状态', 20, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM dict_data dd
    WHERE dd.type_id = (SELECT id FROM dict_types WHERE type_code = 'user_status' LIMIT 1)
    AND dd.data_value = '0'
);



-- 插入字典数据 - 设备状态
INSERT INTO dict_data (type_id, data_label, data_value, description, sort_order, is_active, created_at, updated_at)
SELECT
    (SELECT id FROM dict_types WHERE type_code = 'device_status' LIMIT 1),
    '正常', 'normal', '设备运行正常', 10, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM dict_data dd
    WHERE dd.type_id = (SELECT id FROM dict_types WHERE type_code = 'device_status' LIMIT 1)
    AND dd.data_value = 'normal'
);

INSERT INTO dict_data (type_id, data_label, data_value, description, sort_order, is_active, created_at, updated_at)
SELECT
    (SELECT id FROM dict_types WHERE type_code = 'device_status' LIMIT 1),
    '故障', 'fault', '设备出现故障', 20, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM dict_data dd
    WHERE dd.type_id = (SELECT id FROM dict_types WHERE type_code = 'device_status' LIMIT 1)
    AND dd.data_value = 'fault'
);

INSERT INTO dict_data (type_id, data_label, data_value, description, sort_order, is_active, created_at, updated_at)
SELECT
    (SELECT id FROM dict_types WHERE type_code = 'device_status' LIMIT 1),
    '维护', 'maintenance', '设备维护中', 30, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM dict_data dd
    WHERE dd.type_id = (SELECT id FROM dict_types WHERE type_code = 'device_status' LIMIT 1)
    AND dd.data_value = 'maintenance'
);

-- 插入字典数据 - 预警级别
INSERT INTO dict_data (type_id, data_label, data_value, description, sort_order, is_active, created_at, updated_at)
SELECT
    (SELECT id FROM dict_types WHERE type_code = 'warning_level' LIMIT 1),
    '一级预警', '1', '最高级别预警', 10, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM dict_data dd
    WHERE dd.type_id = (SELECT id FROM dict_types WHERE type_code = 'warning_level' LIMIT 1)
    AND dd.data_value = '1'
);

INSERT INTO dict_data (type_id, data_label, data_value, description, sort_order, is_active, created_at, updated_at)
SELECT
    (SELECT id FROM dict_types WHERE type_code = 'warning_level' LIMIT 1),
    '二级预警', '2', '高级别预警', 20, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM dict_data dd
    WHERE dd.type_id = (SELECT id FROM dict_types WHERE type_code = 'warning_level' LIMIT 1)
    AND dd.data_value = '2'
);

INSERT INTO dict_data (type_id, data_label, data_value, description, sort_order, is_active, created_at, updated_at)
SELECT
    (SELECT id FROM dict_types WHERE type_code = 'warning_level' LIMIT 1),
    '三级预警', '3', '中级别预警', 30, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM dict_data dd
    WHERE dd.type_id = (SELECT id FROM dict_types WHERE type_code = 'warning_level' LIMIT 1)
    AND dd.data_value = '3'
);

INSERT INTO dict_data (type_id, data_label, data_value, description, sort_order, is_active, created_at, updated_at)
SELECT
    (SELECT id FROM dict_types WHERE type_code = 'warning_level' LIMIT 1),
    '四级预警', '4', '低级别预警', 40, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM dict_data dd
    WHERE dd.type_id = (SELECT id FROM dict_types WHERE type_code = 'warning_level' LIMIT 1)
    AND dd.data_value = '4'
);

-- 插入字典数据 - 预警类型
INSERT INTO dict_data (type_id, data_label, data_value, description, sort_order, is_active, created_at, updated_at)
SELECT
    (SELECT id FROM dict_types WHERE type_code = 'warning_type' LIMIT 1),
    '水位预警', '水位', '水位监测预警', 10, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM dict_data dd
    WHERE dd.type_id = (SELECT id FROM dict_types WHERE type_code = 'warning_type' LIMIT 1)
    AND dd.data_value = '水位'
);

INSERT INTO dict_data (type_id, data_label, data_value, description, sort_order, is_active, created_at, updated_at)
SELECT
    (SELECT id FROM dict_types WHERE type_code = 'warning_type' LIMIT 1),
    '流量预警', '流量', '流量监测预警', 20, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM dict_data dd
    WHERE dd.type_id = (SELECT id FROM dict_types WHERE type_code = 'warning_type' LIMIT 1)
    AND dd.data_value = '流量'
);

INSERT INTO dict_data (type_id, data_label, data_value, description, sort_order, is_active, created_at, updated_at)
SELECT
    (SELECT id FROM dict_types WHERE type_code = 'warning_type' LIMIT 1),
    '水质预警', '水质', '水质监测预警', 30, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM dict_data dd
    WHERE dd.type_id = (SELECT id FROM dict_types WHERE type_code = 'warning_type' LIMIT 1)
    AND dd.data_value = '水质'
);

INSERT INTO dict_data (type_id, data_label, data_value, description, sort_order, is_active, created_at, updated_at)
SELECT
    (SELECT id FROM dict_types WHERE type_code = 'warning_type' LIMIT 1),
    '设备预警', '设备', '设备状态预警', 40, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM dict_data dd
    WHERE dd.type_id = (SELECT id FROM dict_types WHERE type_code = 'warning_type' LIMIT 1)
    AND dd.data_value = '设备'
);

INSERT INTO dict_data (type_id, data_label, data_value, description, sort_order, is_active, created_at, updated_at)
SELECT
    (SELECT id FROM dict_types WHERE type_code = 'warning_type' LIMIT 1),
    '水温预警', '水温', '水温预警类型', 40, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM dict_data dd
    WHERE dd.type_id = (SELECT id FROM dict_types WHERE type_code = 'warning_type' LIMIT 1)
    AND dd.data_value = '水温'
);

INSERT INTO dict_data (type_id, data_label, data_value, description, sort_order, is_active, created_at, updated_at)
SELECT
    (SELECT id FROM dict_types WHERE type_code = 'warning_type' LIMIT 1),
    '雨情预警', '雨情', '雨情预警类型', 60, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM dict_data dd
    WHERE dd.type_id = (SELECT id FROM dict_types WHERE type_code = 'warning_type' LIMIT 1)
    AND dd.data_value = '雨情'
);

-- 插入字典数据 - 预警状态
INSERT INTO dict_data (type_id, data_label, data_value, description, sort_order, is_active, created_at, updated_at)
SELECT
    (SELECT id FROM dict_types WHERE type_code = 'warning_status' LIMIT 1),
    '进行中', '进行中', '预警正在进行中', 10, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM dict_data dd
    WHERE dd.type_id = (SELECT id FROM dict_types WHERE type_code = 'warning_status' LIMIT 1)
    AND dd.data_value = '进行中'
);

INSERT INTO dict_data (type_id, data_label, data_value, description, sort_order, is_active, created_at, updated_at)
SELECT
    (SELECT id FROM dict_types WHERE type_code = 'warning_status' LIMIT 1),
    '已解除', '已解除', '预警已解除', 20, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM dict_data dd
    WHERE dd.type_id = (SELECT id FROM dict_types WHERE type_code = 'warning_status' LIMIT 1)
    AND dd.data_value = '已解除'
);

-- 插入字典数据 - 部门状态
INSERT INTO dict_data (type_id, data_label, data_value, description, sort_order, is_active, created_at, updated_at)
SELECT
    (SELECT id FROM dict_types WHERE type_code = 'department_status' LIMIT 1),
    '启用', '1', '部门启用状态', 10, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM dict_data dd
    WHERE dd.type_id = (SELECT id FROM dict_types WHERE type_code = 'department_status' LIMIT 1)
    AND dd.data_value = '1'
);

INSERT INTO dict_data (type_id, data_label, data_value, description, sort_order, is_active, created_at, updated_at)
SELECT
    (SELECT id FROM dict_types WHERE type_code = 'department_status' LIMIT 1),
    '禁用', '0', '部门禁用状态', 20, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM dict_data dd
    WHERE dd.type_id = (SELECT id FROM dict_types WHERE type_code = 'department_status' LIMIT 1)
    AND dd.data_value = '0'
);

-- 插入字典数据 - 数据来源
INSERT INTO dict_data (type_id, data_label, data_value, description, sort_order, is_active, created_at, updated_at)
SELECT
    (SELECT id FROM dict_types WHERE type_code = 'data_source' LIMIT 1),
    '手动录入', 'manual', '人工手动录入数据', 10, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM dict_data dd
    WHERE dd.type_id = (SELECT id FROM dict_types WHERE type_code = 'data_source' LIMIT 1)
    AND dd.data_value = 'manual'
);

INSERT INTO dict_data (type_id, data_label, data_value, description, sort_order, is_active, created_at, updated_at)
SELECT
    (SELECT id FROM dict_types WHERE type_code = 'data_source' LIMIT 1),
    '自动采集', 'auto', '系统自动采集数据', 20, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM dict_data dd
    WHERE dd.type_id = (SELECT id FROM dict_types WHERE type_code = 'data_source' LIMIT 1)
    AND dd.data_value = 'auto'
);

INSERT INTO dict_data (type_id, data_label, data_value, description, sort_order, is_active, created_at, updated_at)
SELECT
    (SELECT id FROM dict_types WHERE type_code = 'data_source' LIMIT 1),
    '第三方接口', 'api', '通过第三方接口获取数据', 30, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM dict_data dd
    WHERE dd.type_id = (SELECT id FROM dict_types WHERE type_code = 'data_source' LIMIT 1)
    AND dd.data_value = 'api'
);

-- --------------------------------------------------------------------------------
-- 工程信息服务模块字典数据
-- --------------------------------------------------------------------------------

-- 设施类型数据
INSERT INTO dict_data (type_id, data_label, data_value, description, sort_order, is_active, created_at, updated_at)
SELECT
    (SELECT id FROM dict_types WHERE type_code = 'facility_type' LIMIT 1),
    '加压泵站', 'pumping_station', '用于加压供水的泵站', 10, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM dict_data dd
    WHERE dd.type_id = (SELECT id FROM dict_types WHERE type_code = 'facility_type' LIMIT 1)
    AND dd.data_value = 'pumping_station'
);

INSERT INTO dict_data (type_id, data_label, data_value, description, sort_order, is_active, created_at, updated_at)
SELECT
    (SELECT id FROM dict_types WHERE type_code = 'facility_type' LIMIT 1),
    '水厂', 'water_plant', '水处理厂', 20, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM dict_data dd
    WHERE dd.type_id = (SELECT id FROM dict_types WHERE type_code = 'facility_type' LIMIT 1)
    AND dd.data_value = 'water_plant'
);

INSERT INTO dict_data (type_id, data_label, data_value, description, sort_order, is_active, created_at, updated_at)
SELECT
    (SELECT id FROM dict_types WHERE type_code = 'facility_type' LIMIT 1),
    '水库', 'reservoir', '蓄水水库', 30, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM dict_data dd
    WHERE dd.type_id = (SELECT id FROM dict_types WHERE type_code = 'facility_type' LIMIT 1)
    AND dd.data_value = 'reservoir'
);

INSERT INTO dict_data (type_id, data_label, data_value, description, sort_order, is_active, created_at, updated_at)
SELECT
    (SELECT id FROM dict_types WHERE type_code = 'facility_type' LIMIT 1),
    '取水泵站', 'intake_station', '取水用泵站', 40, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM dict_data dd
    WHERE dd.type_id = (SELECT id FROM dict_types WHERE type_code = 'facility_type' LIMIT 1)
    AND dd.data_value = 'intake_station'
);

INSERT INTO dict_data (type_id, data_label, data_value, description, sort_order, is_active, created_at, updated_at)
SELECT
    (SELECT id FROM dict_types WHERE type_code = 'facility_type' LIMIT 1),
    '监测站', 'monitoring_station', '监测站设施', 50, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM dict_data dd
    WHERE dd.type_id = (SELECT id FROM dict_types WHERE type_code = 'facility_type' LIMIT 1)
    AND dd.data_value = 'monitoring_station'
);

-- 运行方式数据
INSERT INTO dict_data (type_id, data_label, data_value, description, sort_order, is_active, created_at, updated_at)
SELECT
    (SELECT id FROM dict_types WHERE type_code = 'operation_mode' LIMIT 1),
    '1用1备', '1_main_1_backup', '一台主用一台备用', 10, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM dict_data dd
    WHERE dd.type_id = (SELECT id FROM dict_types WHERE type_code = 'operation_mode' LIMIT 1)
    AND dd.data_value = '1_main_1_backup'
);

INSERT INTO dict_data (type_id, data_label, data_value, description, sort_order, is_active, created_at, updated_at)
SELECT
    (SELECT id FROM dict_types WHERE type_code = 'operation_mode' LIMIT 1),
    '2用1备', '2_main_1_backup', '两台主用一台备用', 20, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM dict_data dd
    WHERE dd.type_id = (SELECT id FROM dict_types WHERE type_code = 'operation_mode' LIMIT 1)
    AND dd.data_value = '2_main_1_backup'
);

INSERT INTO dict_data (type_id, data_label, data_value, description, sort_order, is_active, created_at, updated_at)
SELECT
    (SELECT id FROM dict_types WHERE type_code = 'operation_mode' LIMIT 1),
    '单机运行', 'single_operation', '单台设备运行', 30, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM dict_data dd
    WHERE dd.type_id = (SELECT id FROM dict_types WHERE type_code = 'operation_mode' LIMIT 1)
    AND dd.data_value = 'single_operation'
);

INSERT INTO dict_data (type_id, data_label, data_value, description, sort_order, is_active, created_at, updated_at)
SELECT
    (SELECT id FROM dict_types WHERE type_code = 'operation_mode' LIMIT 1),
    '并联运行', 'parallel_operation', '多台设备并联运行', 40, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM dict_data dd
    WHERE dd.type_id = (SELECT id FROM dict_types WHERE type_code = 'operation_mode' LIMIT 1)
    AND dd.data_value = 'parallel_operation'
);

-- 工程等级数据
INSERT INTO dict_data (type_id, data_label, data_value, description, sort_order, is_active, created_at, updated_at)
SELECT
    (SELECT id FROM dict_types WHERE type_code = 'engineering_grade' LIMIT 1),
    'Ⅰ等', 'grade_1', '一等工程', 10, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM dict_data dd
    WHERE dd.type_id = (SELECT id FROM dict_types WHERE type_code = 'engineering_grade' LIMIT 1)
    AND dd.data_value = 'grade_1'
);

INSERT INTO dict_data (type_id, data_label, data_value, description, sort_order, is_active, created_at, updated_at)
SELECT
    (SELECT id FROM dict_types WHERE type_code = 'engineering_grade' LIMIT 1),
    'Ⅱ等', 'grade_2', '二等工程', 20, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM dict_data dd
    WHERE dd.type_id = (SELECT id FROM dict_types WHERE type_code = 'engineering_grade' LIMIT 1)
    AND dd.data_value = 'grade_2'
);

INSERT INTO dict_data (type_id, data_label, data_value, description, sort_order, is_active, created_at, updated_at)
SELECT
    (SELECT id FROM dict_types WHERE type_code = 'engineering_grade' LIMIT 1),
    'Ⅲ等', 'grade_3', '三等工程', 30, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM dict_data dd
    WHERE dd.type_id = (SELECT id FROM dict_types WHERE type_code = 'engineering_grade' LIMIT 1)
    AND dd.data_value = 'grade_3'
);

INSERT INTO dict_data (type_id, data_label, data_value, description, sort_order, is_active, created_at, updated_at)
SELECT
    (SELECT id FROM dict_types WHERE type_code = 'engineering_grade' LIMIT 1),
    'Ⅳ等', 'grade_4', '四等工程', 40, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM dict_data dd
    WHERE dd.type_id = (SELECT id FROM dict_types WHERE type_code = 'engineering_grade' LIMIT 1)
    AND dd.data_value = 'grade_4'
);

INSERT INTO dict_data (type_id, data_label, data_value, description, sort_order, is_active, created_at, updated_at)
SELECT
    (SELECT id FROM dict_types WHERE type_code = 'engineering_grade' LIMIT 1),
    'Ⅴ等', 'grade_5', '五等工程', 50, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM dict_data dd
    WHERE dd.type_id = (SELECT id FROM dict_types WHERE type_code = 'engineering_grade' LIMIT 1)
    AND dd.data_value = 'grade_5'
);

INSERT INTO dict_data (type_id, data_label, data_value, description, sort_order, is_active, created_at, updated_at)
SELECT
    (SELECT id FROM dict_types WHERE type_code = 'engineering_grade' LIMIT 1),
    'V', 'V', 'V等工程', 60, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM dict_data dd
    WHERE dd.type_id = (SELECT id FROM dict_types WHERE type_code = 'engineering_grade' LIMIT 1)
    AND dd.data_value = 'V'
);

-- 工程规模数据
INSERT INTO dict_data (type_id, data_label, data_value, description, sort_order, is_active, created_at, updated_at)
SELECT
    (SELECT id FROM dict_types WHERE type_code = 'engineering_scale' LIMIT 1),
    '大型', 'large', '大型水利工程', 10, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM dict_data dd
    WHERE dd.type_id = (SELECT id FROM dict_types WHERE type_code = 'engineering_scale' LIMIT 1)
    AND dd.data_value = 'large'
);

INSERT INTO dict_data (type_id, data_label, data_value, description, sort_order, is_active, created_at, updated_at)
SELECT
    (SELECT id FROM dict_types WHERE type_code = 'engineering_scale' LIMIT 1),
    '中型', 'medium', '中型水利工程', 20, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM dict_data dd
    WHERE dd.type_id = (SELECT id FROM dict_types WHERE type_code = 'engineering_scale' LIMIT 1)
    AND dd.data_value = 'medium'
);

INSERT INTO dict_data (type_id, data_label, data_value, description, sort_order, is_active, created_at, updated_at)
SELECT
    (SELECT id FROM dict_types WHERE type_code = 'engineering_scale' LIMIT 1),
    '小型', 'small', '小型水利工程', 30, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM dict_data dd
    WHERE dd.type_id = (SELECT id FROM dict_types WHERE type_code = 'engineering_scale' LIMIT 1)
    AND dd.data_value = 'small'
);

INSERT INTO dict_data (type_id, data_label, data_value, description, sort_order, is_active, created_at, updated_at)
SELECT
    (SELECT id FROM dict_types WHERE type_code = 'engineering_scale' LIMIT 1),
    '小(1)型', 'small_1', '小(1)型水利工程', 40, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM dict_data dd
    WHERE dd.type_id = (SELECT id FROM dict_types WHERE type_code = 'engineering_scale' LIMIT 1)
    AND dd.data_value = 'small_1'
);

-- 管道类型数据
INSERT INTO dict_data (type_id, data_label, data_value, description, sort_order, is_active, created_at, updated_at)
SELECT
    (SELECT id FROM dict_types WHERE type_code = 'pipeline_type' LIMIT 1),
    '供水干管', 'main_pipeline', '主要供水管道', 10, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM dict_data dd
    WHERE dd.type_id = (SELECT id FROM dict_types WHERE type_code = 'pipeline_type' LIMIT 1)
    AND dd.data_value = 'main_pipeline'
);

INSERT INTO dict_data (type_id, data_label, data_value, description, sort_order, is_active, created_at, updated_at)
SELECT
    (SELECT id FROM dict_types WHERE type_code = 'pipeline_type' LIMIT 1),
    '供水支管', 'branch_pipeline', '分支供水管道', 20, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM dict_data dd
    WHERE dd.type_id = (SELECT id FROM dict_types WHERE type_code = 'pipeline_type' LIMIT 1)
    AND dd.data_value = 'branch_pipeline'
);

INSERT INTO dict_data (type_id, data_label, data_value, description, sort_order, is_active, created_at, updated_at)
SELECT
    (SELECT id FROM dict_types WHERE type_code = 'pipeline_type' LIMIT 1),
    '输水管道', 'transmission_pipeline', '长距离输水管道', 30, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM dict_data dd
    WHERE dd.type_id = (SELECT id FROM dict_types WHERE type_code = 'pipeline_type' LIMIT 1)
    AND dd.data_value = 'transmission_pipeline'
);

INSERT INTO dict_data (type_id, data_label, data_value, description, sort_order, is_active, created_at, updated_at)
SELECT
    (SELECT id FROM dict_types WHERE type_code = 'pipeline_type' LIMIT 1),
    '配水管道', 'distribution_pipeline', '配水管网', 40, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM dict_data dd
    WHERE dd.type_id = (SELECT id FROM dict_types WHERE type_code = 'pipeline_type' LIMIT 1)
    AND dd.data_value = 'distribution_pipeline'
);

-- 监测项目数据
INSERT INTO dict_data (type_id, data_label, data_value, description, sort_order, is_active, created_at, updated_at)
SELECT
    (SELECT id FROM dict_types WHERE type_code = 'monitoring_item' LIMIT 1),
    '流量监测', 'Q', '流量监测站点', 10, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM dict_data dd
    WHERE dd.type_id = (SELECT id FROM dict_types WHERE type_code = 'monitoring_item' LIMIT 1)
    AND dd.data_value = 'Q'
);

INSERT INTO dict_data (type_id, data_label, data_value, description, sort_order, is_active, created_at, updated_at)
SELECT
    (SELECT id FROM dict_types WHERE type_code = 'monitoring_item' LIMIT 1),
    '视频监控', 'V', '视频监控站点', 20, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM dict_data dd
    WHERE dd.type_id = (SELECT id FROM dict_types WHERE type_code = 'monitoring_item' LIMIT 1)
    AND dd.data_value = 'V'
);

INSERT INTO dict_data (type_id, data_label, data_value, description, sort_order, is_active, created_at, updated_at)
SELECT
    (SELECT id FROM dict_types WHERE type_code = 'monitoring_item' LIMIT 1),
    '水位监测', 'H', '水位监测站点', 30, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM dict_data dd
    WHERE dd.type_id = (SELECT id FROM dict_types WHERE type_code = 'monitoring_item' LIMIT 1)
    AND dd.data_value = 'H'
);

INSERT INTO dict_data (type_id, data_label, data_value, description, sort_order, is_active, created_at, updated_at)
SELECT
    (SELECT id FROM dict_types WHERE type_code = 'monitoring_item' LIMIT 1),
    '水质监测', 'WQ', '水质监测站点', 40, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM dict_data dd
    WHERE dd.type_id = (SELECT id FROM dict_types WHERE type_code = 'monitoring_item' LIMIT 1)
    AND dd.data_value = 'WQ'
);

-- 新增水质细分监测项目数据
INSERT INTO dict_data (type_id, data_label, data_value, description, sort_order, is_active, created_at, updated_at)
SELECT
    (SELECT id FROM dict_types WHERE type_code = 'monitoring_item' LIMIT 1),
    '水温监测', 'WT', '水温监测项目', 50, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM dict_data dd
    WHERE dd.type_id = (SELECT id FROM dict_types WHERE type_code = 'monitoring_item' LIMIT 1)
    AND dd.data_value = 'WT'
);

INSERT INTO dict_data (type_id, data_label, data_value, description, sort_order, is_active, created_at, updated_at)
SELECT
    (SELECT id FROM dict_types WHERE type_code = 'monitoring_item' LIMIT 1),
    '浊度监测', 'TU', '浊度监测项目', 60, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM dict_data dd
    WHERE dd.type_id = (SELECT id FROM dict_types WHERE type_code = 'monitoring_item' LIMIT 1)
    AND dd.data_value = 'TU'
);

INSERT INTO dict_data (type_id, data_label, data_value, description, sort_order, is_active, created_at, updated_at)
SELECT
    (SELECT id FROM dict_types WHERE type_code = 'monitoring_item' LIMIT 1),
    'PH监测', 'PH', 'PH值监测项目', 70, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM dict_data dd
    WHERE dd.type_id = (SELECT id FROM dict_types WHERE type_code = 'monitoring_item' LIMIT 1)
    AND dd.data_value = 'PH'
);

INSERT INTO dict_data (type_id, data_label, data_value, description, sort_order, is_active, created_at, updated_at)
SELECT
    (SELECT id FROM dict_types WHERE type_code = 'monitoring_item' LIMIT 1),
    '电导率监测', 'EC', '电导率监测项目', 80, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM dict_data dd
    WHERE dd.type_id = (SELECT id FROM dict_types WHERE type_code = 'monitoring_item' LIMIT 1)
    AND dd.data_value = 'EC'
);

INSERT INTO dict_data (type_id, data_label, data_value, description, sort_order, is_active, created_at, updated_at)
SELECT
    (SELECT id FROM dict_types WHERE type_code = 'monitoring_item' LIMIT 1),
    '溶解氧监测', 'DO', '溶解氧监测项目', 90, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM dict_data dd
    WHERE dd.type_id = (SELECT id FROM dict_types WHERE type_code = 'monitoring_item' LIMIT 1)
    AND dd.data_value = 'DO'
);

INSERT INTO dict_data (type_id, data_label, data_value, description, sort_order, is_active, created_at, updated_at)
SELECT
    (SELECT id FROM dict_types WHERE type_code = 'monitoring_item' LIMIT 1),
    '氨氮监测', 'AN', '氨氮监测项目', 100, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM dict_data dd
    WHERE dd.type_id = (SELECT id FROM dict_types WHERE type_code = 'monitoring_item' LIMIT 1)
    AND dd.data_value = 'AN'
);

INSERT INTO dict_data (type_id, data_label, data_value, description, sort_order, is_active, created_at, updated_at)
SELECT
    (SELECT id FROM dict_types WHERE type_code = 'monitoring_item' LIMIT 1),
    '化学需氧量监测', 'COD', '化学需氧量监测项目', 110, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM dict_data dd
    WHERE dd.type_id = (SELECT id FROM dict_types WHERE type_code = 'monitoring_item' LIMIT 1)
    AND dd.data_value = 'COD'
);

INSERT INTO dict_data (type_id, data_label, data_value, description, sort_order, is_active, created_at, updated_at)
SELECT
    (SELECT id FROM dict_types WHERE type_code = 'monitoring_item' LIMIT 1),
    '余氯监测', 'RC', '余氯监测项目', 120, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM dict_data dd
    WHERE dd.type_id = (SELECT id FROM dict_types WHERE type_code = 'monitoring_item' LIMIT 1)
    AND dd.data_value = 'RC'
);

INSERT INTO dict_data (type_id, data_label, data_value, description, sort_order, is_active, created_at, updated_at)
SELECT
    (SELECT id FROM dict_types WHERE type_code = 'monitoring_item' LIMIT 1),
    '雨情监测', 'R', '雨情监测站点', 130, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM dict_data dd
    WHERE dd.type_id = (SELECT id FROM dict_types WHERE type_code = 'monitoring_item' LIMIT 1)
    AND dd.data_value = 'R'
);

-- 运行状态字典数据
INSERT INTO dict_data (type_id, data_label, data_value, description, sort_order, is_active, created_at, updated_at)
SELECT
    (SELECT id FROM dict_types WHERE type_code = 'operation_status' LIMIT 1),
    '正常运行', 'normal', '设备正常运行', 10, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM dict_data dd
    WHERE dd.type_id = (SELECT id FROM dict_types WHERE type_code = 'operation_status' LIMIT 1)
    AND dd.data_value = 'normal'
);

INSERT INTO dict_data (type_id, data_label, data_value, description, sort_order, is_active, created_at, updated_at)
SELECT
    (SELECT id FROM dict_types WHERE type_code = 'operation_status' LIMIT 1),
    '停运', 'stopped', '设备停运状态', 20, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM dict_data dd
    WHERE dd.type_id = (SELECT id FROM dict_types WHERE type_code = 'operation_status' LIMIT 1)
    AND dd.data_value = 'stopped'
);

INSERT INTO dict_data (type_id, data_label, data_value, description, sort_order, is_active, created_at, updated_at)
SELECT
    (SELECT id FROM dict_types WHERE type_code = 'operation_status' LIMIT 1),
    '检修', 'maintenance', '设备检修状态', 30, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM dict_data dd
    WHERE dd.type_id = (SELECT id FROM dict_types WHERE type_code = 'operation_status' LIMIT 1)
    AND dd.data_value = 'maintenance'
);

-- 管道材质字典数据
INSERT INTO dict_data (type_id, data_label, data_value, description, sort_order, is_active, created_at, updated_at)
SELECT
    (SELECT id FROM dict_types WHERE type_code = 'pipeline_material' LIMIT 1),
    '预应力钢筒混凝土管', 'concrete', '预应力钢筒混凝土管', 10, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM dict_data dd
    WHERE dd.type_id = (SELECT id FROM dict_types WHERE type_code = 'pipeline_material' LIMIT 1)
    AND dd.data_value = 'concrete'
);

INSERT INTO dict_data (type_id, data_label, data_value, description, sort_order, is_active, created_at, updated_at)
SELECT
    (SELECT id FROM dict_types WHERE type_code = 'pipeline_material' LIMIT 1),
    '球墨铸铁管', 'cast_iron', '球墨铸铁管', 20, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM dict_data dd
    WHERE dd.type_id = (SELECT id FROM dict_types WHERE type_code = 'pipeline_material' LIMIT 1)
    AND dd.data_value = 'cast_iron'
);

INSERT INTO dict_data (type_id, data_label, data_value, description, sort_order, is_active, created_at, updated_at)
SELECT
    (SELECT id FROM dict_types WHERE type_code = 'pipeline_material' LIMIT 1),
    'PE管', 'pe', '聚乙烯管', 30, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM dict_data dd
    WHERE dd.type_id = (SELECT id FROM dict_types WHERE type_code = 'pipeline_material' LIMIT 1)
    AND dd.data_value = 'pe'
);

INSERT INTO dict_data (type_id, data_label, data_value, description, sort_order, is_active, created_at, updated_at)
SELECT
    (SELECT id FROM dict_types WHERE type_code = 'pipeline_material' LIMIT 1),
    '钢管', 'steel', '钢质管道', 40, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM dict_data dd
    WHERE dd.type_id = (SELECT id FROM dict_types WHERE type_code = 'pipeline_material' LIMIT 1)
    AND dd.data_value = 'steel'
);

-- 数据质量字典数据
INSERT INTO dict_data (type_id, data_label, data_value, description, sort_order, is_active, created_at, updated_at)
SELECT
    (SELECT id FROM dict_types WHERE type_code = 'data_quality' LIMIT 1),
    '正常', '1', '数据质量正常', 10, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM dict_data dd
    WHERE dd.type_id = (SELECT id FROM dict_types WHERE type_code = 'data_quality' LIMIT 1)
      AND dd.data_value = '1'
);

INSERT INTO dict_data (type_id, data_label, data_value, description, sort_order, is_active, created_at, updated_at)
SELECT
    (SELECT id FROM dict_types WHERE type_code = 'data_quality' LIMIT 1),
    '异常', '2', '数据质量异常', 20, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM dict_data dd
    WHERE dd.type_id = (SELECT id FROM dict_types WHERE type_code = 'data_quality' LIMIT 1)
      AND dd.data_value = '2'
);

INSERT INTO dict_data (type_id, data_label, data_value, description, sort_order, is_active, created_at, updated_at)
SELECT
    (SELECT id FROM dict_types WHERE type_code = 'data_quality' LIMIT 1),
    '缺失', '3', '数据缺失', 30, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM dict_data dd
    WHERE dd.type_id = (SELECT id FROM dict_types WHERE type_code = 'data_quality' LIMIT 1)
      AND dd.data_value = '3'
);

-- 采集方式字典数据
INSERT INTO dict_data (type_id, data_label, data_value, description, sort_order, is_active, created_at, updated_at)
SELECT
    (SELECT id FROM dict_types WHERE type_code = 'collection_method' LIMIT 1),
    '自动采集', 'AUTO', '设备自动采集数据', 10, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM dict_data dd
    WHERE dd.type_id = (SELECT id FROM dict_types WHERE type_code = 'collection_method' LIMIT 1)
      AND dd.data_value = 'AUTO'
);

INSERT INTO dict_data (type_id, data_label, data_value, description, sort_order, is_active, created_at, updated_at)
SELECT
    (SELECT id FROM dict_types WHERE type_code = 'collection_method' LIMIT 1),
    '手动录入', 'MANUAL', '人工手动录入数据', 20, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM dict_data dd
    WHERE dd.type_id = (SELECT id FROM dict_types WHERE type_code = 'collection_method' LIMIT 1)
      AND dd.data_value = 'MANUAL'
);

-- 数据来源设备类型字典数据
INSERT INTO dict_data (type_id, data_label, data_value, description, sort_order, is_active, created_at, updated_at)
SELECT
    (SELECT id FROM dict_types WHERE type_code = 'data_source_type' LIMIT 1),
    '超声波流量计', 'ULTRASONIC', '超声波流量计设备', 10, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM dict_data dd
    WHERE dd.type_id = (SELECT id FROM dict_types WHERE type_code = 'data_source_type' LIMIT 1)
      AND dd.data_value = 'ULTRASONIC'
);

INSERT INTO dict_data (type_id, data_label, data_value, description, sort_order, is_active, created_at, updated_at)
SELECT
    (SELECT id FROM dict_types WHERE type_code = 'data_source_type' LIMIT 1),
    '电磁流量计', 'ELECTROMAGNETIC', '电磁流量计设备', 20, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM dict_data dd
    WHERE dd.type_id = (SELECT id FROM dict_types WHERE type_code = 'data_source_type' LIMIT 1)
      AND dd.data_value = 'ELECTROMAGNETIC'
);

INSERT INTO dict_data (type_id, data_label, data_value, description, sort_order, is_active, created_at, updated_at)
SELECT
    (SELECT id FROM dict_types WHERE type_code = 'data_source_type' LIMIT 1),
    '涡轮流量计', 'TURBINE', '涡轮流量计设备', 30, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM dict_data dd
    WHERE dd.type_id = (SELECT id FROM dict_types WHERE type_code = 'data_source_type' LIMIT 1)
      AND dd.data_value = 'TURBINE'
);

INSERT INTO dict_data (type_id, data_label, data_value, description, sort_order, is_active, created_at, updated_at)
SELECT
    (SELECT id FROM dict_types WHERE type_code = 'data_source_type' LIMIT 1),
    '孔板流量计', 'ORIFICE', '孔板流量计设备', 40, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM dict_data dd
    WHERE dd.type_id = (SELECT id FROM dict_types WHERE type_code = 'data_source_type' LIMIT 1)
      AND dd.data_value = 'ORIFICE'
);

INSERT INTO dict_data (type_id, data_label, data_value, description, sort_order, is_active, created_at, updated_at)
SELECT
    (SELECT id FROM dict_types WHERE type_code = 'data_source_type' LIMIT 1),
    '手动测量', 'MANUAL_MEASURE', '人工手动测量设备', 50, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM dict_data dd
    WHERE dd.type_id = (SELECT id FROM dict_types WHERE type_code = 'data_source_type' LIMIT 1)
      AND dd.data_value = 'MANUAL_MEASURE'
);

-- --------------------------------------------------------------------------------
-- 工程巡检模块 - 字典类型
-- --------------------------------------------------------------------------------
INSERT INTO dict_types (type_code, type_name, description, sort_order, is_active, created_at, updated_at)
SELECT 'inspection_status', '巡检状态', '巡检任务的状态', 240, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM dict_types WHERE type_code = 'inspection_status');

INSERT INTO dict_types (type_code, type_name, description, sort_order, is_active, created_at, updated_at)
SELECT 'inspection_frequency', '巡检频次', '巡检任务的执行频率', 250, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM dict_types WHERE type_code = 'inspection_frequency');

-- 巡检状态字典数据
INSERT INTO dict_data (type_id, data_label, data_value, description, sort_order, is_active, created_at, updated_at)
SELECT (SELECT id FROM dict_types WHERE type_code = 'inspection_status' LIMIT 1), '未开始', 'PENDING', '任务未开始', 10, 1, NOW(), NOW()
WHERE NOT EXISTS (
  SELECT 1 FROM dict_data dd
  WHERE dd.type_id = (SELECT id FROM dict_types WHERE type_code = 'inspection_status' LIMIT 1)
    AND dd.data_value = 'PENDING'
);

INSERT INTO dict_data (type_id, data_label, data_value, description, sort_order, is_active, created_at, updated_at)
SELECT (SELECT id FROM dict_types WHERE type_code = 'inspection_status' LIMIT 1), '进行中', 'IN_PROGRESS', '任务进行中', 20, 1, NOW(), NOW()
WHERE NOT EXISTS (
  SELECT 1 FROM dict_data dd
  WHERE dd.type_id = (SELECT id FROM dict_types WHERE type_code = 'inspection_status' LIMIT 1)
    AND dd.data_value = 'IN_PROGRESS'
);

INSERT INTO dict_data (type_id, data_label, data_value, description, sort_order, is_active, created_at, updated_at)
SELECT (SELECT id FROM dict_types WHERE type_code = 'inspection_status' LIMIT 1), '已完成', 'COMPLETED', '任务已完成', 30, 1, NOW(), NOW()
WHERE NOT EXISTS (
  SELECT 1 FROM dict_data dd
  WHERE dd.type_id = (SELECT id FROM dict_types WHERE type_code = 'inspection_status' LIMIT 1)
    AND dd.data_value = 'COMPLETED'
);

INSERT INTO dict_data (type_id, data_label, data_value, description, sort_order, is_active, created_at, updated_at)
SELECT (SELECT id FROM dict_types WHERE type_code = 'inspection_status' LIMIT 1), '异常', 'EXCEPTION', '任务异常', 40, 1, NOW(), NOW()
WHERE NOT EXISTS (
  SELECT 1 FROM dict_data dd
  WHERE dd.type_id = (SELECT id FROM dict_types WHERE type_code = 'inspection_status' LIMIT 1)
    AND dd.data_value = 'EXCEPTION'
);

-- 巡检频次字典数据
INSERT INTO dict_data (type_id, data_label, data_value, description, sort_order, is_active, created_at, updated_at)
SELECT (SELECT id FROM dict_types WHERE type_code = 'inspection_frequency' LIMIT 1), '日', 'daily', '按日执行', 10, 1, NOW(), NOW()
WHERE NOT EXISTS (
  SELECT 1 FROM dict_data dd
  WHERE dd.type_id = (SELECT id FROM dict_types WHERE type_code = 'inspection_frequency' LIMIT 1)
    AND dd.data_value = 'daily'
);

INSERT INTO dict_data (type_id, data_label, data_value, description, sort_order, is_active, created_at, updated_at)
SELECT (SELECT id FROM dict_types WHERE type_code = 'inspection_frequency' LIMIT 1), '周', 'weekly', '按周执行', 20, 1, NOW(), NOW()
WHERE NOT EXISTS (
  SELECT 1 FROM dict_data dd
  WHERE dd.type_id = (SELECT id FROM dict_types WHERE type_code = 'inspection_frequency' LIMIT 1)
    AND dd.data_value = 'weekly'
);

INSERT INTO dict_data (type_id, data_label, data_value, description, sort_order, is_active, created_at, updated_at)
SELECT (SELECT id FROM dict_types WHERE type_code = 'inspection_frequency' LIMIT 1), '月', 'monthly', '按月执行', 30, 1, NOW(), NOW()
WHERE NOT EXISTS (
  SELECT 1 FROM dict_data dd
  WHERE dd.type_id = (SELECT id FROM dict_types WHERE type_code = 'inspection_frequency' LIMIT 1)
    AND dd.data_value = 'monthly'
);

INSERT INTO dict_data (type_id, data_label, data_value, description, sort_order, is_active, created_at, updated_at)
SELECT (SELECT id FROM dict_types WHERE type_code = 'inspection_frequency' LIMIT 1), '季', 'quarterly', '按季度执行', 40, 1, NOW(), NOW()
WHERE NOT EXISTS (
  SELECT 1 FROM dict_data dd
  WHERE dd.type_id = (SELECT id FROM dict_types WHERE type_code = 'inspection_frequency' LIMIT 1)
    AND dd.data_value = 'quarterly'
);

INSERT INTO dict_data (type_id, data_label, data_value, description, sort_order, is_active, created_at, updated_at)
SELECT (SELECT id FROM dict_types WHERE type_code = 'inspection_frequency' LIMIT 1), '应急', 'emergency', '应急巡检', 50, 1, NOW(), NOW()
WHERE NOT EXISTS (
  SELECT 1 FROM dict_data dd
  WHERE dd.type_id = (SELECT id FROM dict_types WHERE type_code = 'inspection_frequency' LIMIT 1)
    AND dd.data_value = 'emergency'
);
