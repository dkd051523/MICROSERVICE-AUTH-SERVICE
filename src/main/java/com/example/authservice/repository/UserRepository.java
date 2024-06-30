package com.example.authservice.repository;
/*
 *  @author diemdz
 */

import com.example.authservice.entity.UserEntity;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity,String> {

    Optional<UserEntity> findByUserName(String userName);
    List<UserEntity> findAll(Specification<UserEntity> specification);
}
