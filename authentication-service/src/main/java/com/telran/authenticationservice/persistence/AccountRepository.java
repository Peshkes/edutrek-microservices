package com.telran.authenticationservice.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends MongoRepository<AccountDocument, UUID> {
    Optional<AccountDocument> findAccountDocumentByLogin(String login);
    AccountDocument findAccountDocumentByAccountId(UUID id);
    boolean existsAccountDocumentByEmail(String email);
    boolean existsAccountDocumentByLogin(String login);
    boolean existsAccountDocumentByAccountId(UUID id);
    AccountDocument deleteAccountDocumentByAccountId(UUID id);
}