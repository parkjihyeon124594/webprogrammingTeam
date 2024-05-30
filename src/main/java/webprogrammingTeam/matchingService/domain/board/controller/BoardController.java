package webprogrammingTeam.matchingService.domain.board.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import webprogrammingTeam.matchingService.domain.board.dto.request.BoardSaveRequest;
import webprogrammingTeam.matchingService.domain.board.dto.request.BoardUpdateRequest;
import webprogrammingTeam.matchingService.domain.board.dto.response.BoardAllReadResponse;
import webprogrammingTeam.matchingService.domain.board.dto.response.BoardIdReadResponse;
import webprogrammingTeam.matchingService.domain.board.service.BoardService;
import webprogrammingTeam.matchingService.domain.Image.entity.Image;
import webprogrammingTeam.matchingService.domain.Image.service.ImageService;
import webprogrammingTeam.matchingService.global.util.ApiUtil;

import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping("/board")
public class BoardController {
    private BoardService boardService;
    private ImageService imageService;


    @PostMapping()
    public ResponseEntity<ApiUtil.ApiSuccessResult<Long>> createBoard(
            @RequestPart(value="boardSaveRequest") BoardSaveRequest boardSaveRequest,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) throws IOException {

        List<Image> listImage = imageService.saveImageList(images);
        Long saveId = boardService.saveBoard(boardSaveRequest,listImage);

        return ResponseEntity.ok().body(ApiUtil.success(HttpStatus.CREATED,saveId));
    }

    @GetMapping()
    public ResponseEntity<ApiUtil.ApiSuccessResult<List<BoardAllReadResponse>>> getAllBoard(){
        List<BoardAllReadResponse> allBoard = boardService.findAllBoard();
        return ResponseEntity.ok().body(ApiUtil.success(HttpStatus.OK, allBoard));
    }

    @GetMapping("/{boardId}")
    public ResponseEntity<ApiUtil.ApiSuccessResult<BoardIdReadResponse>> getBoard(
            @PathVariable("boardId") Long boardId
    )throws IOException{
        BoardIdReadResponse boardIdReadResponse = boardService.findOneBoard(boardId);
        return ResponseEntity.ok().body(ApiUtil.success(HttpStatus.OK, boardIdReadResponse));
    }

    @PutMapping("/{boardId}")
    public ResponseEntity<ApiUtil.ApiSuccessResult<Long>> updateBoard(
            @RequestBody BoardUpdateRequest boardUpdateRequest,
            @RequestParam("boardId") Long boardId
    )throws IOException{
        Long updateId = boardService.updateBoard(boardUpdateRequest, boardId);

        return ResponseEntity.ok().body(ApiUtil.success(HttpStatus.OK, updateId));

    }
    @DeleteMapping("/{boardId}")
    public ResponseEntity<ApiUtil.ApiSuccessResult<?>> deleteBoard(
            @PathVariable("boardId") Long boardId){
        boardService.deleteBoard(boardId);
        return ResponseEntity.ok().body(ApiUtil.success(HttpStatus.OK));
    }


}
