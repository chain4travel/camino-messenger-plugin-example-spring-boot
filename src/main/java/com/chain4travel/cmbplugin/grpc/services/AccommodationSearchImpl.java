

package com.chain4travel.cmbplugin.grpc.services;


import static com.chain4travel.cmbplugin.grpc.metadata.MetadataInterceptor.*;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.chain4travel.cmbplugin.cache.CacheSearchType;
import com.chain4travel.cmbplugin.cache.CacheService;

import build.buf.gen.cmp.services.accommodation.v1alpha.AccommodationSearchRequest;
import build.buf.gen.cmp.services.accommodation.v1alpha.AccommodationSearchResponse;
import build.buf.gen.cmp.services.accommodation.v1alpha.AccommodationSearchServiceGrpc;
import build.buf.gen.cmp.types.v1alpha.SearchResponseMetadata;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;


@GrpcService
public class AccommodationSearchImpl extends AccommodationSearchServiceGrpc.AccommodationSearchServiceImplBase {

    @Autowired
    private CacheService cacheService;

    @Value("${cmbplugin.cache.enabled}")
    private boolean      cacheEnabled;


    @Override
    public void accommodationSearch(AccommodationSearchRequest request, StreamObserver<AccommodationSearchResponse> responseObserver) {
        String recipient = METADATA_CTX_KEY.get();

        // TODO call recipient's legacy system.

        var searchId = UUID.randomUUID();
        AccommodationSearchResponse response = AccommodationSearchResponse.newBuilder().setMetadata(SearchResponseMetadata.newBuilder().setSearchId(build.buf.gen.cmp.types.v1alpha.UUID.newBuilder().setValue(searchId.toString())).setContext("Hello from Spring Boot grpc partner plugin!")).build();

        // If legacy system is not stateful, store data in cache for later validation
        if (cacheEnabled) {
            cacheService.cacheSearchData(searchId, CacheSearchType.accommodation, request, response);
        }

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
