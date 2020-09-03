SHELL := /bin/bash

DOCKER_REGISTRY_URL=docker.url
DOCKER_IMAGE=serviceorder-api/serviceorder-be
APP_VERSION=$(shell mvn org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate \
			-Dexpression=project.version | sed -n -e '/^\[.*\]/ !{ /^[0-9]/ { p; q } }')

.ONESHELL:
clean:
	@echo " -- Cleaning project with Maven -- "
	@echo "APP_VERSION = ${APP_VERSION}"
	@mvn clean

build:
	@echo " -- Building project with Maven -- "
	@mvn install -DskipTests

test:
	@echo " -- Running Tests -- "
	@mvn test

build_docker:
	@echo " -- Building Docker image -- "
	@echo "APP_VERSION = ${APP_VERSION}"
	@docker build --build-arg APP_VERSION=${APP_VERSION} -t ${DOCKER_REGISTRY_URL}/${DOCKER_IMAGE} -t ${DOCKER_REGISTRY_URL}/${DOCKER_IMAGE}:${APP_VERSION} -t ${DOCKER_REGISTRY_URL}/${DOCKER_IMAGE}:latest .

push_docker:
	@echo " -- Docker login --"
	@docker login ${DOCKER_REGISTRY_URL} -u $(USER) -p $(PASS)

	@echo " -- Pushing Docker image -- "
	@docker push ${DOCKER_REGISTRY_URL}/${DOCKER_IMAGE}

deploy_docker:
	@make build_docker
	@make push_docker