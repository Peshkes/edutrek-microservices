package com.telran.contactservice.persistence.archive;

import com.telran.contactservice.persistence.AbstractContacts;
import com.telran.contactservice.persistence.current.ContactsEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.time.LocalDate;


@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(schema = "archive", name = "contacts")
public class ContactArchiveEntity extends AbstractContacts {

    @Column(name = "reason_of_archivation")
    private  String reasonOfArchivation;
    @Column(name = "archivation_date")
    private LocalDate archivationDate;

    public ContactArchiveEntity(ContactsEntity contactsEntity, String reasonOfArchivation) {
        super(contactsEntity);
        this.reasonOfArchivation = reasonOfArchivation;
        this.archivationDate = LocalDate.now();
    }


}
