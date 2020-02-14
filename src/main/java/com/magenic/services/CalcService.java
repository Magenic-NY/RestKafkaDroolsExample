package com.magenic.services;

import com.google.gson.Gson;
import com.magenic.models.Base;
import com.magenic.models.Extra;
import org.drools.compiler.compiler.PackageBuilder;
import org.drools.core.RuleBase;
import org.drools.core.RuleBaseFactory;
import org.drools.core.WorkingMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;


public class CalcService {

    Gson gson = new Gson();

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    String kafkaTopic = "calculations_out";

    public void doCalc(Base base) {

        Extra extra = new Extra();
        extra.setCalcType("Standard");

        WorkingMemory workingMemory = setUpDrools();

        workingMemory.insert(base);
        workingMemory.insert(extra);
        workingMemory.fireAllRules();

        kafkaTemplate.send(kafkaTopic, gson.toJson(base));

    }

    @KafkaListener(topics = "hello_world_topic")
    public void listen(String message) {
        doCalc(gson.fromJson(message, Base.class));
    }

    private WorkingMemory setUpDrools() {
        PackageBuilder packageBuilder = new PackageBuilder();

        try {
            File file = ResourceUtils.getFile("classpath:rules.drl");
            Reader reader = new FileReader(file);
            packageBuilder.addPackageFromDrl(reader);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        org.drools.core.rule.Package rulesPackage = packageBuilder.getPackage();
        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage(rulesPackage);

        return ruleBase.newStatefulSession();
    }


}
