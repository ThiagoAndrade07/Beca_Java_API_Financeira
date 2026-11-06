package com.becajava.userservice.port.in;

import com.becajava.userservice.application.model.ImportUserCommand;
import com.becajava.userservice.application.model.ImportUsersResult;

import java.util.List;

public interface ImportUsersUseCase {
    ImportUsersResult importUsers(List<ImportUserCommand> commands);
}
