package com.swx.message.service.iml;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dubbo.api.huobi.service.HuoBiCoinSearchService;
import com.dubbo.api.model.OutBean;
import com.swx.message.service.MethService;
import com.swx.message.service.SearchCoinPriceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@Slf4j
public class SearchCoinPriceServiceIml implements SearchCoinPriceService, MethService {

    @Reference(version = "1.0.0")
    HuoBiCoinSearchService<OutBean> huoBiCoinSearchService;

    @Override
    public String searchCoinPrice(String coin) {
        String prid="1min";
        String size ="1";
        log.info("coin: {}",coin);
        OutBean outBean = huoBiCoinSearchService.getCoinByCode(coin,prid,size);
        log.info("outBean:{}",outBean);
        HashMap<String,Object> result = (HashMap<String, Object>) outBean.getData();
        String price = (String) result.get("price");
        String dayTime =(String) result.get("time");
        String rs = String.format("%s-%s-%s美元",coin,dayTime,price);
        log.info("返回结果：{}",rs);
        return rs;
    }

    @Override
    public String getIndexOf() {
        return "usdt";
    }

    @Override
    public Object handleMessage(String message) {
        return  this.searchCoinPrice(message);
    }
}
