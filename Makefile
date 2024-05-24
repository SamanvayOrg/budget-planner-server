help:
	@IFS=$$'\n' ; \
	help_lines=(`fgrep -h "##" $(MAKEFILE_LIST) | fgrep -v fgrep | sed -e 's/\\$$//'`); \
	for help_line in $${help_lines[@]}; do \
	    IFS=$$'#' ; \
	    help_split=($$help_line) ; \
	    help_command=`echo $${help_split[0]} | sed -e 's/^ *//' -e 's/ *$$//'` ; \
	    help_info=`echo $${help_split[2]} | sed -e 's/^ *//' -e 's/ *$$//'` ; \
	    printf "%-30s %s\n" $$help_command $$help_info ; \
	done

SU:=$(shell id -un)
#######

####### BUILD, TEST, LOCAL RUN
build-server: ## Builds the jar file
	./gradlew clean build -x test

test-server: ## Builds the jar file after tests
	./gradlew clean build

start:
	./gradlew bootRun

start-with-local-db:
	BPS_DATASOURCE=jdbc:postgresql://localhost:5442/budget GRADLE_OPTS="-Xmx2048m" ./gradlew bootRun

debug-with-local-db:
	BPS_DATASOURCE=jdbc:postgresql://localhost:5442/budget java -Xmx2048m -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005 -jar build/libs/budget-planner-server-0.0.1-SNAPSHOT.jar

start-with-remote-db:
ifndef dbUser
	@echo "Provde the dbUser variable"
	exit 1
endif
ifndef dbPassword
	@echo "Provde the dbPassword variable"
	exit 1
endif
	BPS_DB_PASSWORD=$(dbPassword) BPS_DB_USER=$(dbUser) BPS_DATASOURCE=jdbc:postgresql://localhost:6015/budget GRADLE_OPTS="-Xmx2048m" ./gradlew bootRun

debug-with-remote-db:
ifndef dbUser
	@echo "Provde the dbUser variable"
	exit 1
endif
ifndef dbPassword
	@echo "Provde the dbPassword variable"
	exit 1
endif
	BPS_DB_PASSWORD=$(dbPassword) BPS_DB_USER=$(dbUser) BPS_DATASOURCE=jdbc:postgresql://localhost:6015/budget java -Xmx2048m -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005 -jar build/libs/budget-planner-server-0.0.1-SNAPSHOT.jar

tunnel-db:
ifndef host
	@echo "Provde the host variable"
	exit 1
endif
ifndef dbServer
	@echo "Provde the hostName variable"
	exit 1
endif
	ssh $(host) -L 6015:$(dbServer):5432

tunnel-prod-db:
	make tunnel-db host=citybudgets.in dbServer=cwas-cm-sql.postgres.database.azure.com