package com.telran.contactservice.dto;



import com.telran.contactservice.persistence.AbstractContacts;


import java.util.List;

public record ContactSearchDto(List<? extends AbstractContacts> contacts,  int page, int pageSIze, long totalItems) {}
