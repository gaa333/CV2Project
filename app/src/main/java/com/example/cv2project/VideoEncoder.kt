import android.graphics.Bitmap
import android.media.*
import android.util.Log
import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.ByteBuffer

class VideoEncoder(private val outputFile: File, private val width: Int, private val height: Int, private val frameRate: Int) {
    private var mediaCodec: MediaCodec? = null
    private var mediaMuxer: MediaMuxer? = null
    private var trackIndex: Int = -1
    private var bufferInfo: MediaCodec.BufferInfo = MediaCodec.BufferInfo()
    private var isMuxerStarted = false
    private var isRecording = false
    private var presentationTimeUs: Long = 0
    private var inputBuffer: ByteBuffer? = null

    fun start() {
        try {
            val format = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, width, height)
            format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible)
            format.setInteger(MediaFormat.KEY_BIT_RATE, 5_000_000) // 비트 레이트 감소
            format.setInteger(MediaFormat.KEY_FRAME_RATE, frameRate)
            format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1)

            mediaCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC)
            mediaCodec?.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
            mediaCodec?.start()

            val inputBuffers = mediaCodec?.inputBuffers
            inputBuffer = inputBuffers?.get(0) // 첫 번째 입력 버퍼를 사용
            mediaMuxer = MediaMuxer(outputFile.absolutePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
            isRecording = true
            Log.d("VideoEncoder", "✅ Encoder and Muxer initialized")
        } catch (e: Exception) {
            Log.e("VideoEncoder", "❌ Failed to initialize encoder or muxer: ${e.message}")
            release()
        }
    }

    fun encodeFrame(bitmap: Bitmap) {
        if (!isRecording) return

        val inputBufferIndex = mediaCodec?.dequeueInputBuffer(10000L)
        if (inputBufferIndex != null && inputBufferIndex >= 0) {
            val buffer = mediaCodec?.getInputBuffer(inputBufferIndex)
            buffer?.clear()

            val byteArray = bitmapToByteArray(bitmap, buffer?.capacity() ?: 0)
            if (byteArray.isNotEmpty()) {
                buffer?.put(byteArray)
                mediaCodec?.queueInputBuffer(inputBufferIndex, 0, byteArray.size, presentationTimeUs, 0)
                presentationTimeUs += 1000000L / frameRate
            } else {
                mediaCodec?.queueInputBuffer(inputBufferIndex, 0, 0, presentationTimeUs, 0)
            }
        }

        var outputBufferIndex = mediaCodec?.dequeueOutputBuffer(bufferInfo, 10000L) ?: -1
        while (outputBufferIndex >= 0) {
            when (outputBufferIndex) {
                MediaCodec.INFO_TRY_AGAIN_LATER -> {
                    Log.d("VideoEncoder", "⏳ No output buffer available yet")
                    break
                }
                MediaCodec.INFO_OUTPUT_FORMAT_CHANGED -> {
                    val format = mediaCodec?.outputFormat
                    trackIndex = mediaMuxer?.addTrack(format!!) ?: -1
                    mediaMuxer?.start()
                    isMuxerStarted = true
                    Log.d("VideoEncoder", "📌 Muxer started with track index $trackIndex")
                }
                else -> {
                    val outputBuffer = mediaCodec?.getOutputBuffer(outputBufferIndex)
                    if (bufferInfo.flags and MediaCodec.BUFFER_FLAG_CODEC_CONFIG != 0) {
                        mediaCodec?.releaseOutputBuffer(outputBufferIndex, false)
                        outputBufferIndex = mediaCodec?.dequeueOutputBuffer(bufferInfo, 10000L) ?: -1
                        continue
                    }

                    if (isMuxerStarted && bufferInfo.size > 0) {
                        outputBuffer?.position(bufferInfo.offset)
                        outputBuffer?.limit(bufferInfo.offset + bufferInfo.size)
                        try {
                            mediaMuxer?.writeSampleData(trackIndex, outputBuffer!!, bufferInfo)
                        } catch (e: Exception) {
                            Log.e("VideoEncoder", "❌ Failed to write sample data: ${e.message}")
                        }
                    }

                    mediaCodec?.releaseOutputBuffer(outputBufferIndex, false)
                }
            }
            outputBufferIndex = mediaCodec?.dequeueOutputBuffer(bufferInfo, 10000L) ?: -1
        }
    }

    fun finish() {
        isRecording = false
        if (isMuxerStarted) {
            try {
                mediaCodec?.signalEndOfInputStream()
                var outputBufferIndex = mediaCodec?.dequeueOutputBuffer(bufferInfo, 10000L) ?: -1
                while (outputBufferIndex != MediaCodec.INFO_TRY_AGAIN_LATER) {
                    if (outputBufferIndex >= 0) {
                        val outputBuffer = mediaCodec?.getOutputBuffer(outputBufferIndex)
                        if (bufferInfo.size > 0) {
                            outputBuffer?.position(bufferInfo.offset)
                            outputBuffer?.limit(bufferInfo.offset + bufferInfo.size)
                            mediaMuxer?.writeSampleData(trackIndex, outputBuffer!!, bufferInfo)
                        }
                        mediaCodec?.releaseOutputBuffer(outputBufferIndex, false)
                    }
                    outputBufferIndex = mediaCodec?.dequeueOutputBuffer(bufferInfo, 10000L) ?: -1
                }
                mediaMuxer?.stop()
            } catch (e: Exception) {
                Log.e("VideoEncoder", "⚠️ Error during finishing encoding: ${e.message}")
            } finally {
                release()
            }
        } else {
            release()
        }
        Log.d("VideoEncoder", "✅ Encoding finished")
    }

    private fun release() {
        try {
            mediaCodec?.stop()
            mediaCodec?.release()
            mediaMuxer?.release()
        } catch (e: Exception) {
            Log.e("VideoEncoder", "❌ Error during release: ${e.message}")
        } finally {
            mediaCodec = null
            mediaMuxer = null
            isMuxerStarted = false
        }
    }

    private fun bitmapToByteArray(bitmap: Bitmap, bufferSize: Int): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, stream) // JPEG 압축률 증가
        val byteArray = stream.toByteArray()
        return if (byteArray.size > bufferSize) {
            Log.e("VideoEncoder", "❌ Buffer Overflow: Input buffer exceeded (${byteArray.size} > $bufferSize)")
            ByteArray(0)
        } else {
            byteArray
        }
    }
}