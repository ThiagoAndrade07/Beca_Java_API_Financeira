package com.becajava.userservice.adapter.in.web.mapper;

import com.becajava.userservice.adapter.in.web.dto.ImportUserErrorResponse;
import com.becajava.userservice.adapter.in.web.dto.ImportUsersResponse;
import com.becajava.userservice.application.model.ImportUserError;
import com.becajava.userservice.application.model.ImportUsersResult;

import java.util.ArrayList;
import java.util.List;

public class ImportUsersWebMapper {

    public static ImportUsersResponse toResponse(ImportUsersResult result) {

        List<ImportUserErrorResponse> errorResponses = new ArrayList<>();

        for(ImportUserError error : result.getErrors()) {
            ImportUserErrorResponse errorResponse =
                    new ImportUserErrorResponse(error.getLineNumber(), error.getMessage());

            errorResponses.add(errorResponse);
        }

        return new ImportUsersResponse(
                result.getTotalLines(),
                result.getImportedUsers(),
                result.getFailedLines(),
                errorResponses
        );
    }


}
