package com.signbridge.ml

class SlidingWindowBuffer(
    private val windowSize: Int,
    private val frameSize: Int,
) {
    private val frames = ArrayDeque<FloatArray>()

    val isReady: Boolean
        get() = frames.size == windowSize

    fun add(frame: FloatArray) {
        require(frame.size == frameSize) {
            "Expected frame size $frameSize but got ${frame.size}"
        }
        if (frames.size == windowSize) {
            frames.removeFirst()
        }
        frames.addLast(frame.copyOf())
    }

    fun reset() {
        frames.clear()
    }

    fun toTensor(): FloatArray {
        require(isReady) {
            "Sliding window needs $windowSize frames before tensor export"
        }
        val tensor = FloatArray(windowSize * frameSize)
        frames.forEachIndexed { frameIndex, frame ->
            frame.copyInto(
                destination = tensor,
                destinationOffset = frameIndex * frameSize,
            )
        }
        return tensor
    }
}
