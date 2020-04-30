package com.nabiki.corona.kernel.data;

import java.util.Collection;

import com.nabiki.corona.kernel.api.KerError;
import com.nabiki.corona.kernel.api.KerOrderEvalue;
import com.nabiki.corona.kernel.api.KerPositionDetail;

public class KerOrderEvalueImpl implements KerOrderEvalue {

	public KerOrderEvalueImpl() {
		// TODO constructor
	}

	@Override
	public KerError error() {
		// TODO error
		return null;
	}

	@Override
	public Collection<KerPositionDetail> positionToClose() {
		// TODO positions
		return null;
	}

}
