package com.chain4travel.cmbplugin.grpc.services;

import build.buf.gen.cmp.services.accommodation.v1alpha1.AccommodationSearchRequest;
import build.buf.gen.cmp.services.accommodation.v1alpha1.AccommodationSearchResponse;
import build.buf.gen.cmp.services.accommodation.v1alpha1.AccommodationSearchServiceGrpc;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import static com.chain4travel.cmbplugin.grpc.metadata.MetadataInterceptor.METADATA_CTX_KEY;

@GrpcService
public class AccommodationSearchImpl extends AccommodationSearchServiceGrpc.AccommodationSearchServiceImplBase {

    @Override
    public void accommodationSearch(AccommodationSearchRequest request, StreamObserver<AccommodationSearchResponse> responseObserver) {
        String recipient = METADATA_CTX_KEY.get();
        //TODO call recipient's legacy system.

        AccommodationSearchResponse response = AccommodationSearchResponse.newBuilder().setContext("Hello from Spring Boot grpc partner plugin!").build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
