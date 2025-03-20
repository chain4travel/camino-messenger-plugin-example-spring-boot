package com.chain4travel.cmbplugin.grpc.metadata;

import build.buf.gen.cmp.types.v1.ResponseHeader;
import build.buf.gen.cmp.types.v1.StatusType;
import io.grpc.*;
import io.grpc.ForwardingServerCall.SimpleForwardingServerCall;

/**
 * Interceptor that automatically sets a base header with default status (error) for all responses.
 * Services can then update just the status field.
 */
public class ResponseHeaderInterceptor implements ServerInterceptor {

  @Override
  public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
      ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {
    return next.startCall(
        new SimpleForwardingServerCall<ReqT, RespT>(call) {
          @Override
          public void sendMessage(RespT message) {
            if (message instanceof ResponseHeader) {
              ResponseHeader header = (ResponseHeader) message;
              if (header.equals(ResponseHeader.getDefaultInstance())) {
                // Set default header with error status
                ResponseHeader defaultHeader =
                    HeaderUtil.createResponseHeader(StatusType.STATUS_TYPE_UNSPECIFIED);
                @SuppressWarnings("unchecked")
                RespT newMessage = (RespT) defaultHeader;
                super.sendMessage(newMessage);
                return;
              }
            }
            super.sendMessage(message);
          }
        },
        headers);
  }
}
