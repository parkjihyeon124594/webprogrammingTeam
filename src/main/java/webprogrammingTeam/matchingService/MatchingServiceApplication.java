package webprogrammingTeam.matchingService;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaAuditing
@EnableBatchProcessing
@EnableJpaRepositories(basePackages = {"webprogrammingTeam.matchingService.domain.channel.repository",
"webprogrammingTeam.matchingService.domain.Image.repository","webprogrammingTeam.matchingService.domain.member.repository"
		,"webprogrammingTeam.matchingService.domain.message.repository","webprogrammingTeam.matchingService.domain.notification.repository"
		,"webprogrammingTeam.matchingService.domain.program.repository","webprogrammingTeam.matchingService.domain.recruitment.repository",
		"webprogrammingTeam.matchingService.domain.review.respository","webprogrammingTeam.matchingService.domain.subscription.repository"})
public class MatchingServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MatchingServiceApplication.class, args);
	}

}
