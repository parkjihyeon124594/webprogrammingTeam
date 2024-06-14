package webprogrammingTeam.matchingService.domain.Image.service;


import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import webprogrammingTeam.matchingService.domain.program.entity.Program;
import webprogrammingTeam.matchingService.domain.Image.entity.Image;
import webprogrammingTeam.matchingService.domain.Image.repository.ImageRepository;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ImageService {

    private final Storage storage;

    private String bucketName = "matching_service";
    private final ImageRepository imageRepository;

    public List<Image> uploadImages(Program program, MultipartFile[] multipartFiles) throws IOException {

        List<Image> images = new ArrayList<>();

        for (MultipartFile multipartFile : multipartFiles) {
            String uuid = UUID.randomUUID().toString();
            String originalFilename = multipartFile.getOriginalFilename();
            String type = FilenameUtils.getExtension(originalFilename);

            storage.create(BlobInfo.newBuilder(BlobId.of(bucketName, uuid))
                    .setContentType(type)
                    .build(), multipartFile.getInputStream());

            String fileUrl = String.format("https://storage.googleapis.com/%s/%s", bucketName, uuid);
            Image image = Image.builder()
                    .url(fileUrl)
                    .program(program)
                    .build();

            imageRepository.save(image);
            images.add(image);
            program.getImages().add(image);

            log.info("저장!");
        }

        return images;
    }




    public List<Image> getImageList(Optional<Program> board)
    {
        List<Image> images = imageRepository.findByProgram(board);

        return images;
    }



}


