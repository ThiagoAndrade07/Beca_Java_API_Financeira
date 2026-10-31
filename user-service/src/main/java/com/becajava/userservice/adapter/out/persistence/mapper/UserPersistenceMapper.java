package com.becajava.userservice.adapter.out.persistence.mapper;

import com.becajava.userservice.adapter.out.persistence.entity.UserJpaEntity;
import com.becajava.userservice.domain.model.User;

public class UserPersistenceMapper {

    public static UserJpaEntity toJpaEntity(User user) {
        return new UserJpaEntity(user.getName(), user.getEmail(), user.getPasswordHash());
    }

    public static User toDomain(UserJpaEntity entity) {
        return new User(entity.getId(), entity.getName(), entity.getEmail(), entity.getPasswordHash());
    }
}
