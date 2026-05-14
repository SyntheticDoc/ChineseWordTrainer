package com.chinesewordtrainer;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

/**
 * Minimal Azure Speech Text-to-Speech REST client for Mandarin word audio. Uses SSML phoneme tags with Azure zh-CN SAPI pinyin.
 */
public final class ChineseTtsService {

    public static final String DEFAULT_LOCALE = "zh-CN";
    public static final String DEFAULT_VOICE = "zh-CN-XiaochenNeural";
    public static final String DEFAULT_SPEAKING_RATE = "medium";
    public static final String DEFAULT_OUTPUT_FORMAT = "audio-24khz-48kbitrate-mono-mp3";

    private static final long TOKEN_REFRESH_AFTER_MILLIS = Duration.ofMinutes(9).toMillis();

    private final String region;
    private final String subscriptionKey;
    private final String locale;
    private final String voiceName;
    private final String speakingRate;
    private final String outputFormat;
    private final String userAgent;
    private final PinyinToAzureSapiConverter pinyinConverter;
    private final HttpClient httpClient;

    private String cachedAccessToken;
    private long cachedAccessTokenCreatedAtMillis;

    public ChineseTtsService(String region, String subscriptionKey) {
	this(region, subscriptionKey, DEFAULT_VOICE);
    }

    public ChineseTtsService(String region, String subscriptionKey, String voiceName) {
	this(region, subscriptionKey, DEFAULT_LOCALE, voiceName, DEFAULT_OUTPUT_FORMAT,
		"ChineseWordTrainer", new PinyinToAzureSapiConverter(), DEFAULT_SPEAKING_RATE);
    }

    public ChineseTtsService(String region,
	    String subscriptionKey,
	    String locale,
	    String voiceName,
	    String outputFormat,
	    String userAgent,
	    PinyinToAzureSapiConverter pinyinConverter,
	    String speakingRate) {
	this.region = requireNonBlank(region, "region");
	this.subscriptionKey = requireNonBlank(subscriptionKey, "subscriptionKey");
	this.locale = requireNonBlank(locale, "locale");
	this.voiceName = requireNonBlank(voiceName, "voiceName");
	this.outputFormat = requireNonBlank(outputFormat, "outputFormat");
	this.userAgent = requireNonBlank(userAgent, "userAgent");
	this.pinyinConverter = pinyinConverter == null ? new PinyinToAzureSapiConverter() : pinyinConverter;
	this.speakingRate = isBlank(speakingRate) ? DEFAULT_SPEAKING_RATE : speakingRate.trim();

	this.httpClient = HttpClient.newBuilder()
		.connectTimeout(Duration.ofSeconds(20))
		.version(HttpClient.Version.HTTP_1_1)
		.build();
    }

    public static ChineseTtsService fromEnvironment() {
	String region = System.getenv("AZURE_SPEECH_REGION");
	String key = System.getenv("AZURE_SPEECH_KEY");

	if (isBlank(region) || isBlank(key)) {
	    throw new IllegalStateException(
		    "Missing Azure Speech configuration. Set environment variables "
		    + "AZURE_SPEECH_REGION and AZURE_SPEECH_KEY.");
	}

	String voice = System.getenv("AZURE_SPEECH_VOICE");
	if (isBlank(voice)) {
	    voice = DEFAULT_VOICE;
	}

	String rate = System.getenv("AZURE_SPEECH_RATE");
	if (isBlank(rate)) {
	    rate = DEFAULT_SPEAKING_RATE;
	}

	return new ChineseTtsService(region, key, DEFAULT_LOCALE, voice, DEFAULT_OUTPUT_FORMAT,
		"ChineseWordTrainer", new PinyinToAzureSapiConverter(), rate);
    }

    public Path synthesizeWordToMp3(Word word, Path outputFile)
	    throws IOException, InterruptedException {
	if (word == null) {
	    throw new IllegalArgumentException("word must not be null.");
	}

	return synthesizeToMp3(word.getSimpleHanzi(), word.getPinyin(), outputFile);
    }

