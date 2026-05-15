package com.example.myroom.domain.room3D.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.example.myroom.domain.room3D.dto.response.Room3DResponseDto;
import com.example.myroom.global.jwt.CustomUserDetails;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "🏠 Room3D", description = "도면 이미지 기반 Room3D 생성/수정/조회/삭제 API")
public interface Room3DApi {

    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "201", description = "Room3D 생성 성공",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Room3DResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "인증되지 않음", content = @Content(schema = @Schema(hidden = true)))
        }
    )
    @Operation(
        summary = "도면 이미지 업로드 및 Room3D 생성",
        description = """
            도면 이미지를 업로드하여 AI 기반 Room3D 생성을 요청합니다.

            **인증 필요:** Bearer Token

            **지원 이미지 형식:** JPG, JPEG, PNG

            **처리 과정:**
            1. 이미지 업로드 → S3 저장
            2. Room3D 생성 요청 (PROCESSING 상태)
            3. AI 서버에서 XML 생성
            4. 완료 시 WebSocket으로 알림 (SUCCESS/FAILED 상태)

            ### 📡 WebSocket 알림 정보

            **WebSocket 연결:** `/ws/notifications` (STOMP 프로토콜)
            **구독 토픽:** `/topic/room3d/{userId}` (개인 알림)

            #### 🎯 생성 성공 시 WebSocket 응답
            ```json
            {
                "notificationType": "ROOM3D_GENERATION_SUCCESS",
                "memberId": 10,
                "room3dId": 1,
                "drawingImageUrl": "https://asa-room.s3.amazonaws.com/room3d/images/a.png",
                "xmlFileUrl": "https://asa-room.s3.amazonaws.com/room3d/xml/a.xml",
                "status": "SUCCESS",
                "message": "Room3D 생성이 완료되었습니다.",
                "timestamp": 1705312300000
            }
            ```

            #### ❌ 생성 실패 시 WebSocket 응답
            ```json
            {
                "notificationType": "ROOM3D_GENERATION_FAILED",
                "memberId": 10,
                "room3dId": 1,
                "drawingImageUrl": "https://asa-room.s3.amazonaws.com/room3d/images/a.png",
                "xmlFileUrl": null,
                "status": "FAILED",
                "message": "도면 인식 실패로 Room3D 생성에 실패했습니다.",
                "timestamp": 1705312300000
            }
            ```

            #### ⚙️ 처리 중 상태 WebSocket 응답 (선택적)
            ```json
            {
                "notificationType": "ROOM3D_GENERATION_PROGRESS",
                "memberId": 10,
                "room3dId": 1,
                "drawingImageUrl": "https://asa-room.s3.amazonaws.com/room3d/images/a.png",
                "xmlFileUrl": null,
                "status": "PROCESSING",
                "message": "Room3D 생성을 진행 중입니다.",
                "timestamp": 1705312250000
            }
            ```

            **주의사항:**
            - Room3D 생성은 비동기로 처리됩니다.
            - 생성 완료 알림은 WebSocket을 통해 받을 수 있습니다.
            - 처리 시간은 이미지 복잡도에 따라 수십 초 이상 소요될 수 있습니다.
            """,
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<Room3DResponseDto> createRoom3D(
            @Parameter(description = "도면 이미지 파일", required = true)
            @RequestPart(value = "image", required = true) MultipartFile imageFile,
            @Parameter(description = "방 이름", required = true, example = "안방")
            @RequestParam(value = "room_name") String roomName,
            @Parameter(description = "방 설명", required = false, example = "붙박이장이 있는 안방")
            @RequestParam(value = "description", required = false) String description,
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails member
    );

    // XML 파일을 통한 Room3D 생성 API
    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "201", description = "Room3D 생성 성공",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Room3DResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "인증되지 않음", content = @Content(schema = @Schema(hidden = true)))
        }
    )
    @Operation(summary = "XML 파일 업로드 및 Room3D 생성", security = @SecurityRequirement(name = "Bearer Authentication"))
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, path = "/xml")
    ResponseEntity<Room3DResponseDto> createRoom3DWithXml(
            @Parameter(description = "XML 파일", required = true)
            @RequestPart(value = "xml_file", required = true) MultipartFile xmlFile,
            @Parameter(description = "방 이름", required = true, example = "안방")
            @RequestParam(value = "room_name") String roomName,
            @Parameter(description = "방 설명", required = false, example = "붙박이장이 있는 안방")
            @RequestParam(value = "description", required = false) String description,
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails member
    );

    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "200", description = "Room3D 수정 성공",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Room3DResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Room3D를 찾을 수 없음", content = @Content(schema = @Schema(hidden = true)))
        }
    )
    @Operation(summary = "Room3D 수정", security = @SecurityRequirement(name = "Bearer Authentication"))
    @PutMapping(value = "/{room3dId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<Room3DResponseDto> updateRoom3D(
            @Parameter(description = "Room3D ID", required = true, example = "1")
            @PathVariable(name = "room3dId") Long room3dId,
            @Parameter(description = "새 XML 파일", required = false)
            @RequestPart(value = "xml_file", required = false) MultipartFile xmlFile,
            @Parameter(description = "방 이름 (선택)", required = false, example = "거실")
            @RequestParam(value = "room_name", required = false) String roomName,
            @Parameter(description = "방 설명 (선택)", required = false, example = "창문이 큰 거실")
            @RequestParam(value = "description", required = false) String description,
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails member
    );

    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "200", description = "Room3D 단건 조회 성공",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Room3DResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Room3D를 찾을 수 없음", content = @Content(schema = @Schema(hidden = true)))
        }
    )
    @Operation(summary = "Room3D 단건 조회", security = @SecurityRequirement(name = "Bearer Authentication"))
    @GetMapping("/{room3dId}")
    ResponseEntity<Room3DResponseDto> getRoom3DById(
            @Parameter(description = "Room3D ID", required = true, example = "1")
            @PathVariable(name = "room3dId") Long room3dId,
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails member
    );

    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "200", description = "내 Room3D 목록 조회 성공",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class)))
        }
    )
    @Operation(summary = "내 Room3D 목록 조회", security = @SecurityRequirement(name = "Bearer Authentication"))
    @GetMapping("/my")
    ResponseEntity<Page<Room3DResponseDto>> getMyRoom3Ds(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails member,
            @PageableDefault(size = 10) Pageable pageable
    );

    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "204", description = "Room3D 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "Room3D를 찾을 수 없음", content = @Content(schema = @Schema(hidden = true)))
        }
    )
    @Operation(summary = "Room3D 삭제", security = @SecurityRequirement(name = "Bearer Authentication"))
    @DeleteMapping("/{room3dId}")
    ResponseEntity<Void> deleteRoom3D(
            @Parameter(description = "Room3D ID", required = true, example = "1")
            @PathVariable(name = "room3dId") Long room3dId,
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails member
    );
}
