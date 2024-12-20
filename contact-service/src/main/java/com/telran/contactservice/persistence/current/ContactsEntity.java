package com.telran.contactservice.persistence.current;
import java.util.UUID;

import com.telran.contactservice.dto.AbstractStudentDto;
import com.telran.contactservice.persistence.AbstractContacts;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Table(schema = "current", name = "contacts")
public class ContactsEntity extends AbstractContacts {

    public ContactsEntity(String contactName, String phone, String email, int statusId, int branchId, UUID targetCourseId, String comment) {
        super(contactName, phone, email, statusId, branchId, targetCourseId, comment);
    }

    public ContactsEntity(AbstractContacts contactEntity) {
        super(contactEntity);
    }

    public ContactsEntity(AbstractStudentDto abstractStudentDto) {
        super(abstractStudentDto);
    }

}
