package lv.herbis.cnakes.sound;

import lv.herbis.cnakes.tools.DataUtil;
import org.lwjgl.stb.STBVorbisInfo;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.stb.STBVorbis.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class SoundBuffer {
	private final int bufferId;

	private ShortBuffer pcm = null;


	public SoundBuffer(final String file) throws IOException {
		this.bufferId = alGenBuffers();
		try (final STBVorbisInfo info = STBVorbisInfo.malloc()) {
			final ShortBuffer tempPcm = readVorbis(file, 32 * 1024, info);

			// Copy to buffer
			alBufferData(this.bufferId, info.channels() == 1 ? AL_FORMAT_MONO16 : AL_FORMAT_STEREO16, tempPcm,
						 info.sample_rate());
		}
	}

	public int getBufferId() {
		return this.bufferId;
	}

	public void cleanup() {
		alDeleteBuffers(getBufferId());
		if (this.pcm != null) {
			MemoryUtil.memFree(this.pcm);
		}
	}

	private ShortBuffer readVorbis(final String resource, final int bufferSize,
								   final STBVorbisInfo info) throws IOException {
		try (final MemoryStack stack = MemoryStack.stackPush()) {
			final ByteBuffer vorbis = DataUtil.ioResourceToByteBuffer(resource, bufferSize);
			final IntBuffer error = stack.mallocInt(1);
			final long decoder = stb_vorbis_open_memory(vorbis, error, null);
			if (decoder == NULL) {
				throw new IOException("Failed to open Ogg Vorbis file. Error: " + error.get(0));
			}

			stb_vorbis_get_info(decoder, info);

			final int channels = info.channels();

			final int lengthSamples = stb_vorbis_stream_length_in_samples(decoder);

			this.pcm = MemoryUtil.memAllocShort(lengthSamples);

			this.pcm.limit(stb_vorbis_get_samples_short_interleaved(decoder, channels, this.pcm) * channels);
			stb_vorbis_close(decoder);

			return this.pcm;
		}
	}
}
