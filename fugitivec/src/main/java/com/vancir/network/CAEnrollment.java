package com.vancir.network;

import java.security.PrivateKey;
import org.hyperledger.fabric.sdk.Enrollment;

public class CAEnrollment implements Enrollment {
	private PrivateKey key;
	private String cert;

	public CAEnrollment(PrivateKey pKey, String certPem) {
		this.key = pKey;
		this.cert = certPem;
	}

	public PrivateKey getKey() {
		return key;
	}

	public String getCert() {
		return cert;
	}

}
