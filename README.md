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
grpcurl -plaintext localhost:9090 cmp.services.ping.v1alpha1.PingService.Ping
```

You can list the services and methods with:
```sh
grpcurl -plaintext localhost:9090 list
```
or get the proto description with:
```sh
 grpcurl -plaintext localhost:9090 describe
 ```
