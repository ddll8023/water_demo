# API 接口文档

本文档详细说明了项目提供的所有 API 接口。

## 1. 认证模块 (`/api/auth`)

**功能描述**

认证模块负责处理用户身份认证、授权管理及令牌（Token）的生命周期。

### 1.1 用户登录

**功能描述**

用户通过提供用户名和密码进行身份验证。验证成功后，系统将返回一个访问令牌（Access Token）和一个刷新令牌（Refresh Token），用于后续的 API 请求。

- **URL**: `/api/auth/login`
- **HTTP 方法**: `POST`
- **Content-Type**: `application/json`

#### 请求体 (Request Body)

| 字段       | 类型   | 是否必需 | 描述                               |
| :--------- | :----- | :------- | :--------------------------------- |
| `username` | String | 是       | 用户名 (不能为空)                  |
| `password` | String | 是       | 密码 (长度必须在 6-100 个字符之间) |

#### 请求示例

```json
{
	"username": "admin",
	"password": "password123"
}
```

#### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "登录成功",
	"data": {
		"accessToken": "eyJhbGciOiJIUzUxMiJ9...",
		"refreshToken": "eyJhbGciOiJIUzUxMiJ9...",
		"tokenType": "Bearer",
		"expiresIn": 3600,
		"userInfo": {
			"id": 1,
			"username": "admin",
			"roleName": "管理员"
		},
		"permissions": ["user:create", "user:read", "user:update", "user:delete"],
		"roleName": "管理员"
	}
}
```

**失败响应 (400 Bad Request / 401 Unauthorized)**

```json
{
	"code": 401,
	"message": "用户名或密码错误",
	"data": null
}
```

---

### 1.2 用户登出

**功能描述**

用户登出系统。服务端会接收并处理该请求，可能会将当前令牌加入黑名单以使其失效。

- **URL**: `/api/auth/logout`
- **HTTP 方法**: `POST`

#### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

#### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "登出成功",
	"data": null
}
```

**失败响应 (401 Unauthorized)**

```json
{
	"code": 401,
	"message": "Token无效或已过期",
	"data": null
}
```

---

### 1.3 获取当前用户信息

**功能描述**

获取当前已登录用户的详细信息，包括用户 ID、用户名、角色和权限列表。

- **URL**: `/api/auth/me`
- **HTTP 方法**: `GET`

#### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

#### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "获取用户信息成功",
	"data": {
		"id": 1,
		"username": "admin",
		"roleName": "管理员",
		"permissions": ["user:read", "dashboard:view"]
	}
}
```

**失败响应 (401 Unauthorized)**

```json
{
	"code": 401,
	"message": "未提供认证Token",
	"data": null
}
```

---

### 1.4 刷新令牌

**功能描述**

当访问令牌（Access Token）过期后，客户端可以使用刷新令牌（Refresh Token）来获取一个新的访问令牌，无需用户重新登录。

- **URL**: `/api/auth/refresh`
- **HTTP 方法**: `POST`
- **Content-Type**: `application/json`

#### 请求体 (Request Body)

| 字段           | 类型   | 是否必需 | 描述                |
| :------------- | :----- | :------- | :------------------ |
| `refreshToken` | String | 是       | 刷新令牌 (不能为空) |

#### 请求示例

```json
{
	"refreshToken": "eyJhbGciOiJIUzUxMiJ9..."
}
```

#### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "Token刷新成功",
	"data": {
		"accessToken": "eyJhbGciOiJIUzUxMiJ9... (新的)",
		"refreshToken": "eyJhbGciOiJIUzUxMiJ9... (可能也是新的)",
		"tokenType": "Bearer",
		"expiresIn": 3600
	}
}
```

**失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "无效的刷新令牌",
	"data": null
}
```

---

### 1.5 验证令牌有效性

**功能描述**

客户端可以使用此接口来验证当前持有的访问令牌是否依然有效。

- **URL**: `/api/auth/validate`
- **HTTP 方法**: `GET`

#### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

#### 响应 (Response)

**成功响应 (200 OK - Token 有效)**

```json
{
	"code": 200,
	"message": "Token有效",
	"data": {
		"isValid": true
	}
}
```

**失败响应 (401 Unauthorized - Token 无效或过期)**

```json
{
	"code": 401,
	"message": "Token无效或已过期",
	"data": {
		"isValid": false
	}
}
```

---

## 2. 系统管理模块

### 2.1 部门管理 (`/api/departments`)

**功能描述**

提供部门相关的增删改查操作。

#### 2.1.1 分页查询部门列表

**功能描述**

分页查询部门列表，支持按名称关键词模糊搜索和按状态筛选。

- **URL**: `/api/departments`
- **HTTP 方法**: `GET`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 请求参数 (Query Parameters)

| 字段       | 类型    | 是否必需 | 描述                                 |
| :--------- | :------ | :------- | :----------------------------------- |
| `page`     | Integer | 否       | 当前页码 (默认: 1)                   |
| `size`     | Integer | 否       | 每页记录数 (默认: 10)                |
| `keyword`  | String  | 否       | 搜索关键词 (部门名称模糊匹配)        |
| `isActive` | Boolean | 否       | 部门状态筛选 (true:启用, false:禁用) |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "查询成功",
	"data": {
		"total": 20,
		"page": 1,
		"size": 10,
		"data": [
			{
				"id": 1,
				"name": "研发部",
				"parentId": null,
				"isActive": true
			}
		]
	}
}
```

**失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "查询失败，参数无效",
	"data": null
}
```

---

#### 2.1.2 检查部门名称是否可用

**功能描述**

检查部门名称在同级部门中是否唯一。

- **URL**: `/api/departments/check-name`
- **HTTP 方法**: `GET`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 请求参数 (Query Parameters)

| 字段        | 类型   | 是否必需 | 描述                       |
| :---------- | :----- | :------- | :------------------------- |
| `name`      | String | 是       | 待检查的部门名称           |
| `parentId`  | Long   | 否       | 父部门 ID                  |
| `excludeId` | Long   | 否       | 排除的部门 ID (编辑时使用) |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "检查完成",
	"data": {
		"isAvailable": true,
		"message": "名称可用"
	}
}
```

---

#### 2.1.3 创建部门

**功能描述**

创建新的部门。

- **URL**: `/api/departments`
- **HTTP 方法**: `POST`
- **Content-Type**: `application/json`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 请求体 (Request Body)

| 字段       | 类型    | 是否必需 | 描述      |
| :--------- | :------ | :------- | :-------- |
| `name`     | String  | 是       | 部门名称  |
| `parentId` | Long    | 否       | 父部门 ID |
| `isActive` | Boolean | 否       | 是否启用  |

##### 请求示例

```json
{
	"name": "新部门",
	"parentId": 1,
	"isActive": true
}
```

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "部门创建成功",
	"data": {
		"id": 2,
		"name": "新部门",
		"parentId": 1,
		"isActive": true
	}
}
```

**失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "部门名称已存在",
	"data": null
}
```

---

#### 2.1.4 更新部门信息

**功能描述**

更新指定 ID 的部门信息。

- **URL**: `/api/departments/{id}`
- **HTTP 方法**: `PUT`
- **Content-Type**: `application/json`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### URL 路径参数

| 字段 | 类型 | 描述    |
| :--- | :--- | :------ |
| `id` | Long | 部门 ID |

##### 请求体 (Request Body)

| 字段       | 类型    | 是否必需 | 描述      |
| :--------- | :------ | :------- | :-------- |
| `name`     | String  | 是       | 部门名称  |
| `parentId` | Long    | 否       | 父部门 ID |
| `isActive` | Boolean | 否       | 是否启用  |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "部门更新成功",
	"data": {
		"id": 1,
		"name": "更新后的部门名称",
		"parentId": null,
		"isActive": true
	}
}
```

---

#### 2.1.5 删除部门

**功能描述**

删除指定 ID 的部门。

- **URL**: `/api/departments/{id}`
- **HTTP 方法**: `DELETE`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### URL 路径参数

| 字段 | 类型 | 描述    |
| :--- | :--- | :------ |
| `id` | Long | 部门 ID |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "部门删除成功",
	"data": null
}
```

**失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "部门下存在子部门或用户，无法删除",
	"data": null
}
```

---

### 2.2 数据字典管理 (`/api/system/dict`)

**功能描述**

提供数据字典类型和数据相关的增删改查操作。

#### 2.2.1 分页查询字典类型列表

**功能描述**

分页查询字典类型列表，支持按关键词、类型编码、类型名称、状态和排序进行筛选。

- **URL**: `/api/system/dict/types`
- **HTTP 方法**: `GET`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 请求参数 (Query Parameters)

| 字段       | 类型    | 是否必需 | 描述                                        |
| :--------- | :------ | :------- | :------------------------------------------ |
| `page`     | Integer | 否       | 当前页码 (默认: 1)                          |
| `size`     | Integer | 否       | 每页记录数 (默认: 10)                       |
| `keyword`  | String  | 否       | 搜索关键词 (模糊匹配类型编码或类型名称)     |
| `typeCode` | String  | 否       | 字典类型编码                                |
| `typeName` | String  | 否       | 字典类型名称                                |
| `isActive` | Boolean | 否       | 字典类型状态 (true:启用, false:禁用)        |
| `sort`     | String  | 否       | 排序字段 (例如:`id,desc` 或 `typeName,asc`) |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "查询成功",
	"data": {
		"total": 100,
		"page": 1,
		"size": 10,
		"data": [
			{
				"id": 1,
				"typeCode": "gender",
				"typeName": "性别",
				"description": "用户性别",
				"isActive": true
			}
		]
	}
}
```

---

#### 2.2.2 根据 ID 查询字典类型详情

**功能描述**

根据字典类型 ID 查询其详细信息。

- **URL**: `/api/system/dict/types/{id}`
- **HTTP 方法**: `GET`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### URL 路径参数

| 字段 | 类型 | 描述        |
| :--- | :--- | :---------- |
| `id` | Long | 字典类型 ID |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "查询成功",
	"data": {
		"id": 1,
		"typeCode": "gender",
		"typeName": "性别",
		"description": "用户性别",
		"isActive": true
	}
}
```

---

#### 2.2.3 根据类型编码查询字典类型

**功能描述**

根据字典类型编码查询其详细信息。

- **URL**: `/api/system/dict/types/code/{typeCode}`
- **HTTP 方法**: `GET`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### URL 路径参数

| 字段       | 类型   | 描述         |
| :--------- | :----- | :----------- |
| `typeCode` | String | 字典类型编码 |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "查询成功",
	"data": {
		"id": 1,
		"typeCode": "gender",
		"typeName": "性别",
		"description": "用户性别",
		"isActive": true
	}
}
```

---

#### 2.2.4 创建字典类型

**功能描述**

创建新的字典类型。

- **URL**: `/api/system/dict/types`
- **HTTP 方法**: `POST`
- **Content-Type**: `application/json`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 请求体 (Request Body)

| 字段          | 类型    | 是否必需 | 描述         |
| :------------ | :------ | :------- | :----------- |
| `typeCode`    | String  | 是       | 字典类型编码 |
| `typeName`    | String  | 是       | 字典类型名称 |
| `description` | String  | 否       | 描述         |
| `isActive`    | Boolean | 否       | 是否启用     |

##### 请求示例

```json
{
	"typeCode": "user_status",
	"typeName": "用户状态",
	"description": "用户账号状态",
	"isActive": true
}
```

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "字典类型创建成功",
	"data": {
		"id": 2,
		"typeCode": "user_status",
		"typeName": "用户状态",
		"description": "用户账号状态",
		"isActive": true
	}
}
```

**失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "字典类型编码已存在",
	"data": null
}
```

---

#### 2.2.5 更新字典类型

**功能描述**

更新指定 ID 的字典类型信息。

- **URL**: `/api/system/dict/types/{id}`
- **HTTP 方法**: `PUT`
- **Content-Type**: `application/json`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### URL 路径参数

| 字段 | 类型 | 描述        |
| :--- | :--- | :---------- |
| `id` | Long | 字典类型 ID |

##### 请求体 (Request Body)

| 字段          | 类型    | 是否必需 | 描述         |
| :------------ | :------ | :------- | :----------- |
| `typeCode`    | String  | 是       | 字典类型编码 |
| `typeName`    | String  | 是       | 字典类型名称 |
| `description` | String  | 否       | 描述         |
| `isActive`    | Boolean | 否       | 是否启用     |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "字典类型更新成功",
	"data": {
		"id": 1,
		"typeCode": "gender",
		"typeName": "性别",
		"description": "用户性别",
		"isActive": true
	}
}
```

---

#### 2.2.6 删除字典类型

**功能描述**

删除指定 ID 的字典类型。

- **URL**: `/api/system/dict/types/{id}`
- **HTTP 方法**: `DELETE`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### URL 路径参数

| 字段 | 类型 | 描述        |
| :--- | :--- | :---------- |
| `id` | Long | 字典类型 ID |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "字典类型删除成功",
	"data": null
}
```

**失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "字典类型下存在字典数据，无法删除",
	"data": null
}
```

---

#### 2.2.7 检查类型编码是否存在

**功能描述**

检查字典类型编码是否已存在。

- **URL**: `/api/system/dict/types/check-code`
- **HTTP 方法**: `GET`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 请求参数 (Query Parameters)

| 字段        | 类型   | 是否必需 | 描述                           |
| :---------- | :----- | :------- | :----------------------------- |
| `typeCode`  | String | 是       | 待检查的字典类型编码           |
| `excludeId` | Long   | 否       | 排除的字典类型 ID (编辑时使用) |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "检查完成",
	"data": {
		"exists": true
	}
}
```

---

#### 2.2.8 根据类型编码查询字典数据

**功能描述**

根据字典类型编码查询该类型下的所有字典数据。

- **URL**: `/api/system/dict/data/type/{typeCode}`
- **HTTP 方法**: `GET`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### URL 路径参数

| 字段       | 类型   | 描述         |
| :--------- | :----- | :----------- |
| `typeCode` | String | 字典类型编码 |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "查询成功",
	"data": [
		{
			"id": 101,
			"typeId": 1,
			"typeCode": "gender",
			"dataCode": "male",
			"dataValue": "男",
			"sortOrder": 1,
			"isActive": true
		},
		{
			"id": 102,
			"typeId": 1,
			"typeCode": "gender",
			"dataCode": "female",
			"dataValue": "女",
			"sortOrder": 2,
			"isActive": true
		}
	]
}
```

---

#### 2.2.9 根据类型 ID 查询字典数据

**功能描述**

根据字典类型 ID 查询该类型下的所有字典数据。

- **URL**: `/api/system/dict/data/type-id/{typeId}`
- **HTTP 方法**: `GET`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### URL 路径参数

| 字段     | 类型 | 描述        |
| :------- | :--- | :---------- |
| `typeId` | Long | 字典类型 ID |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "查询成功",
	"data": [
		{
			"id": 101,
			"typeId": 1,
			"typeCode": "gender",
			"dataCode": "male",
			"dataValue": "男",
			"sortOrder": 1,
			"isActive": true
		},
		{
			"id": 102,
			"typeId": 1,
			"typeCode": "gender",
			"dataCode": "female",
			"dataValue": "女",
			"sortOrder": 2,
			"isActive": true
		}
	]
}
```

---

#### 2.2.10 创建字典数据

**功能描述**

创建新的字典数据。

- **URL**: `/api/system/dict/data`
- **HTTP 方法**: `POST`
- **Content-Type**: `application/json`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 请求体 (Request Body)

| 字段        | 类型    | 是否必需 | 描述            |
| :---------- | :------ | :------- | :-------------- |
| `typeId`    | Long    | 是       | 所属字典类型 ID |
| `dataCode`  | String  | 是       | 字典数据编码    |
| `dataValue` | String  | 是       | 字典数据值      |
| `sortOrder` | Integer | 否       | 排序            |
| `isActive`  | Boolean | 否       | 是否启用        |

##### 请求示例

```json
{
	"typeId": 1,
	"dataCode": "other",
	"dataValue": "其他",
	"sortOrder": 3,
	"isActive": true
}
```

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "字典数据创建成功",
	"data": {
		"id": 103,
		"typeId": 1,
		"dataCode": "other",
		"dataValue": "其他",
		"sortOrder": 3,
		"isActive": true
	}
}
```

**失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "字典数据编码已存在",
	"data": null
}
```

---

#### 2.2.11 更新字典数据

**功能描述**

更新指定 ID 的字典数据信息。

- **URL**: `/api/system/dict/data/{id}`
- **HTTP 方法**: `PUT`
- **Content-Type**: `application/json`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### URL 路径参数

| 字段 | 类型 | 描述        |
| :--- | :--- | :---------- |
| `id` | Long | 字典数据 ID |

##### 请求体 (Request Body)

| 字段        | 类型    | 是否必需 | 描述            |
| :---------- | :------ | :------- | :-------------- |
| `typeId`    | Long    | 是       | 所属字典类型 ID |
| `dataCode`  | String  | 是       | 字典数据编码    |
| `dataValue` | String  | 是       | 字典数据值      |
| `sortOrder` | Integer | 否       | 排序            |
| `isActive`  | Boolean | 否       | 是否启用        |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "字典数据更新成功",
	"data": {
		"id": 101,
		"typeId": 1,
		"dataCode": "male",
		"dataValue": "男性",
		"sortOrder": 1,
		"isActive": true
	}
}
```

---

#### 2.2.12 删除字典数据

**功能描述**

删除指定 ID 的字典数据。

- **URL**: `/api/system/dict/data/{id}`
- **HTTP 方法**: `DELETE`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### URL 路径参数

| 字段 | 类型 | 描述        |
| :--- | :--- | :---------- |
| `id` | Long | 字典数据 ID |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "字典数据删除成功",
	"data": null
}
```

---

### 2.3 权限管理 (`/api/permissions`)

**功能描述**

提供权限的增删查、树结构、类型筛选、唯一性校验等操作。

#### 2.3.1 分页查询权限列表

**功能描述**

分页获取权限列表，支持按关键字和类型筛选。

- **URL**: `/api/permissions`
- **HTTP 方法**: `GET`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 请求参数 (Query Parameters)

| 字段      | 类型   | 是否必需 | 描述              |
| :-------- | :----- | :------- | :---------------- |
| `page`    | int    | 否       | 页码，默认 1      |
| `size`    | int    | 否       | 每页数量，默认 10 |
| `keyword` | string | 否       | 关键字搜索        |
| `type`    | string | 否       | 权限类型          |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "查询成功",
	"data": {
		"total": 100,
		"page": 1,
		"size": 10,
		"data": [
			{
				"id": 1,
				"name": "用户管理",
				"code": "user:manage",
				"type": "menu",
				"parentId": null,
				"children": [],
				"remark": "系统菜单"
			}
		]
	}
}
```

**失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "参数无效",
	"data": null
}
```

---

#### 2.3.2 查询权限详情

**功能描述**

根据 ID 获取权限详情。

- **URL**: `/api/permissions/{id}`
- **HTTP 方法**: `GET`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### URL 路径参数

| 字段 | 类型 | 描述    |
| :--- | :--- | :------ |
| `id` | Long | 权限 ID |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "查询成功",
	"data": {
		"id": 1,
		"name": "用户管理",
		"code": "user:manage",
		"type": "menu",
		"parentId": null,
		"children": [],
		"remark": "系统菜单"
	}
}
```

**失败响应 (404 Not Found)**

```json
{
	"code": 404,
	"message": "权限不存在",
	"data": null
}
```

---

#### 2.3.3 获取权限树结构

**功能描述**

获取全部权限的树结构。

- **URL**: `/api/permissions/tree`
- **HTTP 方法**: `GET`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "查询成功",
	"data": [
		{
			"id": 1,
			"name": "系统管理",
			"code": "system:manage",
			"type": "menu",
			"parentId": null,
			"children": [
				{
					"id": 2,
					"name": "用户管理",
					"code": "user:manage",
					"type": "menu",
					"parentId": 1,
					"children": []
				}
			]
		}
	]
}
```

---

#### 2.3.4 获取菜单权限树

**功能描述**

获取菜单类型权限的树结构。

- **URL**: `/api/permissions/menu-tree`
- **HTTP 方法**: `GET`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "查询成功",
	"data": [
		{
			"id": 1,
			"name": "系统管理",
			"code": "system:manage",
			"type": "menu",
			"parentId": null,
			"children": [
				{
					"id": 2,
					"name": "用户管理",
					"code": "user:manage",
					"type": "menu",
					"parentId": 1,
					"children": []
				}
			]
		}
	]
}
```

---

#### 2.3.5 根据类型获取权限列表

**功能描述**

根据类型获取权限列表。

- **URL**: `/api/permissions/by-type`
- **HTTP 方法**: `GET`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 请求参数 (Query Parameters)

| 字段   | 类型   | 是否必需 | 描述     |
| :----- | :----- | :------- | :------- |
| `type` | string | 是       | 权限类型 |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "查询成功",
	"data": [
		{
			"id": 1,
			"name": "用户管理",
			"code": "user:manage",
			"type": "menu"
		}
	]
}
```

---

#### 2.3.6 获取所有可用权限

**功能描述**

获取所有可用权限。

- **URL**: `/api/permissions/available`
- **HTTP 方法**: `GET`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "查询成功",
	"data": [
		{
			"id": 1,
			"name": "用户管理",
			"code": "user:manage",
			"type": "menu"
		}
	]
}
```

---

#### 2.3.7 删除权限

**功能描述**

根据 ID 删除权限。

- **URL**: `/api/permissions/{id}`
- **HTTP 方法**: `DELETE`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### URL 路径参数

| 字段 | 类型 | 描述    |
| :--- | :--- | :------ |
| `id` | Long | 权限 ID |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "权限删除成功",
	"data": null
}
```

**失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "权限下存在子权限，无法删除",
	"data": null
}
```

---

#### 2.3.8 检查权限编码可用性

**功能描述**

校验权限编码是否可用（唯一）。

- **URL**: `/api/permissions/check-code`
- **HTTP 方法**: `GET`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 请求参数 (Query Parameters)

| 字段        | 类型   | 是否必需 | 描述                                  |
| :---------- | :----- | :------- | :------------------------------------ |
| `code`      | string | 是       | 权限编码                              |
| `excludeId` | Long   | 否       | 排除的权限 ID（用于编辑时自身不校验） |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "检查完成",
	"data": {
		"available": true
	}
}
```

**失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "权限编码已存在",
	"data": {
		"available": false
	}
}
```

---

### 2.4 岗位管理 (`/api/positions`)

**功能描述**

提供岗位的增删改查、人员查询、名称唯一性校验等操作。

#### 2.4.1 分页查询岗位列表

**功能描述**

分页获取岗位列表，支持按关键字搜索。

- **URL**: `/api/positions`
- **HTTP 方法**: `GET`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 请求参数 (Query Parameters)

| 字段      | 类型   | 是否必需 | 描述              |
| :-------- | :----- | :------- | :---------------- |
| `page`    | int    | 否       | 页码，默认 1      |
| `size`    | int    | 否       | 每页数量，默认 10 |
| `keyword` | string | 否       | 关键字搜索        |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "查询成功",
	"data": {
		"total": 100,
		"page": 1,
		"size": 10,
		"data": [
			{
				"id": 1,
				"name": "运维岗",
				"description": "负责系统运维",
				"level": "中级"
			}
		]
	}
}
```

**失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "参数无效",
	"data": null
}
```

---

#### 2.4.2 创建岗位

**功能描述**

创建新的岗位。

- **URL**: `/api/positions`
- **HTTP 方法**: `POST`
- **Content-Type**: `application/json`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 请求体 (Request Body)

| 字段          | 类型   | 是否必需 | 描述     |
| :------------ | :----- | :------- | :------- |
| `name`        | String | 是       | 岗位名称 |
| `description` | String | 否       | 岗位描述 |
| `level`       | String | 否       | 岗位级别 |

##### 请求示例

```json
{
	"name": "测试岗位",
	"description": "这是一个测试岗位",
	"level": "初级"
}
```

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "岗位创建成功",
	"data": {
		"id": 2,
		"name": "测试岗位",
		"description": "这是一个测试岗位",
		"level": "初级"
	}
}
```

**失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "岗位名称已存在",
	"data": null
}
```

---

#### 2.4.3 更新岗位信息

**功能描述**

根据 ID 更新岗位信息。

- **URL**: `/api/positions/{id}`
- **HTTP 方法**: `PUT`
- **Content-Type**: `application/json`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### URL 路径参数

| 字段 | 类型 | 描述    |
| :--- | :--- | :------ |
| `id` | Long | 岗位 ID |

##### 请求体 (Request Body)

| 字段          | 类型   | 是否必需 | 描述     |
| :------------ | :----- | :------- | :------- |
| `name`        | String | 是       | 岗位名称 |
| `description` | String | 否       | 岗位描述 |
| `level`       | String | 否       | 岗位级别 |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "岗位更新成功",
	"data": {
		"id": 1,
		"name": "更新后的岗位名称",
		"description": "更新后的描述",
		"level": "高级"
	}
}
```

