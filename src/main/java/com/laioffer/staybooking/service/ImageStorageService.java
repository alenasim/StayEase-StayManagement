package com.laioffer.staybooking.service;

import com.google.cloud.storage.Acl;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.laioffer.staybooking.exception.GCSUploadException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

@Service
public class ImageStorageService {

    @Value("${gcs.bucket}")     //This annotation is used to inject the value of the gcs.bucket property from the application.properties file. It represents the name of the Google Cloud Storage bucket where the images will be uploaded.
    private String bucketName;     //整个project里，bucket name肯定不会变，而且假如bucket name一旦改变可能后果惨重。preferred的写法是，把这个写成一个final field，在constructor里创建。

    private final Storage storage;   //an instance of the Storage class, which provides methods to interact with Google Cloud Storage.

    public ImageStorageService(Storage storage) {
        this.storage = storage;
    }

    public String save(MultipartFile file) throws GCSUploadException {    // images可以从前端发好几张 => 所以multi part file
        String filename = UUID.randomUUID().toString();           //UUID: Universally Unique Identifier 肯定不会有重复
        BlobInfo blobInfo = null;                         //BlobInfo 文件
        try {
            blobInfo = storage.createFrom(         // 这是上传file的功能。This block of code creates a new BlobInfo instance and uploads the image file to the GCS bucket. The BlobInfo defines metadata for the uploaded object, such as its filename, content type, and access control.
                    BlobInfo                 // 以下是文件属性定义 - 在这里，build pattern用起来很方便，祝需要把需要定义的field拿出来定义，其他不用mention就设置为default =》 代码干净
                            .newBuilder(bucketName, filename)
                            .setContentType("image/jpeg")
                            .setAcl(new ArrayList<>(Arrays.asList(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER))))   //This grants public read access to the uploaded image, allowing anyone to view it without needing authentication.
                            .build(),
                    file.getInputStream());      //把文件变成binary的数据流再上传上去
        } catch (IOException exception) {
            throw new GCSUploadException("Failed to upload file to GCS");
        }

        return blobInfo.getMediaLink();          // After the upload is successful, the method returns the media link URL of the uploaded image
    }
}

// 以上操作做的是，上传完，把媒体的链接记录下来。
// 下一步，把imageStorage inject 到 image service里面。当我们上传完stay的时候，把image都拿下来上传到google cloud，把图片的URL存到DB里。