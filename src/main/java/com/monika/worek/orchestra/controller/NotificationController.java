//package com.monika.worek.orchestra.controller;
//
//import org.springframework.messaging.handler.annotation.MessageMapping;
//import org.springframework.messaging.handler.annotation.SendTo;
//import org.springframework.stereotype.Controller;
//
//
//@Controller
//public class NotificationController {
//    @MessageMapping("/send")
//    @SendTo("/topic/notifications")
//    public String sendNotification(String message) {
//        return message;
//    }
//}
