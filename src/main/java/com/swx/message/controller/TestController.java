package com.swx.message.controller;

import com.swx.message.service.iml.HandleMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TestController {

    @Autowired
    HandleMessageService handleMessageService;
    @RequestMapping("/t3")
    @ResponseBody
    public String test(){
        String res = "default";
        Object object =  handleMessageService.handleMessage("eosusdt");
        if(object!=null){
            res = (String)object;
        }
        return res;
    }
}
