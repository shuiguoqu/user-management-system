.PHONY: help dev build up down logs clean rebuild backend frontend

help:
	@echo "用户管理系统 - 快捷命令"
	@echo ""
	@echo "使用方法: make <command>"
	@echo ""
	@echo "可用命令:"
	@echo "  dev       启动开发环境（构建并后台运行所有服务）"
	@echo "  build     构建所有服务的 Docker 镜像"
	@echo "  up        启动所有服务（后台运行）"
	@echo "  down      停止并移除所有容器"
	@echo "  logs      查看所有服务的日志"
	@echo "  logs-b    查看后端服务日志"
	@echo "  logs-f    查看前端服务日志"
	@echo "  logs-db   查看数据库服务日志"
	@echo "  clean     清理构建产物和未使用的镜像"
	@echo "  rebuild   重新构建所有服务（不使用缓存）"
	@echo "  backend   仅构建后端服务"
	@echo "  frontend  仅构建前端服务"
	@echo "  ps        查看服务状态"
	@echo ""

dev: build up
	@echo ""
	@echo "服务已启动，访问地址:"
	@echo "  前端页面: http://localhost:3010"
	@echo "  后端 API: http://localhost:3011"
	@echo "  Swagger:  http://localhost:3011/swagger-ui/index.html"

build:
	docker compose build

up:
	docker compose up -d

down:
	docker compose down

logs:
	docker compose logs -f

logs-b:
	docker compose logs -f backend

logs-f:
	docker compose logs -f frontend

logs-db:
	docker compose logs -f db

clean:
	docker compose down -v --rmi local
	docker system prune -f

rebuild:
	docker compose build --no-cache

backend:
	docker compose build backend

frontend:
	docker compose build frontend

ps:
	docker compose ps
