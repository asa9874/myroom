package com.example.myroom.fake.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.example.myroom.fake.dto.response.FakeRoomResponseDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "가짜 방", description = "XML 업로드 및 조회를 위한 공개 API입니다.")
public interface FakeRoomApi {

    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "201",
                description = "가짜 방 생성 성공",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = FakeRoomResponseDto.class))
            ),
            @ApiResponse(
                responseCode = "400",
                description = "잘못된 요청",
                content = @Content(schema = @Schema(hidden = true))
            )
        }
    )
    @Operation(summary = "XML 업로드 및 방 정보 등록")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<FakeRoomResponseDto> createFakeRoom(
            @Parameter(description = "방 XML 파일", required = true)
            @RequestPart(value = "xml_file", required = true) MultipartFile xmlFile,
            @Parameter(description = "방 이름", required = true, example = "안방")
            @RequestParam(value = "room_name") String roomName,
            @Parameter(description = "방 설명", required = false, example = "동쪽 벽에 창문이 있는 방")
            @RequestParam(value = "description", required = false) String description
    );

    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200",
                description = "가짜 방 조회 성공",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = FakeRoomResponseDto.class))
            ),
            @ApiResponse(
                responseCode = "404",
                description = "가짜 방을 찾을 수 없음",
                content = @Content(schema = @Schema(hidden = true))
            )
        }
    )
    @Operation(summary = "가짜 방 단건 조회")
    @GetMapping("/{fakeRoomId}")
    ResponseEntity<FakeRoomResponseDto> getFakeRoomById(
            @Parameter(description = "가짜 방 ID", required = true, example = "1")
            @PathVariable(name = "fakeRoomId") Long fakeRoomId
    );

    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "201", description = "웹소켓 알림 예약 완료"),
            @ApiResponse(responseCode = "404", description = "가짜 방을 찾을 수 없음",
                    content = @Content(schema = @Schema(hidden = true)))
        }
    )
    @Operation(
        summary = "가짜 Room3D 알림 트리거",
        description = """
            가짜 Room3D 알림을 트리거합니다.

            **처리 흐름:**
            1. 요청 수신 후 약 5초 대기
            2. Room3D와 동일한 형식의 WebSocket 알림 전송

            **WebSocket 연결:** `/ws` (STOMP 프로토콜)
            **구독 토픽:** `/topic/room3d/{memberId}`

            #### 📦 전송되는 WebSocket 데이터 예시
            ```json
            {
                "notificationType": "ROOM3D_GENERATION_SUCCESS",
                "memberId": 1,
                "room3dId": 1,
                "drawingImageUrl": "https://example-bucket.s3.amazonaws.com/fake-room/xml/a.xml",
                "xmlFileUrl": "https://example-bucket.s3.amazonaws.com/fake-room/xml/a.xml",
                "status": "SUCCESS",
                "message": "Room3D generation completed.",
                "timestamp": 1705312300000
            }
            ```
            """
    )
    @PostMapping("/{fakeRoomId}")
    ResponseEntity<Void> postFakeRoom(
            @Parameter(description = "가짜 방 ID", required = true, example = "1")
            @PathVariable(name = "fakeRoomId") Long fakeRoomId
    );

    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200",
                description = "가짜 방 수정 성공",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = FakeRoomResponseDto.class))
            ),
            @ApiResponse(
                responseCode = "404",
                description = "가짜 방을 찾을 수 없음",
                content = @Content(schema = @Schema(hidden = true))
            )
        }
    )
    @Operation(summary = "가짜 방 정보 수정")
    @PutMapping(value = "/{fakeRoomId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<FakeRoomResponseDto> updateFakeRoom(
            @Parameter(description = "가짜 방 ID", required = true, example = "1")
            @PathVariable(name = "fakeRoomId") Long fakeRoomId,
            @Parameter(description = "방 XML 파일", required = false)
            @RequestPart(value = "xml_file", required = false) MultipartFile xmlFile,
            @Parameter(description = "방 이름", required = false, example = "거실")
            @RequestParam(value = "room_name", required = false) String roomName,
            @Parameter(description = "방 설명", required = false, example = "창문이 큰 거실")
            @RequestParam(value = "description", required = false) String description
    );

    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "204", description = "가짜 방 삭제 성공"),
            @ApiResponse(
                responseCode = "404",
                description = "가짜 방을 찾을 수 없음",
                content = @Content(schema = @Schema(hidden = true))
            )
        }
    )
    @Operation(summary = "가짜 방 삭제")
    @DeleteMapping("/{fakeRoomId}")
    ResponseEntity<Void> deleteFakeRoom(
            @Parameter(description = "가짜 방 ID", required = true, example = "1")
            @PathVariable(name = "fakeRoomId") Long fakeRoomId
    );
}
