package com.telran.groupservice.key;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ComposeLecturerKey implements Serializable {
    private UUID groupId;
    private UUID lecturerId;
    private boolean isWebinarist;
}