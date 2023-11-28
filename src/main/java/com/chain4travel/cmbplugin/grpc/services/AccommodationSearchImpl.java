package com.chain4travel.cmbplugin.grpc.services;

import build.buf.gen.cmp.services.accommodation.v1alpha1.AccommodationSearchRequest;
import build.buf.gen.cmp.services.accommodation.v1alpha1.AccommodationSearchResponse;
import build.buf.gen.cmp.services.accommodation.v1alpha1.AccommodationSearchServiceGrpc;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class AccommodationSearchImpl extends AccommodationSearchServiceGrpc.AccommodationSearchServiceImplBase {

    @Override
    public void accommodationSearch(AccommodationSearchRequest request, StreamObserver<AccommodationSearchResponse> responseObserver) {
        // TODO implement call to ext system convert response to proto and return
        AccommodationSearchResponse response = AccommodationSearchResponse.newBuilder().setContext("Hello from Spring Boot grpc partner plugin!").build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
