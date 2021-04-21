package com.taaha.photopia.models;

import org.springframework.beans.factory.annotation.Value;

public class blah {
    @Value("${spring.datasource.url}")
    public String datasourceurl;

    @Value("${spring.datasource.username}")
    public String datasourceusername;

    @Value("${spring.datasource.password}")
    public String datasourcepassword;

    @Value("${server.port}")
    public String serverport;

    @Value("${jwt.secret}")
    public String jwtsecret;

    @Value("${spring.mail.host}")
    public String mailhost;

    @Value("${spring.mail.port}")
    public String mailport;

    @Value("${spring.mail.username}")
    public String mailusername;

    @Value("${spring.mail.password}")
    public String mailpassword;

    @Value("${spring.mail.properties.mail.smtp.auth}")
    public String mailauth;

    @Value("${spring.mail.mime.address.strict}")
    public String mailstrict;

    @Value("${spring.mail.properties.mail.smtp.starttls.enable}")
    public String mailenabled;

    @Value("${spring.jpa.properties.javax.persistence.validation.mode}")
    public String jpamode;

    public String blahvar;

    public void blahMethod(){
        blahvar=datasourceurl+datasourceusername+datasourcepassword+serverport+jwtsecret+mailhost+mailport+mailusername
        +mailpassword+mailauth+mailstrict+mailenabled+jpamode;
    }

    @Override
    public String toString() {
        return "blah{" +
                "datasourceurl='" + datasourceurl + '\'' +
                ", datasourceusername='" + datasourceusername + '\'' +
                ", datasourcepassword='" + datasourcepassword + '\'' +
                ", serverport='" + serverport + '\'' +
                ", jwtsecret='" + jwtsecret + '\'' +
                ", mailhost='" + mailhost + '\'' +
                ", mailport='" +  mailport+ '\'' +
                ", mailusername='" + mailusername + '\'' +
                ", mailpassword='" + mailpassword + '\'' +
                ", mailauth='" + mailauth + '\'' +
                ", mailstrict='" + mailstrict + '\'' +
                ", mailenabled='" + mailenabled + '\'' +
                ", jpamode='" + jpamode + '\'' +
                '}';
    }
}
