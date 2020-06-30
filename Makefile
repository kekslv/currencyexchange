SHELL=/bin/bash

ROOT_DIR := $(shell pwd)

.PHONY: build
build:
	docker-compose build --no-cache --force-rm
	make prune

.PHONY: up
up:
	docker-compose up -d

.PHONY: down
down:
	docker-compose down

.PHONY: prune
prune:
	docker image prune -f --filter label=stage=intermediate