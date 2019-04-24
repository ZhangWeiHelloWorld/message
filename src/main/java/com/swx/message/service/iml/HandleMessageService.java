package com.swx.message.service.iml;

import com.swx.message.service.MethService;
import com.swx.message.utils.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Slf4j
@Service
public class HandleMessageService {

    private  ArrayList<MethService> serviceList;

    @Autowired
    private  SearchCoinPriceServiceIml searchCoinPriceServiceIml;

    /**
     *
     * @param message
     * @return
     */
    public Object handleMessage(String message){
        init();
        for (MethService methService:serviceList){
            log.info("methService name:{}",methService.getClass().getName());
            log.info("message:{}",message);
            if(message.indexOf(methService.getIndexOf())>-1){
                return methService.handleMessage(message);
            }
        }
        return null;
    }

    public ArrayList<MethService> getServiceList() {
        return serviceList;
    }

    public void setServiceList(ArrayList<MethService> serviceList) {
        this.serviceList = serviceList;
    }
    public ArrayList<MethService> init(){
        serviceList = new ArrayList<MethService>();
        serviceList.add(searchCoinPriceServiceIml);
        return serviceList;
    }
}
