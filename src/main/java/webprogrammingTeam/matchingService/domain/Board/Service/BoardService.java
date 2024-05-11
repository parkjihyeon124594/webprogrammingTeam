package webprogrammingTeam.matchingService.domain.Board.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webprogrammingTeam.matchingService.User.User;
import webprogrammingTeam.matchingService.domain.Board.Dto.Request.BoardSaveRequest;
import webprogrammingTeam.matchingService.domain.Board.Dto.Response.BoardAllReadResponse;
import webprogrammingTeam.matchingService.domain.Board.Dto.Response.BoardIdReadResponse;
import webprogrammingTeam.matchingService.domain.Board.Entity.Board;
import webprogrammingTeam.matchingService.domain.Board.Repository.BoardRepository;
import webprogrammingTeam.matchingService.domain.Image.Entity.Image;
import webprogrammingTeam.matchingService.domain.Image.Repository.ImageRepository;
import webprogrammingTeam.matchingService.domain.Image.Service.ImageService;

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
    private final ImageRepository imageRepository;
    private final ImageService imageService;
    LocalDateTime currentTime = LocalDateTime.now();


    //Controller에서 인증된 user 정보를 얻어옴.고쳐야됌.
    User user ;
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
                        new BoardAllReadResponse(board.getUser(), board.getId(), board.getTitle(), board.getDate())
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
    public void deleteBoard(Long boardId){
        Board board = boardRepository.findById(boardId)
                .orElseThrow();

        boardRepository.delete(board);
    }


}
