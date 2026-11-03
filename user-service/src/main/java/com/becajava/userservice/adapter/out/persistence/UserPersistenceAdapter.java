package com.becajava.userservice.adapter.out.persistence;


import com.becajava.userservice.adapter.out.persistence.entity.UserJpaEntity;
import com.becajava.userservice.adapter.out.persistence.mapper.UserPersistenceMapper;
import com.becajava.userservice.adapter.out.persistence.repository.UserJpaRepository;
import com.becajava.userservice.domain.model.User;
import com.becajava.userservice.port.out.UserRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    @Override
    public List<User> listAll() {

        List<UserJpaEntity> entities = userJpaRepository.findAll();

        List<User> users = new ArrayList<>();

        for (UserJpaEntity entity : entities) {

            User user = UserPersistenceMapper.toDomain(entity);

            users.add(user);
        }

        return users;

    }

    @Override
    public Optional<User> findById(Long id) {
        return userJpaRepository.findById(id).map(entity -> UserPersistenceMapper.toDomain(entity));
    }

    @Override
    public void deleteById(Long id) {
        userJpaRepository.deleteById(id);
    }

}
