# Video Replay Tracking API

## Overview

This project provides a RESTful API for tracking video replays and managing replay-related information.

## Table of Contents

# Features

- Create and track replay for videos with specific replayed sections.
- Increment the replay count for a given video section.
- Retrieve the replay count for a specific video section.
- Get all replay counts for a video.

# Getting Started

## Prerequisites

- Java 8 or higher
- Maven
- Spring Boot

## Installation

```bash
# Clone the repository
git clone https://github.com/Dibyendu-Merlock/Dibyendu-Mondal-VideoReplaysection-Backend-Developer---Offline-Assignment-IBM

# Build the project
cd your-repo
mvn clean install


To run the application, follow these steps:

1. Ensure that you have Java and Maven installed.
2. Clone the repository.
3. Navigate to the project directory in the terminal.
4. Run the following command to build and run the application:
   ```bash
   mvn spring-boot:run
```
# Usage

## Create and Track Replay

**Endpoint:** `POST http://localhost:8080/video-details

Create and track a replay for a video with specific replayed sections.

**Request:**

```json
{
  "videoId": "sampleVideo",
  "replayedSections": [
    {
      "startTime": 10,
      "endTime": 50
    },
    {
      "startTime": 20,
      "endTime": 30
    },
    {
      "startTime": 10,
      "endTime": 30
    }
  ],
  "duration": 60
}


1.videoId: ID of the video.
2.duration: Duration of the video in HH:mm:ss format.
3.replayedSections: List of replayed sections, each with a start and end time.
4.startTime: Start time of the replayed section.
5.endTime: End time of the replayed section.
```
**Response:**
```json
{
  "replayedSections": [
    {
      "startTime": 10,
      "endTime": 50
    },
    {
      "startTime": 20,
      "endTime": 30
    },
    {
      "startTime": 10,
      "endTime": 30
    }
  ],
  "videoId": "sampleVideo",
  "message": "Replay tracked successfully"
}
```

## Increment Replay Count

**Endpoint:** `POST  http://localhost:8080/increase-replay-count?videoId=sampleVideo&startTime=30&endTime=50`

Increment the replay count for a specific video section.

**Response:**
```json
{
  "videoId": "sampleVideo",
  "message": "Replay count increased successfully"
}
```

## Get Replay Count

**Endpoint:** `GET http://localhost:8080/replay-count?videoId=sampleVideo&startTime=30&endTime=50`

Retrieve the replay count for a specific video section.

**Response:**
```json
{
  "replayCount": 5
}
```

