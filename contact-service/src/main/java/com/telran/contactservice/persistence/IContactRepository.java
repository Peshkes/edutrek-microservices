package com.telran.contactservice.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;
import java.util.UUID;

@NoRepositoryBean
public interface IContactRepository<T extends AbstractContacts> extends JpaRepository<T, UUID>, JpaSpecificationExecutor<T> {
   Optional <AbstractContacts> getByContactId(UUID id);

   boolean existsByPhoneOrEmail( String phone, String email);
}
