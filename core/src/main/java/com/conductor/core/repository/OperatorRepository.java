package com.conductor.core.repository;

import com.conductor.core.model.user.Operator;
import com.conductor.core.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OperatorRepository extends JpaRepository<Operator, Long> {

    public Optional<Operator> findByUser(User user);

    Optional<Operator> findByExternalId(String externalId);

    Optional<Operator> findByUser_ExternalId(String externalId);
}
