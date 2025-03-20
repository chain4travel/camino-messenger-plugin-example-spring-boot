package com.chain4travel.cmbplugin.grpc.services;

import build.buf.gen.cmp.services.ping.v1.PingRequest;
import build.buf.gen.cmp.services.ping.v1.PingResponse;
import build.buf.gen.cmp.services.ping.v1.PingServiceGrpc;
import com.chain4travel.cmbplugin.grpc.metadata.HeaderUtil;
import com.google.protobuf.Timestamp;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class PingImpl extends PingServiceGrpc.PingServiceImplBase {

  @Override
  public void ping(PingRequest request, StreamObserver<PingResponse> responseObserver) {
    // Current time as timestamp
    long currentTimeMillis = System.currentTimeMillis();
    Timestamp timestamp =
        Timestamp.newBuilder()
            .setSeconds(currentTimeMillis / 1000)
            .setNanos((int) ((currentTimeMillis % 1000) * 1000000))
            .build();

    responseObserver.onNext(
        PingResponse.newBuilder()
            .setPingMessage(request.getPingMessage())
            // Setting Headers is required - bot will check if the Headers Exists
            // If your business logic suggests Failure, return a different success type
            .setHeader(HeaderUtil.createSuccessHeader())
            .setTimestamp(timestamp)
            .build());

    responseObserver.onCompleted();
  }
}
