package com.server.crews.recruitment.domain;

import com.server.crews.recruitment.dto.request.RecruitmentSaveRequest;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Document(collection = "recruitments")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Recruitment {
    @Id
    private String id;

    @Indexed(unique = true)
    private String secretCode;

    private String title;

    private String description;

    private Progress progress;

    private List<Section> sections;

    public Recruitment(final String secretCode) {
        this.secretCode = secretCode;
        this.progress = Progress.IN_PROGRESS;
    }

    public static Recruitment from(final RecruitmentSaveRequest request, final String id) {
        return Recruitment.builder()
                .id(id)
                .title(request.title())
                .description(request.description())
                .sections(request.sections())
                .build();
    }
}
