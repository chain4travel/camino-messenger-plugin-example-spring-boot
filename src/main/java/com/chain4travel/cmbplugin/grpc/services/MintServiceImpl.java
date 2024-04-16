

package com.chain4travel.cmbplugin.grpc.services;


import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.chain4travel.cmbplugin.cache.CacheService;
import com.chain4travel.cmbplugin.grpc.converter.MessageConverter;

import build.buf.gen.cmp.services.book.v1alpha.MintRequest;
import build.buf.gen.cmp.services.book.v1alpha.MintResponse;
import build.buf.gen.cmp.services.book.v1alpha.MintServiceGrpc.MintServiceImplBase;
import build.buf.gen.cmp.services.book.v1alpha.ValidationRequest;
import build.buf.gen.cmp.services.book.v1alpha.ValidationResponse;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;


@GrpcService
public class MintServiceImpl extends MintServiceImplBase {

    @Autowired
    private CacheService cacheService;

    @Value("${cmbplugin.cache.enabled}")
    private boolean      cacheEnabled;


    @Override
    public void mint(MintRequest request, StreamObserver<MintResponse> responseObserver) {
        var validationId = UUID.fromString(request.getValidationId());

        // If legacy system is not stateful, get data from the cache and then mint
        if (cacheEnabled) {
            var cachedRequest = MessageConverter.readValidationRequest(cacheService.readValidationRequestData(validationId));
            var cachedResponse = MessageConverter.readValidationResponse(cacheService.readValidationResponseData(validationId));

            mintAccommodation(validationId, cachedRequest, cachedResponse);
        }
        else {
            mintAccommodation(validationId);
        }

        // TODO add cases for other search types like transport search etc.

        var mintId = UUID.randomUUID();
        var response = MintResponse.newBuilder().setMintId(mintId.toString()).setValidationId(request.getValidationId()).build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }


    private void mintAccommodation(UUID validationId, ValidationRequest cachedRequest, ValidationResponse cachedResponse) {
        // TODO mint accommodation with data from cache
    }


    private void mintAccommodation(UUID validationId) {
        // TODO mint accommodation by handing over just the validation id to the legacy system
    }
}
