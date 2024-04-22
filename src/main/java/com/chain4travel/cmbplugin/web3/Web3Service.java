

package com.chain4travel.cmbplugin.web3;


import java.io.IOException;
import java.math.BigInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

import com.chain4travel.cmbplugin.web3.model.NFT;


@Service
public class Web3Service {

    private final static int  POLLING_MAX_ATTEMPTS  = 60;
    private final static long POLLING_MAX_WAIT_TIME = 1000;

    @Value("${cmbplugin.web3.network.chainUrl}")
    private String            chainUrl;

    @Value("${cmbplugin.web3.network.chainId}")
    private long              chainId;

    @Value("${cmbplugin.web3.smartContract.walletPrivateKey}")
    private String            walletPrivateKey;

    @Value("${cmbplugin.web3.smartContract.contractAddress}")
    private String            contractAddress;

    private Web3j             web3j;


    public NFT loadSmartContract() {
        var web3j = getWeb3j();
        var credentials = Credentials.create(walletPrivateKey);
        var transactionManager = new RawTransactionManager(web3j, credentials, chainId, POLLING_MAX_ATTEMPTS, POLLING_MAX_WAIT_TIME);
        var contract = NFT.load(contractAddress, web3j, transactionManager, new DynamicGasProvider(web3j));
        return contract;
    }


    public Web3j getWeb3j() {
        if (web3j == null) {
            web3j = Web3j.build(new HttpService(chainUrl));
        }

        return web3j;
    }


    private static class DynamicGasProvider implements ContractGasProvider {

        private final static Logger logger   = LoggerFactory.getLogger(DynamicGasProvider.class);

        private Web3j               web3j;
        private BigInteger          gasLimit = BigInteger.valueOf(8_000_000);


        public DynamicGasProvider(Web3j web3j) {
            this.web3j = web3j;
        }


        @Override
        public BigInteger getGasPrice(String contractFunc) {
            return getGasPrice();
        }


        @Override
        public BigInteger getGasPrice() {
            try {
                var gasPrice = web3j.ethGasPrice().send();
                return gasPrice.getGasPrice();
            }
            catch (IOException e) {
                logger.error("Could not get gas price from network.", e);
                return null;
            }
        }


        @Override
        public BigInteger getGasLimit(String contractFunc) {
            return getGasLimit();
        }


        @Override
        public BigInteger getGasLimit() {
            return gasLimit;
        }
    }
}
