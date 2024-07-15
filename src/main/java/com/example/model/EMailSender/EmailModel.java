package com.example.model.EMailSender;

import lombok.Data;

@Data
public class EmailModel {
    private String name;
    private String email;
    private String subject;
    private String message;
}