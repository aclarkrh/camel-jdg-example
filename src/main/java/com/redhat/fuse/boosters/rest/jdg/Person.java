package com.redhat.fuse.boosters.rest.jdg;

import java.io.Serializable;

import org.infinispan.protostream.annotations.ProtoDoc;
import org.infinispan.protostream.annotations.ProtoField;

@ProtoDoc("@Indexed")
public class Person implements Serializable{
	private static final long serialVersionUID = 1L;
	private String uid;
	private String name;
	private String country;

	@ProtoField(number = 1)
	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

    @ProtoDoc("@Field(analyze = Analyze.YES)")
	@ProtoField(number = 2)
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	@ProtoDoc("@Field(analyze = Analyze.YES)")
	@ProtoField(number = 3)
	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}
}