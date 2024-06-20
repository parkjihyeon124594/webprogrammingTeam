package webprogrammingTeam.matchingService.global.config;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import webprogrammingTeam.matchingService.domain.channel.entity.Channel;
import webprogrammingTeam.matchingService.domain.program.entity.Open;
import webprogrammingTeam.matchingService.domain.program.entity.Program;
import webprogrammingTeam.matchingService.domain.program.repository.ProgramRepository;
import webprogrammingTeam.matchingService.domain.recruitment.repository.RecruitmentRepository;
import webprogrammingTeam.matchingService.domain.subscription.service.MemberChannelSubscriptionService;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
@Slf4j
//Spring Batch의 배치 작업을 설정
public class BatchConfig {

    //배치 작업의 메타데이터를 관리
    @Autowired
    private JobRepository jobRepository;

    private final ProgramRepository programRepository;

    private final MemberChannelSubscriptionService memberChannelSubscriptionService;
    @Autowired
    private PlatformTransactionManager transactionManager;

    private final RecruitmentRepository recruitmentRepository;

    /*job은 여러개의 step으로 이루어짐.
    job은 하나의 완전한 배치의 작업 단위
    step은 실제로 수행되는 개별 작업 단위*/


    @Bean
    public Job updateProgramStatusJob() {
        //첫 번째 매개변수는 작업의 이름
        return new JobBuilder("updateProgramStatusJob", jobRepository)
                .incrementer(new RunIdIncrementer())//RunIdIncrementer를 사용하여 실행 ID를 자동으로 증가시킵니다. 이는 각 작업 실행이 고유하도록 보장
                .start(updateProgramStatusStep())//이 작업이 시작될 때 실행될 Step을 지정합니다.
                .build();
    }

    @Bean
    public Step updateProgramStatusStep() {
        return new StepBuilder("updateProgramStatusStep", jobRepository)
                .tasklet(updateProgramStatusTasklet(), transactionManager)//이 단계에서 실행될 Tasklet과 트랜잭션 관리자를 지정
                .build();
    }

    @Bean
    public Tasklet updateProgramStatusTasklet() { //실제 배치 작업에서 수행할 로직을 정의한 Tasklet을 반환합니다.
        //contribution과 chunkContext는 배치 실행 시 제공되는 컨텍스트
        return (contribution, chunkContext) -> {
            log.info("=================updateProgramStatusTasklet 실행===============");
            // 모집기간이 끝난 프로그램의 상태를 "CLOSED"로 업데이트하는 로직
            String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            List<Program> programList = programRepository.findProgramsToClose(now);
            for(Program program : programList){
                program.updateOpen(Open.CLOSED);

                Long programId = program.getId();
     

                Channel newPrivateChannel = memberChannelSubscriptionService.createPrivateChannelAndSubscriptions(program);
                program.updatePrivateChannel(newPrivateChannel);

            }
            programRepository.saveAll(programList);
            return RepeatStatus.FINISHED; //작업이 완료되었음
        };
    }


}
