package util

class PCG32Random {
  private val N = 6364136223846793005L

  private var state = 0x853c49e6748fea9bL
  private var inc = 0xda3e39cb94b95bdbL
  
  def setSeed(seedState: Long, seedSequence: Long): Unit = {
    state = 0
    inc = (seedSequence << 1) | 1
    nextInt()
    state += seedState
    nextInt()
  }
  
  def setSeed(seeds: (Long, Long)): Unit = {
    setSeed(seeds._1, seeds._2)
  }

  def nextInt(): Int = {
    val old = state
    state = old * N + inc
    val shifted = (((old >>> 18) ^ old) >>> 27).toInt
    val rot = (old >>> 59).toInt
    (shifted >>> rot) | (shifted << ((~rot + 1) & 31))
  }
  
  def nextInt(max: Int): Int = {
    if(max < 0) throw IllegalArgumentException("The max value must be greater than 0.")
    nextValBetween(0, max)
  }

  def nextUnsignedLong(): Long = {
    val r = nextInt()
    Integer.toUnsignedLong(r)
  }
  
  def nextValBetween(min: Int, max: Int): Int = {
    if(min > max) throw IllegalArgumentException("The max value must be greater than the min " +
      "value!")
    val range = max - min
    val rand = nextUnsignedLong()
    (rand % range).toInt + min
  }

  def nextFloat(): Double = {
    val u = nextInt() & 0xffffffffL
    u.toDouble / (1L << 32)
  }
}

object PCG32Random {
  def apply(seedState: Long, seedSequence: Long): PCG32Random = {
    val pcg = new PCG32Random()
    pcg.setSeed(seedState, seedSequence)
    pcg
  }

  def apply(seed: (Long, Long)): PCG32Random = {
    val pcg = new PCG32Random()
    pcg.setSeed(seed._1, seed._2)
    pcg
  }
}