package sol;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 4.5.16.
 */
@SuppressWarnings("rawtypes")
public class ConvertLib extends Contract {
    public static final String BINARY = "0x60c3610025600b82828239805160001a60731461001857fe5b30600052607381538281f3fe730000000000000000000000000000000000000000301460806040526004361060335760003560e01c806396e4ee3d146038575b600080fd5b606b60048036036040811015604c57600080fd5b8101908080359060200190929190803590602001909291905050506081565b6040518082815260200191505060405180910390f35b600081830290509291505056fea265627a7a7231582046d45afb8c9bc048fc7a0827f5ab5d86595312770a8e017e92d60175c9f8088164736f6c63430005100032";

    public static final String FUNC_CONVERT = "convert";

    protected static final HashMap<String, String> _addresses;

    static {
        _addresses = new HashMap<String, String>();
        _addresses.put("5777", "0x5a0Db6cf415c74A1b6329AeA61B717E8b2c074E8");
    }

    @Deprecated
    protected ConvertLib(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected ConvertLib(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected ConvertLib(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected ConvertLib(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteFunctionCall<BigInteger> convert(BigInteger amount, BigInteger conversionRate) {
        final Function function = new Function(FUNC_CONVERT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(amount), 
                new org.web3j.abi.datatypes.generated.Uint256(conversionRate)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    @Deprecated
    public static ConvertLib load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new ConvertLib(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static ConvertLib load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new ConvertLib(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static ConvertLib load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new ConvertLib(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static ConvertLib load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new ConvertLib(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<ConvertLib> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(ConvertLib.class, web3j, credentials, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<ConvertLib> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(ConvertLib.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<ConvertLib> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(ConvertLib.class, web3j, transactionManager, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<ConvertLib> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(ConvertLib.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    protected String getStaticDeployedAddress(String networkId) {
        return _addresses.get(networkId);
    }

    public static String getPreviouslyDeployedAddress(String networkId) {
        return _addresses.get(networkId);
    }
}
