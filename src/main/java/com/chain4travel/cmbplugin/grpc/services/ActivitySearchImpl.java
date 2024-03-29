package com.chain4travel.cmbplugin.grpc.services;

import build.buf.gen.cmp.services.activity.v1alpha.ActivitySearchRequest;
import build.buf.gen.cmp.services.activity.v1alpha.ActivitySearchResponse;
import build.buf.gen.cmp.services.activity.v1alpha.ActivitySearchServiceGrpc;
import build.buf.gen.cmp.types.v1alpha.SearchResponseMetadata;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class ActivitySearchImpl extends ActivitySearchServiceGrpc.ActivitySearchServiceImplBase{
    @Override
    public void activitySearch(ActivitySearchRequest request, StreamObserver<ActivitySearchResponse> responseObserver) {
        ActivitySearchResponse response = ActivitySearchResponse.newBuilder().setMetadata(SearchResponseMetadata.newBuilder().setContext("Activity search response").build()).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
