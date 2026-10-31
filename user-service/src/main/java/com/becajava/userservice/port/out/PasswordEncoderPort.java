package com.becajava.userservice.port.out;

public interface PasswordEncoderPort {

    String encode(String rawPassword);
}
