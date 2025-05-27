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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class S3Service {

	private final AmazonS3 amazonS3;

	@Value("${spring.cloud.aws.s3.bucket}")
	private String bucket;

	/* ---------- public API ---------- */

	/** 1) 멀티파트 업로드 */
	public String uploadImage(MultipartFile image) throws IOException {
		String key = UUID.randomUUID() + "_" + image.getOriginalFilename();
		ObjectMetadata meta = new ObjectMetadata();
		meta.setContentType(image.getContentType());
		meta.setContentLength(image.getSize());

		amazonS3.putObject(new PutObjectRequest(bucket, key, image.getInputStream(), meta));
		return publicUrl(key);
	}

	/** 2) Base-64(data URI 포함/미포함 모두) 업로드 */
	public String uploadBase64Image(String raw) {

		// ---------- (A) data URI 스킴 분리 ----------
		String base64;
		String ext;

		if (raw.startsWith("data:")) {
			// 예: data:image/jpeg;base64,/9j/4AAQ...
			int comma = raw.indexOf(',');
			if (comma < 0) throw new IllegalArgumentException("Invalid data-URI base64");

			String meta = raw.substring(5, comma);          // "image/jpeg;base64"
			base64     = raw.substring(comma + 1);
			ext        = meta.substring(meta.indexOf('/') + 1, meta.indexOf(';')); // "jpeg"
		} else {
			// 프리픽스 없는 순수 Base-64 → 기본 jpg 로 가정
			base64 = raw;
			ext    = "jpg";
		}

		// ---------- (B) 공백·줄바꿈 제거 ----------
		base64 = base64.replaceAll("\\s+", "");

		// ---------- (C) padding 보정 ----------
		int pad = (4 - base64.length() % 4) % 4;
		base64 += "====".substring(0, pad);

		// ---------- (D) decode ----------
		byte[] bytes;
		try {
			// 줄바꿈 허용 디코더 (iOS/Android가 76바이트마다 LF 넣는 경우)
			bytes = Base64.getMimeDecoder().decode(base64);
		} catch (IllegalArgumentException e) {
			log.error("Base64 decode failed: len={}, head={}...", base64.length(),
				base64.substring(0, Math.min(20, base64.length())));
			throw e;
		}

		// ---------- (E) S3 업로드 ----------
		String key = UUID.randomUUID() + "." + ext;
		ObjectMetadata meta = new ObjectMetadata();
		meta.setContentType("image/" + ext);
		meta.setContentLength(bytes.length);

		amazonS3.putObject(new PutObjectRequest(
			bucket, key, new ByteArrayInputStream(bytes), meta));

		log.info("Uploaded {} ({} bytes) -> {}", key, bytes.length, publicUrl(key));
		return publicUrl(key);
	}

	/* ---------- helper ---------- */

	private String publicUrl(String key) {
		return String.format("https://%s.s3.%s.amazonaws.com/%s",
			bucket, amazonS3.getRegionName(), key);
	}
}
