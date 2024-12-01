package com.telran.contactservice.persistence.current;


import com.telran.contactservice.persistence.AbstractContacts;
import com.telran.contactservice.persistence.IContactRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ContactsRepository extends IContactRepository<ContactsEntity>,JpaRepository<ContactsEntity, UUID> , JpaSpecificationExecutor<ContactsEntity> {
    Optional<ContactsEntity> findByPhoneOrEmail(String phone, String email);
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM current.contacts WHERE contact_id = :id", nativeQuery = true)
    void deleteContactById(UUID id);
}
