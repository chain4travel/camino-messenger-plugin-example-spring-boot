package com.chain4travel.cmbplugin.grpc.client;


import build.buf.gen.cmp.services.ping.v1alpha.PingRequest;
import build.buf.gen.cmp.services.ping.v1alpha.PingResponse;
import build.buf.gen.cmp.services.ping.v1alpha.PingServiceGrpc;
import com.google.protobuf.Descriptors;
import io.grpc.*;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.Map;

import static io.grpc.Metadata.ASCII_STRING_MARSHALLER;

@Service
public class PingService {

    public static final String RECIPIENT = "recipient";
    static MetadataInterceptor metadataInterceptor = new MetadataInterceptor();
    static CallOptions.Key<String> metadataKey = CallOptions.Key.create(RECIPIENT);
    @GrpcClient("cmb-client")
    PingServiceGrpc.PingServiceBlockingStub synchronousClient;

    static class MetadataInterceptor implements ClientInterceptor {
        @Override
        public <ReqT, RespT> ClientCall<ReqT, RespT>
        interceptCall(
                MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next
        ) {
            return new
                    ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(next.newCall(method, callOptions)) {
                        @Override
                        public void start(Listener<RespT> responseListener, Metadata metadata) {
                            metadata.put(Metadata.Key.of("recipient", ASCII_STRING_MARSHALLER), callOptions.getOption(metadataKey));
                            super.start(responseListener, metadata);
                        }
                    };
        }
    }

    public Map<Descriptors.FieldDescriptor, Object> sendPing(String pingMessage, String recipientAddress) {
        PingRequest pingRequest = PingRequest.newBuilder().setPingMessage(pingMessage).build();
        PingResponse pingResponse = synchronousClient.withInterceptors(metadataInterceptor).withOption(metadataKey, recipientAddress).ping(pingRequest);
        return pingResponse.getAllFields();
    }

}