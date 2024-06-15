package webprogrammingTeam.matchingService.global.config;


import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Configuration
@EnableScheduling
//Batch 일을 실형시켜주는 클래스
public class BatchScheduler {

    @Autowired
    private JobLauncher jobLauncher;

    //실행할 배치 작업
    @Autowired
    private Job updateProgramStatusJob;

    //@Scheduled(cron = "0 0 0 * * ?")  // 매일 자정에 실행
    //@Scheduled(cron = "0 0 */1 * * *") // 매 1시간마다 실행
    //@Scheduled(cron = "*/5 * * * * *") // 매 5분마다 실행
    @Scheduled(cron = "0 * * * * *") // 매 분 0초에 실행
    public void runUpdateProgramStatusJob() {
        try {
            //스케줄링된 작업을 실행하는 메서드
            jobLauncher.run(updateProgramStatusJob,
                    new JobParametersBuilder().addLong("time", System.currentTimeMillis()).toJobParameters());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