**失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "岗位不存在，ID: 1",
	"data": null
}
```

---

#### 2.4.4 删除岗位

**功能描述**

根据 ID 删除岗位。

- **URL**: `/api/positions/{id}`
- **HTTP 方法**: `DELETE`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### URL 路径参数

| 字段 | 类型 | 描述    |
| :--- | :--- | :------ |
| `id` | Long | 岗位 ID |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "岗位删除成功",
	"data": null
}
```

**失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "岗位不存在，ID: 1",
	"data": null
}
```

---

#### 2.4.5 获取岗位下的人员列表

**功能描述**

获取指定岗位下的人员列表。

- **URL**: `/api/positions/{id}/personnel`
- **HTTP 方法**: `GET`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### URL 路径参数

| 字段 | 类型 | 描述    |
| :--- | :--- | :------ |
| `id` | Long | 岗位 ID |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "查询成功",
	"data": [
		{
			"personnelId": 101,
			"name": "张三",
			"department": "技术部"
		}
	]
}
```

**失败响应 (500 Internal Server Error)**

```json
{
	"code": 500,
	"message": "系统内部错误",
	"data": null
}
```

---

#### 2.4.6 检查岗位名称是否可用

**功能描述**

校验岗位名称在系统中是否唯一。

- **URL**: `/api/positions/check-name`
- **HTTP 方法**: `GET`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 请求参数 (Query Parameters)

| 字段        | 类型   | 是否必需 | 描述          |
| :---------- | :----- | :------- | :------------ |
| `name`      | String | 是       | 岗位名称      |
| `excludeId` | Long   | 否       | 排除的岗位 ID |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "检查完成",
	"data": {
		"available": true
	}
}
```

**失败响应 (500 Internal Server Error)**

```json
{
	"code": 500,
	"message": "系统内部错误: ...",
	"data": null
}
```

---

### 2.5 角色管理 (`/api/roles`)

**功能描述**

提供角色的增删改查、权限分配、名称唯一性校验等操作。

#### 2.5.1 分页查询角色列表

**功能描述**

分页获取角色列表，支持按角色名称搜索。

- **URL**: `/api/roles`
- **HTTP 方法**: `GET`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 请求参数 (Query Parameters)

| 字段   | 类型   | 是否必需 | 描述              |
| :----- | :----- | :------- | :---------------- |
| `page` | int    | 否       | 页码，默认 1      |
| `size` | int    | 否       | 每页数量，默认 10 |
| `name` | string | 否       | 角色名称搜索      |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "查询成功",
	"data": {
		"total": 100,
		"page": 1,
		"size": 10,
		"data": [
			{
				"id": 1,
				"name": "管理员",
				"code": "admin",
				"description": "系统管理员",
				"isActive": true
			}
		]
	}
}
```

**失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "参数无效",
	"data": null
}
```

---

#### 2.5.2 根据 ID 查询角色详情

**功能描述**

根据角色 ID 查询角色的详细信息。

- **URL**: `/api/roles/{id}`
- **HTTP 方法**: `GET`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### URL 路径参数

| 字段 | 类型 | 描述    |
| :--- | :--- | :------ |
| `id` | Long | 角色 ID |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "查询成功",
	"data": {
		"id": 1,
		"name": "管理员",
		"code": "admin",
		"description": "系统管理员",
		"isActive": true,
		"permissions": [
			{
				"id": 1,
				"name": "用户管理",
				"code": "user:manage"
			}
		]
	}
}
```

**失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "角色不存在",
	"data": null
}
```

---

#### 2.5.3 创建角色

**功能描述**

创建新的角色。

- **URL**: `/api/roles`
- **HTTP 方法**: `POST`
- **Content-Type**: `application/json`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 请求体 (Request Body)

| 字段          | 类型    | 是否必需 | 描述     |
| :------------ | :------ | :------- | :------- |
| `name`        | String  | 是       | 角色名称 |
| `code`        | String  | 是       | 角色编码 |
| `description` | String  | 否       | 角色描述 |
| `isActive`    | Boolean | 否       | 是否启用 |

##### 请求示例

```json
{
	"name": "测试角色",
	"code": "test_role",
	"description": "这是一个测试角色",
	"isActive": true
}
```

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "角色创建成功",
	"data": {
		"id": 2,
		"name": "测试角色",
		"code": "test_role",
		"description": "这是一个测试角色",
		"isActive": true
	}
}
```

**失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "角色名称已存在",
	"data": null
}
```

---

#### 2.5.4 更新角色信息

**功能描述**

根据 ID 更新角色信息。

- **URL**: `/api/roles/{id}`
- **HTTP 方法**: `PUT`
- **Content-Type**: `application/json`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### URL 路径参数

| 字段 | 类型 | 描述    |
| :--- | :--- | :------ |
| `id` | Long | 角色 ID |

##### 请求体 (Request Body)

| 字段          | 类型    | 是否必需 | 描述     |
| :------------ | :------ | :------- | :------- |
| `name`        | String  | 是       | 角色名称 |
| `code`        | String  | 是       | 角色编码 |
| `description` | String  | 否       | 角色描述 |
| `isActive`    | Boolean | 否       | 是否启用 |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "角色更新成功",
	"data": {
		"id": 1,
		"name": "更新后的角色名称",
		"code": "updated_code",
		"description": "更新后的描述",
		"isActive": true
	}
}
```

**失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "角色不存在，ID: 1",
	"data": null
}
```

---

#### 2.5.5 删除角色

**功能描述**

根据 ID 删除角色（软删除）。

- **URL**: `/api/roles/{id}`
- **HTTP 方法**: `DELETE`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### URL 路径参数

| 字段 | 类型 | 描述    |
| :--- | :--- | :------ |
| `id` | Long | 角色 ID |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "角色删除成功",
	"data": null
}
```

**失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "角色不存在，ID: 1",
	"data": null
}
```

---

#### 2.5.6 获取角色权限列表

**功能描述**

获取指定角色已分配的所有权限。

- **URL**: `/api/roles/{id}/permissions`
- **HTTP 方法**: `GET`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### URL 路径参数

| 字段 | 类型 | 描述    |
| :--- | :--- | :------ |
| `id` | Long | 角色 ID |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "查询成功",
	"data": [
		{
			"id": 1,
			"name": "用户管理",
			"code": "user:manage",
			"description": "管理系统用户"
		},
		{
			"id": 2,
			"name": "系统管理",
			"code": "system:manage",
			"description": "管理系统配置"
		}
	]
}
```

**失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "角色不存在",
	"data": null
}
```

---

#### 2.5.7 为角色分配权限

**功能描述**

为指定角色分配权限，会覆盖原有权限。

- **URL**: `/api/roles/{id}/permissions`
- **HTTP 方法**: `PUT`
- **Content-Type**: `application/json`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### URL 路径参数

| 字段 | 类型 | 描述    |
| :--- | :--- | :------ |
| `id` | Long | 角色 ID |

##### 请求体 (Request Body)

| 字段            | 类型 | 是否必需 | 描述         |
| :-------------- | :--- | :------- | :----------- |
| `permissionIds` | List | 是       | 权限 ID 列表 |

##### 请求示例

```json
[1, 2, 3]
```

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "权限分配成功",
	"data": null
}
```

**失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "角色不存在",
	"data": null
}
```

---

#### 2.5.8 获取所有可用角色

**功能描述**

获取所有可用的角色列表，用于前端下拉选择框。

- **URL**: `/api/roles/available`
- **HTTP 方法**: `GET`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "查询成功",
	"data": [
		{
			"id": 1,
			"name": "管理员",
			"code": "admin",
			"description": "系统管理员",
			"isActive": true
		},
		{
			"id": 2,
			"name": "普通用户",
			"code": "user",
			"description": "普通用户",
			"isActive": true
		}
	]
}
```

**失败响应 (500 Internal Server Error)**

```json
{
	"code": 500,
	"message": "系统内部错误",
	"data": null
}
```

---

#### 2.5.9 检查角色名称是否可用

**功能描述**

校验角色名称在系统中是否唯一。

- **URL**: `/api/roles/check-name`
- **HTTP 方法**: `GET`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 请求参数 (Query Parameters)

| 字段        | 类型   | 是否必需 | 描述          |
| :---------- | :----- | :------- | :------------ |
| `name`      | String | 是       | 角色名称      |
| `excludeId` | Long   | 否       | 排除的角色 ID |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "检查完成",
	"data": {
		"available": true
	}
}
```

**失败响应 (500 Internal Server Error)**

```json
{
	"code": 500,
	"message": "系统内部错误",
	"data": null
}
```

---

### 2.6 用户管理 (`/api/users`)

**功能描述**

提供用户的增删改查、角色分配、状态管理等操作。所有接口都需要 `system:manage` 权限。

#### 2.6.1 分页查询用户列表

**功能描述**

分页获取用户列表，支持按用户名、角色 ID、激活状态筛选。

- **URL**: `/api/users`
- **HTTP 方法**: `GET`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 请求参数 (Query Parameters)

| 字段       | 类型    | 是否必需 | 描述              |
| :--------- | :------ | :------- | :---------------- |
| `page`     | Integer | 否       | 页码，默认 1      |
| `size`     | Integer | 否       | 每页数量，默认 10 |
| `username` | String  | 否       | 按用户名筛选      |
| `roleId`   | Long    | 否       | 按角色 ID 筛选    |
| `isActive` | Boolean | 否       | 按激活状态筛选    |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "查询成功",
	"data": {
		"total": 100,
		"page": 1,
		"size": 10,
		"data": [
			{
				"id": 1,
				"username": "admin",
				"email": "admin@example.com",
				"phone": "13800138000",
				"realName": "管理员",
				"departmentId": 1,
				"departmentName": "研发部",
				"roleId": 1,
				"roleName": "管理员",
				"isActive": true,
				"createdAt": "2024-01-01T10:00:00",
				"updatedAt": "2024-01-01T10:00:00"
			}
		]
	}
}
```

**失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "查询失败: 参数无效",
	"data": null
}
```

---

#### 2.6.2 根据 ID 查询用户详情

**功能描述**

根据用户 ID 查询用户的详细信息。

- **URL**: `/api/users/{id}`
- **HTTP 方法**: `GET`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### URL 路径参数

| 字段 | 类型 | 描述    |
| :--- | :--- | :------ |
| `id` | Long | 用户 ID |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "查询成功",
	"data": {
		"id": 1,
		"username": "admin",
		"email": "admin@example.com",
		"phone": "13800138000",
		"realName": "管理员",
		"departmentId": 1,
		"departmentName": "研发部",
		"roleId": 1,
		"roleName": "管理员",
		"isActive": true,
		"createdAt": "2024-01-01T10:00:00",
		"updatedAt": "2024-01-01T10:00:00"
	}
}
```

**失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "用户不存在",
	"data": null
}
```

---

#### 2.6.3 创建用户

**功能描述**

创建新的用户账号。

- **URL**: `/api/users`
- **HTTP 方法**: `POST`
- **Content-Type**: `application/json`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 请求体 (Request Body)

| 字段           | 类型    | 是否必需 | 描述     |
| :------------- | :------ | :------- | :------- |
| `username`     | String  | 是       | 用户名   |
| `password`     | String  | 是       | 密码     |
| `email`        | String  | 否       | 邮箱     |
| `phone`        | String  | 否       | 手机号   |
| `realName`     | String  | 否       | 真实姓名 |
| `departmentId` | Long    | 否       | 部门 ID  |
| `roleId`       | Long    | 否       | 角色 ID  |
| `isActive`     | Boolean | 否       | 是否启用 |

##### 请求示例

```json
{
	"username": "newuser",
	"password": "password123",
	"email": "newuser@example.com",
	"phone": "13900139000",
	"realName": "新用户",
	"departmentId": 1,
	"roleId": 2,
	"isActive": true
}
```

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "用户创建成功",
	"data": {
		"id": 2,
		"username": "newuser",
		"email": "newuser@example.com",
		"phone": "13900139000",
		"realName": "新用户",
		"departmentId": 1,
		"departmentName": "研发部",
		"roleId": 2,
		"roleName": "普通用户",
		"isActive": true,
		"createdAt": "2024-01-01T10:00:00",
		"updatedAt": "2024-01-01T10:00:00"
	}
}
```

**失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "用户名已存在",
	"data": null
}
```

---

#### 2.6.4 更新用户信息

**功能描述**

根据 ID 更新用户信息。

- **URL**: `/api/users/{id}`
- **HTTP 方法**: `PUT`
- **Content-Type**: `application/json`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### URL 路径参数

| 字段 | 类型 | 描述    |
| :--- | :--- | :------ |
| `id` | Long | 用户 ID |

##### 请求体 (Request Body)

| 字段           | 类型    | 是否必需 | 描述     |
| :------------- | :------ | :------- | :------- |
| `username`     | String  | 是       | 用户名   |
| `email`        | String  | 否       | 邮箱     |
| `phone`        | String  | 否       | 手机号   |
| `realName`     | String  | 否       | 真实姓名 |
| `departmentId` | Long    | 否       | 部门 ID  |
| `roleId`       | Long    | 否       | 角色 ID  |
| `isActive`     | Boolean | 否       | 是否启用 |

##### 请求示例

```json
{
	"username": "updateduser",
	"email": "updated@example.com",
	"phone": "13700137000",
	"realName": "更新后的用户",
	"departmentId": 2,
	"roleId": 3,
	"isActive": true
}
```

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "用户更新成功",
	"data": {
		"id": 1,
		"username": "updateduser",
		"email": "updated@example.com",
		"phone": "13700137000",
		"realName": "更新后的用户",
		"departmentId": 2,
		"departmentName": "测试部",
		"roleId": 3,
		"roleName": "测试人员",
		"isActive": true,
		"createdAt": "2024-01-01T10:00:00",
		"updatedAt": "2024-01-01T11:00:00"
	}
}
```

**失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "用户不存在，ID: 1",
	"data": null
}
```

---

#### 2.6.5 删除用户（软删除）

**功能描述**

根据 ID 删除用户（软删除）。

- **URL**: `/api/users/{id}`
- **HTTP 方法**: `DELETE`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### URL 路径参数

| 字段 | 类型 | 描述    |
| :--- | :--- | :------ |
| `id` | Long | 用户 ID |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "用户删除成功",
	"data": null
}
```

**失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "用户不存在，ID: 1",
	"data": null
}
```

---

#### 2.6.6 获取用户的角色列表

**功能描述**

获取指定用户已分配的所有角色。

- **URL**: `/api/users/{id}/roles`
- **HTTP 方法**: `GET`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### URL 路径参数

| 字段 | 类型 | 描述    |
| :--- | :--- | :------ |
| `id` | Long | 用户 ID |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "查询成功",
	"data": [
		{
			"id": 1,
			"name": "管理员",
			"code": "admin",
			"description": "系统管理员",
			"isActive": true
		},
		{
			"id": 2,
			"name": "普通用户",
			"code": "user",
			"description": "普通用户",
			"isActive": true
		}
	]
}
```

**失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "用户不存在",
	"data": null
}
```

---

#### 2.6.7 为用户分配角色

**功能描述**

为指定用户分配角色，会覆盖原有角色。

- **URL**: `/api/users/{id}/roles`
- **HTTP 方法**: `PUT`
- **Content-Type**: `application/json`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### URL 路径参数

| 字段 | 类型 | 描述    |
| :--- | :--- | :------ |
| `id` | Long | 用户 ID |

##### 请求体 (Request Body)

| 字段      | 类型 | 是否必需 | 描述         |
| :-------- | :--- | :------- | :----------- |
| `roleIds` | List | 是       | 角色 ID 列表 |

##### 请求示例

```json
[1, 2, 3]
```

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "角色分配成功",
	"data": null
}
```

**失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "用户不存在",
	"data": null
}
```

---

## 3. 工程信息服务模块 (`/api/engineering-service`)

**功能描述**

提供工程服务相关的功能，包括设施管理、消毒材料管理等。

### 3.1 消毒材料管理模块 (`/api/engineering-service/disinfection-materials`)

**功能描述**

提供消毒材料相关的增删改查操作、批量操作、统计信息查询等功能。

#### 3.1.1 分页查询消毒材料列表

**功能描述**

根据条件分页查询消毒材料信息，支持关键词搜索（材料名称）和条件筛选（所属水厂）。

- **URL**: `/api/engineering-service/disinfection-materials`
- **HTTP 方法**: `GET`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 请求参数 (Query Parameters)

| 字段           | 类型    | 是否必需 | 描述                          |
| :------------- | :------ | :------- | :---------------------------- |
| `page`         | Integer | 否       | 当前页码 (默认: 1)            |
| `size`         | Integer | 否       | 每页记录数 (默认: 10)         |
| `keyword`      | String  | 否       | 搜索关键词 (材料名称模糊匹配) |
| `waterPlantId` | Long    | 否       | 所属水厂 ID 筛选              |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "查询成功",
	"data": {
		"total": 50,
		"page": 1,
		"size": 10,
		"data": [
			{
				"id": 1,
				"name": "次氯酸钠",
				"waterPlantId": 1,
				"waterPlantName": "襄阳第一水厂",
				"storageCondition": "阴凉干燥处",
				"productionDate": "2024-01-15",
				"validityPeriod": "12个月",
				"quantity": 100.5,
				"unit": "kg",
				"remark": "10%浓度液氯，水厂消毒用",
				"createdAt": "2024-01-01T10:00:00",
				"updatedAt": "2024-01-01T10:00:00"
			}
		]
	}
}
```

**失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "查询失败，参数无效",
	"data": null
}
```

---

#### 3.1.2 根据 ID 查询消毒材料详情

**功能描述**

根据消毒材料 ID 查询其详细信息。

- **URL**: `/api/engineering-service/disinfection-materials/{id}`
- **HTTP 方法**: `GET`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### URL 路径参数

| 字段 | 类型 | 描述        |
| :--- | :--- | :---------- |
| `id` | Long | 消毒材料 ID |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "查询成功",
	"data": {
		"id": 1,
		"name": "次氯酸钠",
		"waterPlantId": 1,
		"waterPlantName": "襄阳第一水厂",
		"storageCondition": "阴凉干燥处",
		"productionDate": "2024-01-15",
		"validityPeriod": "12个月",
		"quantity": 100.5,
		"unit": "kg",
		"remark": "10%浓度液氯，水厂消毒用",
		"createdAt": "2024-01-01T10:00:00",
		"updatedAt": "2024-01-01T10:00:00"
	}
}
```

**失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "消毒材料不存在，ID: 1",
	"data": null
}
```

---

#### 3.1.3 创建消毒材料

**功能描述**

创建新的消毒材料信息，需提供必要的材料信息。

- **URL**: `/api/engineering-service/disinfection-materials`
- **HTTP 方法**: `POST`
- **Content-Type**: `application/json`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 请求体 (Request Body)

| 字段               | 类型       | 是否必需 | 描述                  |
| :----------------- | :--------- | :------- | :-------------------- |
| `name`             | String     | 是       | 材料名称              |
| `waterPlantId`     | Long       | 是       | 所属水厂 ID           |
| `storageCondition` | String     | 否       | 存储条件              |
| `productionDate`   | LocalDate  | 否       | 生产日期 (YYYY-MM-DD) |
| `validityPeriod`   | String     | 否       | 有效期                |
| `quantity`         | BigDecimal | 否       | 库存数量              |
| `unit`             | String     | 否       | 单位                  |
| `remark`           | String     | 否       | 备注                  |

#### 请求示例

```json
{
	"name": "次氯酸钠",
	"waterPlantId": 1,
	"storageCondition": "阴凉干燥处",
	"productionDate": "2024-01-15",
	"validityPeriod": "12个月",
	"quantity": 100.5,
	"unit": "kg",
	"remark": "10%浓度液氯，水厂消毒用"
}
```

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "创建成功",
	"data": {
		"id": 2,
		"name": "次氯酸钠",
		"waterPlantId": 1,
		"waterPlantName": "襄阳第一水厂",
		"storageCondition": "阴凉干燥处",
		"productionDate": "2024-01-15",
		"validityPeriod": "12个月",
		"quantity": 100.5,
		"unit": "kg",
		"remark": "10%浓度液氯，水厂消毒用",
		"createdAt": "2024-01-01T10:00:00",
		"updatedAt": "2024-01-01T10:00:00"
	}
}
```

**失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "材料名称已存在",
	"data": null
}
```

---

#### 3.1.4 更新消毒材料信息

**功能描述**

根据 ID 更新消毒材料的信息。

- **URL**: `/api/engineering-service/disinfection-materials/{id}`
- **HTTP 方法**: `PUT`
- **Content-Type**: `application/json`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### URL 路径参数

| 字段 | 类型 | 描述        |
| :--- | :--- | :---------- |
| `id` | Long | 消毒材料 ID |

##### 请求体 (Request Body)

| 字段               | 类型       | 是否必需 | 描述                  |
| :----------------- | :--------- | :------- | :-------------------- |
| `name`             | String     | 是       | 材料名称              |
| `waterPlantId`     | Long       | 是       | 所属水厂 ID           |
| `storageCondition` | String     | 否       | 存储条件              |
| `productionDate`   | LocalDate  | 否       | 生产日期 (YYYY-MM-DD) |
| `validityPeriod`   | String     | 否       | 有效期                |
| `quantity`         | BigDecimal | 否       | 库存数量              |
| `unit`             | String     | 否       | 单位                  |
| `remark`           | String     | 否       | 备注                  |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "更新成功",
	"data": {
		"id": 1,
		"name": "更新后的次氯酸钠",
		"waterPlantId": 1,
		"waterPlantName": "襄阳第一水厂",
		"storageCondition": "阴凉干燥处",
		"productionDate": "2024-01-15",
		"validityPeriod": "12个月",
		"quantity": 150.0,
		"unit": "kg",
		"remark": "更新后的备注信息",
		"createdAt": "2024-01-01T10:00:00",
		"updatedAt": "2024-01-01T11:00:00"
	}
}
```

**失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "消毒材料不存在，ID: 1",
	"data": null
}
```

---

#### 3.1.5 删除消毒材料

**功能描述**

根据 ID 删除消毒材料（软删除）。

- **URL**: `/api/engineering-service/disinfection-materials/{id}`
- **HTTP 方法**: `DELETE`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### URL 路径参数

| 字段 | 类型 | 描述        |
| :--- | :--- | :---------- |
| `id` | Long | 消毒材料 ID |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "删除成功",
	"data": null
}
```

**失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "消毒材料不存在，ID: 1",
	"data": null
}
```

---

#### 3.1.6 批量删除消毒材料

**功能描述**

根据 ID 列表批量删除多个消毒材料（软删除）。

- **URL**: `/api/engineering-service/disinfection-materials/batch`
- **HTTP 方法**: `DELETE`
- **Content-Type**: `application/json`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 请求体 (Request Body)

| 字段  | 类型         | 是否必需 | 描述             |
| :---- | :----------- | :------- | :--------------- |
| `ids` | List`<Long>` | 是       | 消毒材料 ID 列表 |

##### 请求示例

```json
[1, 2, 3]
```

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "批量删除成功",
	"data": null
}
```

**失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "删除ID列表不能为空",
	"data": null
}
```

---

#### 3.1.7 获取所有可用消毒材料

**功能描述**

获取所有可用的消毒材料列表，通常用于下拉选择框。

- **URL**: `/api/engineering-service/disinfection-materials/available`
- **HTTP 方法**: `GET`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "查询成功",
	"data": [
		{
			"id": 1,
			"name": "次氯酸钠",
			"waterPlantId": 1,
			"storageCondition": "阴凉干燥处",
			"productionDate": "2024-01-15",
			"validityPeriod": "12个月",
			"quantity": 100.5,
			"unit": "kg",
			"remark": "10%浓度液氯，水厂消毒用"
		},
		{
			"id": 2,
			"name": "二氧化氯",
			"waterPlantId": 1,
			"storageCondition": "避光保存",
			"productionDate": "2024-01-20",
			"validityPeriod": "6个月",
			"quantity": 50.0,
			"unit": "kg",
			"remark": "高效消毒剂"
		}
	]
}
```

---

#### 3.1.8 统计消毒材料总数

**功能描述**

统计系统中消毒材料的总数量。

- **URL**: `/api/engineering-service/disinfection-materials/count`
- **HTTP 方法**: `GET`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "统计成功",
	"data": 50
}
```

---

#### 3.1.9 获取库存统计信息

**功能描述**

获取消毒材料的库存统计信息，包括总数、库存不足、即将过期等。

- **URL**: `/api/engineering-service/disinfection-materials/statistics`
- **HTTP 方法**: `GET`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "统计成功",
	"data": {
		"total": 50,
		"lowStock": 5,
		"nearExpiry": 3,
		"totalQuantity": 2500.5,
		"averageQuantity": 50.01
	}
}
```

