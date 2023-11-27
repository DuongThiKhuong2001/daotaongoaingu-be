//package com.quanly.trungtamngoaingu.controller;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.messaging.handler.annotation.MessageMapping;
//import org.springframework.messaging.handler.annotation.Payload;
//import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.stereotype.Controller;
//
//@Controller
//public class WebSocketController {
//    @Autowired
//    private SimpMessagingTemplate simpMessagingTemplate;
//
//    @MessageMapping("/send")
//    public void send(SimpMessageHeaderAccessor sha, @Payload String username) {
//        String senderName = "Unknown";
//        if (sha.getUser() != null) {
//            senderName = sha.getUser().getName();
//        }
//
//        String message = "Hello from " + senderName;
//
//        simpMessagingTemplate.convertAndSendToUser(username, "/queue/messages", message);
//    }
//}
