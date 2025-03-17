package com.chain4travel.cmbplugin.grpc.services;

import static com.chain4travel.cmbplugin.grpc.metadata.MetadataInterceptor.*;

import build.buf.gen.cmp.services.accommodation.v3.AccommodationSearchRequest;
import build.buf.gen.cmp.services.accommodation.v3.AccommodationSearchResponse;
import build.buf.gen.cmp.services.accommodation.v3.AccommodationSearchServiceGrpc;
import build.buf.gen.cmp.types.v1.Header;
import build.buf.gen.cmp.types.v1.ResponseHeader;
import build.buf.gen.cmp.types.v1.StatusType;
import build.buf.gen.cmp.types.v3.SearchResponseMetadata;
import com.chain4travel.cmbplugin.cache.CacheSearchType;
import com.chain4travel.cmbplugin.cache.CacheService;
import io.grpc.stub.StreamObserver;
import java.util.UUID;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

@GrpcService
public class AccommodationSearchImpl
    extends AccommodationSearchServiceGrpc.AccommodationSearchServiceImplBase {

  @Autowired private CacheService cacheService;

  @Value("${cmbplugin.cache.enabled}")
  private boolean cacheEnabled;

  @Override
  public void accommodationSearch(
      AccommodationSearchRequest request,
      StreamObserver<AccommodationSearchResponse> responseObserver) {
    String recipient = METADATA_CTX_KEY.get();

    // TODO call recipient's legacy system.

    var searchId = UUID.randomUUID();

    AccommodationSearchResponse response =
        AccommodationSearchResponse.newBuilder()
            .setHeader(
                ResponseHeader.newBuilder()
                    .setStatus(StatusType.STATUS_TYPE_SUCCESS)
                    .setBaseHeader(Header.getDefaultInstance())
                    .build())
            .setMetadata(
                SearchResponseMetadata.newBuilder()
                    .setSearchId(
                        build.buf.gen.cmp.types.v1.UUID.newBuilder().setValue(searchId.toString()))
                    .setContext("Accommodation search response from plugin"))
            .build();

    // If legacy system is not stateful, store data in cache for later validation
    if (cacheEnabled) {
      cacheService.cacheSearchData(searchId, CacheSearchType.accommodation, request, response);
    }

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }
}
