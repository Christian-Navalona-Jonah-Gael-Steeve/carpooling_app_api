package mbds.car.pooling.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PoolingController {

    @GetMapping("/public/test")
    public String publicEndpoint() {
        return "This is a public endpoint.";
    }

    @GetMapping("/private/test")
    public String privateEndpoint() {
        return "This is a private endpoint. Auth required.";
    }
}
