# Install Hardhat & dependencies

npm install --save-dev hardhat
npm install --save-dev hardhat-abi-exporter
npm install dotenv --save
npm install @openzeppelin/contracts
npm install @openzeppelin/contracts-upgradeable
npm install --save-dev @openzeppelin/hardhat-upgrades

To start a new project: npx hardhat init

# Compile contracts

npx hardhat compile


# Generate Java file for contract

npx hardhat export-abi

web3j generate solidity -a=abi/contracts/NFTM.sol/NFT.json -o=../src/main/java -p=com.chain4travel.cmbplugin.web3.model


# Deploy / Upgrade contract locally

npx hardhat node

npx hardhat ignition deploy ./ignition/modules/NFTM.ts --network localhost


# Deploy / upgrade contract on Columbus (Camino Testnet)

Create .env file with contents:

COLUMBUS_WALLET=<Camino Wallet -> Active Key -> View C Chain Private Key>

npx hardhat ignition deploy ./ignition/modules/NFTM.ts --network columbus


# Deploy / upgrade contract on Camino

Create .env file with contents:

CAMINO_WALLET=<Camino Wallet -> Manage Keys -> View Static Keys>

npx hardhat ignition deploy ./ignition/modules/NFTM.ts --network camino
