package webprogrammingTeam.matchingService.domain.Board.Controller;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import webprogrammingTeam.matchingService.domain.Board.Dto.Request.BoardSaveRequest;
import webprogrammingTeam.matchingService.domain.Board.Dto.Response.BoardAllReadResponse;
import webprogrammingTeam.matchingService.domain.Board.Dto.Response.BoardIdReadResponse;
import webprogrammingTeam.matchingService.domain.Board.Service.BoardService;
import webprogrammingTeam.matchingService.domain.Image.Entity.Image;
import webprogrammingTeam.matchingService.domain.Image.Service.ImageService;
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

    @DeleteMapping("/{boardId}")
    public ResponseEntity<ApiUtil.ApiSuccessResult<?>> deleteBoard(
            @PathVariable("boardId") Long boardId){
        boardService.deleteBoard(boardId);
        return ResponseEntity.ok().body(ApiUtil.success(HttpStatus.OK));
    }


}
