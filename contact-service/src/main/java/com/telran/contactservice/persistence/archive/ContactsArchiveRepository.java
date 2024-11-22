package com.telran.contactservice.persistence.archive;

import com.telran.contactservice.persistence.IContactRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ContactsArchiveRepository   extends IContactRepository<ContactArchiveEntity>,JpaRepository<ContactArchiveEntity, UUID>, JpaSpecificationExecutor<ContactArchiveEntity> {

}
