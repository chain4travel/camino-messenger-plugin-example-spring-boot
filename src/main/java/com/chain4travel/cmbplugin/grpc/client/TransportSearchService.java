package com.chain4travel.cmbplugin.grpc.client;

import static io.grpc.Metadata.*;

import build.buf.gen.cmp.services.transport.v3.TransportSearchRequest;
import build.buf.gen.cmp.services.transport.v3.TransportSearchResponse;
import build.buf.gen.cmp.services.transport.v3.TransportSearchServiceGrpc;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ForwardingClientCall;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Service
public class TransportSearchService {

  private static final String RECIPIENT = "recipient";
  private static final MetadataInterceptor metadataInterceptor = new MetadataInterceptor();
  private static final CallOptions.Key<String> metadataKey = CallOptions.Key.create(RECIPIENT);

  @GrpcClient("cmb-client")
  private TransportSearchServiceGrpc.TransportSearchServiceBlockingStub synchronousClient;

  public TransportSearchResponse sendTransportSearchRequest(
      String jsonString, String recipientAddress) {
    TransportSearchRequest.Builder transportSearchBuilder = TransportSearchRequest.newBuilder();

    try {
      JsonFormat.parser().ignoringUnknownFields().merge(jsonString, transportSearchBuilder);
    } catch (InvalidProtocolBufferException e) {
      throw new RuntimeException("Failed to parse JSON into TransportSearchRequest", e);
    }

    TransportSearchRequest transportSearchRequest = transportSearchBuilder.build();
    TransportSearchResponse transportSearchResponse =
        synchronousClient
            .withInterceptors(metadataInterceptor)
            .withOption(metadataKey, recipientAddress)
            .transportSearch(transportSearchRequest);
    return transportSearchResponse;
  }

  private static class MetadataInterceptor implements ClientInterceptor {

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
        MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {
      return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(
          next.newCall(method, callOptions)) {

        @Override
        public void start(Listener<RespT> responseListener, Metadata metadata) {
          metadata.put(
              Metadata.Key.of("recipient", ASCII_STRING_MARSHALLER),
              callOptions.getOption(metadataKey));
          super.start(responseListener, metadata);
        }
      };
    }
  }
}
