.PHONY: help dev build start stop restart logs clean

# 默认目标
help:
	@echo "用户管理系统 - 快捷命令"
	@echo ""
	@echo "可用命令:"
	@echo "  make dev     - 启动开发环境 (docker-compose up -d)"
	@echo "  make build   - 重新构建并启动 (docker-compose up -d --build)"
	@echo "  make start   - 启动所有服务"
	@echo "  make stop    - 停止所有服务"
	@echo "  make restart - 重启所有服务"
	@echo "  make logs    - 查看日志"
	@echo "  make clean   - 停止服务并删除卷 (数据将被清除)"
	@echo "  make ps      - 查看运行状态"
	@echo "  make backend-logs  - 查看后端日志"
	@echo "  make frontend-logs - 查看前端日志"
	@echo "  make db-logs       - 查看数据库日志"

# 开发环境 - 启动服务
dev:
	docker-compose up -d

# 重新构建并启动
build:
	docker-compose up -d --build

# 启动服务
start:
	docker-compose start

# 停止服务
stop:
	docker-compose stop

# 重启服务
restart:
	docker-compose restart

# 查看日志
logs:
	docker-compose logs -f

# 查看后端日志
backend-logs:
	docker-compose logs -f backend

# 查看前端日志
frontend-logs:
	docker-compose logs -f frontend

# 查看数据库日志
db-logs:
	docker-compose logs -f db

# 清理环境（包括数据卷）
clean:
	docker-compose down -v

# 查看运行状态
ps:
	docker-compose ps
