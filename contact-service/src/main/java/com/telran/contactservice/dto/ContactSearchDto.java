package com.telran.contactservice.dto;


import java.util.List;

public record ContactSearchDto(List<Object> contacts,  int page, int pageSIze, long totalItems) {}
