package org.ataraxii.wishlistdiscovery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class WishlistDiscoveryApplication {

	public static void main(String[] args) {
		SpringApplication.run(WishlistDiscoveryApplication.class, args);
	}

}
