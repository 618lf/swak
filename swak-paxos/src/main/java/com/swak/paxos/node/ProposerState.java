package com.swak.paxos.node;

/**
 * ProposerState
 * 
 * @author lifeng
 * @date 2020年12月28日 上午11:53:43
 */
public class ProposerState {
	private long proposalID;
	private long highestOtherProposalID;
	private byte[] value;
	private BallotNumber highestOtherPreAcceptBallot = new BallotNumber(0, 0);

	public ProposerState() {
		this.highestOtherProposalID = 0;
		this.value = new byte[] {};
	}

	public void setStartProposalID(long proposalID) {
		this.proposalID = proposalID;
	}

	public void newPrepare() {
		long maxProposalId = this.proposalID > this.highestOtherProposalID ? this.proposalID
				: this.highestOtherProposalID;
		this.proposalID = maxProposalId + 1;
	}

	public long getProposalID() {
		return proposalID;
	}

	public byte[] getValue() {
		return this.value;
	}

	public void setValue(byte[] value) {
		this.value = value;
	}

	public void setOtherProposalID(long otherProposalID) {
		if (otherProposalID > this.highestOtherProposalID) {
			this.highestOtherProposalID = otherProposalID;
		}
	}

	public void resetHighestOtherPreAcceptBallot() {
		this.highestOtherPreAcceptBallot.reset();
	}

	public long getHighestOtherProposalID() {
		return highestOtherProposalID;
	}

	public void addPreAcceptValue(BallotNumber otherPreAcceptBallot, byte[] otherPreAcceptValue) {
		if (otherPreAcceptBallot.isNull()) {
			return;
		}

		if (otherPreAcceptBallot.gt(this.highestOtherPreAcceptBallot)) {
			this.highestOtherPreAcceptBallot = otherPreAcceptBallot;
			this.value = otherPreAcceptValue;
		}
	}
}
