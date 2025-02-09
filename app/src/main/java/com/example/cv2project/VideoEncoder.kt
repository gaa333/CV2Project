import android.graphics.Bitmap
import android.media.*
import android.util.Log
import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.ByteBuffer

class VideoEncoder(private val outputFile: File) {
    private var mediaCodec: MediaCodec? = null
    private var mediaMuxer: MediaMuxer? = null
    private var trackIndex = -1
    private var isRecording = false

    fun start() {
        val format = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, 720, 1280)
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface)
        format.setInteger(MediaFormat.KEY_BIT_RATE, 4000000)
        format.setInteger(MediaFormat.KEY_FRAME_RATE, 30)
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1)

        mediaCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC)
        mediaCodec?.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
        mediaMuxer = MediaMuxer(outputFile.absolutePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)

        mediaCodec?.start()
        isRecording = true
    }

    fun encodeFrame(bitmap: Bitmap) {
        if (!isRecording) return

        val inputBufferIndex = mediaCodec?.dequeueInputBuffer(10000L) ?: return
        if (inputBufferIndex >= 0) {
            val inputBuffer = mediaCodec?.getInputBuffer(inputBufferIndex)
            inputBuffer?.clear()
            val byteArray = bitmapToByteArray(bitmap)
            if (byteArray.size <= inputBuffer?.remaining() ?: 0) {
                inputBuffer?.put(byteArray)
                mediaCodec?.queueInputBuffer(inputBufferIndex, 0, byteArray.size, System.nanoTime() / 1000, 0)
            } else {
                Log.e("VideoEncoder", "❌ Buffer Overflow: InputBuffer is too small for frame data.")
            }
        }

        // 🔹 Output Buffer 처리
        val bufferInfo = MediaCodec.BufferInfo()
        var outputBufferIndex = mediaCodec?.dequeueOutputBuffer(bufferInfo, 10000L) ?: -1
        while (outputBufferIndex >= 0) {
            val outputBuffer = mediaCodec?.getOutputBuffer(outputBufferIndex)
            if (outputBuffer != null) {
                val outputData = ByteArray(bufferInfo.size)
                outputBuffer.get(outputData)
                outputBuffer.clear()

                if (bufferInfo.flags and MediaCodec.BUFFER_FLAG_CODEC_CONFIG == 0) {
                    mediaMuxer?.writeSampleData(trackIndex, outputBuffer, bufferInfo)
                }
            }
            mediaCodec?.releaseOutputBuffer(outputBufferIndex, false)
            outputBufferIndex = mediaCodec?.dequeueOutputBuffer(bufferInfo, 10000L) ?: -1
        }
    }

    fun finish() {
        if (!isRecording) return
        isRecording = false
        try {
            mediaCodec?.stop()
            mediaCodec?.release()
            mediaCodec = null

            mediaMuxer?.stop()
            mediaMuxer?.release()
            mediaMuxer = null
        } catch (e: IllegalStateException) {
            Log.e("VideoEncoder", "❌ Failed to stop MediaCodec or MediaMuxer: ${e.message}")
        }
    }

    private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        return stream.toByteArray()
    }
}
