.PHONY: default help build run clean rerun

PROJECT_NAME=saas-sdc

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
	@mvn package

clean:
	@mvn clean

run:
	@mvn clean package
	@java -jar saas-sdc-rest/target/saas-sdc-rest.jar --spring.profiles.active=local
