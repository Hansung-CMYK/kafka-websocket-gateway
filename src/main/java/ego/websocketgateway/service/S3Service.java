package ego.websocketgateway.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

@Service
public class S3Service {
	private final AmazonS3 amazonS3;

	@Value("${spring.cloud.aws.s3.bucket}")
	private String bucket;

	public S3Service(AmazonS3 amazonS3) {
		this.amazonS3 = amazonS3;
	}

	public String uploadImage(MultipartFile image) throws IOException {
		String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();

		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentType(image.getContentType());
		metadata.setContentLength(image.getSize());

		PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, fileName, image.getInputStream(), metadata);

		amazonS3.putObject(putObjectRequest);

		return getPublicUrl(fileName);
	}

	public String uploadBase64Image(String dataUri) {
		String[] parts = dataUri.split(",");
		if (parts.length != 2) throw new IllegalArgumentException("Invalid base64 format");

		String metadata = parts[0];
		String base64Data = parts[1];

		String fileExtension = extractExtensionFromMetadata(metadata);

		byte[] decodedBytes = Base64.getDecoder().decode(base64Data);
		String fileName = UUID.randomUUID() + "." + fileExtension;

		ObjectMetadata s3Metadata = new ObjectMetadata();
		s3Metadata.setContentType("image/" + fileExtension);
		s3Metadata.setContentLength(decodedBytes.length);

		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(decodedBytes);
		PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, fileName, byteArrayInputStream, s3Metadata);
		amazonS3.putObject(putObjectRequest);

		return getPublicUrl(fileName);
	}

	private String extractExtensionFromMetadata(String metadata) {
		if (metadata == null || !metadata.contains("/")) {
			throw new IllegalArgumentException("Invalid metadata format");
		}
		return metadata.split("/")[1].split(";")[0];
	}


	private String getPublicUrl(String fileName) {
		return String.format("https://%s.s3.%s.amazonaws.com/%s", bucket, amazonS3.getRegionName(), fileName);
	}
}
