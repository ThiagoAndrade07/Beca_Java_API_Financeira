package com.becajava.userservice.port.in;

import com.becajava.userservice.domain.model.User;

public interface GetAuthenticatedUserUseCase {
    User getAuthenticatedUser(String email);
}
