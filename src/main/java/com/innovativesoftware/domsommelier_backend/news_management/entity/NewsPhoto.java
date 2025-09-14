package com.innovativesoftware.domsommelier_backend.news_management.entity;

import com.innovativesoftware.domsommelier_backend.file_management.model.File;
import com.innovativesoftware.domsommelier_backend.file_management.model.FileProjection;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@Entity
@SuperBuilder
@Table(name = "news_photo")
public class NewsPhoto extends File implements FileProjection {

    @OneToOne()
    @JoinColumn(name = "news_id")
    private News aNews;
}
