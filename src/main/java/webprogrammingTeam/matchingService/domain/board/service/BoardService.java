package webprogrammingTeam.matchingService.domain.board.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webprogrammingTeam.matchingService.domain.board.dto.request.BoardSaveRequest;
import webprogrammingTeam.matchingService.domain.board.dto.request.BoardUpdateRequest;
import webprogrammingTeam.matchingService.domain.board.dto.response.BoardAllReadResponse;
import webprogrammingTeam.matchingService.domain.board.dto.response.BoardIdReadResponse;
import webprogrammingTeam.matchingService.domain.board.entity.Board;
import webprogrammingTeam.matchingService.domain.board.repository.BoardRepository;
import webprogrammingTeam.matchingService.domain.Image.entity.Image;
import webprogrammingTeam.matchingService.domain.Image.repository.ImageRepository;
import webprogrammingTeam.matchingService.domain.Image.service.ImageService;
import webprogrammingTeam.matchingService.domain.user.entity.User;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final BoardService boardService;
    private final ImageRepository imageRepository;
    private final ImageService imageService;
    LocalDateTime currentTime = LocalDateTime.now();


    //Controller에서 인증된 user 정보를 얻어옴.고쳐야됌.
    User user;
    @Transactional
    public Long saveBoard(BoardSaveRequest boardSaveRequest, List<Image> imageList){
        Board board = Board.builder()
                .user(user)
                .title(boardSaveRequest.title())
                .content(boardSaveRequest.content())
                .date(String.valueOf(currentTime))
                .build();
        for(Image i : imageList){
            board.addImageList(i);
        }
        boardRepository.save(board);

        return board.getId();

    }

    public List<BoardAllReadResponse> findAllBoard() {
        try{
            List<Board> BoardList = boardRepository.findAll();

            List<BoardAllReadResponse> responseList = new ArrayList<>();

            for(Board board : BoardList){
                responseList.add(
                        new BoardAllReadResponse(board.getId(), board.getTitle(), board.getDate())
                );
            }
            return responseList;
        }catch(Exception e){
        }
        return null;
    }

    public BoardIdReadResponse findOneBoard(Long id)throws IOException{
        Board board = boardRepository.findById(id)
                .orElseThrow();


        List<Image> imageList =  imageService.getImageList(Optional.ofNullable(board));

        List<byte[]> imageByteList = new ArrayList<>();

        for(Image i : imageList){
            byte[] imageData = imageService.downloadImage(i);
            imageByteList.add(imageData);
        }


        return BoardIdReadResponse.builder()
                .tile(board.getTitle())
                .date(board.getDate())
                .content(board.getContent())
                .imagesByte(imageByteList)
                .build();
    }

    @Transactional
    public Long updateBoard(BoardUpdateRequest boardUpdateRequest, Long boardId)throws IOException{
        Board board = boardRepository.findById(boardId)
                .orElseThrow();

        board.updateBoard(boardUpdateRequest);

        return board.getId();
    }

    @Transactional
    public void deleteBoard(Long boardId){
        Board board = boardRepository.findById(boardId)
                .orElseThrow();

        boardRepository.delete(board);
    }


}
