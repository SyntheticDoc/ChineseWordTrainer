package com.chinesewordtrainer;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javazoom.jl.player.Player;

/**
 * Generates, caches and plays Mandarin word audio for ChineseWordTrainer.
 *
 * Cache strategy: - If a word MP3 exists, play it. - If not, generate it once via Azure TTS and save it. - Then play the saved local MP3.
 */
public final class WordAudioService {

    private final ChineseTtsService ttsService;
    private final PinyinToAzureSapiConverter pinyinConverter;
    private final ConsoleHandler console;
    private final Path cacheDirectory;
    private final ExecutorService executor;

    public WordAudioService(ChineseTtsService ttsService, ConsoleHandler console) {
	this(ttsService, console,
		Paths.get(System.getProperty("user.home"), ".chinesewordtrainer", "audio-cache"));
    }

    public WordAudioService(ChineseTtsService ttsService, ConsoleHandler console, Path cacheDirectory) {
	if (ttsService == null) {
	    throw new IllegalArgumentException("ttsService must not be null.");
	}
	if (cacheDirectory == null) {
	    throw new IllegalArgumentException("cacheDirectory must not be null.");
	}

	this.ttsService = ttsService;
	this.console = console;
	this.cacheDirectory = cacheDirectory;
	this.pinyinConverter = new PinyinToAzureSapiConverter();
	this.executor = Executors.newSingleThreadExecutor(r -> {
	    Thread t = new Thread(r, "ChineseWordTrainer-Audio");
	    t.setDaemon(true);
	    return t;
	});
    }

    public void playWordAsync(Word word) {
	executor.submit(() -> {
	    try {
		playWordBlocking(word);
	    } catch (Exception ex) {
		logErr("Could not play audio for word " + describeWord(word) + ": " + ex.getMessage());
		ex.printStackTrace();
	    }
	});
    }

    public void playWordBlocking(Word word) throws IOException, InterruptedException {
	if (word == null) {
	    throw new IllegalArgumentException("word must not be null.");
	}

	Files.createDirectories(cacheDirectory);

	Path stableFile = getCacheFileFor(word);
	Path audioFile = findExistingCacheFileFor(word);

	if (audioFile == null) {
	    audioFile = stableFile;

	    logMsg("  Audio not found, generating audio for " + word.getSimpleHanzi() + " / " + word.getPinyin());
	    ttsService.synthesizeWordToMp3(word, audioFile);
	    logMsg("  Audio cached: " + audioFile.toAbsolutePath());
	} else if (!audioFile.equals(stableFile) && !Files.exists(stableFile)) {
	    Files.copy(audioFile, stableFile);
	    audioFile = stableFile;
	    logMsg("  Migrated cached audio to stable filename: " + stableFile.toAbsolutePath());
	}

	playMp3File(audioFile);
    }

    public Path getCacheFileFor(Word word) {
	if (word == null) {
	    throw new IllegalArgumentException("word must not be null.");
	}

	String numberedPinyin = pinyinConverter.toCompactNumberedPinyin(word.getPinyin());
	String readablePart = sanitizeForFileName(numberedPinyin);

	return cacheDirectory.resolve(readablePart + ".mp3");
    }

    public void preGenerateWordAudioAsync(Word word) {
	executor.submit(() -> {
	    try {
		Path audioFile = getCacheFileFor(word);
		if (!Files.exists(audioFile)) {
		    ttsService.synthesizeWordToMp3(word, audioFile);
		}
	    } catch (Exception ex) {
		logErr("Could not pre-generate audio for word " + describeWord(word) + ": " + ex.getMessage());
		ex.printStackTrace();
	    }
	});
    }

    public void shutdown() {
	executor.shutdownNow();
    }

    private Path findExistingCacheFileFor(Word word) throws IOException {
	Path stableFile = getCacheFileFor(word);

	if (Files.exists(stableFile) && Files.size(stableFile) > 0L) {
	    return stableFile;
	}

	String numberedPinyin = pinyinConverter.toCompactNumberedPinyin(word.getPinyin());
	String readablePart = sanitizeForFileName(numberedPinyin);

	if (!Files.isDirectory(cacheDirectory)) {
	    return null;
	}

	try ( var stream = Files.list(cacheDirectory)) {
	    return stream
		    .filter(path -> Files.isRegularFile(path))
		    .filter(path -> {
			String fileName = path.getFileName().toString().toLowerCase();

			return fileName.equals(readablePart + ".mp3")
				|| fileName.startsWith(readablePart + "_") && fileName.endsWith(".mp3");
		    })
		    .filter(path -> {
			try {
			    return Files.size(path) > 0L;
			} catch (IOException ex) {
			    return false;
			}
		    })
		    .findFirst()
		    .orElse(null);
	}
    }

    private void playMp3File(Path audioFile) throws IOException {
	try ( InputStream in = new BufferedInputStream(Files.newInputStream(audioFile))) {
	    Player player = new Player(in);
	    player.play();
	} catch (IOException ex) {
	    throw ex;
	} catch (Exception ex) {
	    throw new IOException("Could not decode/play MP3 file: " + audioFile, ex);
	}
    }

    private String sanitizeForFileName(String input) {
	String sanitized = input.toLowerCase()
		.replaceAll("[^a-z0-9_-]", "_")
		.replaceAll("_+", "_");

	if (sanitized.length() > 80) {
	    sanitized = sanitized.substring(0, 80);
	}

	if (sanitized.isEmpty()) {
	    sanitized = "word";
	}

	return sanitized;
    }

    private static String sha256Hex(String value) {
	try {
	    MessageDigest digest = MessageDigest.getInstance("SHA-256");
	    byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
	    StringBuilder sb = new StringBuilder(hash.length * 2);

	    for (byte b : hash) {
		sb.append(String.format("%02x", b));
	    }

	    return sb.toString();
	} catch (NoSuchAlgorithmException ex) {
	    throw new IllegalStateException("SHA-256 is not available.", ex);
	}
    }

    private String describeWord(Word word) {
	if (word == null) {
	    return "<null>";
	}
	return word.getSimpleHanzi() + " / " + word.getPinyin();
    }

    private void logMsg(String message) {
	if (console != null) {
	    console.logMsg(message);
	} else {
	    System.out.println(message);
	}
    }

    private void logErr(String message) {
	if (console != null) {
	    console.logErr(message);
	} else {
	    System.err.println(message);
	}
    }
}
