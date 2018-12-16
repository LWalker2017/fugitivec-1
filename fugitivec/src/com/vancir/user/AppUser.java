package com.vancir.user;

import java.io.Serializable;
import java.util.Set;

import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.User;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * AppUser
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class AppUser implements User, Serializable {

	// HACK: Serializable AppUser object
	private static final long serialVersionUID = 1L;
	
    protected String name;
	protected Set<String> roles;
	protected String account;
	protected String affiliation;
	protected Enrollment enrollment;
    protected String mspId;
	
	public AppUser(String name, String affiliation, String mspId, Enrollment enrollment) {
		this.name = name;
		this.affiliation = affiliation;
		this.mspId = mspId;
		this.enrollment = enrollment;
	}

	// NOTE: This method seems useless
	public boolean isEnrolled() {
		return this.enrollment != null;
	}
}