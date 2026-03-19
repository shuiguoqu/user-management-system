# 全栈用户管理系统

## 🧭 Project Type
- Type: A) FULLSTACK_WEB
- RUN_SLOT: 0
- COMPOSE_PROJECT_NAME: labelease-label-3974-slot0

## 🧩 技术栈
- Frontend: Vue 3 + Vite + Element Plus + Axios
- Backend: Java 17 + Spring Boot 3.x + MyBatis-Plus
- Database: MySQL 8 (utf8mb4)
- Build: Maven (backend), npm (frontend)
- Docker: 3 services (frontend + backend + db)

## 🚀 快速启动（唯一命令）
1. 确保 Docker Desktop 已启动
2. 在项目根目录运行：`docker compose up`
3. 等待所有服务启动（首次构建约 3-5 分钟）
4. 访问地址：
   - 前端页面：http://localhost:3010
   - 后端 API：http://localhost:3011
   - Swagger 文档：http://localhost:3011/swagger-ui/index.html
   - MySQL（外部访问）：localhost:3012

> 注：端口可能因环境冲突调整，以 docker-compose.yml 中映射为准。

## 👤 测试账号
| 用户名 | 密码 | 角色 | 状态 |
|--------|------|------|------|
| admin | admin123 | 管理员 | 启用 |
| zhangsan | password123 | 普通用户 | 启用 |
| lisi | password123 | 普通用户 | 启用 |
| wangwu | password123 | 普通用户 | 启用 |
| zhaoliu | password123 | 普通用户 | 禁用 |

## ✅ 功能清单
- [x] F1 (R001): Docker 三服务一键启动（frontend + backend + db）
- [ ] F2 (R006-R009): 燕麦拿铁设计系统（CSS 变量已定义，页面待精修）
- [ ] F3 (R010): 用户列表页（搜索/分页/表格/悬停）
- [ ] F4 (R011): 用户表单页（新增/编辑/校验/提交）
- [ ] F5 (R012): 用户详情抽屉（含关联订单）
- [x] F6 (R013): User/Order CRUD + 分页 API
- [x] F7 (R014): 用户-订单一对多联查
- [x] F8 (R015): 统一响应封装（code/message/data/timestamp）
- [ ] F9 (R005): Swagger + Axios 联调验证
- [ ] F10 (R016-R017): 中文注释 + ESLint/Java 规范
- [x] F11 (R020): SQL 初始化（建表 + 中文种子数据）
- [ ] F12 (R018-R022): README 与运行文档完善

## 🔎 自测说明
### 成功路径
1. 打开 http://localhost:3010 → 自动跳转登录页
2. 输入 admin / admin123 → 登录成功 → 跳转用户列表
3. 搜索框输入关键词 → 表格过滤
4. 点击"新增用户"→ 填写表单 → 提交成功
5. 点击表格行 → 弹出详情抽屉 → 查看关联订单
6. 点击"编辑"→ 修改信息 → 保存
7. 点击"删除"→ 确认对话框 → 删除成功

### 失败路径
1. 未登录访问 /users → 自动跳转 /login
2. 登录时输入错误密码 → 提示"用户名或密码错误"
3. 表单提交时缺少必填字段 → 校验提示
4. API 无 Token 访问 → 返回 401

### 边界/异常
- 禁用账户登录 → 提示"账户已被禁用"
- 搜索无结果 → 空数据提示
- 分页切换 → 数据正确加载

## 🧾 证据文件
- `evidence/run-slot0/` — 运行证据
- `evidence/qa/` — QA 截图

## 📦 最小质检压缩包清单
- `backend/`（不含 target/）
- `frontend/`（不含 node_modules/）
- `docker-compose.yml`
- `README.md`
- `evidence/`
- `requirements/`
- `scripts/`
- `SELF_CHECK.md`
- `QA_REPORT.md`

## 🏗️ 当前阶段
- **骨架搭建完成（Claude Code Phase）**
- Docker Gate 已通过：三服务正常启动
- 后端 API 全部可用（CRUD + 分页 + 联查 + JWT 鉴权）
- 前端页面骨架就绪（登录/列表/表单/抽屉）
- 待 Kimi 主开发完善 UI 精修与业务细节
