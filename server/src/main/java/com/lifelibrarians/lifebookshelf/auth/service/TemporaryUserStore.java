package com.lifelibrarians.lifebookshelf.auth.service;

import com.lifelibrarians.lifebookshelf.auth.domain.TemporaryUser;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TemporaryUserStore {
	private final Map<String, TemporaryUser> store = new ConcurrentHashMap<>();

	public void save(String email, TemporaryUser temporaryUser) {
		store.put(email, temporaryUser);
	}

	public Optional<TemporaryUser> find(String email) {
		TemporaryUser user = store.get(email);
		if (user != null && user.isExpired()) {
			store.remove(email);
			return Optional.empty();
		}
		return Optional.ofNullable(user);
	}

	public void remove(String email) {
		store.remove(email);
	}
}
