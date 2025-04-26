package com.innovativesoftware.domsommelier_backend.news_management.entity;

import com.innovativesoftware.domsommelier_backend.file_management.model.File;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "news_photo")
public class NewsPhoto extends File {

    @OneToOne()
    @JoinColumn(name = "news_id")
    private News aNews;
}
