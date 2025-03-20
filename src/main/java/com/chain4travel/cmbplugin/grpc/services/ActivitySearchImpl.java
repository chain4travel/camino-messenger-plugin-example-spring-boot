package com.chain4travel.cmbplugin.grpc.services;

import build.buf.gen.cmp.services.activity.v3.ActivitySearchRequest;
import build.buf.gen.cmp.services.activity.v3.ActivitySearchResponse;
import build.buf.gen.cmp.services.activity.v3.ActivitySearchServiceGrpc;
import build.buf.gen.cmp.types.v3.SearchResponseMetadata;
import com.chain4travel.cmbplugin.grpc.metadata.HeaderUtil;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class ActivitySearchImpl extends ActivitySearchServiceGrpc.ActivitySearchServiceImplBase {
  @Override
  public void activitySearch(
      ActivitySearchRequest request, StreamObserver<ActivitySearchResponse> responseObserver) {
    ActivitySearchResponse response =
        ActivitySearchResponse.newBuilder()
            // Setting Headers is required - bot will check if the Headers Exists
            // If your business logic suggests Failure, return a different success type
            .setHeader(HeaderUtil.createSuccessHeader())
            .setMetadata(
                SearchResponseMetadata.newBuilder()
                    .setContext("Activity search response from plugin")
                    .build())
            .build();

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }
}
