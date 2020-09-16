package lv.herbis.cnakes.sound;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Vector3f;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static lv.herbis.cnakes.constants.CnakesConstants.LOG_STACKTRACE;
import static org.lwjgl.openal.AL10.alDistanceModel;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class SoundManager {
	private static final Logger LOG = LogManager.getLogger(SoundManager.class);

	private long device;

	private long context;

	private SoundListener listener;

	private final List<SoundBuffer> soundBufferList;

	private final Map<String, SoundSource> soundSourceMap;

	public SoundManager() {
		this.soundBufferList = new ArrayList<>();
		this.soundSourceMap = new HashMap<>();
	}

	public void init() {
		this.device = alcOpenDevice((ByteBuffer) null);
		if (this.device == NULL) {
			throw new IllegalStateException("Failed to open the default OpenAL device.");
		}
		final ALCCapabilities deviceCaps = ALC.createCapabilities(this.device);
		this.context = alcCreateContext(this.device, (IntBuffer) null);
		if (this.context == NULL) {
			throw new IllegalStateException("Failed to create OpenAL context.");
		}
		alcMakeContextCurrent(this.context);
		AL.createCapabilities(deviceCaps);
	}

	public void addSoundSource(final String name, final SoundSource soundSource) {
		this.soundSourceMap.put(name, soundSource);
	}

	public SoundSource getSoundSource(final String name) {
		return this.soundSourceMap.get(name);
	}

	public void createSound(final String name, final String path) {
		final SoundSource source = this.soundSourceMap.get(name);
		if (source != null) {
			LOG.debug("Sound source '{}' already exists. Reusing existing one.", name);
			return;
		}

		try {
			final SoundBuffer newSound = new SoundBuffer(path);

			final SoundSource newSource = new SoundSource(false, false);
			newSource.setPosition(new Vector3f(0, 0, 0));
			newSource.setBuffer(newSound.getBufferId());

			addSoundBuffer(newSound);
			addSoundSource(name, newSource);
		} catch (final IOException e) {
			LOG.error("Could not create a new sound '{}' from path '{}'. Reason: {}", name, path, e.getMessage());
			LOG.debug(LOG_STACKTRACE, e);
		}
	}

	public void playSoundSourceIfNotPlaying(final String name) {
		final SoundSource soundSource = this.soundSourceMap.get(name);
		if (soundSource != null && !soundSource.isPlaying()) {
			soundSource.play();
		} else {
			LOG.warn("Cannot play sound source \"{}\", it is not in sound source map.", name);
		}
	}

	public void playSoundSource(final String name) {
		final SoundSource soundSource = this.soundSourceMap.get(name);
		if (soundSource != null) {
			soundSource.play();
		} else {
			LOG.warn("Cannot play sound source \"{}\", it is not in sound source map.", name);
		}
	}

	public void removeSoundSource(final String name) {
		this.soundSourceMap.remove(name);
	}

	public void addSoundBuffer(final SoundBuffer soundBuffer) {
		this.soundBufferList.add(soundBuffer);
	}

	public SoundListener getListener() {
		return this.listener;
	}

	public void setListener(final SoundListener listener) {
		this.listener = listener;
	}

	public void setAttenuationModel(final int model) {
		alDistanceModel(model);
	}

	public void cleanup() {
		for (final SoundSource soundSource : this.soundSourceMap.values()) {
			soundSource.cleanup();
		}
		this.soundSourceMap.clear();
		for (final SoundBuffer soundBuffer : this.soundBufferList) {
			soundBuffer.cleanup();
		}
		this.soundBufferList.clear();
		if (this.context != NULL) {
			alcDestroyContext(this.context);
		}
		if (this.device != NULL) {
			alcCloseDevice(this.device);
		}
	}
}
