package com.chain4travel.cmbplugin.grpc.metadata;

import build.buf.gen.cmp.types.v1.Header;
import io.grpc.*;

/**
 * Simple interceptor that adds the base header to all responses. Services are still responsible for
 * setting the status.
 */
public class ResponseHeaderInterceptor implements ServerInterceptor {

  @Override
  public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
      ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {

    return next.startCall(call, headers);
  }

  /**
   * Creates a standard base header that can be used by all services. This method can be called from
   * services to get the standard header.
   */
  public static Header createBaseHeader() {
    return Header.getDefaultInstance();
  }
}
