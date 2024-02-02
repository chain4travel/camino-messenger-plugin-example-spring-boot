package com.chain4travel.cmbplugin.grpc.services;

import build.buf.gen.cmp.services.ping.v1alpha.PingRequest;
import build.buf.gen.cmp.services.ping.v1alpha.PingResponse;
import build.buf.gen.cmp.services.ping.v1alpha.PingServiceGrpc;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class PingImpl extends PingServiceGrpc.PingServiceImplBase{

    @Override
    public void ping(PingRequest request, StreamObserver<PingResponse> responseObserver) {
        responseObserver.onNext(PingResponse.newBuilder().setPingMessage("Hello from Spring Boot grpc partner plugin!").build());
        responseObserver.onCompleted();
    }
}