---

### 3.2 浮船管理模块 (`/api/engineering-service/floating-boats`)

**功能描述**

提供浮船信息相关的增删改查操作、批量操作、统计信息查询等功能。

#### 3.2.1 分页查询浮船信息列表

**功能描述**

根据条件分页查询浮船信息，支持关键词搜索（浮船名称）和条件筛选（抽水状态）。

- **URL**: `/api/engineering-service/floating-boats`
- **HTTP 方法**: `GET`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 请求参数 (Query Parameters)

| 字段            | 类型    | 是否必需 | 描述                          |
| :-------------- | :------ | :------- | :---------------------------- |
| `page`          | Integer | 否       | 当前页码 (默认: 1)            |
| `size`          | Integer | 否       | 每页记录数 (默认: 10)         |
| `keyword`       | String  | 否       | 搜索关键词 (浮船名称模糊匹配) |
| `pumpingStatus` | String  | 否       | 抽水状态筛选                  |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "查询成功",
	"data": {
		"total": 30,
		"page": 1,
		"size": 10,
		"data": [
			{
				"id": 1,
				"name": "取水浮船1号",
				"longitude": 114.123456,
				"latitude": 30.123456,
				"capacity": 200.0,
				"powerConsumption": 50.0,
				"pumpingStatus": "normal",
				"pumpingStatusName": "正常运行",
				"remark": "正常运行",
				"createdAt": "2024-01-01T10:00:00",
				"updatedAt": "2024-01-01T10:00:00"
			}
		]
	}
}
```

**失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "查询失败，参数无效",
	"data": null
}
```

---

#### 3.2.2 根据 ID 查询浮船信息详情

**功能描述**

根据浮船信息 ID 查询其详细信息。

- **URL**: `/api/engineering-service/floating-boats/{id}`
- **HTTP 方法**: `GET`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### URL 路径参数

| 字段 | 类型 | 描述        |
| :--- | :--- | :---------- |
| `id` | Long | 浮船信息 ID |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "查询成功",
	"data": {
		"id": 1,
		"name": "取水浮船1号",
		"longitude": 114.123456,
		"latitude": 30.123456,
		"capacity": 200.0,
		"powerConsumption": 50.0,
		"pumpingStatus": "normal",
		"pumpingStatusName": "正常运行",
		"remark": "正常运行",
		"createdAt": "2024-01-01T10:00:00",
		"updatedAt": "2024-01-01T10:00:00"
	}
}
```

**失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "浮船信息不存在，ID: 1",
	"data": null
}
```

---

#### 3.2.3 创建浮船信息

**功能描述**

创建新的浮船信息，需提供必要的浮船信息。

- **URL**: `/api/engineering-service/floating-boats`
- **HTTP 方法**: `POST`
- **Content-Type**: `application/json`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 请求体 (Request Body)

| 字段               | 类型       | 是否必需 | 描述             |
| :----------------- | :--------- | :------- | :--------------- |
| `name`             | String     | 是       | 浮船名称         |
| `longitude`        | BigDecimal | 否       | 经度             |
| `latitude`         | BigDecimal | 否       | 纬度             |
| `capacity`         | BigDecimal | 否       | 抽水能力（m³/h） |
| `powerConsumption` | BigDecimal | 否       | 功率（kW）       |
| `pumpingStatus`    | String     | 否       | 抽水状态         |
| `remark`           | String     | 否       | 备注             |

##### 请求示例

```json
{
	"name": "取水浮船2号",
	"longitude": 114.234567,
	"latitude": 30.234567,
	"capacity": 250.0,
	"powerConsumption": 60.0,
	"pumpingStatus": "normal",
	"remark": "新建浮船"
}
```

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "创建成功",
	"data": {
		"id": 2,
		"name": "取水浮船2号",
		"longitude": 114.234567,
		"latitude": 30.234567,
		"capacity": 250.0,
		"powerConsumption": 60.0,
		"pumpingStatus": "normal",
		"pumpingStatusName": "正常运行",
		"remark": "新建浮船",
		"createdAt": "2024-01-01T10:00:00",
		"updatedAt": "2024-01-01T10:00:00"
	}
}
```

**失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "浮船名称已存在",
	"data": null
}
```

---

#### 3.2.4 更新浮船信息

**功能描述**

根据 ID 更新浮船信息。

- **URL**: `/api/engineering-service/floating-boats/{id}`
- **HTTP 方法**: `PUT`
- **Content-Type**: `application/json`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### URL 路径参数

| 字段 | 类型 | 描述        |
| :--- | :--- | :---------- |
| `id` | Long | 浮船信息 ID |

##### 请求体 (Request Body)

| 字段               | 类型       | 是否必需 | 描述             |
| :----------------- | :--------- | :------- | :--------------- |
| `name`             | String     | 是       | 浮船名称         |
| `longitude`        | BigDecimal | 否       | 经度             |
| `latitude`         | BigDecimal | 否       | 纬度             |
| `capacity`         | BigDecimal | 否       | 抽水能力（m³/h） |
| `powerConsumption` | BigDecimal | 否       | 功率（kW）       |
| `pumpingStatus`    | String     | 否       | 抽水状态         |
| `remark`           | String     | 否       | 备注             |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "更新成功",
	"data": {
		"id": 1,
		"name": "更新后的取水浮船1号",
		"longitude": 114.123456,
		"latitude": 30.123456,
		"capacity": 220.0,
		"powerConsumption": 55.0,
		"pumpingStatus": "maintenance",
		"pumpingStatusName": "维护中",
		"remark": "更新后的备注信息",
		"createdAt": "2024-01-01T10:00:00",
		"updatedAt": "2024-01-01T11:00:00"
	}
}
```

**失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "浮船信息不存在，ID: 1",
	"data": null
}
```

---

#### 3.2.5 删除浮船信息

**功能描述**

根据 ID 删除浮船信息（软删除）。

- **URL**: `/api/engineering-service/floating-boats/{id}`
- **HTTP 方法**: `DELETE`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### URL 路径参数

| 字段 | 类型 | 描述        |
| :--- | :--- | :---------- |
| `id` | Long | 浮船信息 ID |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "删除成功",
	"data": null
}
```

**失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "浮船信息不存在，ID: 1",
	"data": null
}
```

---

#### 3.2.6 批量删除浮船信息

**功能描述**

根据 ID 列表批量删除多个浮船信息（软删除）。

- **URL**: `/api/engineering-service/floating-boats/batch`
- **HTTP 方法**: `DELETE`
- **Content-Type**: `application/json`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 请求体 (Request Body)

| 字段  | 类型         | 是否必需 | 描述             |
| :---- | :----------- | :------- | :--------------- |
| `ids` | List`<Long>` | 是       | 浮船信息 ID 列表 |

##### 请求示例

```json
[1, 2, 3]
```

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "批量删除成功",
	"data": null
}
```

**失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "删除ID列表不能为空",
	"data": null
}
```

---

#### 3.2.7 获取所有可用浮船信息

**功能描述**

获取所有可用的浮船信息列表，通常用于下拉选择框。

- **URL**: `/api/engineering-service/floating-boats/available`
- **HTTP 方法**: `GET`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "查询成功",
	"data": [
		{
			"id": 1,
			"name": "取水浮船1号"
		},
		{
			"id": 2,
			"name": "取水浮船2号"
		}
	]
}
```

---

#### 3.2.8 统计浮船信息总数

**功能描述**

统计系统中浮船信息的总数量。

- **URL**: `/api/engineering-service/floating-boats/count`
- **HTTP 方法**: `GET`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "统计成功",
	"data": 30
}
```

---

### 3.3 监测站点管理模块 (`/api/engineering-service/monitoring-stations`)

**功能描述**

提供监测站点相关的增删改查、批量操作、统计信息查询等功能。

#### 3.3.1 分页查询监测站点列表

- **URL**: `/api/engineering-service/monitoring-stations`
- **HTTP 方法**: `GET`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 请求参数 (Query Parameters)

| 字段             | 类型    | 是否必需 | 描述                               |
| :--------------- | :------ | :------- | :--------------------------------- |
| `page`           | Integer | 否       | 当前页码 (默认: 1)                 |
| `size`           | Integer | 否       | 每页记录数 (默认: 10)              |
| `keyword`        | String  | 否       | 搜索关键词 (站点名称/编码模糊匹配) |
| `monitoringType` | String  | 否       | 监测类型筛选                       |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "查询成功",
	"data": {
		"total": 20,
		"page": 1,
		"size": 10,
		"data": [
			{
				"id": 1,
				"stationCode": "ST001",
				"name": "襄阳水厂监测站",
				"monitoringType": "water",
				"location": "东经114.3,北纬30.5",
				"isActive": true
			}
		]
	}
}
```

---

#### 3.3.2 根据 ID 查询监测站点详情

- **URL**: `/api/engineering-service/monitoring-stations/{id}`
- **HTTP 方法**: `GET`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### URL 路径参数

| 字段 | 类型 | 描述        |
| :--- | :--- | :---------- |
| `id` | Long | 监测站点 ID |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "查询成功",
	"data": {
		"id": 1,
		"stationCode": "ST001",
		"name": "襄阳水厂监测站",
		"monitoringType": "water",
		"location": "东经114.3,北纬30.5",
		"isActive": true,
		"createdAt": "2024-01-01T10:00:00",
		"updatedAt": "2024-01-01T10:00:00"
	}
}
```

---

#### 3.3.3 检查监测站点编码是否存在

- **URL**: `/api/engineering-service/monitoring-stations/check-code`
- **HTTP 方法**: `GET`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 请求参数 (Query Parameters)

| 字段          | 类型   | 是否必需 | 描述                      |
| :------------ | :----- | :------- | :------------------------ |
| `stationCode` | String | 是       | 监测站点编码              |
| `excludeId`   | Long   | 否       | 排除的站点 ID（编辑时用） |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "检查完成",
	"data": {
		"exists": false
	}
}
```

---

#### 3.3.4 获取所有可用监测站点（下拉选择）

- **URL**: `/api/engineering-service/monitoring-stations/available`
- **HTTP 方法**: `GET`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "查询成功",
	"data": [
		{
			"id": 1,
			"name": "襄阳水厂监测站"
		},
		{
			"id": 2,
			"name": "汉江水厂监测站"
		}
	]
}
```

---

#### 3.3.5 根据监测类型获取可用监测站点

- **URL**: `/api/engineering-service/monitoring-stations/available-by-type`
- **HTTP 方法**: `GET`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 请求参数 (Query Parameters)

| 字段                 | 类型   | 是否必需 | 描述         |
| :------------------- | :----- | :------- | :----------- |
| `monitoringItemCode` | String | 是       | 监测类型编码 |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "查询成功",
	"data": [
		{
			"id": 1,
			"name": "襄阳水厂监测站"
		}
	]
}
```

---

#### 3.3.6 统计监测站点总数

- **URL**: `/api/engineering-service/monitoring-stations/count`
- **HTTP 方法**: `GET`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "统计成功",
	"data": 10
}
```

---

#### 3.3.7 根据站码列表批量查询监测站点信息

- **URL**: `/api/engineering-service/monitoring-stations/by-codes`
- **HTTP 方法**: `POST`
- **Content-Type**: `application/json`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 请求体 (Request Body)

| 字段           | 类型           | 是否必需 | 描述     |
| :------------- | :------------- | :------- | :------- |
| `stationCodes` | List`<String>` | 是       | 站码列表 |

##### 请求示例

```json
["ST001", "ST002"]
```

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "查询成功",
	"data": [
		{
			"id": 1,
			"stationCode": "ST001",
			"name": "襄阳水厂监测站"
		},
		{
			"id": 2,
			"stationCode": "ST002",
			"name": "汉江水厂监测站"
		}
	]
}
```

---

#### 3.4 管道管理模块 (`/api/engineering-service/pipelines`)

**功能描述**

提供管道信息相关的增删改查、批量操作、统计信息查询等功能。

##### 3.4.1 分页查询管道信息列表

- **URL**: `/api/engineering-service/pipelines`
- **HTTP 方法**: `GET`

###### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

###### 请求参数 (Query Parameters)

| 字段              | 类型    | 是否必需 | 描述                         |
| :---------------- | :------ | :------- | :--------------------------- |
| `page`            | Integer | 否       | 当前页码 (默认: 1)           |
| `size`            | Integer | 否       | 每页记录数 (默认: 10)        |
| `keyword`         | String  | 否       | 搜索关键词（管道名称、编码） |
| `pipelineType`    | String  | 否       | 管道类型                     |
| `departmentId`    | Long    | 否       | 管理部门 ID                  |
| `managerId`       | Long    | 否       | 负责人 ID                    |
| `material`        | String  | 否       | 管道材质                     |
| `operationStatus` | String  | 否       | 运行状态                     |

###### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "查询成功",
	"data": {
		"total": 100,
		"page": 1,
		"size": 10,
		"data": [
			{
				"id": 1,
				"name": "主供水管道",
				"pipelineType": "主干",
				"departmentId": 1,
				"managerId": 2,
				"material": "球墨铸铁",
				"operationStatus": "normal",
				"length": 1200.5,
				"diameter": 800,
				"createdAt": "2024-01-01T10:00:00",
				"updatedAt": "2024-01-01T10:00:00"
			}
		]
	}
}
```

**失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "查询失败，参数无效",
	"data": null
}
```

---

##### 3.4.2 查询单个管道详情

- **URL**: `/api/engineering-service/pipelines/{id}`
- **HTTP 方法**: `GET`

###### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

###### URL 路径参数

| 字段 | 类型 | 描述    |
| :--- | :--- | :------ |
| `id` | Long | 管道 ID |

###### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "查询成功",
	"data": {
		"id": 1,
		"name": "主供水管道",
		"pipelineType": "主干",
		"departmentId": 1,
		"managerId": 2,
		"material": "球墨铸铁",
		"operationStatus": "normal",
		"length": 1200.5,
		"diameter": 800,
		"createdAt": "2024-01-01T10:00:00",
		"updatedAt": "2024-01-01T10:00:00"
	}
}
```

**失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "管道信息不存在，ID: 1",
	"data": null
}
```

---

##### 3.4.3 创建管道信息

- **URL**: `/api/engineering-service/pipelines`
- **HTTP 方法**: `POST`
- **Content-Type**: `application/json`

###### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

###### 请求体 (Request Body)

| 字段              | 类型    | 是否必需 | 描述         |
| :---------------- | :------ | :------- | :----------- |
| `name`            | String  | 是       | 管道名称     |
| `pipelineType`    | String  | 否       | 管道类型     |
| `departmentId`    | Long    | 否       | 管理部门 ID  |
| `managerId`       | Long    | 否       | 负责人 ID    |
| `material`        | String  | 否       | 管道材质     |
| `operationStatus` | String  | 否       | 运行状态     |
| `length`          | Double  | 否       | 管道长度(m)  |
| `diameter`        | Integer | 否       | 管道直径(mm) |
| `remark`          | String  | 否       | 备注         |

###### 请求示例

```json
{
	"name": "主供水管道",
	"pipelineType": "主干",
	"departmentId": 1,
	"managerId": 2,
	"material": "球墨铸铁",
	"operationStatus": "normal",
	"length": 1200.5,
	"diameter": 800,
	"remark": "城市主供水"
}
```

###### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "创建成功",
	"data": {
		"id": 2,
		"name": "主供水管道",
		"pipelineType": "主干",
		"departmentId": 1,
		"managerId": 2,
		"material": "球墨铸铁",
		"operationStatus": "normal",
		"length": 1200.5,
		"diameter": 800,
		"remark": "城市主供水",
		"createdAt": "2024-01-01T10:00:00",
		"updatedAt": "2024-01-01T10:00:00"
	}
}
```

**失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "管道名称已存在",
	"data": null
}
```

---

##### 3.4.4 更新管道信息

- **URL**: `/api/engineering-service/pipelines/{id}`
- **HTTP 方法**: `PUT`
- **Content-Type**: `application/json`

###### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

###### URL 路径参数

| 字段 | 类型 | 描述    |
| :--- | :--- | :------ |
| `id` | Long | 管道 ID |

###### 请求体 (Request Body)

| 字段              | 类型    | 是否必需 | 描述         |
| :---------------- | :------ | :------- | :----------- |
| `name`            | String  | 是       | 管道名称     |
| `pipelineType`    | String  | 否       | 管道类型     |
| `departmentId`    | Long    | 否       | 管理部门 ID  |
| `managerId`       | Long    | 否       | 负责人 ID    |
| `material`        | String  | 否       | 管道材质     |
| `operationStatus` | String  | 否       | 运行状态     |
| `length`          | Double  | 否       | 管道长度(m)  |
| `diameter`        | Integer | 否       | 管道直径(mm) |
| `remark`          | String  | 否       | 备注         |

###### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "更新成功",
	"data": {
		"id": 1,
		"name": "更新后的主供水管道",
		"pipelineType": "主干",
		"departmentId": 1,
		"managerId": 2,
		"material": "球墨铸铁",
		"operationStatus": "maintenance",
		"length": 1200.5,
		"diameter": 800,
		"remark": "维护中",
		"createdAt": "2024-01-01T10:00:00",
		"updatedAt": "2024-01-01T11:00:00"
	}
}
```

**失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "管道信息不存在，ID: 1",
	"data": null
}
```

---

##### 3.4.5 删除管道信息（软删除）

- **URL**: `/api/engineering-service/pipelines/{id}`
- **HTTP 方法**: `DELETE`

###### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

###### URL 路径参数

| 字段 | 类型 | 描述    |
| :--- | :--- | :------ |
| `id` | Long | 管道 ID |

###### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "删除成功",
	"data": null
}
```

**失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "管道信息不存在，ID: 1",
	"data": null
}
```

---

##### 3.4.6 批量删除管道信息（软删除）

- **URL**: `/api/engineering-service/pipelines/batch`
- **HTTP 方法**: `DELETE`
- **Content-Type**: `application/json`

###### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

###### 请求体 (Request Body)

| 字段  | 类型         | 是否必需 | 描述         |
| :---- | :----------- | :------- | :----------- |
| `ids` | List`<Long>` | 是       | 管道 ID 列表 |

###### 请求示例

```json
[1, 2, 3]
```

###### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "批量删除成功",
	"data": null
}
```

**失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "删除ID列表不能为空",
	"data": null
}
```

---

##### 3.4.7 获取所有可用管道信息（下拉选择）

- **URL**: `/api/engineering-service/pipelines/available`
- **HTTP 方法**: `GET`

###### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

###### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "查询成功",
	"data": [
		{
			"id": 1,
			"name": "主供水管道"
		},
		{
			"id": 2,
			"name": "备用管道"
		}
	]
}
```

---

##### 3.4.8 统计管道总数

- **URL**: `/api/engineering-service/pipelines/count`
- **HTTP 方法**: `GET`

###### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

###### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "统计成功",
	"data": 100
}
```

---

### 3.5 泵站管理模块 (`/api/engineering-service/pumping-stations`)

**功能描述**

提供泵站信息相关的增删改查、批量操作、统计信息查询等功能。

#### 3.5.1 分页查询泵站列表

**功能描述**

根据条件分页查询泵站信息，支持关键词搜索和条件筛选。

- **URL**: `/api/engineering-service/pumping-stations`
- **HTTP 方法**: `GET`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 请求参数 (Query Parameters)

| 字段            | 类型    | 是否必需 | 描述                               |
| :-------------- | :------ | :------- | :--------------------------------- |
| `page`          | Integer | 否       | 当前页码 (默认: 1)                 |
| `size`          | Integer | 否       | 每页记录数 (默认: 10)              |
| `keyword`       | String  | 否       | 搜索关键词（泵站名称、编码、地址） |
| `name`          | String  | 否       | 泵站名称                           |
| `stationType`   | String  | 否       | 泵站类型                           |
| `waterProject`  | String  | 否       | 所属供水工程                       |
| `operationMode` | String  | 否       | 运行方式                           |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "查询成功",
	"data": {
		"total": 20,
		"page": 1,
		"size": 10,
		"data": [
			{
				"id": 1,
				"name": "襄阳一号泵站",
				"stationType": "主泵站",
				"waterProject": "襄阳供水工程",
				"operationMode": "自动",
				"address": "襄阳市高新区",
				"createdAt": "2024-01-01T10:00:00",
				"updatedAt": "2024-01-01T10:00:00"
			}
		]
	}
}
```

**失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "查询失败，参数无效",
	"data": null
}
```

---

#### 3.5.2 根据 ID 查询泵站详情

**功能描述**

根据泵站 ID 查询其详细信息。

- **URL**: `/api/engineering-service/pumping-stations/{id}`
- **HTTP 方法**: `GET`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### URL 路径参数

| 字段 | 类型 | 描述    |
| :--- | :--- | :------ |
| `id` | Long | 泵站 ID |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "查询成功",
	"data": {
		"id": 1,
		"name": "襄阳一号泵站",
		"stationType": "主泵站",
		"waterProject": "襄阳供水工程",
		"operationMode": "自动",
		"address": "襄阳市高新区",
		"createdAt": "2024-01-01T10:00:00",
		"updatedAt": "2024-01-01T10:00:00"
	}
}
```

**失败响应 (404 Not Found)**

```json
{
	"code": 404,
	"message": "泵站信息不存在，ID: 1",
	"data": null
}
```

---

#### 3.5.3 创建泵站

**功能描述**

创建新的泵站信息，需提供必要的泵站信息。

- **URL**: `/api/engineering-service/pumping-stations`
- **HTTP 方法**: `POST`
- **Content-Type**: `application/json`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 请求体 (Request Body)

| 字段            | 类型   | 是否必需 | 描述         |
| :-------------- | :----- | :------- | :----------- |
| `name`          | String | 是       | 泵站名称     |
| `stationType`   | String | 否       | 泵站类型     |
| `waterProject`  | String | 否       | 所属供水工程 |
| `operationMode` | String | 否       | 运行方式     |
| `address`       | String | 否       | 地址         |

##### 请求示例

```json
{
	"name": "襄阳一号泵站",
	"stationType": "主泵站",
	"waterProject": "襄阳供水工程",
	"operationMode": "自动",
	"address": "襄阳市高新区"
}
```

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "创建成功",
	"data": {
		"id": 2,
		"name": "襄阳一号泵站",
		"stationType": "主泵站",
		"waterProject": "襄阳供水工程",
		"operationMode": "自动",
		"address": "襄阳市高新区",
		"createdAt": "2024-01-01T10:00:00",
		"updatedAt": "2024-01-01T10:00:00"
	}
}
```

**失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "泵站名称已存在",
	"data": null
}
```

---

#### 3.5.4 更新泵站信息

**功能描述**

根据 ID 更新泵站信息。

- **URL**: `/api/engineering-service/pumping-stations/{id}`
- **HTTP 方法**: `PUT`
- **Content-Type**: `application/json`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### URL 路径参数

| 字段 | 类型 | 描述    |
| :--- | :--- | :------ |
| `id` | Long | 泵站 ID |

##### 请求体 (Request Body)

| 字段            | 类型   | 是否必需 | 描述         |
| :-------------- | :----- | :------- | :----------- |
| `name`          | String | 是       | 泵站名称     |
| `stationType`   | String | 否       | 泵站类型     |
| `waterProject`  | String | 否       | 所属供水工程 |
| `operationMode` | String | 否       | 运行方式     |
| `address`       | String | 否       | 地址         |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "更新成功",
	"data": {
		"id": 1,
		"name": "更新后的泵站名称",
		"stationType": "主泵站",
		"waterProject": "襄阳供水工程",
		"operationMode": "自动",
		"address": "襄阳市高新区",
		"createdAt": "2024-01-01T10:00:00",
		"updatedAt": "2024-01-01T11:00:00"
	}
}
```

**失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "泵站信息不存在，ID: 1",
	"data": null
}
```

---

#### 3.5.5 删除泵站

**功能描述**

根据 ID 删除泵站信息（软删除）。

- **URL**: `/api/engineering-service/pumping-stations/{id}`
- **HTTP 方法**: `DELETE`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### URL 路径参数

