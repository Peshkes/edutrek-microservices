package com.telran.contactservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContactServiceNestedObject<C> {
    int page;
    int pageSize;
    private int contactElementsSize = 0;
    private Pageable additionalPageable;
    private int offset = 0;
    private Page<C> foundContactsPaged;
    private long elementsCount;
    List<C> foundContacts;

    public ContactServiceNestedObject(int page, int pageSize, Page<C> foundContactsPaged ) {
        this.page = page;
        this.pageSize = pageSize;
        this.contactElementsSize = (page + 1) * pageSize - (int) foundContactsPaged.getTotalElements();
        this.additionalPageable = PageRequest.of(0, contactElementsSize);
        this.offset = page != 0 ? contactElementsSize - pageSize : 0;
        this.foundContactsPaged = foundContactsPaged;
        this.foundContacts = foundContactsPaged.getContent();
        this.elementsCount = foundContactsPaged.getTotalElements();
    }
}
