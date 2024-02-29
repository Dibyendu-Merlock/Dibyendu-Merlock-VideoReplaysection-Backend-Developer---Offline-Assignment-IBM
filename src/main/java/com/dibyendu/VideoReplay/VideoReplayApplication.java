package com.dibyendu.VideoReplay;

import lombok.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@SpringBootApplication
@RestController
public class VideoReplayApplication {

	private final Map<String, Map<String, Integer>> sectionReplayMap = new HashMap<>();


	// First upload the video details with videoId, the sections to be replayed and total duration of the videos
	@PostMapping("/video-details")
	public ResponseEntity<Map<String, Object>> trackReplay(@RequestBody TrackReplayRequest request) {
		try {
			validateReplayRequest(request);

			for (ReplayedSectionRequest section : request.getReplayedSections()) {
				String key = request.getVideoId() + "_" + section.getStartTime() + "_" + section.getEndTime();
				sectionReplayMap
						.computeIfAbsent(request.getVideoId(), k -> new HashMap<>())
						.put(key, sectionReplayMap.getOrDefault(request.getVideoId(), new HashMap<>()).getOrDefault(key, 0) + 1);
			}

			return ResponseEntity.ok(getSuccessResponse(request));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(getErrorResponse(e.getMessage()));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(getErrorResponse("Error tracking replay"));
		}
	}

	// To increase the replay of the sections we want
	@PostMapping("/increase-replay-count")
	public ResponseEntity<Map<String, Object>> increaseReplayCount(@RequestParam String videoId,
																   @RequestParam String startTime,
																   @RequestParam String endTime) {
		try {
			String key = videoId + "_" + startTime + "_" + endTime;
			sectionReplayMap.computeIfAbsent(videoId, k -> new HashMap<>());

			int currentCount = sectionReplayMap.get(videoId).getOrDefault(key, 0);
			sectionReplayMap.get(videoId).put(key, currentCount + 1);

			return ResponseEntity.ok(getSuccessResponse(videoId));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(getErrorResponse("Error increasing replay count"));
		}
	}

	// Finally to count the number of replays of a particular section
	@GetMapping("/replay-count")
	public ResponseEntity<Map<String, Long>> getReplayCount(@RequestParam String videoId,
															@RequestParam String startTime,
															@RequestParam String endTime) {
		String key = videoId + "_" + startTime + "_" + endTime;
		long replayCount = sectionReplayMap.getOrDefault(videoId, Collections.emptyMap()).getOrDefault(key, 0);
		Map<String, Long> response = Collections.singletonMap("replayCount", replayCount);
		return ResponseEntity.ok(response);
	}




	// Validation
	private void validateReplayRequest(TrackReplayRequest request) {
		if (request.getReplayedSections() == null || request.getReplayedSections().isEmpty() || !isVideoIdUnique(request.getVideoId())) {
			throw new IllegalArgumentException("Invalid replay request");
		}

		Set<String> sectionKeys = new HashSet<>();

		for (ReplayedSectionRequest section : request.getReplayedSections()) {
			try {
				// Validate startTime must be greater or equal to 0 and endTime must be greater than 0
				if (section.getStartTime() < 0 || section.getEndTime() <= 0) {
					throw new IllegalArgumentException("Invalid replay section: startTime and endTime must be greater than 0");
				}

				// Create a unique section key
				String sectionKey = section.getStartTime() + "_" + section.getEndTime();

				// Check for duplicate section
				if (sectionKeys.contains(sectionKey)) {
					throw new IllegalArgumentException("Invalid replay section: Duplicate section found");
				}

				// Add sectionKey to the set
				sectionKeys.add(sectionKey);

				// Convert time values to seconds
				int startTimeInSeconds = section.getStartTime();
				int endTimeInSeconds = section.getEndTime();
				int durationInSeconds = request.getDuration() * 60; // Assuming duration is in minutes

				// Validate replay section
				if (startTimeInSeconds >= endTimeInSeconds ||
						startTimeInSeconds >= durationInSeconds ||
						endTimeInSeconds > durationInSeconds ||
						endTimeInSeconds <= 0) {
					throw new IllegalArgumentException("Invalid replay section: startTime and endTime must be within the duration, and endTime must be greater than 0");
				}
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("Invalid replay section: startTime and endTime must be valid integers");
			}
		}
	}



	private boolean isVideoIdUnique(String videoId) {
		return sectionReplayMap.keySet().stream().noneMatch(id -> id.equals(videoId));
	}

	// RESPONSE

	private Map<String, Object> getSuccessResponse(TrackReplayRequest request) {
		Map<String, Object> response = new HashMap<>();
		response.put("message", "Replay tracked successfully");
		response.put("videoId", request.getVideoId());
		response.put("replayedSections", request.getReplayedSections());
		return response;
	}

	private Map<String, Object> getSuccessResponse(String videoId) {
		Map<String, Object> response = new HashMap<>();
		response.put("message", "Replay count increased successfully");
		response.put("videoId", videoId);
		return response;
	}

	private Map<String, Object> getErrorResponse(String errorMessage) {
		Map<String, Object> response = new HashMap<>();
		response.put("error", errorMessage);
		return response;
	}

	// REQUEST

	@Data
	public static class ReplayedSectionRequest {
		private int startTime;
		private int endTime;

		public ReplayedSectionRequest() {
		}
	}

	@Data
	public static class TrackReplayRequest {
		private String videoId;
		private List<ReplayedSectionRequest> replayedSections;
		private int duration;

		public TrackReplayRequest() {
		}
	}


	public static void main(String[] args) {
		SpringApplication.run(VideoReplayApplication.class, args);
	}



}
