package webprogrammingTeam.matchingService.domain.Image.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import webprogrammingTeam.matchingService.domain.program.entity.Program;
import webprogrammingTeam.matchingService.domain.Image.entity.Image;
import webprogrammingTeam.matchingService.domain.Image.repository.ImageRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Getter
public class ImageService {
    private static final String STORAGE_LOCATION = "/Users/gyuri/Desktop/webtest";
    private final ImageRepository imageRepository;

    public List<Image> saveImageList(List<MultipartFile> multipartFiles) throws IOException {
        List<Image> imageList = new ArrayList<>();

        for(MultipartFile m: multipartFiles){
            m.transferTo(new File(STORAGE_LOCATION+m.getOriginalFilename()));

            Image image = Image.builder()
                    .fileName(m.getOriginalFilename())
                    .url(STORAGE_LOCATION+m.getOriginalFilename())
                    .build();

            imageList.add(image);
        }

        return imageList;
    }

    public List<Image> getImageList(Optional<Program> board)
    {
        List<Image> images = imageRepository.findByProgram(board);

        return images;
    }

    public byte[] downloadImage(Image image)throws IOException{
        return Files.readAllBytes(new File(image.getUrl()).toPath());
    }

}
