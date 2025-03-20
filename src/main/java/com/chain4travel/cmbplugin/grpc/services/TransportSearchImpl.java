package com.chain4travel.cmbplugin.grpc.services;

import build.buf.gen.cmp.services.transport.v3.TransportSearchRequest;
import build.buf.gen.cmp.services.transport.v3.TransportSearchResponse;
import build.buf.gen.cmp.services.transport.v3.TransportSearchServiceGrpc;
import build.buf.gen.cmp.types.v3.SearchResponseMetadata;
import com.chain4travel.cmbplugin.grpc.metadata.HeaderUtil;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class TransportSearchImpl extends TransportSearchServiceGrpc.TransportSearchServiceImplBase {
  @Override
  public void transportSearch(
      TransportSearchRequest request, StreamObserver<TransportSearchResponse> responseObserver) {
    TransportSearchResponse response =
        TransportSearchResponse.newBuilder()
            // Setting Headers is required - bot will check if the Headers Exists
            // If your business logic suggests Failure, return a different success type
            .setHeader(HeaderUtil.createSuccessHeader())
            .setMetadata(
                SearchResponseMetadata.newBuilder()
                    .setContext("Transport search response from plugin")
                    .build())
            .build();

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }
}
