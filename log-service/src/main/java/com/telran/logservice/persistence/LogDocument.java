package com.telran.logservice.persistence;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Document("log")
public class LogDocument {
    @Id
    private UUID contactId;
    @Setter
    private List<String> logs;

    public LogDocument(UUID contactId) {
        this.contactId = contactId;
        this.logs = List.of();
    }
}