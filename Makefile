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
	BPS_DATASOURCE=jdbc:postgresql://localhost:5442/budget GRADLE_OPTS="-Xmx256m" ./gradlew bootRun

debug-with-local-db:
	BPS_DATASOURCE=jdbc:postgresql://localhost:5442/budget java -Xmx2048m -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005 -jar build/libs/budget-planner-server-0.0.1-SNAPSHOT.jar
