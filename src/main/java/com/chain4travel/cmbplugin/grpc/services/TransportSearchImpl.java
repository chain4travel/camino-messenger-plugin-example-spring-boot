package com.chain4travel.cmbplugin.grpc.services;

import build.buf.gen.cmp.services.transport.v1alpha.TransportSearchRequest;
import build.buf.gen.cmp.services.transport.v1alpha.TransportSearchResponse;
import build.buf.gen.cmp.services.transport.v1alpha.TransportSearchServiceGrpc;
import build.buf.gen.cmp.types.v1alpha.SearchResponseMetadata;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class TransportSearchImpl extends TransportSearchServiceGrpc.TransportSearchServiceImplBase{
    @Override
    public void transportSearch(TransportSearchRequest request, StreamObserver<TransportSearchResponse> responseObserver) {
        TransportSearchResponse response = TransportSearchResponse.newBuilder().setMetadata(SearchResponseMetadata.newBuilder().setContext("Transport search response").build()).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
