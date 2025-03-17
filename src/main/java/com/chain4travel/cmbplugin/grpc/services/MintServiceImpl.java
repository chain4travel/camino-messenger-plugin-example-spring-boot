package com.chain4travel.cmbplugin.grpc.services;

import build.buf.gen.cmp.services.book.v2.MintRequest;
import build.buf.gen.cmp.services.book.v2.MintResponse;
import build.buf.gen.cmp.services.book.v2.MintServiceGrpc.MintServiceImplBase;
import build.buf.gen.cmp.services.book.v2.ValidationRequest;
import build.buf.gen.cmp.services.book.v2.ValidationResponse;
import build.buf.gen.cmp.types.v1.Header;
import build.buf.gen.cmp.types.v1.ResponseHeader;
import build.buf.gen.cmp.types.v1.StatusType;
import build.buf.gen.cmp.types.v2.Currency;
import build.buf.gen.cmp.types.v2.Price;
import com.chain4travel.cmbplugin.cache.CacheService;
import com.chain4travel.cmbplugin.grpc.converter.MessageConverter;
import com.google.protobuf.Empty;
import com.google.protobuf.Timestamp;
import io.grpc.stub.StreamObserver;
import java.util.UUID;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

@GrpcService
public class MintServiceImpl extends MintServiceImplBase {

  @Autowired private CacheService cacheService;

  @Value("${cmbplugin.cache.enabled}")
  private boolean cacheEnabled;

  @Override
  public void mint(MintRequest request, StreamObserver<MintResponse> responseObserver) {
    var validationId = UUID.fromString(request.getValidationId().getValue());

    // If legacy system is not stateful, get data from the cache and then mint
    if (cacheEnabled) {
      var cachedRequest =
          MessageConverter.readValidationRequest(
              cacheService.readValidationRequestData(validationId));
      var cachedResponse =
          MessageConverter.readValidationResponse(
              cacheService.readValidationResponseData(validationId));

      mintAccommodation(validationId, cachedRequest, cachedResponse);
    } else {
      mintAccommodation(validationId);
    }

    // TODO add cases for other search types like transport search etc.

    var mintId = UUID.randomUUID();

    // Current time as timestamp
    long currentTimeMillis = System.currentTimeMillis();
    Timestamp timestamp =
        Timestamp.newBuilder()
            .setSeconds(currentTimeMillis / 1000)
            .setNanos((int) ((currentTimeMillis % 1000) * 1000000))
            .build();

    var response =
        MintResponse.newBuilder()
            .setHeader(
                ResponseHeader.newBuilder()
                    .setStatus(StatusType.STATUS_TYPE_SUCCESS)
                    .setBaseHeader(Header.getDefaultInstance())
                    .build())
            .setMintId(build.buf.gen.cmp.types.v1.UUID.newBuilder().setValue(mintId.toString()))
            .setValidationId(request.getValidationId())
            .setProviderBookingTimestamp(timestamp)
            .setPrice(
                Price.newBuilder()
                    .setCurrency(
                        Currency.newBuilder().setNativeToken(Empty.getDefaultInstance()).build())
                    .setValue("000")
                    .setDecimals(2)
                    .build())
            .build();

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  private void mintAccommodation(
      UUID validationId, ValidationRequest cachedRequest, ValidationResponse cachedResponse) {
    // TODO mint accommodation with data from cache
  }

  private void mintAccommodation(UUID validationId) {
    // TODO mint accommodation by handing over just the validation id to the legacy
    // system
  }
}
