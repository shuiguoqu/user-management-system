# 全栈用户管理系统 - Makefile 快捷命令

.PHONY: help dev build up down restart logs clean build-backend build-frontend

# 默认目标：显示帮助
help:
	@echo "=== 全栈用户管理系统 快捷命令 ==="
	@echo ""
	@echo "开发模式:"
	@echo "  make dev              - 启动开发环境（前台后台并行启动）"
	@echo ""
	@echo "Docker 模式:"
	@echo "  make build            - 构建所有 Docker 镜像"
	@echo "  make up               - 启动所有服务（后台模式）"
	@echo "  make down             - 停止并移除所有服务"
	@echo "  make restart          - 重启所有服务"
	@echo ""
	@echo "日志查看:"
	@echo "  make logs             - 查看所有服务日志"
	@echo "  make logs-backend     - 查看后端日志"
	@echo "  make logs-frontend    - 查看前端日志"
	@echo "  make logs-db          - 查看数据库日志"
	@echo ""
	@echo "构建命令:"
	@echo "  make build-backend    - 构建后端项目"
	@echo "  make build-frontend   - 构建前端项目"
	@echo ""
	@echo "清理命令:"
	@echo "  make clean            - 清理构建产物和 Docker 资源"

# 开发模式：前后台并行启动
dev:
	@echo "启动开发环境..."
	@echo "后端: http://localhost:8080"
	@echo "前端: http://localhost:5173"
	@echo ""
	@echo "请在两个终端分别执行以下命令:"
	@echo "后端: cd backend && mvn spring-boot:run"
	@echo "前端: cd frontend && npm run dev"

# Docker 命令
build:
	docker compose build

up:
	docker compose up -d

down:
	docker compose down

restart:
	docker compose restart

# 日志命令
logs:
	docker compose logs -f

logs-backend:
	docker compose logs -f backend

logs-frontend:
	docker compose logs -f frontend

logs-db:
	docker compose logs -f db

# 项目构建命令
build-backend:
	cd backend && mvn clean package -DskipTests

build-frontend:
	cd frontend && npm install && npm run build

# 清理命令
clean:
	docker compose down -v --rmi local
	cd backend && mvn clean
	cd frontend && rm -rf dist node_modules
	@echo "清理完成"
