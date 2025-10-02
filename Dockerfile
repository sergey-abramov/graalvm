# Stage 1: Build the native executable
FROM springio/spring-boot-native-builder:latest AS build

WORKDIR /workspace

# Copy Maven project files
COPY .mvn .mvn
COPY mvnw .
COPY pom.xml .
COPY src src

# Build the native executable
RUN ./mvnw -Pnative -DskipTests native:compile

# Stage 2: Create the Lambda runtime image
FROM public.ecr.aws/lambda/provided:al2

# Copy the native executable from the build stage
COPY --from=build /workspace/target/bootstrap /var/task/bootstrap

CMD ["/var/task/bootstrap"]