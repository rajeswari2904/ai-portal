package com.transcription.transcription.authentication;



import java.util.ArrayList;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.transcription.transcription.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	private UserRepository ourUserRepo;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		com.transcription.transcription.model.User users = ourUserRepo.findByUsername(username).orElseThrow();
//	    	Users user= ourUserRepo.findById(4L).orElseThrow();

		return new User(users.getUsername(), users.getPassword(), new ArrayList<>());
	}
}