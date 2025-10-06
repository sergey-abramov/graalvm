build-ServerlessWebNativeFunction:
	mvn clean package
	mkdir -p $(ARTIFACTS_DIR)/lib
	cp ./target/Serverless*.jar $(ARTIFACTS_DIR)/lib/