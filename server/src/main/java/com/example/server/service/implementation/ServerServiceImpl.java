package com.example.server.service.implementation;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Collection;
import java.util.Random;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.server.model.Server;
import com.example.server.repo.ServerRepo;
import com.example.server.service.ServerService;
import static com.example.server.enumeration.Status.SERVER_UP;
import static com.example.server.enumeration.Status.SERVER_DOWN;
import static org.springframework.data.domain.PageRequest.of;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class ServerServiceImpl implements ServerService {
	private final ServerRepo serverRepo;

	@Override
	public Server create(Server server) {
		log.info("Saving new server: {}", server.getName());
		server.setImageUrl(setServerImageUrl());
		return serverRepo.save(server);
	}

	@Override
	public Server ping(String ipAddress) throws IOException {
		log.info("Pinging server IP: {}", ipAddress);
		Server server = serverRepo.findByIpAddress(ipAddress);
		InetAddress address = InetAddress.getByName(ipAddress);

		server.setStatus(address.isReachable(10000) ? SERVER_UP : SERVER_DOWN);
		serverRepo.save(server);
		return server;
	}

	@Override
	public Collection<Server> list(int limit) {
		log.info("Fetching all servers");
		return serverRepo.findAll(of(0, limit)).toList();
	}

	@Override
	public Server get(Long id) {
		log.info("Fetching server by id: {}", id);
		return serverRepo.findById(id).get();
	}

	@Override
	public Server update(Server server) {
		log.info("Updating server: {}", server.getName());
		return serverRepo.save(server);
	}

	@Override
	public Boolean delete(Long id) {
		log.info("Deleting server by ID: {}", id);
		serverRepo.deleteById(id);
		return Boolean.TRUE;
	}

	private String setServerImageUrl() {
		String[] imageNames = { "server0.png", "server1.png", "server2.png", "server3.png" };
		return ServletUriComponentsBuilder.fromCurrentContextPath()
				.path("server/image/" + imageNames[new Random().nextInt(4)]).toUriString();
	}
}
