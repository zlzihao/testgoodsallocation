.PHONY: default help build run clean rerun

PROJECT_NAME=goods-allocation

default: help

help:
	@echo 'Management commands for SDC:'
	@echo
	@echo 'Usage:'
	@echo '    make build           Compile the project.'
	@echo '    make run             Run the project.'
	@echo '    make clean           Clean the project.'
	@echo '    make rerun           Clean&Build&Run project.'
	@echo


build:
	@mvn compile
	@mvn package -Dmaven.test.skip=true

clean:
	@mvn clean

run:
	@mvn clean package -Dmaven.test.skip=true
	@java -jar goods-allocation-rest/target/goods-allocation-rest.jar --spring.profiles.active=dev
