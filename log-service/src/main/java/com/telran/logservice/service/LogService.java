package com.telran.logservice.service;

import com.telran.logservice.error.LogNotFoundException;
import com.telran.logservice.logging.Loggable;
import com.telran.logservice.persistence.LogDocument;
import com.telran.logservice.persistence.LogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LogService {

    private final LogRepository repository;

    @Loggable
    public void add(UUID id, String log) {
        LogDocument document = repository.findById(id)
                .orElse(new LogDocument(id));
        List<String> logs = new ArrayList<>(document.getLogs());
        logs.add(LocalDate.now() + " – " + log);
        document.setLogs(logs);
        repository.save(document);
    }

    @Loggable
    public List<String> getById(UUID uuid) {
        return repository.findById(uuid)
                .orElseThrow(() -> new LogNotFoundException(uuid.toString()))
                .getLogs();
    }

    @Loggable
    public void  deleteById(UUID uuid) {
       repository.deleteById(uuid);
    }
}