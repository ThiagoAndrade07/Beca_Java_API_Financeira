package com.becajava.userservice.adapter.out.persistence;


import com.becajava.userservice.adapter.out.persistence.entity.UserJpaEntity;
import com.becajava.userservice.adapter.out.persistence.mapper.UserPersistenceMapper;
import com.becajava.userservice.adapter.out.persistence.repository.UserJpaRepository;
import com.becajava.userservice.domain.model.User;
import com.becajava.userservice.port.out.UserRepositoryPort;
import org.springframework.stereotype.Component;

@Component
public class UserPersistenceAdapter implements UserRepositoryPort {

    private final UserJpaRepository userJpaRepository;

    public UserPersistenceAdapter(UserJpaRepository userJpaRepository) {
        this.userJpaRepository = userJpaRepository;
    }

    @Override
    public User save(User user) {
        UserJpaEntity entity = UserPersistenceMapper.toJpaEntity(user);

        UserJpaEntity savedEntity = userJpaRepository.save(entity);

        return UserPersistenceMapper.toDomain(savedEntity);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }

}