| 字段 | 类型 | 描述    |
| :--- | :--- | :------ |
| `id` | Long | 泵站 ID |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "删除成功",
	"data": null
}
```

**失败响应 (404 Not Found)**

```json
{
	"code": 404,
	"message": "泵站信息不存在，ID: 1",
	"data": null
}
```

---

### 3.6 水库管理模块 (`/api/engineering-service/reservoirs`)

**功能描述**

提供水库信息相关的增删改查、批量操作、统计信息查询等功能。

#### 3.6.1 分页查询水库列表

**功能描述**

根据条件分页查询水库信息，支持关键词搜索和条件筛选。

- **URL**: `/api/engineering-service/reservoirs`
- **HTTP 方法**: `GET`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 请求参数 (Query Parameters)

| 字段             | 类型    | 是否必需 | 描述                           |
| :--------------- | :------ | :------- | :----------------------------- |
| `page`           | Integer | 否       | 当前页码 (默认: 1)             |
| `size`           | Integer | 否       | 每页记录数 (默认: 10)          |
| `keyword`        | String  | 否       | 搜索关键词（水库名称模糊匹配） |
| `reservoirLevel` | String  | 否       | 工程等级筛选                   |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "查询成功",
	"data": {
		"total": 50,
		"page": 1,
		"size": 10,
		"data": [
			{
				"id": 1,
				"reservoirCode": "RS001",
				"name": "襄阳水库",
				"waterProject": "襄阳水利工程",
				"longitude": 114.123456,
				"latitude": 30.123456,
				"location": "襄阳市高新区",
				"registrationNo": "RS2024001",
				"adminRegionCode": "420600",
				"engineeringGrade": "large",
				"engineeringGradeName": "大型",
				"engineeringScale": "large",
				"engineeringScaleName": "大型",
				"totalCapacity": 1000.5,
				"regulatingCapacity": 800.0,
				"deadCapacity": 100.0,
				"establishmentDate": "2020-01-01",
				"createdAt": "2024-01-01T10:00:00",
				"updatedAt": "2024-01-01T10:00:00"
			}
		]
	}
}
```

**失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "查询失败，参数无效",
	"data": null
}
```

---

#### 3.6.2 根据 ID 查询水库详情

**功能描述**

根据水库 ID 查询其详细信息。

- **URL**: `/api/engineering-service/reservoirs/{id}`
- **HTTP 方法**: `GET`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### URL 路径参数

| 字段 | 类型 | 描述    |
| :--- | :--- | :------ |
| `id` | Long | 水库 ID |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "查询成功",
	"data": {
		"id": 1,
		"reservoirCode": "RS001",
		"name": "襄阳水库",
		"waterProject": "襄阳水利工程",
		"longitude": 114.123456,
		"latitude": 30.123456,
		"location": "襄阳市高新区",
		"registrationNo": "RS2024001",
		"adminRegionCode": "420600",
		"engineeringGrade": "large",
		"engineeringGradeName": "大型",
		"engineeringScale": "large",
		"engineeringScaleName": "大型",
		"totalCapacity": 1000.5,
		"regulatingCapacity": 800.0,
		"deadCapacity": 100.0,
		"establishmentDate": "2020-01-01",
		"createdAt": "2024-01-01T10:00:00",
		"updatedAt": "2024-01-01T10:00:00"
	}
}
```

**失败响应 (404 Not Found)**

```json
{
	"code": 404,
	"message": "水库信息不存在，ID: 1",
	"data": null
}
```

---

#### 3.6.3 创建水库

**功能描述**

创建新的水库信息，需提供必要的水库信息。

- **URL**: `/api/engineering-service/reservoirs`
- **HTTP 方法**: `POST`
- **Content-Type**: `application/json`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 请求体 (Request Body)

| 字段                 | 类型       | 是否必需 | 描述                  |
| :------------------- | :--------- | :------- | :-------------------- |
| `reservoirCode`      | String     | 否       | 水库编码              |
| `name`               | String     | 是       | 水库名称              |
| `waterProject`       | String     | 否       | 所属水利工程          |
| `longitude`          | BigDecimal | 否       | 经度                  |
| `latitude`           | BigDecimal | 否       | 纬度                  |
| `location`           | String     | 否       | 水库所在位置          |
| `registrationNo`     | String     | 否       | 水库注册登记号        |
| `adminRegionCode`    | String     | 否       | 所属行政区划代码      |
| `engineeringGrade`   | String     | 否       | 工程等级              |
| `engineeringScale`   | String     | 否       | 工程规模              |
| `totalCapacity`      | BigDecimal | 否       | 总库容（万 m³）       |
| `regulatingCapacity` | BigDecimal | 否       | 调节库容（万 m³）     |
| `deadCapacity`       | BigDecimal | 否       | 死库容（万 m³）       |
| `establishmentDate`  | LocalDate  | 否       | 建库年月 (YYYY-MM-DD) |

##### 请求示例

```json
{
	"reservoirCode": "RS002",
	"name": "汉江水库",
	"waterProject": "汉江水利工程",
	"longitude": 114.234567,
	"latitude": 30.234567,
	"location": "襄阳市襄城区",
	"registrationNo": "RS2024002",
	"adminRegionCode": "420602",
	"engineeringGrade": "medium",
	"engineeringScale": "medium",
	"totalCapacity": 500.0,
	"regulatingCapacity": 400.0,
	"deadCapacity": 50.0,
	"establishmentDate": "2018-06-01"
}
```

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "创建成功",
	"data": {
		"id": 2,
		"reservoirCode": "RS002",
		"name": "汉江水库",
		"waterProject": "汉江水利工程",
		"longitude": 114.234567,
		"latitude": 30.234567,
		"location": "襄阳市襄城区",
		"registrationNo": "RS2024002",
		"adminRegionCode": "420602",
		"engineeringGrade": "medium",
		"engineeringGradeName": "中型",
		"engineeringScale": "medium",
		"engineeringScaleName": "中型",
		"totalCapacity": 500.0,
		"regulatingCapacity": 400.0,
		"deadCapacity": 50.0,
		"establishmentDate": "2018-06-01",
		"createdAt": "2024-01-01T10:00:00",
		"updatedAt": "2024-01-01T10:00:00"
	}
}
```

**失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "水库名称已存在",
	"data": null
}
```

---

#### 3.6.4 更新水库信息

**功能描述**

根据 ID 更新水库信息。

- **URL**: `/api/engineering-service/reservoirs/{id}`
- **HTTP 方法**: `PUT`
- **Content-Type**: `application/json`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### URL 路径参数

| 字段 | 类型 | 描述    |
| :--- | :--- | :------ |
| `id` | Long | 水库 ID |

##### 请求体 (Request Body)

| 字段                 | 类型       | 是否必需 | 描述                  |
| :------------------- | :--------- | :------- | :-------------------- |
| `reservoirCode`      | String     | 否       | 水库编码              |
| `name`               | String     | 是       | 水库名称              |
| `waterProject`       | String     | 否       | 所属水利工程          |
| `longitude`          | BigDecimal | 否       | 经度                  |
| `latitude`           | BigDecimal | 否       | 纬度                  |
| `location`           | String     | 否       | 水库所在位置          |
| `registrationNo`     | String     | 否       | 水库注册登记号        |
| `adminRegionCode`    | String     | 否       | 所属行政区划代码      |
| `engineeringGrade`   | String     | 否       | 工程等级              |
| `engineeringScale`   | String     | 否       | 工程规模              |
| `totalCapacity`      | BigDecimal | 否       | 总库容（万 m³）       |
| `regulatingCapacity` | BigDecimal | 否       | 调节库容（万 m³）     |
| `deadCapacity`       | BigDecimal | 否       | 死库容（万 m³）       |
| `establishmentDate`  | LocalDate  | 否       | 建库年月 (YYYY-MM-DD) |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "更新成功",
	"data": {
		"id": 1,
		"reservoirCode": "RS001",
		"name": "更新后的襄阳水库",
		"waterProject": "襄阳水利工程",
		"longitude": 114.123456,
		"latitude": 30.123456,
		"location": "襄阳市高新区",
		"registrationNo": "RS2024001",
		"adminRegionCode": "420600",
		"engineeringGrade": "large",
		"engineeringGradeName": "大型",
		"engineeringScale": "large",
		"engineeringScaleName": "大型",
		"totalCapacity": 1200.0,
		"regulatingCapacity": 900.0,
		"deadCapacity": 120.0,
		"establishmentDate": "2020-01-01",
		"createdAt": "2024-01-01T10:00:00",
		"updatedAt": "2024-01-01T11:00:00"
	}
}
```

**失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "水库信息不存在，ID: 1",
	"data": null
}
```

---

#### 3.6.5 删除水库

**功能描述**

根据 ID 删除水库信息（软删除）。

- **URL**: `/api/engineering-service/reservoirs/{id}`
- **HTTP 方法**: `DELETE`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### URL 路径参数

| 字段 | 类型 | 描述    |
| :--- | :--- | :------ |
| `id` | Long | 水库 ID |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "删除成功",
	"data": null
}
```

**失败响应 (404 Not Found)**

```json
{
	"code": 404,
	"message": "水库信息不存在，ID: 1",
	"data": null
}
```

---

#### 3.6.6 批量删除水库

**功能描述**

根据 ID 列表批量删除水库（软删除）。

- **URL**: `/api/engineering-service/reservoirs/batch`
- **HTTP 方法**: `DELETE`
- **Content-Type**: `application/json`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 请求体 (Request Body)

| 字段  | 类型         | 是否必需 | 描述         |
| :---- | :----------- | :------- | :----------- |
| `ids` | List`<Long>` | 是       | 水库 ID 列表 |

##### 请求示例

```json
[1, 2, 3]
```

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "批量删除成功",
	"data": null
}
```

**失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "删除ID列表不能为空",
	"data": null
}
```

---

#### 3.6.7 获取所有可用水库

**功能描述**

获取所有可用的水库列表，通常用于下拉选择框。

- **URL**: `/api/engineering-service/reservoirs/available`
- **HTTP 方法**: `GET`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "查询成功",
	"data": [
		{
			"id": 1,
			"reservoirCode": "RS001",
			"name": "襄阳水库"
		},
		{
			"id": 2,
			"reservoirCode": "RS002",
			"name": "汉江水库"
		}
	]
}
```

---

#### 3.6.8 统计水库总数

**功能描述**

统计系统中水库的总数量。

- **URL**: `/api/engineering-service/reservoirs/count`
- **HTTP 方法**: `GET`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "统计成功",
	"data": 50
}
```

---

### 3.7 水厂管理模块 (`/api/engineering-service/water-plants`)

**功能描述**

提供水厂信息相关的增删改查、批量操作、统计信息查询等功能。

#### 3.7.1 分页查询水厂列表

**功能描述**

分页查询水厂列表，支持按名称关键词模糊搜索。

- **URL**: `/api/engineering-service/water-plants`
- **HTTP 方法**: `GET`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 请求参数 (Query Parameters)

| 字段      | 类型    | 是否必需 | 描述                          |
| :-------- | :------ | :------- | :---------------------------- |
| `page`    | Integer | 否       | 当前页码 (默认: 1)            |
| `size`    | Integer | 否       | 每页记录数 (默认: 10)         |
| `keyword` | String  | 否       | 搜索关键词 (水厂名称模糊匹配) |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "查询成功",
	"data": {
		"total": 50,
		"page": 1,
		"size": 10,
		"data": [
			{
				"id": 1,
				"plantCode": "WP001",
				"name": "襄阳第一水厂",
				"waterProject": "襄阳供水工程",
				"departmentId": 1,
				"departmentName": "供水管理部",
				"managerId": 2,
				"managerName": "张三",
				"managerPhone": "13800138000",
				"address": "襄阳市高新区",
				"managementUnit": "襄阳水务集团",
				"longitude": 114.123456,
				"latitude": 30.123456,
				"designScale": 50000.0,
				"supplyArea": "高新区、襄城区",
				"supplyLoadRatio": 85.5,
				"supplyPopulation": 50,
				"contactPhone": "0710-12345678",
				"establishmentDate": "2020-01-01",
				"createdAt": "2024-01-01T10:00:00",
				"updatedAt": "2024-01-01T10:00:00"
			}
		]
	}
}
```

**失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "查询失败，参数无效",
	"data": null
}
```

---

#### 3.7.2 查询水厂详情

**功能描述**

根据水厂 ID 查询其详细信息。

- **URL**: `/api/engineering-service/water-plants/{id}`
- **HTTP 方法**: `GET`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### URL 路径参数

| 字段 | 类型 | 描述    |
| :--- | :--- | :------ |
| `id` | Long | 水厂 ID |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "查询成功",
	"data": {
		"id": 1,
		"plantCode": "WP001",
		"name": "襄阳第一水厂",
		"waterProject": "襄阳供水工程",
		"departmentId": 1,
		"departmentName": "供水管理部",
		"managerId": 2,
		"managerName": "张三",
		"managerPhone": "13800138000",
		"address": "襄阳市高新区",
		"managementUnit": "襄阳水务集团",
		"longitude": 114.123456,
		"latitude": 30.123456,
		"designScale": 50000.0,
		"supplyArea": "高新区、襄城区",
		"supplyLoadRatio": 85.5,
		"supplyPopulation": 50,
		"contactPhone": "0710-12345678",
		"establishmentDate": "2020-01-01",
		"createdAt": "2024-01-01T10:00:00",
		"updatedAt": "2024-01-01T10:00:00"
	}
}
```

**失败响应 (404 Not Found)**

```json
{
	"code": 404,
	"message": "水厂不存在，ID: 1",
	"data": null
}
```

---

#### 3.7.3 创建水厂

**功能描述**

创建新的水厂信息，需提供必要的水厂信息。

- **URL**: `/api/engineering-service/water-plants`
- **HTTP 方法**: `POST`
- **Content-Type**: `application/json`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 请求体 (Request Body)

| 字段                | 类型       | 是否必需 | 描述                  |
| :------------------ | :--------- | :------- | :-------------------- |
| `plantCode`         | String     | 否       | 水厂编码              |
| `name`              | String     | 是       | 水厂名称              |
| `waterProject`      | String     | 否       | 所属供水工程          |
| `departmentId`      | Long       | 否       | 管理部门 ID           |
| `managerId`         | Long       | 否       | 负责人 ID             |
| `address`           | String     | 否       | 地址                  |
| `managementUnit`    | String     | 否       | 管理单位              |
| `longitude`         | BigDecimal | 否       | 经度                  |
| `latitude`          | BigDecimal | 否       | 纬度                  |
| `designScale`       | BigDecimal | 否       | 设计规模（m³/天）     |
| `supplyArea`        | String     | 否       | 供水范围（村镇）      |
| `supplyLoadRatio`   | BigDecimal | 否       | 供水负荷率（%）       |
| `supplyPopulation`  | Integer    | 否       | 供水人口（万人）      |
| `contactPhone`      | String     | 否       | 联系电话              |
| `establishmentDate` | LocalDate  | 否       | 建站年月 (YYYY-MM-DD) |

##### 请求示例

```json
{
	"plantCode": "WP002",
	"name": "襄阳第二水厂",
	"waterProject": "襄阳供水工程",
	"departmentId": 1,
	"managerId": 3,
	"address": "襄阳市襄城区",
	"managementUnit": "襄阳水务集团",
	"longitude": 114.234567,
	"latitude": 30.234567,
	"designScale": 30000.0,
	"supplyArea": "襄城区、樊城区",
	"supplyLoadRatio": 75.0,
	"supplyPopulation": 30,
	"contactPhone": "0710-87654321",
	"establishmentDate": "2021-06-01"
}
```

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "创建成功",
	"data": {
		"id": 2,
		"plantCode": "WP002",
		"name": "襄阳第二水厂",
		"waterProject": "襄阳供水工程",
		"departmentId": 1,
		"departmentName": "供水管理部",
		"managerId": 3,
		"managerName": "李四",
		"managerPhone": "13900139000",
		"address": "襄阳市襄城区",
		"managementUnit": "襄阳水务集团",
		"longitude": 114.234567,
		"latitude": 30.234567,
		"designScale": 30000.0,
		"supplyArea": "襄城区、樊城区",
		"supplyLoadRatio": 75.0,
		"supplyPopulation": 30,
		"contactPhone": "0710-87654321",
		"establishmentDate": "2021-06-01",
		"createdAt": "2024-01-01T10:00:00",
		"updatedAt": "2024-01-01T10:00:00"
	}
}
```

**失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "水厂名称已存在",
	"data": null
}
```

---

#### 3.7.4 更新水厂信息

**功能描述**

根据 ID 更新水厂信息。

- **URL**: `/api/engineering-service/water-plants/{id}`
- **HTTP 方法**: `PUT`
- **Content-Type**: `application/json`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### URL 路径参数

| 字段 | 类型 | 描述    |
| :--- | :--- | :------ |
| `id` | Long | 水厂 ID |

##### 请求体 (Request Body)

| 字段                | 类型       | 是否必需 | 描述                  |
| :------------------ | :--------- | :------- | :-------------------- |
| `plantCode`         | String     | 否       | 水厂编码              |
| `name`              | String     | 是       | 水厂名称              |
| `waterProject`      | String     | 否       | 所属供水工程          |
| `departmentId`      | Long       | 否       | 管理部门 ID           |
| `managerId`         | Long       | 否       | 负责人 ID             |
| `address`           | String     | 否       | 地址                  |
| `managementUnit`    | String     | 否       | 管理单位              |
| `longitude`         | BigDecimal | 否       | 经度                  |
| `latitude`          | BigDecimal | 否       | 纬度                  |
| `designScale`       | BigDecimal | 否       | 设计规模（m³/天）     |
| `supplyArea`        | String     | 否       | 供水范围（村镇）      |
| `supplyLoadRatio`   | BigDecimal | 否       | 供水负荷率（%）       |
| `supplyPopulation`  | Integer    | 否       | 供水人口（万人）      |
| `contactPhone`      | String     | 否       | 联系电话              |
| `establishmentDate` | LocalDate  | 否       | 建站年月 (YYYY-MM-DD) |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "更新成功",
	"data": {
		"id": 1,
		"plantCode": "WP001",
		"name": "更新后的襄阳第一水厂",
		"waterProject": "襄阳供水工程",
		"departmentId": 1,
		"departmentName": "供水管理部",
		"managerId": 2,
		"managerName": "张三",
		"managerPhone": "13800138000",
		"address": "襄阳市高新区",
		"managementUnit": "襄阳水务集团",
		"longitude": 114.123456,
		"latitude": 30.123456,
		"designScale": 55000.0,
		"supplyArea": "高新区、襄城区、樊城区",
		"supplyLoadRatio": 90.0,
		"supplyPopulation": 60,
		"contactPhone": "0710-12345678",
		"establishmentDate": "2020-01-01",
		"createdAt": "2024-01-01T10:00:00",
		"updatedAt": "2024-01-01T11:00:00"
	}
}
```

**失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "水厂不存在，ID: 1",
	"data": null
}
```

---

#### 3.7.5 删除水厂

**功能描述**

根据 ID 删除水厂（软删除）。

- **URL**: `/api/engineering-service/water-plants/{id}`
- **HTTP 方法**: `DELETE`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### URL 路径参数

| 字段 | 类型 | 描述    |
| :--- | :--- | :------ |
| `id` | Long | 水厂 ID |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "删除成功",
	"data": null
}
```

**失败响应 (404 Not Found)**

```json
{
	"code": 404,
	"message": "水厂不存在，ID: 1",
	"data": null
}
```

---

#### 3.7.6 批量删除水厂

**功能描述**

根据 ID 列表批量删除水厂（软删除）。

- **URL**: `/api/engineering-service/water-plants/batch`
- **HTTP 方法**: `DELETE`
- **Content-Type**: `application/json`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 请求体 (Request Body)

| 字段  | 类型         | 是否必需 | 描述         |
| :---- | :----------- | :------- | :----------- |
| `ids` | List`<Long>` | 是       | 水厂 ID 列表 |

##### 请求示例

```json
[1, 2, 3]
```

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "批量删除成功",
	"data": null
}
```

**失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "删除ID列表不能为空",
	"data": null
}
```

---

#### 3.7.7 获取所有可用水厂

**功能描述**

获取所有可用的水厂列表，通常用于下拉选择框。

- **URL**: `/api/engineering-service/water-plants/available`
- **HTTP 方法**: `GET`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "查询成功",
	"data": [
		{
			"id": 1,
			"plantCode": "WP001",
			"name": "襄阳第一水厂"
		},
		{
			"id": 2,
			"plantCode": "WP002",
			"name": "襄阳第二水厂"
		}
	]
}
```

---

#### 3.7.8 统计水厂总数

**功能描述**

统计系统中水厂的总数量。

- **URL**: `/api/engineering-service/water-plants/count`
- **HTTP 方法**: `GET`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "统计成功",
	"data": 50
}
```

---

### 3.8 村庄管理模块 (`/api/engineering-service/villages`)

**功能描述**

提供村庄信息相关的增删改查、批量操作、统计信息查询等功能。

#### 3.8.1 分页查询村庄信息列表

**功能描述**

根据条件分页查询村庄信息，支持关键词搜索（村庄名称）和条件筛选。

- **URL**: `/api/engineering-service/villages`
- **HTTP 方法**: `GET`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 请求参数 (Query Parameters)

| 字段      | 类型    | 是否必需 | 描述                          |
| :-------- | :------ | :------- | :---------------------------- |
| `page`    | Integer | 否       | 当前页码 (默认: 1)            |
| `size`    | Integer | 否       | 每页记录数 (默认: 10)         |
| `keyword` | String  | 否       | 搜索关键词 (村庄名称模糊匹配) |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "查询成功",
	"data": {
		"total": 100,
		"page": 1,
		"size": 10,
		"data": [
			{
				"id": 1,
				"name": "张家村",
				"longitude": 114.123456,
				"latitude": 30.123456,
				"administrativeCode": "420100",
				"currentPopulation": 500,
				"remark": "山区村庄，主要依靠地下水供水",
				"createdAt": "2024-01-01T10:00:00",
				"updatedAt": "2024-01-01T10:00:00"
			}
		]
	}
}
```

**失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "查询失败，参数无效",
	"data": null
}
```

---

#### 3.8.2 根据 ID 查询村庄信息详情

**功能描述**

根据村庄信息 ID 查询其详细信息。

- **URL**: `/api/engineering-service/villages/{id}`
- **HTTP 方法**: `GET`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### URL 路径参数

| 字段 | 类型 | 描述        |
| :--- | :--- | :---------- |
| `id` | Long | 村庄信息 ID |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "查询成功",
	"data": {
		"id": 1,
		"name": "张家村",
		"longitude": 114.123456,
		"latitude": 30.123456,
		"administrativeCode": "420100",
		"currentPopulation": 500,
		"remark": "山区村庄，主要依靠地下水供水",
		"createdAt": "2024-01-01T10:00:00",
		"updatedAt": "2024-01-01T10:00:00"
	}
}
```

**失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "村庄信息不存在，ID: 1",
	"data": null
}
```

---

#### 3.8.3 创建村庄信息

**功能描述**

创建新的村庄信息，需提供必要的村庄信息。

- **URL**: `/api/engineering-service/villages`
- **HTTP 方法**: `POST`
- **Content-Type**: `application/json`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 请求体 (Request Body)

| 字段                 | 类型       | 是否必需 | 描述               |
| :------------------- | :--------- | :------- | :----------------- |
| `name`               | String     | 是       | 村庄名称           |
| `longitude`          | BigDecimal | 否       | 村庄地理位置-经度  |
| `latitude`           | BigDecimal | 否       | 村庄地理位置-纬度  |
| `administrativeCode` | String     | 否       | 行政区划代码       |
| `currentPopulation`  | Integer    | 否       | 现状人口数量（人） |
| `remark`             | String     | 否       | 备注信息           |

##### 请求示例

```json
{
	"name": "李家村",
	"longitude": 114.234567,
	"latitude": 30.234567,
	"administrativeCode": "420100",
	"currentPopulation": 800,
	"remark": "平原村庄，自来水覆盖率高"
}
```

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "创建成功",
	"data": {
		"id": 2,
		"name": "李家村",
		"longitude": 114.234567,
		"latitude": 30.234567,
		"administrativeCode": "420100",
		"currentPopulation": 800,
		"remark": "平原村庄，自来水覆盖率高",
		"createdAt": "2024-01-01T10:00:00",
		"updatedAt": "2024-01-01T10:00:00"
	}
}
```

**失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "村庄名称已存在",
	"data": null
}
```

---

#### 3.8.4 更新村庄信息

**功能描述**

根据 ID 更新村庄信息。

- **URL**: `/api/engineering-service/villages/{id}`
- **HTTP 方法**: `PUT`
- **Content-Type**: `application/json`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### URL 路径参数

| 字段 | 类型 | 描述        |
| :--- | :--- | :---------- |
| `id` | Long | 村庄信息 ID |

##### 请求体 (Request Body)

| 字段                 | 类型       | 是否必需 | 描述               |
| :------------------- | :--------- | :------- | :----------------- |
| `name`               | String     | 是       | 村庄名称           |
| `longitude`          | BigDecimal | 否       | 村庄地理位置-经度  |
| `latitude`           | BigDecimal | 否       | 村庄地理位置-纬度  |
| `administrativeCode` | String     | 否       | 行政区划代码       |
| `currentPopulation`  | Integer    | 否       | 现状人口数量（人） |
| `remark`             | String     | 否       | 备注信息           |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "更新成功",
	"data": {
		"id": 1,
		"name": "更新后的张家村",
		"longitude": 114.123456,
		"latitude": 30.123456,
		"administrativeCode": "420100",
		"currentPopulation": 600,
		"remark": "更新后的备注信息",
		"createdAt": "2024-01-01T10:00:00",
		"updatedAt": "2024-01-01T11:00:00"
	}
}
```

