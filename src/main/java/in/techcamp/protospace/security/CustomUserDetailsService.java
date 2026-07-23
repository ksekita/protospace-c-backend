package in.techcamp.protospace.security;

import in.techcamp.protospace.entity.UserEntity;
import in.techcamp.protospace.repository.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  public CustomUserDetailsService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  //メアドを使ってユーザーを検索
  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    UserEntity user = userRepository.selectByEmail(email);
    if (user == null) {
      throw new UsernameNotFoundException("ユーザーが見つかりません: " + email);
    }
    return new User(
     user.getEmail(),
     user.getPasswordHash(), 
     Collections.emptyList()
    );
  }
}