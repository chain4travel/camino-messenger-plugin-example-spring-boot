package com.chain4travel.cmbplugin.grpc.services;

import build.buf.gen.cmp.services.accommodation.v1alpha.AccommodationSearchRequest;
import build.buf.gen.cmp.services.accommodation.v1alpha.AccommodationSearchResponse;
import build.buf.gen.cmp.services.accommodation.v1alpha.AccommodationSearchServiceGrpc;
import build.buf.gen.cmp.types.v1alpha.SearchResponseMetadata;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import static com.chain4travel.cmbplugin.grpc.metadata.MetadataInterceptor.METADATA_CTX_KEY;

@GrpcService
public class AccommodationSearchImpl extends AccommodationSearchServiceGrpc.AccommodationSearchServiceImplBase {

    @Override
    public void accommodationSearch(AccommodationSearchRequest request, StreamObserver<AccommodationSearchResponse> responseObserver) {
        String recipient = METADATA_CTX_KEY.get();
        //TODO call recipient's legacy system.

        AccommodationSearchResponse response = AccommodationSearchResponse.newBuilder().setMetadata(SearchResponseMetadata.newBuilder().setContext("Hello from Spring Boot grpc partner plugin!")).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
