package com.taaha.photopia;

import ch.qos.logback.core.pattern.color.BlackCompositeConverter;
import com.taaha.photopia.models.blah;
import org.omg.CORBA.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;


@SpringBootApplication
public class PhotopiaApplication {

	public static void main(String[] args) {
		SpringApplication.run(PhotopiaApplication.class, args);
		System.out.println("\n\nSpring Photopia Application started ...\n\n");
	}

}
