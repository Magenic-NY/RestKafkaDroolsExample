package com.magenic.controllers;

import com.google.gson.Gson;
import com.magenic.models.Base;
import com.magenic.services.KafkaSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/magenic")
public class CalculatorController {

    @Autowired
    KafkaSender kafkaSender;

    @PostMapping(path= "/postData", consumes = "application/json", produces = "application/json")
    public String postDeal(@RequestBody Base base) throws Exception {

        Gson gson = new Gson();

        String sendString = gson.toJson(base);
        kafkaSender.send(sendString);

        return "Message sent: " + sendString;
    }

}
