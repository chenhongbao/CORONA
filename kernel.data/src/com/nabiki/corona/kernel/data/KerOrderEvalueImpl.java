package com.nabiki.corona.kernel.data;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.nabiki.corona.kernel.api.KerError;
import com.nabiki.corona.kernel.api.KerOrderEvalue;
import com.nabiki.corona.kernel.api.KerPositionDetail;

public class KerOrderEvalueImpl implements KerOrderEvalue {
	private KerError error = new KerError(0, "");
	private List<KerPositionDetail> details = new LinkedList<>();

	public KerOrderEvalueImpl() {
	}

	@Override
	public KerError error() {
		return this.error;
	}

	@Override
	public List<KerPositionDetail> positionToClose() {
		return this.details;
	}

	@Override
	public void error(KerError e) {
		this.error = e;
	}

	@Override
	public void positionToClose(KerPositionDetail p) {
		this.details.add(p);
	}

	@Override
	public void positionsToClose(Collection<KerPositionDetail> p) {
		this.details.addAll(p);
	}

}
