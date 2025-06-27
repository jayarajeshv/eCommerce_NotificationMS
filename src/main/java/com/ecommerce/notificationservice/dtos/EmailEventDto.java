package com.ecommerce.notificationservice.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailEventDto {
    private String toEmail;
    private String subject;
    private String body;
}
