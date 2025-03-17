package com.chain4travel.cmbplugin.grpc.services;

import build.buf.gen.cmp.services.accommodation.v1.AccommodationSearchRequest;
import build.buf.gen.cmp.services.accommodation.v1.AccommodationSearchResponse;
import build.buf.gen.cmp.services.book.v1.ValidationRequest;
import build.buf.gen.cmp.services.book.v1.ValidationResponse;
import build.buf.gen.cmp.services.book.v1.ValidationServiceGrpc.ValidationServiceImplBase;
import com.chain4travel.cmbplugin.cache.CacheSearchType;
import com.chain4travel.cmbplugin.cache.CacheService;
import com.chain4travel.cmbplugin.grpc.converter.MessageConverter;
import io.grpc.stub.StreamObserver;
import java.util.UUID;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

@GrpcService
public class ValidationServiceImpl extends ValidationServiceImplBase {

  private static final Logger logger = LoggerFactory.getLogger(ValidationServiceImpl.class);

  @Autowired private CacheService cacheService;

  @Value("${cmbplugin.cache.enabled}")
  private boolean cacheEnabled;

  @Override
  public void validation(
      ValidationRequest request, StreamObserver<ValidationResponse> responseObserver) {
    var searchId =
        UUID.fromString(
            request.getValidationObject().getSearchIdentifier().getSearchId().getValue());

    // If legacy system is not stateful, get data from the cache and then validate
    if (cacheEnabled
        && cacheService.getCacheSearchType(searchId) == CacheSearchType.accommodation) {
      var cachedRequest =
          MessageConverter.readAccommodationSearchRequest(
              cacheService.readSearchRequestData(searchId));
      var cachedResponse =
          MessageConverter.readAccommodationSearchResponse(
              cacheService.readSearchResponseData(searchId));

      logger.debug("cachedRequest:\n{}", cachedRequest);
      logger.debug("cachedResponse:\n{}", cachedResponse);

      validateAccommodationSearch(searchId, cachedRequest, cachedResponse);
    } else {
      validateAccommodationSearch(searchId);
    }

    // TODO add cases for other search types like transport search etc.

    var validationId = UUID.randomUUID();
    var response =
        ValidationResponse.newBuilder()
            .setValidationId(
                build.buf.gen.cmp.types.v1.UUID.newBuilder().setValue(validationId.toString()))
            .build();

    if (cacheEnabled) {
      cacheService.cacheValidationData(validationId, request, response);
    }

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  private void validateAccommodationSearch(
      UUID searchId,
      AccommodationSearchRequest cachedRequest,
      AccommodationSearchResponse cachedResponse) {
    // TODO validate search with data from cache
  }

  private void validateAccommodationSearch(UUID searchId) {
    // TODO validate search by handing over just the search id to the legacy system
  }
}
