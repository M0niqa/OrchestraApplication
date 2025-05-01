package com.monika.worek.orchestra.dto;

import com.monika.worek.orchestra.model.Project;
import lombok.Getter;

import java.util.Set;

@Getter
public class MusicianBasicDTO extends UserBasicDTO {

    private final Set<Project> pendingProjects;
    private final Set<Project> acceptedProjects;
    private final Set<Project> rejectedProjects;

    public MusicianBasicDTO(Long id, String firstName, String lastName, Set<Project> pendingProjects, Set<Project> acceptedProjects, Set<Project> rejectedProjects) {
        super(id, firstName, lastName);
        this.pendingProjects = pendingProjects;
        this.acceptedProjects = acceptedProjects;
        this.rejectedProjects = rejectedProjects;
    }
}
