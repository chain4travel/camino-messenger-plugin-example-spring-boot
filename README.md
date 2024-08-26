# Getting Started

This is a simple example of a grpc server written in java spring-boot.
The project leverages the [grpc-server-spring-boot-starter](https://github.com/grpc-ecosystem/grpc-spring)
to start a grpc server and register grpc services.

Furthermore, it uses the [Buf build generated sdk for java](https://buf.build/docs/bsr/generated-sdks/overview) 
to generate the protocolbuffers java classes as well as the grpc stubs. (see mvn dependencies)

## Registering a new service
Add new class under the package `com.chain4travel.cmbplugin.grpc.services` and annotate it with `@GrpcService`.
Extend the generated ServiceImplBase class that you want to implement and override the service methods.
The service will be automatically registered and exposed.

## Calling a grpc service

An easy way to test a service is to use grpcurl. For installation steps, refer to https://github.com/fullstorydev/grpcurl

Call the ping service for instance with:

```sh
grpcurl -plaintext localhost:50052 cmp.services.ping.v1alpha1.PingService.Ping
```

You can list the services and methods with:
```sh
grpcurl -plaintext localhost:50052 list
```
or get the proto description with:
```sh
 grpcurl -plaintext localhost:50052 describe
 ```

## Implementing a grpc-client
An example grpc-client implementation is included in Ping Service client is implemented in the `com.chain4travel.cmbplugin.grpc.client` package.
It includes a `MetadataInterceptor` that adds the `recipient` as grpc metadata to the request.


## Versioning
This project uses semantic versioning and releases a new version regularly upon adding support of a newer release of the camino messenger protocol.
Version names do not have to match identically with the camino messenger protocol version, but they should be in sync.


## Dependencies

This chapter outlines the key dependencies required to build, package, and run the project. See also the pom.xml. Be sure to chose the right version when you download the packages from buf.build.

- **Maven**
  - Maven is a build automation tool primarily used for Java projects. It handles project dependencies, compiles the source code, runs tests, and packages the project.
  - Install Maven from [Maven's official website](https://maven.apache.org/).
  - Verify the installation by running `mvn -version` in your terminal.

- **Java Runtime Environment (JRE) 21**
  - The project requires Java Runtime Environment version 21 to run. Ensure that JRE 21 is installed on your system.
  - Download the appropriate version from the [official JDK website](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html).
  - Verify the installation by running `java -version` in your terminal.

- **gRPC 1.58**
  - gRPC is a high-performance, open-source framework for building remote procedure call (RPC) APIs. Version 1.58 is required for this project.
  - Ensure that gRPC 1.58 is included as a dependency in your `pom.xml` file or is installed in your environment.
  - More information on gRPC can be found on the [gRPC website](https://grpc.io/).

- **Protocol Buffers (proto3)**
  - Protocol Buffers, or protobuf, is a language-neutral, platform-neutral, extensible mechanism for serializing structured data. The project uses version 3 of the Protocol Buffers language, known as proto3.
  - Ensure proto3 is installed and configured for your environment. You can download the `protoc` compiler from the [official Protocol Buffers repository](https://github.com/protocolbuffers/protobuf).

- **protobuf-java 24.0**
  - The project requires the Java bindings for Protocol Buffers, specifically version 24.0.
  - Ensure that `protobuf-java` version 24.0 is included as a dependency in your `pom.xml` file.
  - You can find the protobuf Java library on [Maven Central](https://search.maven.org/artifact/com.google.protobuf/protobuf-java/24.0/jar).

Ensure all these dependencies are correctly set up and configured before attempting to build and run the project.

## Usage

- **mvn package**
  - After setting up Maven, the next step is to package the project. This step compiles the source code, runs the tests, and packages the compiled code into a JAR file.
  - Use the command `mvn package` to execute this step. This command creates a JAR file in the `target/` directory, which will be used in the following steps.

- **Run the Packaged JAR**
  - Once the JAR file is created, you can run the project using the Java command.
  - Use the following command to run the packaged application: `java -jar target/cmb-plugin-0.0.1-SNAPSHOT.jar`. The package version specified in pom.xml , so if the user changes the version in pom.xml the package name will change.