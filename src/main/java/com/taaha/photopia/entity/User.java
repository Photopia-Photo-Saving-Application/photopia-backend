package com.taaha.photopia.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="user")
public class User{

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	private int id;
	
	@Column(name="name")
	private String name;
	
	@Column(name="email")
	private String email;

	@Column(name="address")
	private String address;

	@Column(name="password")
	private String password;

	@Column(name="enabled")
	private boolean enabled;

	@Column(name="verification_code")
	private String verificationCode;
	
	public User() {
		
	}

	public User(String name, String email, String address, String password) {
		this.name = name;
		this.email = email;
		this.address = address;
		this.password = password;
		this.enabled=false;
	}

	// define getter/setter
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getVerificationCode() {
		return verificationCode;
	}

	public void setVerificationCode(String verificationCode) {
		this.verificationCode = verificationCode;
	}

	@Override
	public String toString() {
		return "User{" +
				"id=" + id +
				", name='" + name + '\'' +
				", email='" + email + '\'' +
				", address='" + address + '\'' +
				", password='" + password + '\'' +
				", verificationCode='" + verificationCode + '\'' +
				", enabled='" + enabled + '\'' +
				'}';
	}
}