**失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "村庄信息不存在，ID: 1",
	"data": null
}
```

---

#### 3.8.5 删除村庄信息

**功能描述**

根据 ID 删除村庄信息（软删除）。

- **URL**: `/api/engineering-service/villages/{id}`
- **HTTP 方法**: `DELETE`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### URL 路径参数

| 字段 | 类型 | 描述        |
| :--- | :--- | :---------- |
| `id` | Long | 村庄信息 ID |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "删除成功",
	"data": null
}
```

**失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "村庄信息不存在，ID: 1",
	"data": null
}
```

---

#### 3.8.6 批量删除村庄信息

**功能描述**

根据 ID 列表批量删除多个村庄信息（软删除）。

- **URL**: `/api/engineering-service/villages/batch`
- **HTTP 方法**: `DELETE`
- **Content-Type**: `application/json`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 请求体 (Request Body)

| 字段  | 类型         | 是否必需 | 描述             |
| :---- | :----------- | :------- | :--------------- |
| `ids` | List`<Long>` | 是       | 村庄信息 ID 列表 |

##### 请求示例

```json
[1, 2, 3]
```

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "批量删除成功",
	"data": null
}
```

**失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "删除ID列表不能为空",
	"data": null
}
```

---

#### 3.8.7 获取所有可用村庄信息

**功能描述**

获取所有可用的村庄信息列表，通常用于下拉选择框。

- **URL**: `/api/engineering-service/villages/available`
- **HTTP 方法**: `GET`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "查询成功",
	"data": [
		{
			"id": 1,
			"name": "张家村",
			"longitude": 114.123456,
			"latitude": 30.123456,
			"administrativeCode": "420100",
			"currentPopulation": 500,
			"remark": "山区村庄，主要依靠地下水供水"
		},
		{
			"id": 2,
			"name": "李家村",
			"longitude": 114.234567,
			"latitude": 30.234567,
			"administrativeCode": "420100",
			"currentPopulation": 800,
			"remark": "平原村庄，自来水覆盖率高"
		}
	]
}
```

---

#### 3.8.8 统计村庄信息总数

**功能描述**

统计系统中村庄信息的总数量。

- **URL**: `/api/engineering-service/villages/count`
- **HTTP 方法**: `GET`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "统计成功",
	"data": 100
}
```

---

## 4. 管理信息服务模块 (`/api/management-info`)

**功能描述**

管理信息服务模块负责处理人员信息和部门信息的管理，提供人员信息的 CRUD 操作、部门信息的查询与更新、部门树形结构获取等功能。所有接口需要 `business:manage` 权限。

### 4.1 人员信息管理

#### 4.1.1 分页查询人员信息列表

**功能描述**

分页查询人员信息列表，支持按姓名、工号、部门等条件筛选。

- **URL**: `/api/management-info/personnel`
- **HTTP 方法**: `GET`
- **权限要求**: `business:manage`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 请求参数 (Query Parameters)

| 字段         | 类型    | 是否必需 | 描述                  |
| :----------- | :------ | :------- | :-------------------- |
| `page`       | Integer | 否       | 当前页码 (默认: 1)    |
| `size`       | Integer | 否       | 每页记录数 (默认: 10) |
| `name`       | String  | 否       | 人员姓名模糊搜索      |
| `employeeNo` | String  | 否       | 工号精确搜索          |
| `department` | String  | 否       | 部门名称模糊搜索      |
| `position`   | String  | 否       | 职位模糊搜索          |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "查询成功",
	"data": {
		"total": 50,
		"page": 1,
		"size": 10,
		"data": [
			{
				"id": 1,
				"name": "张三",
				"employeeNo": "EMP001",
				"department": "研发部",
				"position": "高级工程师",
				"phone": "13800138001",
				"email": "zhangsan@example.com",
				"hireDate": "2020-01-15",
				"status": "在职"
			}
		]
	}
}
```

---

#### 4.1.2 根据 ID 查询人员详情

**功能描述**

根据人员 ID 获取详细信息。

- **URL**: `/api/management-info/personnel/{id}`
- **HTTP 方法**: `GET`
- **权限要求**: `business:manage`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### URL 路径参数

| 字段 | 类型 | 描述        |
| :--- | :--- | :---------- |
| `id` | Long | 人员信息 ID |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "查询成功",
	"data": {
		"id": 1,
		"name": "张三",
		"employeeNo": "EMP001",
		"department": "研发部",
		"position": "高级工程师",
		"phone": "13800138001",
		"email": "zhangsan@example.com",
		"hireDate": "2020-01-15",
		"birthDate": "1990-05-20",
		"address": "北京市朝阳区",
		"status": "在职",
		"remark": "技术专家"
	}
}
```

**失败响应 (404 Not Found)**

```json
{
	"code": 404,
	"message": "人员信息不存在",
	"data": null
}
```

---

#### 4.1.3 创建人员信息

**功能描述**

创建新的人员信息记录。

- **URL**: `/api/management-info/personnel`
- **HTTP 方法**: `POST`
- **Content-Type**: `application/json`
- **权限要求**: `business:manage`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 请求体 (Request Body)

| 字段         | 类型   | 是否必需 | 描述     |
| :----------- | :----- | :------- | :------- |
| `name`       | String | 是       | 人员姓名 |
| `employeeNo` | String | 是       | 工号     |
| `department` | String | 否       | 所属部门 |
| `position`   | String | 否       | 职位     |
| `phone`      | String | 否       | 联系电话 |
| `email`      | String | 否       | 邮箱地址 |
| `hireDate`   | String | 否       | 入职日期 |
| `birthDate`  | String | 否       | 出生日期 |
| `address`    | String | 否       | 联系地址 |
| `status`     | String | 否       | 在职状态 |
| `remark`     | String | 否       | 备注信息 |

##### 请求示例

```json
{
	"name": "李四",
	"employeeNo": "EMP002",
	"department": "市场部",
	"position": "销售经理",
	"phone": "13800138002",
	"email": "lisi@example.com",
	"hireDate": "2021-03-10",
	"status": "在职"
}
```

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "人员信息创建成功",
	"data": {
		"id": 2,
		"name": "李四",
		"employeeNo": "EMP002",
		"department": "市场部",
		"position": "销售经理",
		"phone": "13800138002",
		"email": "lisi@example.com",
		"hireDate": "2021-03-10",
		"status": "在职"
	}
}
```

**失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "工号已存在",
	"data": null
}
```

---

#### 4.1.4 更新人员信息

**功能描述**

更新指定 ID 的人员信息。

- **URL**: `/api/management-info/personnel/{id}`
- **HTTP 方法**: `PUT`
- **Content-Type**: `application/json`
- **权限要求**: `business:manage`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### URL 路径参数

| 字段 | 类型 | 描述        |
| :--- | :--- | :---------- |
| `id` | Long | 人员信息 ID |

##### 请求体 (Request Body)

| 字段         | 类型   | 是否必需 | 描述     |
| :----------- | :----- | :------- | :------- |
| `name`       | String | 是       | 人员姓名 |
| `employeeNo` | String | 是       | 工号     |
| `department` | String | 否       | 所属部门 |
| `position`   | String | 否       | 职位     |
| `phone`      | String | 否       | 联系电话 |
| `email`      | String | 否       | 邮箱地址 |
| `hireDate`   | String | 否       | 入职日期 |
| `birthDate`  | String | 否       | 出生日期 |
| `address`    | String | 否       | 联系地址 |
| `status`     | String | 否       | 在职状态 |
| `remark`     | String | 否       | 备注信息 |

##### 请求示例

```json
{
	"name": "张三",
	"employeeNo": "EMP001",
	"department": "技术部",
	"position": "技术总监",
	"phone": "13800138001",
	"email": "zhangsan@example.com",
	"hireDate": "2020-01-15",
	"status": "在职",
	"remark": "晋升为技术总监"
}
```

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "人员信息更新成功",
	"data": {
		"id": 1,
		"name": "张三",
		"employeeNo": "EMP001",
		"department": "技术部",
		"position": "技术总监",
		"phone": "13800138001",
		"email": "zhangsan@example.com",
		"hireDate": "2020-01-15",
		"status": "在职",
		"remark": "晋升为技术总监"
	}
}
```

---

#### 4.1.5 删除人员信息

**功能描述**

删除指定 ID 的人员信息。

- **URL**: `/api/management-info/personnel/{id}`
- **HTTP 方法**: `DELETE`
- **权限要求**: `business:manage`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### URL 路径参数

| 字段 | 类型 | 描述        |
| :--- | :--- | :---------- |
| `id` | Long | 人员信息 ID |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "人员信息删除成功",
	"data": null
}
```

**失败响应 (404 Not Found)**

```json
{
	"code": 404,
	"message": "人员信息不存在",
	"data": null
}
```

---

#### 4.1.6 批量删除人员信息

**功能描述**

根据 ID 列表批量删除多个人员信息。

- **URL**: `/api/management-info/personnel/batch`
- **HTTP 方法**: `DELETE`
- **Content-Type**: `application/json`
- **权限要求**: `business:manage`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 请求体 (Request Body)

| 字段  | 类型         | 是否必需 | 描述             |
| :---- | :----------- | :------- | :--------------- |
| `ids` | List`<Long>` | 是       | 人员信息 ID 列表 |

##### 请求示例

```json
[1, 2, 3]
```

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "批量删除成功",
	"data": null
}
```

**失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "删除ID列表不能为空",
	"data": null
}
```

---

### 4.2 部门信息管理

#### 4.2.1 获取部门树形结构

**功能描述**

获取所有部门的树形结构数据，用于展示部门层级关系。

- **URL**: `/api/management-info/departments/tree`
- **HTTP 方法**: `GET`
- **权限要求**: `business:manage`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "查询成功",
	"data": [
		{
			"id": 1,
			"name": "总公司",
			"children": [
				{
					"id": 2,
					"name": "技术部",
					"children": [
						{
							"id": 3,
							"name": "研发组"
						},
						{
							"id": 4,
							"name": "测试组"
						}
					]
				},
				{
					"id": 5,
					"name": "市场部",
					"children": []
				}
			]
		}
	]
}
```

---

#### 4.2.2 获取部门列表

**功能描述**

获取所有部门列表，不包含层级结构。

- **URL**: `/api/management-info/departments`
- **HTTP 方法**: `GET`
- **权限要求**: `business:manage`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "查询成功",
	"data": [
		{
			"id": 1,
			"name": "总公司",
			"parentId": null,
			"sortOrder": 1,
			"status": "active"
		},
		{
			"id": 2,
			"name": "技术部",
			"parentId": 1,
			"sortOrder": 1,
			"status": "active"
		}
	]
}
```

---

#### 4.2.3 更新部门信息

**功能描述**

更新指定部门的详细信息。

- **URL**: `/api/management-info/departments/{id}`
- **HTTP 方法**: `PUT`
- **Content-Type**: `application/json`
- **权限要求**: `business:manage`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### URL 路径参数

| 字段 | 类型 | 描述    |
| :--- | :--- | :------ |
| `id` | Long | 部门 ID |

##### 请求体 (Request Body)

| 字段        | 类型    | 是否必需 | 描述       |
| :---------- | :------ | :------- | :--------- |
| `name`      | String  | 是       | 部门名称   |
| `parentId`  | Long    | 否       | 父部门 ID  |
| `sortOrder` | Integer | 否       | 排序序号   |
| `status`    | String  | 否       | 部门状态   |
| `manager`   | String  | 否       | 部门负责人 |
| `phone`     | String  | 否       | 联系电话   |
| `address`   | String  | 否       | 办公地址   |
| `remark`    | String  | 否       | 备注信息   |

##### 请求示例

```json
{
	"name": "技术部",
	"parentId": 1,
	"sortOrder": 1,
	"status": "active",
	"manager": "王经理",
	"phone": "010-12345678",
	"address": "北京市朝阳区科技园A座",
	"remark": "负责产品研发和技术支持"
}
```

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "部门信息更新成功",
	"data": {
		"id": 2,
		"name": "技术部",
		"parentId": 1,
		"sortOrder": 1,
		"status": "active",
		"manager": "王经理",
		"phone": "010-12345678",
		"address": "北京市朝阳区科技园A座",
		"remark": "负责产品研发和技术支持"
	}
}
```

**失败响应 (404 Not Found)**

```json
{
	"code": 404,
	"message": "部门信息不存在",
	"data": null
}
```

---

#### 4.2.4 统计部门信息

**功能描述**

统计部门相关的汇总信息，如总人数、部门数量等。

- **URL**: `/api/management-info/statistics`
- **HTTP 方法**: `GET`
- **权限要求**: `business:manage`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "统计成功",
	"data": {
		"totalPersonnel": 120,
		"totalDepartments": 8,
		"activePersonnel": 115,
		"departmentStats": [
			{
				"departmentName": "技术部",
				"personnelCount": 45
			},
			{
				"departmentName": "市场部",
				"personnelCount": 30
			}
		]
	}
}
```

---

## 5. 预警管理模块

**功能描述**

预警管理模块负责处理预警记录的全生命周期管理，包括预警记录的创建、查询、解除、删除等操作，以及预警统计分析功能。所有接口需要 `business:operate` 权限。

### 5.1 预警记录管理 (`/api/warning/records`)

**功能描述**

提供预警记录相关的增删改查操作、统计信息查询、趋势分析等功能。

#### 5.1.1 分页查询预警记录列表

**功能描述**

分页查询预警记录列表，支持按预警地点、类型、等级、状态、所属工程和时间范围等多条件筛选。

- **URL**: `/api/warning/records`
- **HTTP 方法**: `GET`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 请求参数 (Query Parameters)

| 字段              | 类型          | 是否必需 | 描述                                 |
| :---------------- | :------------ | :------- | :----------------------------------- |
| `page`            | Integer       | 否       | 当前页码 (默认: 1)                   |
| `size`            | Integer       | 否       | 每页记录数 (默认: 10)                |
| `warningLocation` | String        | 否       | 预警地点筛选                         |
| `warningType`     | String        | 否       | 预警类型筛选                         |
| `warningLevel`    | String        | 否       | 预警等级筛选                         |
| `warningStatus`   | String        | 否       | 预警状态筛选                         |
| `projectName`     | String        | 否       | 所属工程筛选                         |
| `startTime`       | LocalDateTime | 否       | 开始时间 (格式: yyyy-MM-dd HH:mm:ss) |
| `endTime`         | LocalDateTime | 否       | 结束时间 (格式: yyyy-MM-dd HH:mm:ss) |
| `sort`            | String        | 否       | 排序字段 (例如: occurred_at,desc)    |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "查询成功",
	"data": {
		"total": 100,
		"page": 1,
		"size": 10,
		"data": [
			{
				"id": 1,
				"warningLocation": "襄阳水库",
				"warningType": "water_level",
				"warningLevel": "yellow",
				"warningStatus": "active",
				"projectName": "襄阳水利工程",
				"occurredAt": "2024-01-01T10:00:00",
				"resolvedAt": null,
				"description": "水位超过警戒线",
				"resolutionDescription": null,
				"createdAt": "2024-01-01T10:00:00",
				"updatedAt": "2024-01-01T10:00:00"
			}
		]
	}
}
```

**失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "查询失败，参数无效",
	"data": null
}
```

---

#### 5.1.2 创建预警记录

**功能描述**

创建新的预警记录，需提供必要的预警信息。

- **URL**: `/api/warning/records`
- **HTTP 方法**: `POST`
- **Content-Type**: `application/json`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 请求体 (Request Body)

| 字段              | 类型          | 是否必需 | 描述                                     |
| :---------------- | :------------ | :------- | :--------------------------------------- |
| `warningLocation` | String        | 是       | 预警地点                                 |
| `warningType`     | String        | 是       | 预警类型                                 |
| `warningLevel`    | String        | 是       | 预警等级                                 |
| `projectName`     | String        | 否       | 所属工程                                 |
| `occurredAt`      | LocalDateTime | 是       | 预警发生时间 (格式: yyyy-MM-dd HH:mm:ss) |
| `description`     | String        | 是       | 预警描述                                 |

##### 请求示例

```json
{
	"warningLocation": "襄阳水库",
	"warningType": "water_level",
	"warningLevel": "yellow",
	"projectName": "襄阳水利工程",
	"occurredAt": "2024-01-01T10:00:00",
	"description": "水位超过警戒线，需要密切关注"
}
```

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "预警记录创建成功",
	"data": {
		"id": 2,
		"warningLocation": "襄阳水库",
		"warningType": "water_level",
		"warningLevel": "yellow",
		"warningStatus": "active",
		"projectName": "襄阳水利工程",
		"occurredAt": "2024-01-01T10:00:00",
		"resolvedAt": null,
		"description": "水位超过警戒线，需要密切关注",
		"resolutionDescription": null,
		"createdAt": "2024-01-01T10:00:00",
		"updatedAt": "2024-01-01T10:00:00"
	}
}
```

**失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "创建失败，预警信息不完整",
	"data": null
}
```

---

#### 5.1.3 解除预警

**功能描述**

解除指定的预警记录，并记录解除信息。

- **URL**: `/api/warning/records/{id}/resolve`
- **HTTP 方法**: `PUT`
- **Content-Type**: `application/json`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### URL 路径参数

| 字段 | 类型 | 描述        |
| :--- | :--- | :---------- |
| `id` | Long | 预警记录 ID |

##### 请求体 (Request Body)

| 字段                    | 类型          | 是否必需 | 描述                                 |
| :---------------------- | :------------ | :------- | :----------------------------------- |
| `resolvedAt`            | LocalDateTime | 是       | 解除时间 (格式: yyyy-MM-dd HH:mm:ss) |
| `resolutionDescription` | String        | 是       | 解除说明                             |

##### 请求示例

```json
{
	"resolvedAt": "2024-01-01T12:00:00",
	"resolutionDescription": "水位已恢复正常，预警解除"
}
```

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "预警解除成功",
	"data": null
}
```

**失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "预警记录不存在，ID: 1",
	"data": null
}
```

---

#### 5.1.4 删除预警记录

**功能描述**

删除指定的预警记录（软删除）。

- **URL**: `/api/warning/records/{id}`
- **HTTP 方法**: `DELETE`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### URL 路径参数

| 字段 | 类型 | 描述        |
| :--- | :--- | :---------- |
| `id` | Long | 预警记录 ID |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "预警记录删除成功",
	"data": null
}
```

**失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "预警记录不存在，ID: 1",
	"data": null
}
```

---

#### 5.1.5 获取预警统计信息

**功能描述**

获取预警的整体统计数据，包括总数、各状态数量等。

- **URL**: `/api/warning/records/statistics`
- **HTTP 方法**: `GET`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "查询成功",
	"data": [
		{
			"label": "总预警数",
			"value": 150,
			"type": "total"
		},
		{
			"label": "活跃预警",
			"value": 25,
			"type": "active"
		},
		{
			"label": "已解除预警",
			"value": 125,
			"type": "resolved"
		}
	]
}
```

---

#### 5.1.6 获取各等级预警数量统计

**功能描述**

按预警等级分类统计预警数量，用于图表展示。

- **URL**: `/api/warning/records/level-statistics`
- **HTTP 方法**: `GET`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "查询成功",
	"data": [
		{
			"level": "red",
			"levelName": "红色预警",
			"count": 5
		},
		{
			"level": "orange",
			"levelName": "橙色预警",
			"count": 12
		},
		{
			"level": "yellow",
			"levelName": "黄色预警",
			"count": 30
		},
		{
			"level": "blue",
			"levelName": "蓝色预警",
			"count": 103
		}
	]
}
```

---

#### 5.1.7 获取预警趋势数据

**功能描述**

获取指定时间周期内的预警数量变化趋势，用于趋势分析图表。

- **URL**: `/api/warning/records/trend`
- **HTTP 方法**: `GET`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 请求参数 (Query Parameters)

| 字段     | 类型    | 是否必需 | 描述                 |
| :------- | :------ | :------- | :------------------- |
| `period` | String  | 否       | 时间周期 (默认: day) |
| `days`   | Integer | 否       | 统计天数 (默认: 30)  |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "查询成功",
	"data": [
		{
			"date": "2024-01-01",
			"count": 5
		},
		{
			"date": "2024-01-02",
			"count": 8
		},
		{
			"date": "2024-01-03",
			"count": 3
		}
	]
}
```

---

#### 5.1.8 获取预警地点列表

**功能描述**

从现有预警记录中获取不重复的预警地点列表，用于前端下拉选择。

- **URL**: `/api/warning/records/locations`
- **HTTP 方法**: `GET`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "查询成功",
	"data": ["襄阳水库", "汉江水厂", "高新区泵站", "樊城区监测站", "襄城区供水点"]
}
```

---

### 5.2 预警阈值管理 (`/api/warning/thresholds`)

**功能描述**

提供预警阈值相关的增删改查操作、统计信息查询、重复性检查等功能。

#### 5.2.1 分页查询预警阈值列表

**功能描述**

分页查询预警阈值列表，支持按关键词、测点名称、监测项等条件筛选和排序。

- **URL**: `/api/warning/thresholds`
- **HTTP 方法**: `GET`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 请求参数 (Query Parameters)

| 字段             | 类型    | 是否必需 | 描述                             |
| :--------------- | :------ | :------- | :------------------------------- |
| `page`           | Integer | 否       | 当前页码 (默认: 1)               |
| `size`           | Integer | 否       | 每页记录数 (默认: 10)            |
| `keyword`        | String  | 否       | 搜索关键词 (测点名称模糊匹配)    |
| `stationName`    | String  | 否       | 测点名称筛选                     |
| `monitoringItem` | String  | 否       | 监测项筛选                       |
| `sort`           | String  | 否       | 排序字段 (例如: created_at,desc) |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "查询成功",
	"data": {
		"total": 50,
		"page": 1,
		"size": 10,
		"data": [
			{
				"id": 1,
				"stationName": "襄阳水库监测站",
				"monitoringItem": "water_level",
				"monitoringItemName": "水位",
				"upperUpperLimit": 15.0,
				"upperLimit": 12.0,
				"lowerLimit": 3.0,
				"lowerLowerLimit": 1.0,
				"unit": "m",
				"isActive": true,
				"createdAt": "2024-01-01T10:00:00",
				"updatedAt": "2024-01-01T10:00:00"
			}
		]
	}
}
```

**失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "查询失败，参数无效",
	"data": null
}
```

---

#### 5.2.2 根据 ID 查询预警阈值详情

**功能描述**

根据预警阈值 ID 查询其详细信息。

- **URL**: `/api/warning/thresholds/{id}`
- **HTTP 方法**: `GET`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### URL 路径参数

| 字段 | 类型 | 描述        |
| :--- | :--- | :---------- |
| `id` | Long | 预警阈值 ID |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "查询成功",
	"data": {
		"id": 1,
		"stationName": "襄阳水库监测站",
		"monitoringItem": "water_level",
		"monitoringItemName": "水位",
		"upperUpperLimit": 15.0,
		"upperLimit": 12.0,
		"lowerLimit": 3.0,
		"lowerLowerLimit": 1.0,
		"unit": "m",
		"isActive": true,
		"createdAt": "2024-01-01T10:00:00",
		"updatedAt": "2024-01-01T10:00:00"
	}
}
```

**失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "预警阈值不存在，ID: 1",
	"data": null
}
```

---

#### 5.2.3 创建预警阈值

**功能描述**

创建新的预警阈值，需提供必要的阈值配置信息。

- **URL**: `/api/warning/thresholds`
- **HTTP 方法**: `POST`
- **Content-Type**: `application/json`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 请求体 (Request Body)

| 字段              | 类型       | 是否必需 | 描述     |
| :---------------- | :--------- | :------- | :------- |
| `stationName`     | String     | 是       | 测点名称 |
| `monitoringItem`  | String     | 是       | 监测项   |
| `upperUpperLimit` | BigDecimal | 否       | 上上限   |
| `upperLimit`      | BigDecimal | 否       | 上限     |
| `lowerLimit`      | BigDecimal | 否       | 下限     |
| `lowerLowerLimit` | BigDecimal | 否       | 下下限   |
| `unit`            | String     | 否       | 单位     |
| `isActive`        | Boolean    | 否       | 是否启用 |

##### 请求示例

```json
{
	"stationName": "襄阳水库监测站",
	"monitoringItem": "water_level",
	"upperUpperLimit": 15.0,
	"upperLimit": 12.0,
	"lowerLimit": 3.0,
	"lowerLowerLimit": 1.0,
	"unit": "m",
	"isActive": true
}
```

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "预警指标创建成功",
	"data": {
		"id": 2,
		"stationName": "襄阳水库监测站",
		"monitoringItem": "water_level",
		"monitoringItemName": "水位",
		"upperUpperLimit": 15.0,
		"upperLimit": 12.0,
		"lowerLimit": 3.0,
		"lowerLowerLimit": 1.0,
		"unit": "m",
		"isActive": true,
		"createdAt": "2024-01-01T10:00:00",
		"updatedAt": "2024-01-01T10:00:00"
	}
}
```

