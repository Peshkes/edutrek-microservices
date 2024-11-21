package com.telran.groupservice.persistence;

import java.util.List;
import java.util.UUID;

public interface IJunctionTableRepository {
    void deleteByGroupId(UUID groupId);
    List<?> getByGroupId(UUID uuid);
}
