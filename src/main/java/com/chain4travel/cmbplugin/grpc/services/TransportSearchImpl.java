package com.chain4travel.cmbplugin.grpc.services;

import build.buf.gen.cmp.services.transport.v3.TransportSearchRequest;
import build.buf.gen.cmp.services.transport.v3.TransportSearchResponse;
import build.buf.gen.cmp.services.transport.v3.TransportSearchServiceGrpc;
import build.buf.gen.cmp.types.v1.Header;
import build.buf.gen.cmp.types.v1.ResponseHeader;
import build.buf.gen.cmp.types.v1.StatusType;
import build.buf.gen.cmp.types.v3.SearchResponseMetadata;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class TransportSearchImpl extends TransportSearchServiceGrpc.TransportSearchServiceImplBase {
  @Override
  public void transportSearch(
      TransportSearchRequest request, StreamObserver<TransportSearchResponse> responseObserver) {
    TransportSearchResponse response =
        TransportSearchResponse.newBuilder()
            .setHeader(
                ResponseHeader.newBuilder()
                    .setStatus(StatusType.STATUS_TYPE_SUCCESS)
                    .setBaseHeader(Header.getDefaultInstance())
                    .build())
            .setMetadata(
                SearchResponseMetadata.newBuilder()
                    .setContext("Transport search response from plugin")
                    .build())
            .build();

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }
}
