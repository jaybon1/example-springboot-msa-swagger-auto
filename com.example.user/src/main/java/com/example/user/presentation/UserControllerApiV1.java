package com.example.user.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/users")
public class UserControllerApiV1 {

    @PutMapping("/{id}")
    public ResponseEntity<ResDTO<Object>> putUsersById(
            @PathVariable Long id,
            @RequestParam(required = false) String tempData,
            @RequestBody ReqPutUsersByIdDTOApiV1 reqPutUsersByIdDTOApiV1
    ) {
        return ResponseEntity.ok(
                new ResDTO<>(
                        0,
                        "수정에 성공했습니다.",
                        null
                )
        );
    }

}
