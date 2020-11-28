package com.company;

import kotlin.Experimental;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;


import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

public class Main {

    private final static String PRIVATE_KEY = "a1ed9b0067b2198e8a8841fcedc885fefe9964477ead2f3aad01f66acd08d0b8";

    private final static BigInteger GAS_LIMIT = BigInteger.valueOf(6721975L);
    private final static BigInteger GAS_Price = BigInteger.valueOf(20000000000L);

    private final static String RECIPIENT = "0x698A790ffE1D238a46C01F7a055C8117Aa725743";

    public static void main(String[] args) {
        try {
            new Main();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    private Main() throws Exception {
            Web3j web3j = Web3j.build(new HttpService());

            TransactionManager tansactionManager = new RawTransactionManager(
                    web3j,
                    getCredentialsFromPrivateKey(),
                    (byte)1 //Ethereum chain type (1 is default)
            );

        Transfer transfer = new Transfer(web3j, tansactionManager);

        TransactionReceipt transactionReceipt = transfer.sendFunds(
                RECIPIENT,
                BigDecimal.ONE, //amount to send
                Convert.Unit.ETHER,
                GAS_Price,
                GAS_LIMIT
        ).send();

    }

    public static void printWeb3ClientVersion(Web3j web3j) {
        Web3ClientVersion clientVersion = null;

        try {
            clientVersion = web3j.web3ClientVersion().send();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        String clientVersionString = clientVersion.getWeb3ClientVersion();
        System.out.println("Web3 client version: " + clientVersionString);
    }

    private Credentials getCredentialsFromWallet() throws IOException, CipherException {
        return WalletUtils.loadCredentials("password", "wallet/path");
    }

    /**
     * Get credentials based on a ganache wallet
     * @return
     */
    private Credentials getCredentialsFromPrivateKey() {
        return Credentials.create(PRIVATE_KEY);
    }
}
