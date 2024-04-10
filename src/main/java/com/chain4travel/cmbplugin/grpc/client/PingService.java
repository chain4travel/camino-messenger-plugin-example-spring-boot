

package com.chain4travel.cmbplugin.grpc.client;


import static io.grpc.Metadata.*;

import org.springframework.stereotype.Service;

import build.buf.gen.cmp.services.ping.v1alpha.PingRequest;
import build.buf.gen.cmp.services.ping.v1alpha.PingResponse;
import build.buf.gen.cmp.services.ping.v1alpha.PingServiceGrpc;
import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ForwardingClientCall;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import net.devh.boot.grpc.client.inject.GrpcClient;


@Service
public class PingService {

    private final static String                     RECIPIENT           = "recipient";
    private final static MetadataInterceptor        metadataInterceptor = new MetadataInterceptor();
    private final static CallOptions.Key<String>    metadataKey         = CallOptions.Key.create(RECIPIENT);

    @GrpcClient("cmb-client")
    private PingServiceGrpc.PingServiceBlockingStub synchronousClient;


    public PingResponse sendPing(String pingMessage, String recipientAddress) {
        PingRequest pingRequest = PingRequest.newBuilder().setPingMessage(pingMessage).build();
        PingResponse pingResponse = synchronousClient.withInterceptors(metadataInterceptor).withOption(metadataKey, recipientAddress).ping(pingRequest);
        return pingResponse;
    }


    private static class MetadataInterceptor implements ClientInterceptor {

        @Override
        public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {
            return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(next.newCall(method, callOptions)) {

                @Override
                public void start(Listener<RespT> responseListener, Metadata metadata) {
                    metadata.put(Metadata.Key.of("recipient", ASCII_STRING_MARSHALLER), callOptions.getOption(metadataKey));
                    super.start(responseListener, metadata);
                }
            };
        }
    }
}