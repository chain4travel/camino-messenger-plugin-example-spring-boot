// SPDX-License-Identifier: MIT

pragma solidity ^0.8.9;

import "@openzeppelin/contracts/token/ERC721/ERC721.sol";
import "@openzeppelin/contracts/access/AccessControl.sol";

contract NFT is ERC721, AccessControl {
    constructor() ERC721("MyNFT", "NFT") {
        _grantRole(DEFAULT_ADMIN_ROLE, msg.sender);
    }

    function mint(address to, uint256 tokenId) external onlyRole(DEFAULT_ADMIN_ROLE){
        _mint(to, tokenId);
    }

    function supportsInterface(bytes4 interfaceId) public view virtual override(ERC721, AccessControl) returns (bool) {
        return super.supportsInterface(interfaceId) || interfaceId == type(IAccessControl).interfaceId;
    }
}