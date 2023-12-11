package com.chain4travel.cmbplugin.grpc.metadata;

import io.grpc.*;

public class MetadataInterceptor implements io.grpc.ServerInterceptor{

    public static final Context.Key<String> METADATA_CTX_KEY = Context.key("metadata");
    public static final Metadata.Key<String> METADATA_RECIPIENT_KEY =
            Metadata.Key.of("recipient", Metadata.ASCII_STRING_MARSHALLER);

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata metadata, ServerCallHandler<ReqT, RespT> next) {
        return Contexts.interceptCall(Context.current().withValue(METADATA_CTX_KEY, metadata.get(METADATA_RECIPIENT_KEY)), call, metadata, next);
    }
}