**失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "测点和监测项组合已存在",
	"data": null
}
```

---

#### 5.2.4 更新预警阈值

**功能描述**

根据 ID 更新预警阈值信息。

- **URL**: `/api/warning/thresholds/{id}`
- **HTTP 方法**: `PUT`
- **Content-Type**: `application/json`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### URL 路径参数

| 字段 | 类型 | 描述        |
| :--- | :--- | :---------- |
| `id` | Long | 预警阈值 ID |

##### 请求体 (Request Body)

| 字段              | 类型       | 是否必需 | 描述     |
| :---------------- | :--------- | :------- | :------- |
| `stationName`     | String     | 是       | 测点名称 |
| `monitoringItem`  | String     | 是       | 监测项   |
| `upperUpperLimit` | BigDecimal | 否       | 上上限   |
| `upperLimit`      | BigDecimal | 否       | 上限     |
| `lowerLimit`      | BigDecimal | 否       | 下限     |
| `lowerLowerLimit` | BigDecimal | 否       | 下下限   |
| `unit`            | String     | 否       | 单位     |
| `isActive`        | Boolean    | 是       | 是否启用 |

##### 请求示例

```json
{
	"stationName": "襄阳水库监测站",
	"monitoringItem": "water_level",
	"upperUpperLimit": 16.0,
	"upperLimit": 13.0,
	"lowerLimit": 2.5,
	"lowerLowerLimit": 0.8,
	"unit": "m",
	"isActive": true
}
```

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "预警指标更新成功",
	"data": {
		"id": 1,
		"stationName": "襄阳水库监测站",
		"monitoringItem": "water_level",
		"monitoringItemName": "水位",
		"upperUpperLimit": 16.0,
		"upperLimit": 13.0,
		"lowerLimit": 2.5,
		"lowerLowerLimit": 0.8,
		"unit": "m",
		"isActive": true,
		"createdAt": "2024-01-01T10:00:00",
		"updatedAt": "2024-01-01T11:00:00"
	}
}
```

**失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "预警阈值不存在，ID: 1",
	"data": null
}
```

---

#### 5.2.5 删除预警阈值

**功能描述**

删除指定的预警阈值。

- **URL**: `/api/warning/thresholds/{id}`
- **HTTP 方法**: `DELETE`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### URL 路径参数

| 字段 | 类型 | 描述        |
| :--- | :--- | :---------- |
| `id` | Long | 预警阈值 ID |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "预警指标删除成功",
	"data": null
}
```

**失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "预警阈值不存在，ID: 1",
	"data": null
}
```

---

#### 5.2.6 获取所有可用预警阈值

**功能描述**

获取所有可用的预警阈值列表，通常用于下拉选择框或其他组件。

- **URL**: `/api/warning/thresholds/active`
- **HTTP 方法**: `GET`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "查询成功",
	"data": [
		{
			"id": 1,
			"stationName": "襄阳水库监测站",
			"monitoringItem": "water_level",
			"monitoringItemName": "水位",
			"upperUpperLimit": 15.0,
			"upperLimit": 12.0,
			"lowerLimit": 3.0,
			"lowerLowerLimit": 1.0,
			"unit": "m",
			"isActive": true,
			"createdAt": "2024-01-01T10:00:00",
			"updatedAt": "2024-01-01T10:00:00"
		},
		{
			"id": 2,
			"stationName": "汉江水厂监测站",
			"monitoringItem": "water_quality",
			"monitoringItemName": "水质",
			"upperUpperLimit": 8.5,
			"upperLimit": 8.0,
			"lowerLimit": 6.5,
			"lowerLowerLimit": 6.0,
			"unit": "pH",
			"isActive": true,
			"createdAt": "2024-01-01T10:00:00",
			"updatedAt": "2024-01-01T10:00:00"
		}
	]
}
```

---

#### 5.2.7 获取预警阈值统计信息

**功能描述**

获取预警阈值的统计数据，包括总数、启用数量、按监测项分组的数量等。

- **URL**: `/api/warning/thresholds/statistics`
- **HTTP 方法**: `GET`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "查询成功",
	"data": {
		"totalCount": 20,
		"activeCount": 18,
		"byMonitoringItem": {
			"water_level": 8,
			"water_quality": 5,
			"flow_rate": 3,
			"temperature": 2
		}
	}
}
```

---

#### 5.2.8 检查测点和监测项组合是否存在

**功能描述**

检查指定的测点和监测项组合是否已存在，用于防止重复创建相同的预警阈值。

- **URL**: `/api/warning/thresholds/check-duplicate`
- **HTTP 方法**: `GET`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 请求参数 (Query Parameters)

| 字段             | 类型   | 是否必需 | 描述                       |
| :--------------- | :----- | :------- | :------------------------- |
| `stationName`    | String | 是       | 测点名称                   |
| `monitoringItem` | String | 是       | 监测项                     |
| `excludeId`      | Long   | 否       | 排除的阈值 ID (编辑时使用) |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "检查完成",
	"data": {
		"exists": false
	}
}
```

**组合已存在时 (200 OK)**

```json
{
	"code": 200,
	"message": "检查完成",
	"data": {
		"exists": true
	}
}
```

---

## 6. 实时监测模块

**功能描述**

实时监测模块负责处理各类监测数据的采集、存储、查询、分析和管理，包括流量监测、水位监测、水质监测、雨情监测等核心监测数据。模块提供数据查询、图表展示、数据导入导出、统计分析等功能，为水资源的动态管理和科学决策提供数据支撑。所有接口需要 `business:operate` 权限。

### 6.1 流量监测数据管理 (`/api/monitoring`)

**功能描述**

提供流量监测数据相关的查询、统计、导入导出等功能，包括实时流量数据查询、历史数据分析、图表数据获取和批量数据管理。

#### 6.1.1 分页查询流量监测数据列表

**功能描述**

分页查询流量监测数据列表，支持按监测站点、时间范围、数据质量、采集方式、数据来源等多条件筛选和排序。

- **URL**: `/api/monitoring/flow-data`
- **HTTP 方法**: `GET`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 请求参数 (Query Parameters)

| 字段               | 类型          | 是否必需 | 描述                                  |
| :----------------- | :------------ | :------- | :------------------------------------ |
| `page`             | Integer       | 否       | 当前页码 (默认: 1)                    |
| `size`             | Integer       | 否       | 每页记录数 (默认: 10)                 |
| `stationId`        | Long          | 否       | 监测站点 ID                           |
| `startTime`        | LocalDateTime | 否       | 开始时间 (格式: yyyy-MM-dd HH:mm:ss)  |
| `endTime`          | LocalDateTime | 否       | 结束时间 (格式: yyyy-MM-dd HH:mm:ss)  |
| `dataQuality`      | Integer       | 否       | 数据质量(1:正常,2:异常,3:缺失)        |
| `collectionMethod` | String        | 否       | 采集方式(AUTO:自动,MANUAL:手动)       |
| `dataSource`       | String        | 否       | 数据来源设备                          |
| `sort`             | String        | 否       | 排序字段 (默认: monitoring_time,desc) |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "查询成功",
	"data": {
		"total": 150,
		"page": 1,
		"size": 10,
		"data": [
			{
				"id": 1,
				"stationId": 1,
				"stationName": "襄阳水厂流量站",
				"stationCode": "00000001",
				"monitoringTime": "2024-01-01 14:30:00",
				"flowRate": 12.5,
				"cumulativeFlow": 1580.75,
				"dataQuality": 1,
				"dataQualityText": "正常",
				"collectionMethod": "AUTO",
				"dataSource": "流量计A01",
				"remarks": "正常监测数据",
				"createdAt": "2024-01-01 14:30:05",
				"updatedAt": "2024-01-01 14:30:05"
			}
		]
	}
}
```

**失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "查询失败: 参数无效",
	"data": null
}
```

---

#### 6.1.2 获取流量监测图表数据

**功能描述**

获取指定站点和时间范围的流量图表数据，支持瞬时流量和累计流量的分析展示，提供不同时间间隔的数据聚合。

- **URL**: `/api/monitoring/flow-chart-data`
- **HTTP 方法**: `GET`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 请求参数 (Query Parameters)

| 字段        | 类型          | 是否必需 | 描述                                                                 |
| :---------- | :------------ | :------- | :------------------------------------------------------------------- |
| `stationId` | Long          | 是       | 监测站点 ID                                                          |
| `startTime` | LocalDateTime | 否       | 开始时间 (格式: yyyy-MM-dd HH:mm:ss)                                 |
| `endTime`   | LocalDateTime | 否       | 结束时间 (格式: yyyy-MM-dd HH:mm:ss)                                 |
| `interval`  | String        | 否       | 时间间隔(hour:小时,day:天,month:月) (默认: hour)                     |
| `dataType`  | String        | 否       | 数据类型(flowRate:瞬时流量,cumulativeFlow:累计流量) (默认: flowRate) |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "查询成功",
	"data": {
		"labels": [
			"2024-01-01 10:00",
			"2024-01-01 11:00",
			"2024-01-01 12:00",
			"2024-01-01 13:00",
			"2024-01-01 14:00"
		],
		"values": [11.2, 12.5, 13.1, 12.8, 12.0],
		"datasetName": "瞬时流量",
		"stationName": "襄阳水厂流量站",
		"interval": "hour",
		"unit": "m³/s"
	}
}
```

**失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "查询失败: 必须提供监测站点ID",
	"data": null
}
```

---

#### 6.1.3 导出流量监测数据

**功能描述**

根据查询条件将流量监测数据导出为 CSV 文件，支持大批量数据的导出操作，最大支持导出 10000 条记录。

- **URL**: `/api/monitoring/flow-data/export`
- **HTTP 方法**: `POST`
- **Content-Type**: `application/json`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 请求体 (Request Body)

| 字段               | 类型          | 是否必需 | 描述                                    |
| :----------------- | :------------ | :------- | :-------------------------------------- |
| `stationId`        | Long          | 否       | 监测站点 ID                             |
| `startTime`        | LocalDateTime | 否       | 开始时间 (格式: yyyy-MM-dd HH:mm:ss)    |
| `endTime`          | LocalDateTime | 否       | 结束时间 (格式: yyyy-MM-dd HH:mm:ss)    |
| `dataQuality`      | Integer       | 否       | 数据质量(1:正常,2:异常,3:缺失)          |
| `collectionMethod` | String        | 否       | 采集方式(AUTO:自动,MANUAL:手动)         |
| `dataSource`       | String        | 否       | 数据来源设备                            |
| `size`             | Integer       | 否       | 导出数量限制 (默认: 10000，最大: 10000) |
| `sort`             | String        | 否       | 排序字段 (默认: monitoring_time,desc)   |

##### 请求示例

```json
{
	"stationId": 1,
	"startTime": "2024-01-01 00:00:00",
	"endTime": "2024-01-31 23:59:59",
	"dataQuality": 1,
	"size": 5000
}
```

##### 响应 (Response)

**成功响应 (200 OK)**

返回 CSV 文件的二进制数据流，响应头包含文件下载信息。

```
Response Headers:
Content-Type: application/octet-stream
Content-Disposition: attachment; filename=flow_monitoring_data_1704097200000.csv
Content-Length: 524288
```

**失败响应 (400 Bad Request)**

```json
{
	"success": false,
	"message": "导出失败: 查询条件无效"
}
```

---

#### 6.1.4 导入流量监测数据

**功能描述**

批量导入 Excel 解析后的流量监测数据，支持数据验证、重复检查和错误处理，返回详细的导入结果统计。

- **URL**: `/api/monitoring/flow-data/import`
- **HTTP 方法**: `POST`
- **Content-Type**: `application/json`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 请求体 (Request Body)

请求体为流量数据导入 DTO 数组，每个对象包含以下字段：

| 字段             | 类型       | 是否必需 | 描述                                     |
| :--------------- | :--------- | :------- | :--------------------------------------- |
| `rowNumber`      | Integer    | 是       | Excel 行号 (从 2 开始计数)               |
| `monitoringTime` | String     | 是       | 监测时间 (格式: yyyy-MM-dd HH:mm:ss)     |
| `stationCode`    | String     | 是       | 监测站码 (8 位字符串)                    |
| `instantFlow`    | BigDecimal | 否       | 瞬时流量 (m³/s，范围: -1000.0 ~ 10000.0) |
| `cumulativeFlow` | BigDecimal | 否       | 累计流量 (m³，范围: 0.0 ~ 999999999.0)   |

##### 请求示例

```json
[
	{
		"rowNumber": 2,
		"monitoringTime": "2024-01-01 10:00:00",
		"stationCode": "00000001",
		"instantFlow": 12.5,
		"cumulativeFlow": 1580.75
	},
	{
		"rowNumber": 3,
		"monitoringTime": "2024-01-01 11:00:00",
		"stationCode": "00000001",
		"instantFlow": 13.2,
		"cumulativeFlow": 1628.35
	}
]
```

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "数据导入成功",
	"data": {
		"totalRows": 100,
		"successRows": 95,
		"errorRows": 5,
		"duplicateRows": 3,
		"errors": [
			{
				"rowNumber": 5,
				"stationCode": "00000999",
				"error": "监测站点不存在"
			},
			{
				"rowNumber": 12,
				"stationCode": "00000001",
				"error": "瞬时流量超出范围"
			}
		]
	}
}
```

**部分成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "数据导入完成，成功95条，失败5条",
	"data": {
		"totalRows": 100,
		"successRows": 95,
		"errorRows": 5,
		"duplicateRows": 3,
		"errors": [
			{
				"rowNumber": 5,
				"stationCode": "00000999",
				"error": "监测站点不存在"
			}
		]
	}
}
```

**全部失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "数据导入失败，请检查数据格式",
	"data": {
		"totalRows": 10,
		"successRows": 0,
		"errorRows": 10,
		"duplicateRows": 0,
		"errors": [
			{
				"rowNumber": 2,
				"stationCode": "invalid",
				"error": "站码格式错误，必须是8位字符串"
			}
		]
	}
}
```

**参数错误响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "参数错误: 导入数据不能为空",
	"data": null
}
```

---

### 6.2 降雨监测数据管理 (`/api/monitoring`)

**功能描述**

提供降雨监测数据相关的查询、统计、导入等功能，包括实时降雨数据查询、历史数据分析、图表数据获取和批量数据管理。

#### 6.2.1 分页查询降雨监测数据列表

**功能描述**

分页查询降雨监测数据列表，支持按监测站点、时间范围、数据质量、采集方式、数据来源等多条件筛选和排序。

- **URL**: `/api/monitoring/rainfall-data`
- **HTTP 方法**: `GET`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 请求参数 (Query Parameters)

| 字段               | 类型          | 是否必需 | 描述                                  |
| :----------------- | :------------ | :------- | :------------------------------------ |
| `page`             | Integer       | 否       | 当前页码 (默认: 1)                    |
| `size`             | Integer       | 否       | 每页记录数 (默认: 10)                 |
| `stationId`        | Long          | 否       | 监测站点 ID                           |
| `startTime`        | LocalDateTime | 否       | 开始时间 (格式: yyyy-MM-dd HH:mm:ss)  |
| `endTime`          | LocalDateTime | 否       | 结束时间 (格式: yyyy-MM-dd HH:mm:ss)  |
| `dataQuality`      | Integer       | 否       | 数据质量(1:正常,2:异常,3:缺失)        |
| `collectionMethod` | String        | 否       | 采集方式(AUTO:自动,MANUAL:手动)       |
| `dataSource`       | String        | 否       | 数据来源设备                          |
| `sort`             | String        | 否       | 排序字段 (默认: monitoring_time,desc) |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "查询成功",
	"data": {
		"total": 150,
		"page": 1,
		"size": 10,
		"data": [
			{
				"id": 1,
				"stationId": 1,
				"stationName": "襄阳雨量站",
				"stationCode": "00000001",
				"monitoringTime": "2024-01-01 14:30:00",
				"rainfall": 12.5,
				"cumulativeRainfall": 158.75,
				"rainfallIntensity": 25.0,
				"dataQuality": 1,
				"dataQualityText": "正常",
				"collectionMethod": "AUTO",
				"dataSource": "雨量计A01",
				"remarks": "正常监测数据",
				"createdAt": "2024-01-01 14:30:05",
				"updatedAt": "2024-01-01 14:30:05"
			}
		]
	}
}
```

**失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "查询失败: 参数无效",
	"data": null
}
```

---

#### 6.2.2 获取降雨监测图表数据

**功能描述**

获取指定站点和时间范围的降雨图表数据，支持降雨量、累计降雨量和降雨强度的分析展示，提供不同时间间隔的数据聚合。

- **URL**: `/api/monitoring/rainfall-chart-data`
- **HTTP 方法**: `GET`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 请求参数 (Query Parameters)

| 字段        | 类型          | 是否必需 | 描述                                                     |
| :---------- | :------------ | :------- | :------------------------------------------------------- |
| `stationId` | Long          | 否       | 监测站点 ID                                              |
| `startTime` | LocalDateTime | 否       | 开始时间 (格式: yyyy-MM-dd HH:mm:ss)                     |
| `endTime`   | LocalDateTime | 否       | 结束时间 (格式: yyyy-MM-dd HH:mm:ss)                     |
| `interval`  | String        | 否       | 时间间隔(hour:小时,day:天,week:周,month:月) (默认: hour) |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "查询成功",
	"data": {
		"labels": [
			"2024-01-01 10:00",
			"2024-01-01 11:00",
			"2024-01-01 12:00",
			"2024-01-01 13:00",
			"2024-01-01 14:00"
		],
		"rainfallData": [0.0, 2.5, 5.2, 3.8, 1.0],
		"cumulativeData": [120.5, 123.0, 128.2, 132.0, 133.0],
		"intensityData": [0.0, 5.0, 10.4, 7.6, 2.0],
		"stationName": "襄阳雨量站",
		"interval": "hour",
		"unit": "mm"
	}
}
```

**失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "查询失败: 参数无效",
	"data": null
}
```

---

#### 6.2.3 导入降雨监测数据

**功能描述**

批量导入 Excel 解析后的降雨监测数据，支持数据验证、重复检查和错误处理，返回详细的导入结果统计。

- **URL**: `/api/monitoring/rainfall-data/import`
- **HTTP 方法**: `POST`
- **Content-Type**: `application/json`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 请求参数 (Query Parameters)

| 字段        | 类型 | 是否必需 | 描述        |
| :---------- | :--- | :------- | :---------- |
| `stationId` | Long | 是       | 监测站点 ID |

##### 请求体 (Request Body)

请求体为降雨数据导入 DTO 数组，每个对象包含以下字段：

| 字段                 | 类型       | 是否必需 | 描述                                 |
| :------------------- | :--------- | :------- | :----------------------------------- |
| `monitoringTime`     | String     | 是       | 监测时间 (格式: yyyy-MM-dd HH:mm:ss) |
| `rainfall`           | BigDecimal | 否       | 降雨量 (mm，范围: 0.0 ~ 1000.0)      |
| `cumulativeRainfall` | BigDecimal | 否       | 累计降雨量 (mm，范围: 0.0 ~ 10000.0) |
| `rainfallIntensity`  | BigDecimal | 否       | 降雨强度 (mm/h，范围: 0.0 ~ 500.0)   |
| `dataQuality`        | Integer    | 否       | 数据质量(1:正常,2:异常,3:缺失)       |
| `collectionMethod`   | String     | 否       | 采集方式(AUTO:自动,MANUAL:手动)      |
| `dataSource`         | String     | 否       | 数据来源设备                         |
| `remarks`            | String     | 否       | 备注信息                             |

##### 请求示例

```json
[
	{
		"monitoringTime": "2024-01-01 10:00:00",
		"rainfall": 2.5,
		"cumulativeRainfall": 125.5,
		"rainfallIntensity": 5.0,
		"dataQuality": 1,
		"collectionMethod": "AUTO",
		"dataSource": "雨量计A01",
		"remarks": "正常监测"
	},
	{
		"monitoringTime": "2024-01-01 11:00:00",
		"rainfall": 5.2,
		"cumulativeRainfall": 130.7,
		"rainfallIntensity": 10.4,
		"dataQuality": 1,
		"collectionMethod": "AUTO",
		"dataSource": "雨量计A01",
		"remarks": "正常监测"
	}
]
```

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "数据导入成功",
	"data": {
		"successCount": 95,
		"failCount": 5,
		"duplicateCount": 3,
		"totalCount": 100,
		"failedData": [
			{
				"monitoringTime": "2024-01-01 12:00:00",
				"error": "降雨量超出范围"
			},
			{
				"monitoringTime": "2024-01-01 13:00:00",
				"error": "监测时间格式错误"
			}
		]
	}
}
```

**部分成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "数据导入完成，成功95条，失败5条",
	"data": {
		"successCount": 95,
		"failCount": 5,
		"duplicateCount": 3,
		"totalCount": 100,
		"failedData": [
			{
				"monitoringTime": "2024-01-01 12:00:00",
				"error": "降雨量超出范围"
			}
		]
	}
}
```

**全部失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "数据导入失败，请检查数据格式",
	"data": {
		"successCount": 0,
		"failCount": 10,
		"duplicateCount": 0,
		"totalCount": 10,
		"failedData": [
			{
				"monitoringTime": "invalid-time",
				"error": "监测时间格式错误"
			}
		]
	}
}
```

**参数错误响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "参数错误: 监测站点ID不能为空",
	"data": null
}
```

---

### 6.3 水情监测数据管理 (`/api/monitoring/water-condition`)

**功能描述**

提供水情监测数据相关的查询、统计、导入导出等功能，包括实时水情数据查询、历史数据分析、图表数据获取和批量数据管理。水情监测涵盖水位高度、蓄水量、超汛限、入库流量、出库流量等关键指标。

#### 6.3.1 分页查询水情监测数据列表

**功能描述**

分页查询水情监测数据列表，支持按监测站点、时间范围、数据质量、采集方式、数据来源等多条件筛选和排序。

- **URL**: `/api/monitoring/water-condition`
- **HTTP 方法**: `GET`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 请求参数 (Query Parameters)

| 字段               | 类型          | 是否必需 | 描述                                  |
| :----------------- | :------------ | :------- | :------------------------------------ |
| `page`             | Integer       | 否       | 当前页码 (默认: 1)                    |
| `size`             | Integer       | 否       | 每页记录数 (默认: 10)                 |
| `stationId`        | Long          | 否       | 监测站点 ID                           |
| `stationName`      | String        | 否       | 监测站点名称 (模糊查询)               |
| `startTime`        | LocalDateTime | 否       | 开始时间 (格式: yyyy-MM-dd HH:mm:ss)  |
| `endTime`          | LocalDateTime | 否       | 结束时间 (格式: yyyy-MM-dd HH:mm:ss)  |
| `dataQuality`      | Integer       | 否       | 数据质量(1:正常,2:异常,3:缺失)        |
| `collectionMethod` | String        | 否       | 采集方式(AUTO:自动,MANUAL:手动)       |
| `dataSource`       | String        | 否       | 数据来源设备                          |
| `sort`             | String        | 否       | 排序字段 (默认: monitoring_time,desc) |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "查询成功",
	"data": {
		"total": 120,
		"page": 1,
		"size": 10,
		"data": [
			{
				"id": 1,
				"stationId": 1,
				"stationName": "两河口水情监测站",
				"monitoringTime": "2024-01-01 14:30:00",
				"waterLevel": 165.25,
				"storageCapacity": 1250.6,
				"floodLimitDiff": 2.75,
				"inflow": 45.8,
				"outflow": 38.2,
				"dataQuality": 1,
				"dataQualityDesc": "正常",
				"collectionMethod": "AUTO",
				"collectionMethodDesc": "自动采集",
				"dataSource": "水位计A01",
				"remark": "正常监测数据"
			}
		]
	}
}
```

**失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "查询失败: 参数无效",
	"data": null
}
```

---

#### 6.3.2 获取水情监测图表数据

**功能描述**

获取指定站点和时间范围的水情图表数据，支持水位高度、蓄水量、入库流量、出库流量等指标的分析展示，提供不同时间间隔的数据聚合。

- **URL**: `/api/monitoring/water-condition-chart-data`
- **HTTP 方法**: `GET`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 请求参数 (Query Parameters)

