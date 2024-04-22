

package com.chain4travel.cmbplugin.grpc.services;


import java.math.BigInteger;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.chain4travel.cmbplugin.cache.CacheService;
import com.chain4travel.cmbplugin.grpc.converter.MessageConverter;
import com.chain4travel.cmbplugin.web3.Web3Service;
import com.chain4travel.cmbplugin.web3.model.NFT;

import build.buf.gen.cmp.services.book.v1alpha.MintRequest;
import build.buf.gen.cmp.services.book.v1alpha.MintResponse;
import build.buf.gen.cmp.services.book.v1alpha.MintServiceGrpc.MintServiceImplBase;
import build.buf.gen.cmp.services.book.v1alpha.ValidationRequest;
import build.buf.gen.cmp.services.book.v1alpha.ValidationResponse;
import build.buf.gen.cmp.types.v1alpha.BookingToken;
import io.grpc.stub.StreamObserver;
import jakarta.annotation.PostConstruct;
import net.devh.boot.grpc.server.service.GrpcService;


@GrpcService
public class MintServiceImpl extends MintServiceImplBase {

    private final static Logger logger = LoggerFactory.getLogger(MintServiceImpl.class);

    @Autowired
    private CacheService        cacheService;

    @Autowired
    private Web3Service         web3Service;

    @Value("${cmbplugin.cache.enabled}")
    private boolean             cacheEnabled;

    private NFT                 contract;


    @PostConstruct
    private void setup() {
        contract = web3Service.loadSmartContract();
    }


    @Override
    public void mint(MintRequest request, StreamObserver<MintResponse> responseObserver) {
        var validationId = UUID.fromString(request.getValidationId());

        // If legacy system is not stateful, get data from the cache and then mint
        if (cacheEnabled) {
            var cachedRequest = MessageConverter.readValidationRequest(cacheService.readValidationRequestData(validationId));
            var cachedResponse = MessageConverter.readValidationResponse(cacheService.readValidationResponseData(validationId));

            mintAccommodation(validationId, cachedRequest, cachedResponse);
        }
        else {
            mintAccommodation(validationId);
        }

        // TODO add cases for other search types like transport search etc.

        var mintId = UUID.randomUUID();
        var tokenId = convertUUIDToBigInteger(mintId);
        var response = MintResponse.newBuilder().setMintId(mintId.toString()).setValidationId(request.getValidationId());

        try {
            var receipt = contract.mint(request.getBuyerAddress(), tokenId).send();
            // TODO set token id with correct data type
            var bookingToken = BookingToken.newBuilder().setContract(contract.getContractAddress()).setTokenId(tokenId.intValue());

            response.setBookingToken(bookingToken);
            response.setMintTransactionId(receipt.getTransactionHash());

            logger.debug("Token with token id {} minted. TX hash {}", tokenId, receipt.getTransactionHash());
        }
        catch (Exception e) {
            logger.error("Could not mint token.", e);
        }

        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }


    private void mintAccommodation(UUID validationId, ValidationRequest cachedRequest, ValidationResponse cachedResponse) {
        // TODO mint accommodation with data from cache
    }


    private void mintAccommodation(UUID validationId) {
        // TODO mint accommodation by handing over just the validation id to the legacy system
    }


    private BigInteger convertUUIDToBigInteger(UUID id) {
        var idString = id.toString();
        var sanbitizedIdString = idString.replaceAll("-", "");
        var bigInteger = new BigInteger(sanbitizedIdString, 16);
        return bigInteger;

    }
}
