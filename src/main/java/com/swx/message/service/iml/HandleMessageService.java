package com.swx.message.service.iml;

import com.swx.message.service.MethService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Slf4j
@Service
public class HandleMessageService {

    private static ArrayList<MethService> serviceList = new ArrayList<MethService>();

    public static HandleMessageService handleMessageService = new HandleMessageService();


    static {
        serviceList.add(new SearchCoinPriceServiceIml());
    }
    public static HandleMessageService getInstance(){
        return handleMessageService;
    }

    private HandleMessageService(){

    }
    /**
     *
     * @param message
     * @return
     */
    public Object handleMessage(String message){
        for (MethService methService:serviceList){
            log.info("methService name:{}",methService.getClass().getName());
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
}