| 字段        | 类型          | 是否必需 | 描述                                                                                         |
| :---------- | :------------ | :------- | :------------------------------------------------------------------------------------------- |
| `stationId` | Long          | 是       | 监测站点 ID                                                                                  |
| `startTime` | LocalDateTime | 否       | 开始时间 (格式: yyyy-MM-dd HH:mm:ss)                                                         |
| `endTime`   | LocalDateTime | 否       | 结束时间 (格式: yyyy-MM-dd HH:mm:ss)                                                         |
| `interval`  | String        | 否       | 时间间隔(hour:小时,day:天,month:月) (默认: hour)                                             |
| `dataType`  | String        | 否       | 数据类型(waterLevel:水位,storage:蓄水量,inflow:入库流量,outflow:出库流量) (默认: waterLevel) |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "查询成功",
	"data": {
		"labels": [
			"2024-01-01 10:00",
			"2024-01-01 11:00",
			"2024-01-01 12:00",
			"2024-01-01 13:00",
			"2024-01-01 14:00"
		],
		"values": [164.8, 165.1, 165.25, 165.0, 164.9],
		"datasetName": "水位高度",
		"stationName": "两河口水情监测站",
		"interval": "hour",
		"unit": "m"
	}
}
```

**失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "查询失败: 必须提供监测站点ID",
	"data": null
}
```

---

#### 6.3.3 导出水情监测数据

**功能描述**

根据查询条件将水情监测数据导出为 CSV 文件，支持大批量数据的导出操作，最大支持导出 10000 条记录。

- **URL**: `/api/monitoring/water-condition/export`
- **HTTP 方法**: `POST`
- **Content-Type**: `application/json`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 请求体 (Request Body)

| 字段               | 类型          | 是否必需 | 描述                                    |
| :----------------- | :------------ | :------- | :-------------------------------------- |
| `stationId`        | Long          | 否       | 监测站点 ID                             |
| `stationName`      | String        | 否       | 监测站点名称 (模糊查询)                 |
| `startTime`        | LocalDateTime | 否       | 开始时间 (格式: yyyy-MM-dd HH:mm:ss)    |
| `endTime`          | LocalDateTime | 否       | 结束时间 (格式: yyyy-MM-dd HH:mm:ss)    |
| `dataQuality`      | Integer       | 否       | 数据质量(1:正常,2:异常,3:缺失)          |
| `collectionMethod` | String        | 否       | 采集方式(AUTO:自动,MANUAL:手动)         |
| `dataSource`       | String        | 否       | 数据来源设备                            |
| `size`             | Integer       | 否       | 导出数量限制 (默认: 10000，最大: 10000) |
| `sort`             | String        | 否       | 排序字段 (默认: monitoring_time,desc)   |

##### 请求示例

```json
{
	"stationId": 1,
	"startTime": "2024-01-01 00:00:00",
	"endTime": "2024-01-31 23:59:59",
	"dataQuality": 1,
	"size": 5000
}
```

##### 响应 (Response)

**成功响应 (200 OK)**

返回 CSV 文件的二进制数据流，响应头包含文件下载信息。

```
Response Headers:
Content-Type: application/octet-stream
Content-Disposition: attachment; filename=water_condition_monitoring_data_1704097200000.csv
Content-Length: 524288
```

**失败响应 (400 Bad Request)**

```json
{
	"success": false,
	"message": "导出失败: 查询条件无效"
}
```

---

#### 6.3.4 导入水情监测数据

**功能描述**

批量导入 Excel 解析后的水情监测数据，支持数据验证、重复检查和错误处理，返回详细的导入结果统计。

- **URL**: `/api/monitoring/water-condition/import`
- **HTTP 方法**: `POST`
- **Content-Type**: `application/json`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 请求体 (Request Body)

请求体为水情数据导入 DTO 数组，每个对象包含以下字段：

| 字段              | 类型       | 是否必需 | 描述                                 |
| :---------------- | :--------- | :------- | :----------------------------------- |
| `rowNumber`       | Integer    | 是       | Excel 行号 (从 2 开始计数)           |
| `monitoringTime`  | String     | 是       | 监测时间 (格式: yyyy-MM-dd HH:mm:ss) |
| `stationCode`     | String     | 是       | 监测站码 (8 位字符串)                |
| `waterLevel`      | BigDecimal | 否       | 水位高度 (m，范围: 0.0 ~ 2000.0)     |
| `storageCapacity` | BigDecimal | 否       | 蓄水量 (10⁴m³，范围: 0.0 ~ 999999.0) |
| `floodLimitDiff`  | BigDecimal | 否       | 超汛限 (m，范围: -100.0 ~ 100.0)     |
| `inflow`          | BigDecimal | 否       | 入库流量 (m³/s，范围: 0.0 ~ 10000.0) |
| `outflow`         | BigDecimal | 否       | 出库流量 (m³/s，范围: 0.0 ~ 10000.0) |

##### 请求示例

```json
[
	{
		"rowNumber": 2,
		"monitoringTime": "2024-01-01 10:00:00",
		"stationCode": "24083437",
		"waterLevel": 165.25,
		"storageCapacity": 1250.6,
		"floodLimitDiff": 2.75,
		"inflow": 45.8,
		"outflow": 38.2
	},
	{
		"rowNumber": 3,
		"monitoringTime": "2024-01-01 11:00:00",
		"stationCode": "24083437",
		"waterLevel": 165.3,
		"storageCapacity": 1252.1,
		"floodLimitDiff": 2.8,
		"inflow": 46.2,
		"outflow": 38.5
	}
]
```

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "数据导入成功",
	"data": {
		"totalRows": 100,
		"successRows": 95,
		"errorRows": 5,
		"duplicateRows": 3,
		"errors": [
			{
				"rowNumber": 5,
				"stationCode": "99999999",
				"error": "监测站点不存在"
			},
			{
				"rowNumber": 12,
				"waterLevel": "9999.99",
				"error": "水位高度超出范围"
			}
		]
	}
}
```

**部分成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "数据导入完成，成功95条，失败5条",
	"data": {
		"totalRows": 100,
		"successRows": 95,
		"errorRows": 5,
		"duplicateRows": 3,
		"errors": [
			{
				"rowNumber": 8,
				"storageCapacity": "-100.0",
				"error": "蓄水量不能为负数"
			}
		]
	}
}
```

**全部失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "数据导入失败，请检查数据格式",
	"data": {
		"totalRows": 10,
		"successRows": 0,
		"errorRows": 10,
		"duplicateRows": 0,
		"errors": [
			{
				"rowNumber": 2,
				"monitoringTime": "invalid-time",
				"error": "监测时间格式错误"
			}
		]
	}
}
```

---

### 6.4 水位监测数据管理 (`/api/monitoring`)

**功能描述**

提供水位监测数据相关的查询、统计、导入导出等功能，包括实时水位数据查询、历史数据分析、图表数据获取和批量数据管理。

#### 6.4.1 分页查询水位监测数据列表

**功能描述**

分页查询水位监测数据列表，支持按监测站点、时间范围、数据质量、采集方式、数据来源等多条件筛选和排序。

- **URL**: `/api/monitoring/water-level-data`
- **HTTP 方法**: `GET`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 请求参数 (Query Parameters)

| 字段               | 类型          | 是否必需 | 描述                                  |
| :----------------- | :------------ | :------- | :------------------------------------ |
| `page`             | Integer       | 否       | 当前页码 (默认: 1)                    |
| `size`             | Integer       | 否       | 每页记录数 (默认: 10)                 |
| `stationId`        | Long          | 否       | 监测站点 ID                           |
| `startTime`        | LocalDateTime | 否       | 开始时间 (格式: yyyy-MM-dd HH:mm:ss)  |
| `endTime`          | LocalDateTime | 否       | 结束时间 (格式: yyyy-MM-dd HH:mm:ss)  |
| `dataQuality`      | Integer       | 否       | 数据质量(1:正常,2:异常,3:缺失)        |
| `collectionMethod` | String        | 否       | 采集方式(AUTO:自动,MANUAL:手动)       |
| `dataSource`       | String        | 否       | 数据来源设备                          |
| `sort`             | String        | 否       | 排序字段 (默认: monitoring_time,desc) |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "查询成功",
	"data": {
		"total": 150,
		"page": 1,
		"size": 10,
		"data": [
			{
				"id": 1,
				"stationId": 1,
				"stationName": "襄阳水厂水位站",
				"stationCode": "00000001",
				"monitoringTime": "2024-01-01 14:30:00",
				"waterLevel": 85.25,
				"dataQuality": 1,
				"dataQualityText": "正常",
				"collectionMethod": "AUTO",
				"dataSource": "水位计B01",
				"remarks": "正常监测数据",
				"createdAt": "2024-01-01 14:30:05",
				"updatedAt": "2024-01-01 14:30:05"
			}
		]
	}
}
```

**失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "查询失败: 参数无效",
	"data": null
}
```

---

#### 6.4.2 获取水位监测图表数据

**功能描述**

获取指定站点和时间范围的水位图表数据，支持水位变化趋势的分析展示，提供不同时间间隔的数据聚合。

- **URL**: `/api/monitoring/water-level-chart-data`
- **HTTP 方法**: `GET`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 请求参数 (Query Parameters)

| 字段        | 类型          | 是否必需 | 描述                                                     |
| :---------- | :------------ | :------- | :------------------------------------------------------- |
| `stationId` | Long          | 否       | 监测站点 ID                                              |
| `startTime` | LocalDateTime | 否       | 开始时间 (格式: yyyy-MM-dd HH:mm:ss)                     |
| `endTime`   | LocalDateTime | 否       | 结束时间 (格式: yyyy-MM-dd HH:mm:ss)                     |
| `interval`  | String        | 否       | 时间间隔(hour:小时,day:天,week:周,month:月) (默认: hour) |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "查询成功",
	"data": {
		"labels": [
			"2024-01-01 10:00",
			"2024-01-01 11:00",
			"2024-01-01 12:00",
			"2024-01-01 13:00",
			"2024-01-01 14:00"
		],
		"values": [85.2, 85.5, 85.8, 85.6, 85.3],
		"datasetName": "水位",
		"stationName": "襄阳水厂水位站",
		"interval": "hour",
		"unit": "m"
	}
}
```

**失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "查询失败: 请求参数无效",
	"data": null
}
```

---

#### 6.4.3 导出水位监测数据

**功能描述**

根据查询条件将水位监测数据导出为 CSV 文件，支持大批量数据的导出操作，最大支持导出 10000 条记录。

- **URL**: `/api/monitoring/water-level-data/export`
- **HTTP 方法**: `POST`
- **Content-Type**: `application/json`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 请求体 (Request Body)

| 字段               | 类型          | 是否必需 | 描述                                    |
| :----------------- | :------------ | :------- | :-------------------------------------- |
| `stationId`        | Long          | 否       | 监测站点 ID                             |
| `startTime`        | LocalDateTime | 否       | 开始时间 (格式: yyyy-MM-dd HH:mm:ss)    |
| `endTime`          | LocalDateTime | 否       | 结束时间 (格式: yyyy-MM-dd HH:mm:ss)    |
| `dataQuality`      | Integer       | 否       | 数据质量(1:正常,2:异常,3:缺失)          |
| `collectionMethod` | String        | 否       | 采集方式(AUTO:自动,MANUAL:手动)         |
| `dataSource`       | String        | 否       | 数据来源设备                            |
| `size`             | Integer       | 否       | 导出数量限制 (默认: 10000，最大: 10000) |
| `sort`             | String        | 否       | 排序字段 (默认: monitoring_time,desc)   |

##### 请求示例

```json
{
	"stationId": 1,
	"startTime": "2024-01-01 00:00:00",
	"endTime": "2024-01-31 23:59:59",
	"dataQuality": 1,
	"size": 5000
}
```

##### 响应 (Response)

**成功响应 (200 OK)**

返回 CSV 文件的二进制数据流，响应头包含文件下载信息。

```
Response Headers:
Content-Type: text/csv
Content-Disposition: attachment; filename=water_level_data_1704097200000.csv
Content-Length: 524288
```

**失败响应 (400 Bad Request)**

```json
{
	"success": false,
	"message": "导出失败: 查询条件无效"
}
```

---

#### 6.4.4 导入水位监测数据

**功能描述**

批量导入 Excel 解析后的水位监测数据，支持数据验证、重复检查和错误处理，返回详细的导入结果统计。

- **URL**: `/api/monitoring/water-level-data/import`
- **HTTP 方法**: `POST`
- **Content-Type**: `application/json`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 请求体 (Request Body)

请求体为水位数据导入 DTO 数组，每个对象包含以下字段：

| 字段             | 类型       | 是否必需 | 描述                                 |
| :--------------- | :--------- | :------- | :----------------------------------- |
| `rowNumber`      | Integer    | 是       | Excel 行号 (从 2 开始计数)           |
| `monitoringTime` | String     | 是       | 监测时间 (格式: yyyy-MM-dd HH:mm:ss) |
| `stationCode`    | String     | 是       | 监测站码 (8 位字符串)                |
| `waterLevel`     | BigDecimal | 是       | 水位值 (m，范围: -1000.0 ~ 10000.0)  |

##### 请求示例

```json
[
	{
		"rowNumber": 2,
		"monitoringTime": "2024-01-01 10:00:00",
		"stationCode": "00000001",
		"waterLevel": 85.25
	},
	{
		"rowNumber": 3,
		"monitoringTime": "2024-01-01 11:00:00",
		"stationCode": "00000001",
		"waterLevel": 85.5
	}
]
```

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "数据导入成功",
	"data": {
		"totalRows": 100,
		"successRows": 95,
		"errorRows": 5,
		"duplicateRows": 3,
		"errors": [
			{
				"rowNumber": 5,
				"stationCode": "00000999",
				"error": "监测站点不存在"
			},
			{
				"rowNumber": 12,
				"waterLevel": "10001.0",
				"error": "水位值超出有效范围"
			}
		]
	}
}
```

**部分成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "数据导入完成，成功95条，失败5条",
	"data": {
		"totalRows": 100,
		"successRows": 95,
		"errorRows": 5,
		"duplicateRows": 3,
		"errors": [
			{
				"rowNumber": 8,
				"waterLevel": "-1001.0",
				"error": "水位值不能小于-1000.0"
			}
		]
	}
}
```

**全部失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "数据导入失败，请检查数据格式",
	"data": {
		"totalRows": 10,
		"successRows": 0,
		"errorRows": 10,
		"duplicateRows": 0,
		"errors": [
			{
				"rowNumber": 2,
				"monitoringTime": "invalid-time",
				"error": "监测时间格式错误"
			}
		]
	}
}
```

---

### 6.5 水质监测数据管理 (`/api/monitoring`)

**功能描述**

提供水质监测数据相关的查询、统计、导入导出等功能，支持 8 种水质监测项目（水温 WT、浊度 TU、PH 值 PH、电导率 EC、溶解氧 DO、氨氮 AN、化学需氧量 COD、余氯 RC）的数据管理。

#### 6.5.1 分页查询水质监测数据列表

**功能描述**

分页查询水质监测数据列表，支持按监测站点、时间范围、监测项目代码、数据质量、采集方式、数据来源等多条件筛选和排序。

- **URL**: `/api/monitoring/water-quality-data`
- **HTTP 方法**: `GET`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 请求参数 (Query Parameters)

| 字段                 | 类型          | 是否必需 | 描述                                   |
| :------------------- | :------------ | :------- | :------------------------------------- |
| `page`               | Integer       | 否       | 当前页码 (默认: 1)                     |
| `size`               | Integer       | 否       | 每页记录数 (默认: 10)                  |
| `stationId`          | Long          | 否       | 监测站点 ID                            |
| `startTime`          | LocalDateTime | 否       | 开始时间 (格式: yyyy-MM-dd HH:mm:ss)   |
| `endTime`            | LocalDateTime | 否       | 结束时间 (格式: yyyy-MM-dd HH:mm:ss)   |
| `monitoringItemCode` | String        | 否       | 监测项目代码(WT/TU/PH/EC/DO/AN/COD/RC) |
| `dataQuality`        | Integer       | 否       | 数据质量(1:正常,2:异常,3:缺失)         |
| `collectionMethod`   | String        | 否       | 采集方式(AUTO:自动,MANUAL:手动)        |
| `dataSource`         | String        | 否       | 数据来源设备                           |
| `sort`               | String        | 否       | 排序字段 (默认: monitoring_time,desc)  |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "查询成功",
	"data": {
		"total": 150,
		"page": 1,
		"size": 10,
		"data": [
			{
				"id": 1,
				"stationId": 1,
				"stationName": "襄阳水厂水质站",
				"stationCode": "00000001",
				"monitoringTime": "2024-01-01 14:30:00",
				"waterTemperature": 18.5,
				"turbidity": 1.2,
				"phValue": 7.2,
				"conductivity": 425.3,
				"dissolvedOxygen": 8.5,
				"ammoniaNitrogen": 0.15,
				"codValue": 2.8,
				"residualChlorine": 0.05,
				"dataQuality": 1,
				"dataQualityText": "正常",
				"collectionMethod": "AUTO",
				"dataSource": "水质分析仪A01",
				"remark": "正常监测数据",
				"createdAt": "2024-01-01 14:30:05",
				"updatedAt": "2024-01-01 14:30:05"
			}
		]
	}
}
```

**失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "查询失败: 参数无效",
	"data": null
}
```

---

#### 6.5.2 获取水质监测图表数据

**功能描述**

获取指定站点、监测项目和时间范围的水质图表数据，用于前端绘制趋势图表，支持不同时间间隔的数据聚合。

- **URL**: `/api/monitoring/water-quality-chart-data`
- **HTTP 方法**: `GET`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 请求参数 (Query Parameters)

| 字段                 | 类型          | 是否必需 | 描述                                             |
| :------------------- | :------------ | :------- | :----------------------------------------------- |
| `stationId`          | Long          | 否       | 监测站点 ID                                      |
| `monitoringItemCode` | String        | 是       | 监测项目代码(WT/TU/PH/EC/DO/AN/COD/RC)           |
| `startTime`          | LocalDateTime | 否       | 开始时间 (格式: yyyy-MM-dd HH:mm:ss)             |
| `endTime`            | LocalDateTime | 否       | 结束时间 (格式: yyyy-MM-dd HH:mm:ss)             |
| `interval`           | String        | 否       | 时间间隔(hour:小时,day:天,month:月) (默认: hour) |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "查询成功",
	"data": {
		"labels": [
			"2024-01-01 10:00",
			"2024-01-01 11:00",
			"2024-01-01 12:00",
			"2024-01-01 13:00",
			"2024-01-01 14:00"
		],
		"values": [7.1, 7.2, 7.3, 7.2, 7.1],
		"datasetName": "PH值",
		"stationName": "襄阳水厂水质站",
		"interval": "hour",
		"unit": "pH"
	}
}
```

**失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "查询失败: 必须提供监测项目代码",
	"data": null
}
```

---

#### 6.5.3 导出水质监测数据

**功能描述**

根据查询条件将水质监测数据导出为 CSV 文件，支持大批量数据的导出操作，最大支持导出 10000 条记录。

- **URL**: `/api/monitoring/water-quality-data/export`
- **HTTP 方法**: `POST`
- **Content-Type**: `application/json`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 请求体 (Request Body)

| 字段                 | 类型          | 是否必需 | 描述                                    |
| :------------------- | :------------ | :------- | :-------------------------------------- |
| `stationId`          | Long          | 否       | 监测站点 ID                             |
| `startTime`          | LocalDateTime | 否       | 开始时间 (格式: yyyy-MM-dd HH:mm:ss)    |
| `endTime`            | LocalDateTime | 否       | 结束时间 (格式: yyyy-MM-dd HH:mm:ss)    |
| `monitoringItemCode` | String        | 否       | 监测项目代码(WT/TU/PH/EC/DO/AN/COD/RC)  |
| `dataQuality`        | Integer       | 否       | 数据质量(1:正常,2:异常,3:缺失)          |
| `collectionMethod`   | String        | 否       | 采集方式(AUTO:自动,MANUAL:手动)         |
| `dataSource`         | String        | 否       | 数据来源设备                            |
| `size`               | Integer       | 否       | 导出数量限制 (默认: 10000，最大: 10000) |
| `sort`               | String        | 否       | 排序字段 (默认: monitoring_time,desc)   |

##### 请求示例

```json
{
	"stationId": 1,
	"startTime": "2024-01-01 00:00:00",
	"endTime": "2024-01-31 23:59:59",
	"monitoringItemCode": "PH",
	"dataQuality": 1,
	"size": 5000
}
```

##### 响应 (Response)

**成功响应 (200 OK)**

返回 CSV 文件的二进制数据流，响应头包含文件下载信息。

```
Response Headers:
Content-Type: application/octet-stream
Content-Disposition: attachment; filename=water_quality_monitoring_data_1704097200000.xlsx
Content-Length: 524288
```

**失败响应 (400 Bad Request)**

```json
{
	"success": false,
	"message": "导出失败: 查询条件无效"
}
```

---

#### 6.5.4 导入水质监测数据

**功能描述**

批量导入 Excel 解析后的水质监测数据，支持数据验证、重复检查和错误处理，返回详细的导入结果统计。

- **URL**: `/api/monitoring/water-quality/import`
- **HTTP 方法**: `POST`
- **Content-Type**: `application/json`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 请求体 (Request Body)

请求体为水质数据导入 DTO 数组，每个对象包含以下字段：

| 字段               | 类型       | 是否必需 | 描述                                  |
| :----------------- | :--------- | :------- | :------------------------------------ |
| `rowNumber`        | Integer    | 是       | Excel 行号 (从 2 开始计数)            |
| `monitoringTime`   | String     | 是       | 监测时间 (格式: yyyy-MM-dd HH:mm:ss)  |
| `stationCode`      | String     | 是       | 监测站码 (8 位字符串)                 |
| `waterTemperature` | BigDecimal | 否       | 水温 (℃，范围: -50.0 ~ 100.0)         |
| `turbidity`        | BigDecimal | 否       | 浊度 (NTU，范围: 0.0 ~ 1000.0)        |
| `phValue`          | BigDecimal | 否       | PH 值 (范围: 0.0 ~ 14.0)              |
| `conductivity`     | BigDecimal | 否       | 电导率 (uS/cm，范围: 0.0 ~ 10000.0)   |
| `dissolvedOxygen`  | BigDecimal | 否       | 溶解氧 (mg/L，范围: 0.0 ~ 20.0)       |
| `ammoniaNitrogen`  | BigDecimal | 否       | 氨氮 (mg/L，范围: 0.0 ~ 100.0)        |
| `codValue`         | BigDecimal | 否       | 化学需氧量 (mg/L，范围: 0.0 ~ 1000.0) |
| `residualChlorine` | BigDecimal | 否       | 余氯 (mg/L，范围: 0.0 ~ 10.0)         |

##### 请求示例

```json
[
	{
		"rowNumber": 2,
		"monitoringTime": "2024-01-01 10:00:00",
		"stationCode": "00000001",
		"waterTemperature": 18.5,
		"turbidity": 1.2,
		"phValue": 7.2,
		"conductivity": 425.3,
		"dissolvedOxygen": 8.5,
		"ammoniaNitrogen": 0.15,
		"codValue": 2.8,
		"residualChlorine": 0.05
	},
	{
		"rowNumber": 3,
		"monitoringTime": "2024-01-01 11:00:00",
		"stationCode": "00000001",
		"waterTemperature": 19.0,
		"turbidity": 1.1,
		"phValue": 7.3,
		"conductivity": 430.2,
		"dissolvedOxygen": 8.8,
		"ammoniaNitrogen": 0.12,
		"codValue": 2.5,
		"residualChlorine": 0.06
	}
]
```

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "数据导入成功",
	"data": {
		"totalRows": 100,
		"successRows": 95,
		"errorRows": 5,
		"duplicateRows": 3,
		"errors": [
			{
				"rowNumber": 5,
				"stationCode": "00000999",
				"error": "监测站点不存在"
			},
			{
				"rowNumber": 12,
				"stationCode": "00000001",
				"error": "PH值超出有效范围"
			}
		]
	}
}
```

**部分成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "数据导入完成，成功95条，失败5条",
	"data": {
		"totalRows": 100,
		"successRows": 95,
		"errorRows": 5,
		"duplicateRows": 3,
		"errors": [
			{
				"rowNumber": 8,
				"phValue": "15.0",
				"error": "PH值不能大于14.0"
			}
		]
	}
}
```

**全部失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "数据导入失败，请检查数据格式",
	"data": {
		"totalRows": 10,
		"successRows": 0,
		"errorRows": 10,
		"duplicateRows": 0,
		"errors": [
			{
				"rowNumber": 2,
				"stationCode": "invalid",
				"error": "站码格式错误，必须是8位字符串"
			}
		]
	}
}
```

**参数错误响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "参数错误: 导入数据不能为空",
	"data": null
}
```

