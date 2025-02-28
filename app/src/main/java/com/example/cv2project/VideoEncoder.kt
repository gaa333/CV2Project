import android.graphics.Bitmap
import android.media.*
import android.util.Log
import android.view.Surface
import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.ByteBuffer

class VideoEncoder(
    private val outputFile: File,
    private val width: Int,
    private val height: Int,
    private val frameRate: Int,
    private var inputSurface: Surface? = null

) {
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
            format.setInteger(MediaFormat.KEY_BIT_RATE, 5_000_000) // 비트 레이트 감소
            format.setInteger(MediaFormat.KEY_FRAME_RATE, frameRate)
            // 모든 프레임을 I-프레임으로 만들기 위해 KEY_I_FRAME_INTERVAL 값을 0으로 설정
            format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 0)
            // Surface 입력 모드 사용
            format.setInteger(
                MediaFormat.KEY_COLOR_FORMAT,
                MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface
            )

            mediaCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC)
            mediaCodec?.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
            inputSurface = mediaCodec?.createInputSurface() // Surface 얻기
            mediaCodec?.start()

            mediaMuxer = MediaMuxer(outputFile.absolutePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
            isRecording = true
            Log.d("VideoEncoder", "✅ Encoder and Muxer initialized")
        } catch (e: Exception) {
            Log.e("VideoEncoder", "❌ Failed to initialize encoder or muxer: ${e.message}")
            release()
        }
    }

    fun finish() {
        isRecording = false
        try {
            Log.d("VideoEncoder", "🛑 Finishing encoding...")
            // drainEncoder를 end-of-stream 플래그와 함께 호출하여 모든 출력 버퍼를 비움
            drainEncoder(true)

            mediaCodec?.stop()
            mediaCodec?.release()

            if (isMuxerStarted) {
                mediaMuxer?.stop()
                mediaMuxer?.release()
                Log.d("VideoEncoder", "✅ Muxer stopped and released")
            } else {
                Log.e("VideoEncoder", "❌ Muxer was never started!") // ❌ Muxer가 시작되지 않음
            }
            Log.d("VideoEncoder", "✅ Encoding finished successfully!")
        } catch (e: Exception) {
            Log.e("VideoEncoder", "❌ Error during finishing encoding: ${e.message}")
        } finally {
            // 이미 해제한 상태로 남지 않도록 null로 설정하여 중복 해제를 방지
            mediaCodec = null
            mediaMuxer = null
            isMuxerStarted = false
        }
        Log.d("VideoEncoder", "✅ Encoding finished")
    }

    fun encodeFrame(bitmap: Bitmap) {
        try {
            if (!isRecording) return
            // Surface 입력 모드에서는 입력 버퍼 대신 Canvas를 사용
            inputSurface?.let { surface ->
                val canvas = surface.lockCanvas(null)
                canvas.drawBitmap(bitmap, 0f, 0f, null)
                surface.unlockCanvasAndPost(canvas)
            } ?: run {
                Log.e("VideoEncoder", "❌ inputSurface is null!")
            }

            drainEncoder(false) // ✅ 데이터를 인코더로 보냄
        } catch (e: Exception) {
            Log.e("VideoEncoder", "❌ Error encoding frame: ${e.message}")
        }


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
            Log.e(
                "VideoEncoder",
                "❌ Buffer Overflow: Input buffer exceeded (${byteArray.size} > $bufferSize)"
            )
            ByteArray(0)
        } else {
            byteArray
        }
    }

    fun drainEncoder(endOfStream: Boolean) {
        try {
            val bufferInfo = MediaCodec.BufferInfo()
            while (true) {
                val outputBufferIndex = mediaCodec?.dequeueOutputBuffer(bufferInfo, 10000) ?: -1

                if (outputBufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {
                    // 출력 버퍼가 없으면 종료. endOfStream인 경우에도 대기하지 않음.
                    if (!endOfStream) {
                        break
                    } else {
                        Log.d("VideoEncoder", "No output available, end-of-stream reached")
                        break
                    }
                } else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                    if (isMuxerStarted) {
                        throw RuntimeException("Format changed twice!")
                    }
                    val newFormat = mediaCodec?.outputFormat
                    trackIndex = mediaMuxer?.addTrack(newFormat!!) ?: -1
                    mediaMuxer?.start()
                    isMuxerStarted = true
                    Log.d("VideoEncoder", "✅ Muxer started")
                } else if (outputBufferIndex >= 0) {
                    val encodedData = mediaCodec?.getOutputBuffer(outputBufferIndex) ?: continue

                    if (bufferInfo.size > 0) {
                        encodedData.position(bufferInfo.offset)
                        encodedData.limit(bufferInfo.offset + bufferInfo.size)
                        if (isMuxerStarted) {
                            mediaMuxer?.writeSampleData(trackIndex, encodedData, bufferInfo)
                            Log.d("VideoEncoder", "🟢 Writing ${bufferInfo.size} bytes to muxer") // ✅ 로그 추가
                        } else {
                            Log.e("VideoEncoder", "❌ Muxer is not started yet!") // ❌ Muxer가 시작되지 않음
                        }
                    }

                    mediaCodec?.releaseOutputBuffer(outputBufferIndex, false)

                    // end-of-stream 플래그가 있으면 루프 종료
                    if ((bufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                        Log.d("VideoEncoder", "End-of-stream flag received")
                        break
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("VideoEncoder", "❌ Error draining encoder: ${e.message}")
        }
    }
}
