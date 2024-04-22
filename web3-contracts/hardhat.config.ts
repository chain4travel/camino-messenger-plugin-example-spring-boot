import * as dotenv from "dotenv";
import { HardhatUserConfig } from "hardhat/config";
import "@nomicfoundation/hardhat-toolbox";
import "hardhat-abi-exporter";


dotenv.config();


const config: HardhatUserConfig = {
    solidity: "0.8.24",
    networks: {
        columbus: {
            url: 'https://columbus.camino.network/ext/bc/C/rpc',
            accounts: [
                process.env.COLUMBUS_WALLET as string,
            ]
        },
        camino: {
            url: 'https://api.camino.network/ext/bc/C/rpc',
            accounts: [
                process.env.CAMINO_WALLET as string,
            ]
        },
    },
    abiExporter: {
        path: 'abi',
    }
};

export default config;
