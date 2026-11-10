package com.becajava.transactionservice.port.out;

import com.becajava.transactionservice.application.model.AuthenticatedUser;

public interface UserServicePort {
    AuthenticatedUser getAuthenticatedUser(String authorizationHeader);
}
