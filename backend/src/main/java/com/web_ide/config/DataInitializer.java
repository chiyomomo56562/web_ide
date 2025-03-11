package com.web_ide.config;

import com.web_ide.entity.Project;
import com.web_ide.entity.User;
import com.web_ide.repository.ProjectRepository;
import com.web_ide.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {
	final private PasswordEncoder passwordEncoder;
	public DataInitializer(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}
    @Bean
    public CommandLineRunner initData(UserRepository userRepository, ProjectRepository projectRepository) {
        String pwd = "asdf";
    	String encryptPwd = passwordEncoder.encode(pwd);
    	return args -> {
            User user1 = userRepository.save(new User("asdf1", encryptPwd, "user1@example.com", "user1"));
            User user2 = userRepository.save(new User("asdf2", encryptPwd, "user2@example.com", "user2"));
            User user3 = userRepository.save(new User("asdf3", encryptPwd, "user3@example.com", "user3"));

            // 프로젝트 추가
            projectRepository.save(new Project("Project A", "This is project A", user1, null));
            projectRepository.save(new Project("Project B", "This is project B", user2, null));
            projectRepository.save(new Project("Project C", "This is project C", user3, null));
        };
    }
}
