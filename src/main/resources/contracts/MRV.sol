// SPDX-License-Identifier: Apache-2.0

pragma solidity >=0.6.0 <0.8.0;
pragma abicoder v2;

import '@openzeppelin/contracts/access/Ownable.sol';
import '@openzeppelin/contracts/token/ERC20/ERC20.sol';

contract MRV is Ownable {

    MRVData public data;

    Claim[] public pendingClaims;
    Claim[] public processedClaims;

    bool private _accepted;

    struct MRVData {
        bytes32 id;
        string name;
        string description;
        bytes32 ownerApprovalSignature;
        bytes32 verifierApprovalSignature;
        address verifier;
        string verificationAgreementDate;
        VerificationStandard verificationStandard;
    }

    struct MRVRequirement {
        string measurementModel;
        string verifiedLink;
    }

    struct Claim {
        bytes32 id;
        bytes32 projectId;
        string status;
        string linkToProcessingData;
        string linkToTermsAndConditions;
    }

    struct VerificationStandard {
        string protocolVersion;
        string verifiedLink;
    }

    event ContractCreated(
        bytes32 indexed id,
        string  name,
        address indexed owner
    );

    event ContractAccepted(
        bytes32 indexed id,
        string indexed name,
        address indexed verifier
    );

    event ClaimAdded(
        bytes32 indexed id,
        bytes32 indexed projectId,
        address indexed owner
    );

    event ClaimAccepted(
        bytes32 indexed id,
        bytes32 indexed projectId,
        address indexed verifier
    );

    event TokenIssued(
        bytes32 indexed id,
        string  name,
        address indexed owner
    );

    constructor (MRVData memory _data) {
        data = _data;
        emit ContractCreated(data.id, data.name, owner());
    }

    /*
     * Accept contract by a verifier.
     *
     * Throws if the contract is already accepted.
     */
    function acceptContract() public onlyVerifier {
        require(!_accepted, "Contract already accepted");
        _accepted = true;

        emit ContractAccepted(data.id, data.name, data.verifier);
    }

    /*
     * Adds a new claim to the contract.
     *
     * Throws if the contract has not been accepted yet.
     */
    function addClaim(Claim memory claim) public onlyOwner {
        require(_accepted, "Contract not accepted yet");
        pendingClaims.push(claim);

        emit ClaimAdded(claim.id, claim.projectId, owner());
    }

    /**
     * Accepts last added claim and issues a token if there are no more pending claims.
     *
     * Throws if the contract is not accepted or there are no pending claims.
     */
    function acceptClaim() public onlyVerifier {
        require(_accepted, "Contract not accepted yet");
        require(pendingClaims.length > 0, "No pending claims to accept");

        Claim memory claim = pendingClaims[pendingClaims.length - 1];
        delete pendingClaims[pendingClaims.length - 1];

        processedClaims.push(claim);
        emit ClaimAccepted(claim.id, claim.projectId, data.verifier);

        if (pendingClaims.length == 0) {
            // TODO Issue token
            ERC20 token = new ERC20(data.name, data.name);
            emit TokenIssued(data.id, data.name, owner());
        }
    }

    /**
     * Throws if called by any account other than the verifier.
     */
    modifier onlyVerifier() {
        require(data.verifier == _msgSender(), "Caller is not the contract verifier");
        _;
    }
}
