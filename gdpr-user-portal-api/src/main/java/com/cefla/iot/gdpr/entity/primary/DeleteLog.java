package com.cefla.iot.gdpr.entity.primary;

import lombok.*;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "DeleteLog")
public class DeleteLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "LogFileName", length = 200)
    private String logFileName;

    @Column(name = "TableName", length = 100)
    private String tableName;

    @Column(name = "RecordId", length = 100)
    private String recordId;

    @Column(name = "DeletedAt")
    private LocalDateTime deletedAt;

    @Column(name = "RowsAffected")
    private Integer rowsAffected;

    @Column(name = "ErrorMessage", columnDefinition = "VARCHAR(MAX)")
    private String errorMessage;

    @Column(name = "Status", length = 50)
    private String status;
    
    @Column(name = "Process", length = 50)
    private String process;
}