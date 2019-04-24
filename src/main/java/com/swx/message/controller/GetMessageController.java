package com.swx.message.controller;

import java.util.Date;
import java.util.HashMap;

import com.swx.message.service.iml.HandleMessageService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.util.XmlUtils;
import me.chanjar.weixin.common.util.crypto.WxCryptUtil;
import me.chanjar.weixin.cp.bean.WxCpXmlOutTextMessage;
import me.chanjar.weixin.cp.config.WxCpInMemoryConfigStorage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping({"/swx/receive.do"})
@Slf4j
public class GetMessageController {

    @Value("${ewx.token}")
    private String token;
    @Value("${ewx.encodingAESKey}")
    private String encodingAESKey;
    @Value("${ewx.corpId}")
    private String corpId;

    @Autowired
    private HandleMessageService handleMessageService;

    public GetMessageController() {
    }

    @GetMapping(produces = {"text/plain;charset=utf-8"})
    @ResponseBody
    public String recevieGetWXMessage(@RequestParam(name = "msg_signature",required = false) String signature,
                                      @RequestParam(name = "timestamp",required = false) String timestamp,
                                      @RequestParam(name = "nonce",required = false) String nonce,
                                      @RequestParam(name = "echostr",required = false) String echostr) {
        log.info("----get in recevieGetWXMessage ----------");
        log.info("\n接收到来自微信服务器的认证消息：signature = [{}], timestamp = [{}], nonce = [{}], echostr = [{}]", new Object[]{signature, timestamp, nonce, echostr});
        if (StringUtils.isAllBlank(new CharSequence[]{signature, timestamp, nonce, echostr})) {
            throw new IllegalArgumentException("请求参数非法，请核实!");
        } else {
            WxCryptUtil wxcpt = new WxCryptUtil(this.token, this.encodingAESKey, this.corpId);
            String result = wxcpt.decrypt(echostr);
            log.info("result: {}", result);
            return result;
        }
    }

    @PostMapping(produces = {"text/plain;charset=utf-8"})
    @ResponseBody
    public String receviePostWXMessage(@RequestBody String requestBody,
                                       @RequestParam(name = "msg_signature") String signature,
                                       @RequestParam(name = "timestamp") String timestamp,
                                       @RequestParam(name = "nonce") String nonce) {
        log.info("----get in receviePostWXMessage----------");
        log.info("\n接收到来自微信服务器的认证消息：signature = [{}], timestamp = [{}], nonce = [{}] requestBody = [{}]", new Object[]{signature, timestamp, nonce, requestBody});
        if (StringUtils.isAllBlank(new CharSequence[]{signature, timestamp, nonce})) {
            throw new IllegalArgumentException("请求参数非法，请核实!");
        } else {
            WxCryptUtil wxcpt = new WxCryptUtil(this.token, this.encodingAESKey, this.corpId);
            String result = wxcpt.decrypt(signature, timestamp, nonce, requestBody);
            log.info("------------===================------------------");
            log.info("reponse result: {}", result);
            String outxml = this.getMsg(result);
            return outxml;
        }
    }

    private String getMsg(String result) {
        HashMap<String, Object> dataMap = (HashMap)XmlUtils.xml2Map(result);
        log.info("datamap------->");
        dataMap.forEach((k, v) -> {
            log.info("key:{}---->value:{}", k, v);
        });
        String message = (String) dataMap.get("Content");
        String res = (String) handleMessageService.handleMessage(message);
        if(res==null){
            res="找不到相关信息";
        }
        log.info("res - >{} ",res);
        WxCpXmlOutTextMessage wxCpXmlOutTextMessage = outMessage(dataMap,res);
        String outXml = wxCpXmlOutTextMessage.toEncryptedXml(setWxCMConfigStorage());
        log.info("outxml - > {}", outXml);
        return outXml;
    }

    /**
     *
     * @param dataMap
     * @return
     */
    private WxCpXmlOutTextMessage outMessage(HashMap<String,Object> dataMap,String content){
        String fromUserName = (String)dataMap.get("FromUserName");
        log.info("fromUserName:" + fromUserName);
        String toUserName = (String)dataMap.get("ToUserName");
        log.info("toUserName:" + toUserName);
        String msgType = (String)dataMap.get("MsgType");
        log.info("msgType:" + msgType);
        WxCpXmlOutTextMessage wxCpXmlOutTextMessage = new WxCpXmlOutTextMessage();
        wxCpXmlOutTextMessage.setFromUserName(toUserName);
        wxCpXmlOutTextMessage.setToUserName(fromUserName);
        wxCpXmlOutTextMessage.setMsgType(msgType);
        wxCpXmlOutTextMessage.setCreateTime((new Date()).getTime());
        wxCpXmlOutTextMessage.setContent(content);
        return wxCpXmlOutTextMessage;
    }

    private WxCpInMemoryConfigStorage setWxCMConfigStorage(){
        WxCpInMemoryConfigStorage wxCpInMemoryConfigStorage = new WxCpInMemoryConfigStorage();
        wxCpInMemoryConfigStorage.setAesKey(this.encodingAESKey);
        wxCpInMemoryConfigStorage.setCorpId(this.corpId);
        wxCpInMemoryConfigStorage.setToken(this.token);
        return wxCpInMemoryConfigStorage;
    }
}