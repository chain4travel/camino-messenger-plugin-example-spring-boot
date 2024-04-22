import { buildModule } from "@nomicfoundation/hardhat-ignition/modules";
const hardhat = require("hardhat")


const NFTMModule = buildModule("NFTMModule", (m) => {
    console.log(`Using network ${hardhat.network.name}`);

    const account = m.getAccount(0);

    const nft = m.contract("NFT", [], {
        from: account
    });

    return { nft };
});

export default NFTMModule;
