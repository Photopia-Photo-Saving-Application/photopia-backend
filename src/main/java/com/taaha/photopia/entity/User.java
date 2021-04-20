package com.taaha.photopia.entity;

import com.sun.istack.NotNull;
import com.taaha.photopia.validator.ValidPassword;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Entity
@Table(name="user")
public class User{

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	private int id;

	@NotNull
	@NotEmpty(message="Name(empty) is mandatory")
	@NotBlank(message="Name(blank) is mandatory")
	@Size(min=2,message = "Name(size) at least wo character")
	@Column(name="name")
	private String name;

	@NotNull
	@NotEmpty(message="Email(empty) is mandatory")
	@NotBlank(message="Email(blank) is mandatory")
	@Email(message = "Email is not valid")
	@Column(name="email")
	private String email;

	@NotNull
	@NotEmpty(message="Email(empty) is mandatory")
	@NotBlank(message="Email(blank) is mandatory")
	@Size(min=2,message = "Name(size) at least wo character")
	@Column(name="address")
	private String address;

	@NotNull
	@NotEmpty(message="Email(empty) is mandatory")
	@NotBlank(message="Email(blank) is mandatory")
	@ValidPassword
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











