package com.example.myroom.domain.room3D.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.example.myroom.domain.member.model.Member;
import com.example.myroom.domain.model3D.model.Model3D;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Room3DSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id", nullable = false)
    private Room3DAsset asset;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToMany
    @JoinTable(
        name = "room3d_session_model3d",
        joinColumns = @JoinColumn(name = "session_id"),
        inverseJoinColumns = @JoinColumn(name = "model3d_id")
    )
    private List<Model3D> model3Ds = new ArrayList<>();

    @Column(name = "xml_file_url")
    private String xmlFileUrl;

    @Column(nullable = false)
    private String sessionName;

    @Column(columnDefinition = "TEXT")
    private String sessionDescription;

    @Column(nullable = false)
    private Boolean isShared = false;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public Room3DSession(Room3DAsset asset, Member member, String xmlFileUrl, String sessionName, 
                         String sessionDescription, Boolean isShared, List<Model3D> model3Ds) {
        this.asset = asset;
        this.member = member;
        this.xmlFileUrl = xmlFileUrl;
        this.sessionName = sessionName;
        this.sessionDescription = sessionDescription;
        this.isShared = isShared != null ? isShared : false;
        this.model3Ds = model3Ds != null ? model3Ds : new ArrayList<>();
    }

    public void updateXmlFile(String xmlFileUrl) {
        this.xmlFileUrl = xmlFileUrl;
    }

    public void updateSessionInfo(String sessionName, String sessionDescription, Boolean isShared, List<Model3D> model3Ds) {
        if (sessionName != null) {
            this.sessionName = sessionName;
        }
        if (sessionDescription != null) {
            this.sessionDescription = sessionDescription;
        }
        if (isShared != null) {
            this.isShared = isShared;
        }
        if (model3Ds != null) {
            this.model3Ds.clear();
            this.model3Ds.addAll(model3Ds);
        }
    }
}
