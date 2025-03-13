package com.chain4travel.cmbplugin.grpc.services;

import build.buf.gen.cmp.services.ping.v1.PingRequest;
import build.buf.gen.cmp.services.ping.v1.PingResponse;
import build.buf.gen.cmp.services.ping.v1.PingServiceGrpc;
import build.buf.gen.cmp.types.v1.Header;
import build.buf.gen.cmp.types.v1.ResponseHeader;
import build.buf.gen.cmp.types.v1.StatusType;
import com.google.protobuf.Timestamp;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class PingImpl extends PingServiceGrpc.PingServiceImplBase {

  @Override
  public void ping(PingRequest request, StreamObserver<PingResponse> responseObserver) {
    responseObserver.onNext(
        PingResponse.newBuilder().setPingMessage(request.getPingMessage()).build());
    responseObserver.onCompleted();
  }

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
            .setHeader(
                ResponseHeader.newBuilder()
                    .setStatus(StatusType.STATUS_TYPE_SUCCESS)
                    .setBaseHeader(Header.getDefaultInstance())
                    .build())
            .setTimestamp(timestamp)
            .build());

    responseObserver.onCompleted();
  }
}
