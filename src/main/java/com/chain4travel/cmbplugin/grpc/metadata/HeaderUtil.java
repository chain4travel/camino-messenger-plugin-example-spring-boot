package com.chain4travel.cmbplugin.grpc.metadata;

import build.buf.gen.cmp.types.v1.Header;
import build.buf.gen.cmp.types.v1.ResponseHeader;
import build.buf.gen.cmp.types.v1.StatusType;

/** Utility class for creating standard headers. */
public class HeaderUtil {

  /**
   * Creates a standard response header with the specified status.
   *
   * @param status The status to set in the header
   * @return A ResponseHeader with the standard base header and the specified status
   */
  public static ResponseHeader createResponseHeader(StatusType status) {
    return ResponseHeader.newBuilder()
        .setStatus(status)
        .setBaseHeader(Header.getDefaultInstance())
        .build();
  }

  /**
   * Creates a standard success response header.
   *
   * @return A ResponseHeader with SUCCESS status
   */
  public static ResponseHeader createSuccessHeader() {
    return createResponseHeader(StatusType.STATUS_TYPE_SUCCESS);
  }
}
