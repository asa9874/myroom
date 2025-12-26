package com.example.myroom.domain.model3D.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ModelDimensions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "model_id", nullable = false, unique = true)
    private Model3D model3D;

    private Float width;
    private Float length;
    private Float height;

    @Builder
    public ModelDimensions(Model3D model3D, Float width, Float length, Float height) {
        this.model3D = model3D;
        this.width = width;
        this.length = length;
        this.height = height;
    }

    public void update(Float width, Float length, Float height) {
        if (width != null) {
            this.width = width;
        }
        if (length != null) {
            this.length = length;
        }
        if (height != null) {
            this.height = height;
        }
    }
}
