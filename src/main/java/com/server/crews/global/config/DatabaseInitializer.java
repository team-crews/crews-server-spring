package com.server.crews.global.config;

import com.server.crews.applicant.domain.Application;
import com.server.crews.applicant.domain.NarrativeAnswer;
import com.server.crews.applicant.domain.SelectiveAnswer;
import com.server.crews.applicant.repository.ApplicationRepository;
import com.server.crews.auth.domain.Administrator;
import com.server.crews.auth.domain.Applicant;
import com.server.crews.auth.repository.AdministratorRepository;
import com.server.crews.auth.repository.ApplicantRepository;
import com.server.crews.recruitment.domain.Choice;
import com.server.crews.recruitment.domain.NarrativeQuestion;
import com.server.crews.recruitment.domain.Recruitment;
import com.server.crews.recruitment.domain.Section;
import com.server.crews.recruitment.domain.SelectiveQuestion;
import com.server.crews.recruitment.repository.RecruitmentRepository;
import com.server.crews.recruitment.service.SimpleRedisRecruitmentSearchService;
import com.server.crews.recruitment.service.RediSearchRecruitmentSearchService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Profile(value = "!prod")
@Component
@Transactional
@RequiredArgsConstructor
public class DatabaseInitializer implements ApplicationRunner {
    private final PasswordEncoder passwordEncoder;
    private final AdministratorRepository administratorRepository;
    private final RecruitmentRepository recruitmentRepository;
    private final ApplicantRepository applicantRepository;
    private final ApplicationRepository applicationRepository;
    private final SimpleRedisRecruitmentSearchService simpleRedisRecruitmentSearchService;
    private final RediSearchRecruitmentSearchService rediSearchRecruitmentSearchService;

    @Override
    public void run(ApplicationArguments args) {
        Administrator administrator = new Administrator("admin", passwordEncoder.encode("12341234!"));
        administratorRepository.save(administrator);

        NarrativeQuestion introductionQuestion = new NarrativeQuestion(null, "자기소개해주세요", true, 1, 500);
        List<Choice> personalityChoices = List.of(new Choice(null, "성실함"), new Choice(null, "밝음"),
                new Choice(null, "꼼꼼함"));
        SelectiveQuestion personalityQuestion = new SelectiveQuestion(null, personalityChoices, "장점을 골라주세요", true, 2, 1,
                1);
        Section commonSection = new Section(null, "공통", "웹 어플리케이션 서버 개발 파트", List.of(introductionQuestion),
                List.of(personalityQuestion));

        NarrativeQuestion backendNarrativeQuestion = new NarrativeQuestion(null, "백엔드 파트와 관련된 활동 하나를 서술해주세요.", true, 1,
                600);
        List<Choice> backendStackChoices = List.of(new Choice(null, "Django, Python"),
                new Choice(null, "Springboot, Java"));
        SelectiveQuestion backendStackQuestion = new SelectiveQuestion(null, backendStackChoices,
                "멋사 프로젝트에서 사용하고 싶은 스택을 골라주세요", true, 2, 1, 2);
        Section backendSection = new Section(null, "백엔드", "웹 어플리케이션 서버 개발 파트", List.of(backendNarrativeQuestion),
                List.of(backendStackQuestion));

        NarrativeQuestion frontendNarrativeQuestion = new NarrativeQuestion(null, "프론트엔드 파트와 관련된 활동 하나를 서술해주세요.", true,
                1, 600);
        Section frontendSection = new Section(null, "프론트엔드", "웹 클라이언트 사이드 개발 파트", List.of(frontendNarrativeQuestion),
                List.of());

        Recruitment recruitment = new Recruitment(null, "test-code", "멋쟁이 사자처럼 99기 아기사자 모집",
                "멋쟁이 사자처럼 서강대에서 99기 아기사자를 모집합니다!", LocalDateTime.of(2024, 9, 1, 2, 0), administrator,
                List.of(commonSection, backendSection, frontendSection));
        recruitment.close();
        recruitmentRepository.save(recruitment);
        simpleRedisRecruitmentSearchService.saveRecruitment(recruitment);
        rediSearchRecruitmentSearchService.createIndex();
        rediSearchRecruitmentSearchService.saveRecruitment(recruitment);

        Applicant kh = new Applicant("kh@google.com", passwordEncoder.encode("test-password"));
        Applicant lkh = new Applicant("lkh@google.com", passwordEncoder.encode("test-password"));
        applicantRepository.saveAll(List.of(kh, lkh));

        NarrativeAnswer skhIntroductionAnswer = new NarrativeAnswer(null, introductionQuestion, "안녕하세요");
        SelectiveAnswer skhPersonalityAnswer = new SelectiveAnswer(null, personalityChoices.get(0),
                personalityQuestion);
        NarrativeAnswer skhBackendNarrativeAnswer = new NarrativeAnswer(null, backendNarrativeQuestion,
                "크루즈 프로젝트 서버 개발을 맡았습니다.");
        SelectiveAnswer skhBackendStackAnswer = new SelectiveAnswer(null, backendStackChoices.get(1),
                backendStackQuestion);
        Application skhApplication = new Application(null, recruitment, kh.getId(), "202011414", "컴퓨터공학", "송경호",
                List.of(skhIntroductionAnswer, skhBackendNarrativeAnswer),
                List.of(skhPersonalityAnswer, skhBackendStackAnswer));
        applicationRepository.save(skhApplication);

        NarrativeAnswer lkhIntroductionAnswer = new NarrativeAnswer(null, introductionQuestion, "반갑습니다");
        SelectiveAnswer lkhPersonalityAnswer = new SelectiveAnswer(null, personalityChoices.get(1),
                personalityQuestion);
        NarrativeAnswer lkhFrontendNarrativeAnswer = new NarrativeAnswer(null, frontendNarrativeQuestion,
                "크루즈 프로젝트 프론트 개발을 맡았습니다.");
        Application lkhApplication = new Application(null, recruitment, lkh.getId(), "202013232", "컴퓨터공학", "이규호",
                List.of(lkhIntroductionAnswer, lkhFrontendNarrativeAnswer), List.of(lkhPersonalityAnswer));
        applicationRepository.save(lkhApplication);
    }
}