## 7. 一张图模块 (`/api/map`)

**功能描述**

一张图模块是水利工程信息化系统的核心可视化模块，负责提供系统的综合数据概览、地理信息展示和实时状态监控。该模块整合了设施信息、管理体系、监测数据、预警信息等多维度数据，为用户提供直观、全面的水利工程运行态势展示。所有接口需要 `business:operate` 权限。

### 7.1 一张图数据概览 (`/api/map`)

**功能描述**

提供一张图模块的综合数据获取功能，包括设施位置信息、管理体系信息、监测站点状态、预警信息和统计数据等核心业务数据的集中展示。

#### 7.1.1 获取一张图概览数据

**功能描述**

获取一张图模块的所有核心数据，包括设施分布、管理体系、监测站点、预警信息和统计数据的综合概览，为前端地图组件提供一次性的完整数据加载。

- **URL**: `/api/map/overview`
- **HTTP 方法**: `GET`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "获取一张图概览数据成功",
	"data": {
		"facilities": [
			{
				"id": 1,
				"name": "襄阳第一水厂",
				"type": "water_plant",
				"longitude": 114.123456,
				"latitude": 30.123456,
				"status": "运行正常"
			}
		],
		"managementSystem": [
			{
				"id": 1,
				"departmentName": "水务管理局",
				"managerName": "张三",
				"responsibleArea": "襄阳市区",
				"contactPhone": "0710-12345678"
			}
		],
		"monitoringStations": [
			{
				"id": 1,
				"stationName": "襄阳水库监测站",
				"stationCode": "ST001",
				"longitude": 114.234567,
				"latitude": 30.234567,
				"latestData": {
					"waterLevel": 165.25,
					"monitoringTime": "2024-01-01T14:30:00"
				}
			}
		],
		"warnings": [
			{
				"id": 1,
				"warningLocation": "襄阳水库",
				"warningType": "water_level",
				"warningLevel": "yellow",
				"occurredAt": "2024-01-01T10:00:00",
				"description": "水位超过警戒线"
			}
		],
		"statistics": {
			"totalFacilities": 45,
			"totalStations": 12,
			"activeWarnings": 3,
			"facilitiesByType": {
				"water_plant": 8,
				"reservoir": 15,
				"pumping_station": 12,
				"pipeline": 10
			}
		}
	}
}
```

**失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "获取概览数据失败",
	"data": null
}
```

---

#### 7.1.2 获取水利设施位置信息

**功能描述**

获取系统中所有水利设施的地理位置和基本信息，包括水厂、水库、泵站、管道等各类设施的分布情况，用于在地图上进行标点展示。

- **URL**: `/api/map/facilities`
- **HTTP 方法**: `GET`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "获取设施位置信息成功",
	"data": [
		{
			"id": 1,
			"name": "襄阳第一水厂",
			"type": "water_plant",
			"typeName": "水厂",
			"longitude": 114.123456,
			"latitude": 30.123456,
			"address": "襄阳市高新区",
			"status": "运行正常",
			"capacity": 50000.0,
			"managementUnit": "襄阳水务集团",
			"contactPhone": "0710-12345678"
		},
		{
			"id": 2,
			"name": "襄阳水库",
			"type": "reservoir",
			"typeName": "水库",
			"longitude": 114.234567,
			"latitude": 30.234567,
			"address": "襄阳市襄城区",
			"status": "正常蓄水",
			"capacity": 1000.5,
			"managementUnit": "襄阳水务集团",
			"contactPhone": "0710-87654321"
		},
		{
			"id": 3,
			"name": "襄阳一号泵站",
			"type": "pumping_station",
			"typeName": "泵站",
			"longitude": 114.345678,
			"latitude": 30.345678,
			"address": "襄阳市樊城区",
			"status": "自动运行",
			"capacity": 200.0,
			"managementUnit": "襄阳水务集团",
			"contactPhone": "0710-11223344"
		}
	]
}
```

**失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "获取设施信息失败",
	"data": null
}
```

---

#### 7.1.3 获取管理体系信息

**功能描述**

获取水利工程管理体系的组织架构信息，包括管理部门、负责人员及其管辖区域，用于展示管理责任分工和联系方式。

- **URL**: `/api/map/management-system`
- **HTTP 方法**: `GET`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "获取管理体系信息成功",
	"data": [
		{
			"id": 1,
			"departmentId": 1,
			"departmentName": "水务管理局",
			"departmentType": "行政管理",
			"managerId": 1,
			"managerName": "张三",
			"managerPosition": "局长",
			"responsibleArea": "襄阳市区",
			"responsibilityDescription": "负责全市水务工程的统筹管理",
			"contactPhone": "0710-12345678",
			"contactEmail": "zhangsan@xiangyang.gov.cn",
			"officeAddress": "襄阳市政府大楼A座8楼"
		},
		{
			"id": 2,
			"departmentId": 2,
			"departmentName": "襄阳水务集团",
			"departmentType": "运营管理",
			"managerId": 2,
			"managerName": "李四",
			"managerPosition": "总经理",
			"responsibleArea": "城区供水系统",
			"responsibilityDescription": "负责城区供水设施的运营维护",
			"contactPhone": "0710-87654321",
			"contactEmail": "lisi@xywater.com",
			"officeAddress": "襄阳市水务大厦6楼"
		},
		{
			"id": 3,
			"departmentId": 3,
			"departmentName": "水库管理处",
			"departmentType": "专业管理",
			"managerId": 3,
			"managerName": "王五",
			"managerPosition": "主任",
			"responsibleArea": "襄阳水库群",
			"responsibilityDescription": "负责辖区内水库的安全运行管理",
			"contactPhone": "0710-11223344",
			"contactEmail": "wangwu@xyreservoir.com",
			"officeAddress": "襄阳水库管理大楼2楼"
		}
	]
}
```

**失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "获取管理体系信息失败",
	"data": null
}
```

---

#### 7.1.4 获取监测站点及最新数据

**功能描述**

获取系统中所有监测站点的基本信息及其最新监测数据，包括站点位置、监测类型、最新数据值和数据质量状态，用于实时监控展示。

- **URL**: `/api/map/monitoring-stations`
- **HTTP 方法**: `GET`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "获取监测站点信息成功",
	"data": [
		{
			"id": 1,
			"stationCode": "ST001",
			"stationName": "襄阳水库监测站",
			"stationType": "water_level",
			"stationTypeName": "水位监测",
			"longitude": 114.123456,
			"latitude": 30.123456,
			"location": "襄阳水库大坝",
			"isActive": true,
			"latestData": {
				"dataType": "water_level",
				"dataValue": 165.25,
				"unit": "m",
				"monitoringTime": "2024-01-01T14:30:00",
				"dataQuality": 1,
				"dataQualityDesc": "正常"
			},
			"alertStatus": {
				"hasAlert": true,
				"alertLevel": "yellow",
				"alertMessage": "水位接近警戒线"
			}
		},
		{
			"id": 2,
			"stationCode": "ST002",
			"stationName": "汉江流量监测站",
			"stationType": "flow_rate",
			"stationTypeName": "流量监测",
			"longitude": 114.234567,
			"latitude": 30.234567,
			"location": "汉江大桥上游500米",
			"isActive": true,
			"latestData": {
				"dataType": "flow_rate",
				"dataValue": 125.8,
				"unit": "m³/s",
				"monitoringTime": "2024-01-01T14:30:00",
				"dataQuality": 1,
				"dataQualityDesc": "正常"
			},
			"alertStatus": {
				"hasAlert": false,
				"alertLevel": null,
				"alertMessage": null
			}
		},
		{
			"id": 3,
			"stationCode": "ST003",
			"stationName": "襄阳水厂水质监测站",
			"stationType": "water_quality",
			"stationTypeName": "水质监测",
			"longitude": 114.345678,
			"latitude": 30.345678,
			"location": "襄阳第一水厂出水口",
			"isActive": true,
			"latestData": {
				"dataType": "ph_value",
				"dataValue": 7.2,
				"unit": "pH",
				"monitoringTime": "2024-01-01T14:30:00",
				"dataQuality": 1,
				"dataQualityDesc": "正常"
			},
			"alertStatus": {
				"hasAlert": false,
				"alertLevel": null,
				"alertMessage": null
			}
		}
	]
}
```

**失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "获取监测站点信息失败",
	"data": null
}
```

---

#### 7.1.5 获取当前活跃预警信息

**功能描述**

获取当前状态为"进行中"的所有预警信息，包括预警位置、类型、等级、发生时间和描述信息，用于在地图上进行预警标识和风险提示。

- **URL**: `/api/map/warnings`
- **HTTP 方法**: `GET`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "获取活跃预警信息成功",
	"data": [
		{
			"id": 1,
			"warningLocation": "襄阳水库",
			"warningType": "water_level",
			"warningTypeName": "水位预警",
			"warningLevel": "yellow",
			"warningLevelName": "黄色预警",
			"warningStatus": "active",
			"warningStatusName": "进行中",
			"projectName": "襄阳水利工程",
			"occurredAt": "2024-01-01T10:00:00",
			"description": "水位超过警戒线，当前水位165.25米",
			"priority": 2,
			"longitude": 114.123456,
			"latitude": 30.123456,
			"affectedFacilities": ["襄阳水库", "下游泵站"],
			"recommendedActions": [
				"密切监控水位变化",
				"做好泄洪准备",
				"通知下游相关单位"
			]
		},
		{
			"id": 2,
			"warningLocation": "汉江水厂",
			"warningType": "water_quality",
			"warningTypeName": "水质预警",
			"warningLevel": "blue",
			"warningLevelName": "蓝色预警",
			"warningStatus": "active",
			"warningStatusName": "进行中",
			"projectName": "汉江供水工程",
			"occurredAt": "2024-01-01T12:15:00",
			"description": "出厂水浊度略微偏高，需要关注",
			"priority": 1,
			"longitude": 114.234567,
			"latitude": 30.234567,
			"affectedFacilities": ["汉江水厂", "城区供水网"],
			"recommendedActions": [
				"增加水质检测频次",
				"检查过滤设备状态",
				"通知相关技术人员"
			]
		},
		{
			"id": 3,
			"warningLocation": "高新区泵站",
			"warningType": "equipment_fault",
			"warningTypeName": "设备故障",
			"warningLevel": "orange",
			"warningLevelName": "橙色预警",
			"warningStatus": "active",
			"warningStatusName": "进行中",
			"projectName": "城区供水工程",
			"occurredAt": "2024-01-01T13:45:00",
			"description": "2号水泵运行异常，功率下降",
			"priority": 3,
			"longitude": 114.345678,
			"latitude": 30.345678,
			"affectedFacilities": ["高新区泵站", "高新区供水管网"],
			"recommendedActions": [
				"立即派遣维修人员",
				"启用备用水泵",
				"监控供水压力变化"
			]
		}
	]
}
```

**失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "获取预警信息失败",
	"data": null
}
```

---

#### 7.1.6 获取基础数据统计信息

**功能描述**

获取水利工程系统的基础数据统计信息，包括设施总数、监测点数量、预警统计等关键指标，用于在一张图界面展示系统运行的整体概况。

- **URL**: `/api/map/stats`
- **HTTP 方法**: `GET`

##### 请求头 (Request Headers)

| 头部            | 描述                                       |
| :-------------- | :----------------------------------------- |
| `Authorization` | Bearer 令牌，格式为 `Bearer {accessToken}` |

##### 响应 (Response)

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "获取统计数据成功",
	"data": {
		"facilityStats": {
			"totalFacilities": 45,
			"normalFacilities": 42,
			"warningFacilities": 2,
			"faultFacilities": 1,
			"facilitiesByType": {
				"water_plant": {
					"count": 8,
					"normal": 7,
					"warning": 1,
					"fault": 0
				},
				"reservoir": {
					"count": 15,
					"normal": 14,
					"warning": 1,
					"fault": 0
				},
				"pumping_station": {
					"count": 12,
					"normal": 11,
					"warning": 0,
					"fault": 1
				},
				"pipeline": {
					"count": 10,
					"normal": 10,
					"warning": 0,
					"fault": 0
				}
			}
		},
		"monitoringStats": {
			"totalStations": 28,
			"onlineStations": 26,
			"offlineStations": 2,
			"stationsByType": {
				"water_level": 8,
				"flow_rate": 6,
				"water_quality": 5,
				"rainfall": 4,
				"pressure": 3,
				"temperature": 2
			},
			"dataQualityStats": {
				"normal": 24,
				"abnormal": 2,
				"missing": 2
			}
		},
		"warningStats": {
			"totalWarnings": 3,
			"activeWarnings": 3,
			"resolvedToday": 5,
			"warningsByLevel": {
				"red": 0,
				"orange": 1,
				"yellow": 1,
				"blue": 1
			},
			"warningsByType": {
				"water_level": 1,
				"water_quality": 1,
				"equipment_fault": 1,
				"flow_rate": 0,
				"rainfall": 0
			}
		},
		"systemStats": {
			"managementUnits": 5,
			"onlineUsers": 12,
			"dataUpdateTime": "2024-01-01T14:30:00",
			"systemUptime": "99.8%",
			"totalDataPoints": 156789
		}
	}
}
```

**失败响应 (400 Bad Request)**

```json
{
	"code": 400,
	"message": "获取统计数据失败",
	"data": null
}
```

---

## 8. 工程巡检模块 (`/api/inspection`)

**功能描述**

- 统一管理工程巡检任务与巡检记录（含文字与图片）。
- 覆盖设施：加压泵站、水厂、流量站等，通过 `facility_type + facility_id` 统一引用（设施类型通过工程服务 API 获取）。
- 权限：查询类接口需 `data:view`；创建/更新/删除类接口按职责分别需 `business:operate` 或 `business:manage`。

### 8.1 巡检任务（Tasks）

#### 8.1.1 分页查询任务

- **URL**: `/api/inspection/tasks`
- **方法**: `GET`
- **权限**: `data:view`

**请求参数（Query Parameters）**

| 字段            | 类型    | 必填 | 说明                                                    |
| --------------- | ------- | ---- | ------------------------------------------------------- |
| `page`          | Integer | 否   | 页码，默认 1                                            |
| `size`          | Integer | 否   | 每页数量，默认 10                                       |
| `status`        | String  | 否   | 任务状态：PENDING/IN_PROGRESS/COMPLETED/EXCEPTION       |
| `assigneeId`    | Long    | 否   | 责任人 ID（`personnel.id`）                             |
| `facilityType`  | String  | 否   | 设施类型（通过工程服务 API 获取类型选项）               |
| `facilityId`    | Long    | 否   | 设施 ID                                                 |
| `scheduledDate` | String  | 否   | 计划执行日期（YYYY-MM-DD）                              |
| `sort`          | String  | 否   | 排序（示例：`scheduled_date,asc` 或 `created_at,desc`） |

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "查询成功",
	"data": {
		"total": 1,
		"page": 1,
		"size": 10,
		"data": [
			{
				"id": 1,
				"title": "1号泵站月度巡检",
				"facilityType": "pumping_station",
				"facilityId": 1,
				"frequency": "monthly",
				"content": "检查设备运行状态，记录异常",
				"assigneeId": 5,
				"status": "PENDING",
				"scheduledDate": "2025-08-15",
				"createdAt": "2025-07-20T10:00:00",
				"updatedAt": "2025-07-20T10:00:00"
			}
		]
	}
}
```

---

#### 8.1.2 查询任务详情

- **URL**: `/api/inspection/tasks/{id}`
- **方法**: `GET`
- **权限**: `data:view`

**URL 路径参数**

| 字段 | 类型 | 必填 | 描述    |
| ---- | ---- | ---- | ------- |
| `id` | Long | 是   | 任务 ID |

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "查询成功",
	"data": {
		"id": 1,
		"title": "1号泵站月度巡检",
		"facilityType": "pumping_station",
		"facilityId": 1,
		"frequency": "monthly",
		"content": "检查设备运行状态，记录异常",
		"assigneeId": 5,
		"status": "PENDING",
		"scheduledDate": "2025-08-15",
		"createdAt": "2025-07-20T10:00:00",
		"updatedAt": "2025-07-20T10:00:00"
	}
}
```

---

#### 8.1.3 创建任务

- **URL**: `/api/inspection/tasks`
- **方法**: `POST`
- **权限**: `business:manage`
- **Content-Type**: `application/json`

**请求体 (Request Body)**

| 字段            | 类型   | 必填 | 说明                                    |
| --------------- | ------ | ---- | --------------------------------------- |
| `title`         | String | 是   | 任务标题                                |
| `facilityType`  | String | 是   | 设施类型                                |
| `facilityId`    | Long   | 是   | 设施 ID                                 |
| `frequency`     | String | 否   | 巡检频次（字典 `inspection_frequency`） |
| `content`       | String | 否   | 巡检内容/要求                           |
| `assigneeId`    | Long   | 是   | 责任人 ID (`personnel.id`)              |
| `scheduledDate` | String | 否   | 计划执行日期 (YYYY-MM-DD)               |

**成功响应 (201 Created)**

```json
{
	"code": 200,
	"message": "创建成功",
	"data": {
		"id": 2,
		"title": "2号水厂季度巡检",
		"facilityType": "water_plant",
		"facilityId": 2,
		"frequency": "quarterly",
		"content": "检查水质处理设备",
		"assigneeId": 6,
		"status": "PENDING",
		"scheduledDate": "2025-09-01",
		"createdAt": "2025-07-20T11:00:00",
		"updatedAt": "2025-07-20T11:00:00"
	}
}
```

---

#### 8.1.4 更新任务

- **URL**: `/api/inspection/tasks/{id}`
- **方法**: `PUT`
- **权限**: `business:manage`
- **Content-Type**: `application/json`

**URL 路径参数**

| 字段 | 类型 | 必填 | 描述    |
| ---- | ---- | ---- | ------- |
| `id` | Long | 是   | 任务 ID |

**请求体 (与创建任务一致，所有字段均为可选)**

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "更新成功",
	"data": {
		"id": 2,
		"title": "2号水厂季度巡检（已更新）",
		"facilityType": "water_plant",
		"facilityId": 2,
		"frequency": "quarterly",
		"content": "更新后的巡检内容",
		"assigneeId": 7,
		"status": "PENDING",
		"scheduledDate": "2025-09-05",
		"createdAt": "2025-07-20T11:00:00",
		"updatedAt": "2025-07-20T12:00:00"
	}
}
```

---

#### 8.1.5 更新任务状态

- **URL**: `/api/inspection/tasks/{id}/status`
- **方法**: `PATCH`
- **权限**: `business:manage`

**URL 路径参数**

| 字段 | 类型 | 必填 | 描述    |
| ---- | ---- | ---- | ------- |
| `id` | Long | 是   | 任务 ID |

**请求参数 (Query Parameters)**

| 字段     | 类型   | 必填 | 说明                                            |
| -------- | ------ | ---- | ----------------------------------------------- |
| `status` | String | 是   | 新状态：PENDING/IN_PROGRESS/COMPLETED/EXCEPTION |

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "状态更新成功",
	"data": null
}
```

---

#### 8.1.6 删除任务

- **URL**: `/api/inspection/tasks/{id}`
- **方法**: `DELETE`
- **权限**: `business:manage`

**URL 路径参数**

| 字段 | 类型 | 必填 | 描述    |
| ---- | ---- | ---- | ------- |
| `id` | Long | 是   | 任务 ID |

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "删除成功",
	"data": null
}
```

### 8.2 巡检记录（Records）

#### 8.2.1 分页查询记录

- **URL**: `/api/inspection/records`
- **方法**: `GET`
- **权限**: `data:view`

**请求参数（Query Parameters）**

| 字段           | 类型    | 必填 | 说明                                     |
| -------------- | ------- | ---- | ---------------------------------------- |
| `page`         | Integer | 否   | 页码，默认 1                             |
| `size`         | Integer | 否   | 每页数量，默认 10                        |
| `facilityType` | String  | 否   | 设施类型                                 |
| `facilityId`   | Long    | 否   | 设施 ID                                  |
| `startTime`    | String  | 否   | 记录时间范围（起） `YYYY-MM-DD HH:mm:ss` |
| `endTime`      | String  | 否   | 记录时间范围（止） `YYYY-MM-DD HH:mm:ss` |
| `issueFlag`    | Integer | 否   | 是否发现问题 (0: 否, 1: 是)              |
| `sort`         | String  | 否   | 排序 (示例: `record_time,desc`)          |

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "查询成功",
	"data": {
		"total": 1,
		"page": 1,
		"size": 10,
		"data": [
			{
				"id": 101,
				"taskId": 1,
				"inspectorId": 5,
				"facilityType": "pumping_station",
				"facilityId": 1,
				"recordTime": "2025-08-15T14:30:00",
				"deviceStatus": "normal",
				"issueFlag": 1,
				"issueDescription": "A泵机有异响",
				"resolution": "已上报，待维修",
				"resolvedAt": null,
				"createdAt": "2025-08-15T14:35:00",
				"updatedAt": "2025-08-15T14:35:00"
			}
		]
	}
}
```

---

#### 8.2.2 查询记录详情

- **URL**: `/api/inspection/records/{id}`
- **方法**: `GET`
- **权限**: `data:view`

**URL 路径参数**

| 字段 | 类型 | 必填 | 描述    |
| ---- | ---- | ---- | ------- |
| `id` | Long | 是   | 记录 ID |

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "查询成功",
	"data": {
		"id": 101,
		"taskId": 1,
		"inspectorId": 5,
		"facilityType": "pumping_station",
		"facilityId": 1,
		"recordTime": "2025-08-15T14:30:00",
		"deviceStatus": "normal",
		"issueFlag": 1,
		"issueDescription": "A泵机有异响",
		"resolution": "已上报，待维修",
		"resolvedAt": null,
		"attachments": [
			{
				"id": 201,
				"recordId": 101,
				"fileName": "pump_noise.jpg",
				"filePath": "/uploads/inspection/2025/08/pump_noise.jpg",
				"contentType": "image/jpeg",
				"fileSize": 102400
			}
		]
	}
}
```

---

#### 8.2.3 查询记录附件

- **URL**: `/api/inspection/records/{id}/attachments`
- **方法**: `GET`
- **权限**: `data:view`

**URL 路径参数**

| 字段 | 类型 | 必填 | 描述    |
| ---- | ---- | ---- | ------- |
| `id` | Long | 是   | 记录 ID |

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "查询成功",
	"data": [
		{
			"id": 201,
			"recordId": 101,
			"fileName": "pump_noise.jpg",
			"filePath": "/uploads/inspection/2025/08/pump_noise.jpg",
			"contentType": "image/jpeg",
			"fileSize": 102400,
			"createdAt": "2025-08-15T14:35:00"
		}
	]
}
```

---

#### 8.2.4 创建巡检记录（含图片上传）

- **URL**: `/api/inspection/records`
- **方法**: `POST`
- **权限**: `business:operate`
- **Content-Type**: `multipart/form-data`

**请求体 (Form Data)**

| Part Name | 类型   | 必填 | 说明                       |
| --------- | ------ | ---- | -------------------------- |
| `data`    | JSON   | 是   | 巡检记录的元数据（见下表） |
| `files`   | File[] | 否   | 巡检图片文件数组           |

**`data` Part (JSON 对象)**

| 字段               | 类型    | 必填 | 说明                                      |
| ------------------ | ------- | ---- | ----------------------------------------- |
| `taskId`           | Long    | 否   | 关联的任务 ID，若非计划内巡检可为空       |
| `facilityType`     | String  | 是   | 设施类型                                  |
| `facilityId`       | Long    | 是   | 设施 ID                                   |
| `recordTime`       | String  | 是   | 巡检时间 (YYYY-MM-DD HH:mm:ss)            |
| `deviceStatus`     | String  | 否   | 设备状态 (字典 `device_status`)           |
| `issueFlag`        | Integer | 是   | 是否发现问题 (0: 否, 1: 是)               |
| `issueDescription` | String  | 否   | 问题描述 (当 `issueFlag` 为 1 时建议填写) |
| `resolution`       | String  | 否   | 处理措施                                  |
| `resolvedAt`       | String  | 否   | 问题解决时间 (YYYY-MM-DD HH:mm:ss)        |

**成功响应 (200 OK)**

```json
{
	"code": 200,
	"message": "创建成功",
	"data": {
		"id": 102,
		"taskId": null,
		"inspectorId": 8,
		"facilityType": "pipeline",
		"facilityId": 10,
		"recordTime": "2025-08-16T09:00:00",
		"deviceStatus": "exception",
		"issueFlag": 1,
		"issueDescription": "管道轻微泄漏",
		"resolution": "紧急处理中",
		"resolvedAt": null,
		"createdAt": "2025-08-16T09:05:00",
		"updatedAt": "2025-08-16T09:05:00"
	}
}
```
