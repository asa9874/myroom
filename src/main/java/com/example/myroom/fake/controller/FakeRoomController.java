package com.example.myroom.fake.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.myroom.fake.dto.request.FakeRoomCreateRequestDto;
import com.example.myroom.fake.dto.request.FakeRoomUpdateRequestDto;
import com.example.myroom.fake.dto.response.FakeRoomResponseDto;
import com.example.myroom.fake.service.FakeRoomService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/fake-rooms")
public class FakeRoomController implements FakeRoomApi {

    private final FakeRoomService fakeRoomService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FakeRoomResponseDto> createFakeRoom(
            @RequestPart(value = "xml_file", required = true) MultipartFile xmlFile,
            @RequestParam(value = "room_name") String roomName,
            @RequestParam(value = "description", required = false) String description) {
        FakeRoomCreateRequestDto requestDto = new FakeRoomCreateRequestDto(roomName, description);
        FakeRoomResponseDto responseDto = fakeRoomService.createFakeRoom(xmlFile, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping("/{fakeRoomId}")
    public ResponseEntity<FakeRoomResponseDto> getFakeRoomById(
            @PathVariable(name = "fakeRoomId") Long fakeRoomId) {
        return ResponseEntity.ok(fakeRoomService.getFakeRoomById(fakeRoomId));
    }

    @PostMapping("/{fakeRoomId}")
    public ResponseEntity<Void> postFakeRoom(
            @PathVariable(name = "fakeRoomId") Long fakeRoomId) {
        fakeRoomService.postFakeRoom(fakeRoomId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping(value = "/{fakeRoomId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FakeRoomResponseDto> updateFakeRoom(
            @PathVariable(name = "fakeRoomId") Long fakeRoomId,
            @RequestPart(value = "xml_file", required = false) MultipartFile xmlFile,
            @RequestParam(value = "room_name", required = false) String roomName,
            @RequestParam(value = "description", required = false) String description) {
        FakeRoomUpdateRequestDto requestDto = new FakeRoomUpdateRequestDto(roomName, description);
        FakeRoomResponseDto responseDto = fakeRoomService.updateFakeRoom(fakeRoomId, requestDto, xmlFile);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{fakeRoomId}")
    public ResponseEntity<Void> deleteFakeRoom(
            @PathVariable(name = "fakeRoomId") Long fakeRoomId) {
        fakeRoomService.deleteFakeRoom(fakeRoomId);
        return ResponseEntity.noContent().build();
    }
}