    public Path synthesizeToMp3(String hanzi, String pinyin, Path outputFile)
	    throws IOException, InterruptedException {
	requireNonBlank(hanzi, "hanzi");
	requireNonBlank(pinyin, "pinyin");

	if (outputFile == null) {
	    throw new IllegalArgumentException("outputFile must not be null.");
	}

	Path parent = outputFile.toAbsolutePath().getParent();
	if (parent != null) {
	    Files.createDirectories(parent);
	}

	String ssml = buildSsml(hanzi, pinyin);
	byte[] audio = requestSpeechAudio(ssml);
	Files.write(outputFile, audio);
	return outputFile;
    }

    public String buildSsml(String hanzi, String pinyin) {
	String azureSapiPinyin = pinyinConverter.toAzureSapi(pinyin);

	return "<speak version=\"1.0\" "
		+ "xmlns=\"http://www.w3.org/2001/10/synthesis\" "
		+ "xml:lang=\"" + escapeXmlAttribute(locale) + "\">"
		+ "<voice xml:lang=\"" + escapeXmlAttribute(locale) + "\" "
		+ "name=\"" + escapeXmlAttribute(voiceName) + "\">"
		+ "<prosody rate=\"" + escapeXmlAttribute(speakingRate) + "\">"
		+ "<phoneme alphabet=\"sapi\" ph=\"" + escapeXmlAttribute(azureSapiPinyin) + "\">"
		+ escapeXmlText(hanzi)
		+ "</phoneme>"
		+ "</prosody>"
		+ "</voice>"
		+ "</speak>";
    }

    private byte[] requestSpeechAudio(String ssml) throws IOException, InterruptedException {
	String accessToken = getAccessToken();

	HttpRequest request = HttpRequest.newBuilder()
		.uri(URI.create("https://" + region + ".tts.speech.microsoft.com/cognitiveservices/v1"))
		.timeout(Duration.ofSeconds(60))
		.header("Authorization", "Bearer " + accessToken)
		.header("Content-Type", "application/ssml+xml")
		.header("X-Microsoft-OutputFormat", outputFormat)
		.header("User-Agent", userAgent)
		.POST(HttpRequest.BodyPublishers.ofString(ssml, StandardCharsets.UTF_8))
		.build();

	HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());

	if (response.statusCode() != 200) {
	    String responseBody = new String(response.body(), StandardCharsets.UTF_8);
	    throw new IOException("Azure TTS request failed with HTTP " + response.statusCode()
		    + ". Response body: " + responseBody);
	}

	return response.body();
    }

    private synchronized String getAccessToken() throws IOException, InterruptedException {
	long now = System.currentTimeMillis();

	if (cachedAccessToken != null
		&& now - cachedAccessTokenCreatedAtMillis < TOKEN_REFRESH_AFTER_MILLIS) {
	    return cachedAccessToken;
	}

	HttpRequest request = HttpRequest.newBuilder()
		.uri(URI.create("https://" + region + ".api.cognitive.microsoft.com/sts/v1.0/issueToken"))
		.timeout(Duration.ofSeconds(30))
		.header("Ocp-Apim-Subscription-Key", subscriptionKey)
		.header("Content-Type", "application/x-www-form-urlencoded")
		.POST(HttpRequest.BodyPublishers.noBody())
		.build();

	HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

	if (response.statusCode() != 200) {
	    throw new IOException("Azure token request failed with HTTP " + response.statusCode()
		    + ". Response body: " + response.body());
	}

	cachedAccessToken = response.body();
	cachedAccessTokenCreatedAtMillis = now;
	return cachedAccessToken;
    }

    private static String escapeXmlText(String s) {
	return s.replace("&", "&amp;")
		.replace("<", "&lt;")
		.replace(">", "&gt;");
    }

    private static String escapeXmlAttribute(String s) {
	return escapeXmlText(s)
		.replace("\"", "&quot;")
		.replace("'", "&apos;");
    }

    private static String requireNonBlank(String value, String name) {
	if (isBlank(value)) {
	    throw new IllegalArgumentException(name + " must not be null or blank.");
	}
	return value.trim();
    }

    private static boolean isBlank(String value) {
	return value == null || value.trim().isEmpty();
    }
}
