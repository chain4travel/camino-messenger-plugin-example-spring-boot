package com.chain4travel.cmbplugin.grpc.converter;

import build.buf.gen.cmp.services.accommodation.v3.AccommodationSearchRequest;
import build.buf.gen.cmp.services.accommodation.v3.AccommodationSearchResponse;
import build.buf.gen.cmp.services.book.v2.ValidationRequest;
import build.buf.gen.cmp.services.book.v2.ValidationResponse;
import build.buf.gen.cmp.services.transport.v3.TransportSearchRequest;
import build.buf.gen.cmp.services.transport.v3.TransportSearchResponse;
import java.io.IOException;
import java.io.InputStream;

public class MessageConverter {

  public static AccommodationSearchRequest readAccommodationSearchRequest(InputStream inputStream) {
    if (inputStream == null) {
      return null;
    }

    try {
      return AccommodationSearchRequest.newBuilder().mergeFrom(inputStream).build();
    } catch (IOException e) {
      return null;
    }
  }

  public static AccommodationSearchResponse readAccommodationSearchResponse(
      InputStream inputStream) {
    if (inputStream == null) {
      return null;
    }

    try {
      return AccommodationSearchResponse.newBuilder().mergeFrom(inputStream).build();
    } catch (IOException e) {
      return null;
    }
  }

  public static TransportSearchRequest readTransportSearchRequest(InputStream inputStream) {
    if (inputStream == null) {
      return null;
    }

    try {
      return TransportSearchRequest.newBuilder().mergeFrom(inputStream).build();
    } catch (IOException e) {
      return null;
    }
  }

  public static TransportSearchResponse readTransportSearchResponse(InputStream inputStream) {
    if (inputStream == null) {
      return null;
    }

    try {
      return TransportSearchResponse.newBuilder().mergeFrom(inputStream).build();
    } catch (IOException e) {
      return null;
    }
  }

  public static ValidationRequest readValidationRequest(InputStream inputStream) {
    if (inputStream == null) {
      return null;
    }

    try {
      return ValidationRequest.newBuilder().mergeFrom(inputStream).build();
    } catch (IOException e) {
      return null;
    }
  }

  public static ValidationResponse readValidationResponse(InputStream inputStream) {
    if (inputStream == null) {
      return null;
    }

    try {
      return ValidationResponse.newBuilder().mergeFrom(inputStream).build();
    } catch (IOException e) {
      return null;
    }
  }
}
