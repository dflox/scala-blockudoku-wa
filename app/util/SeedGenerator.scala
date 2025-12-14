package util

import blockudoku.models.Grid

object SeedGenerator {
  def generateSeed(grid: Grid, sessionKey: String): (Long, Long) = {
    val gridString = grid.toUniqueString
    val combinedString = gridString + sessionKey
    
    val sha256hash = java.security.MessageDigest.getInstance("SHA-256")
    val hashBytes = sha256hash.digest(combinedString.getBytes("UTF-8"))
    
    val seed1 = BigInt(hashBytes.slice(0, 8)).toLong
    val seed2 = BigInt(hashBytes.slice(8, 16)).toLong

    (seed1, seed2)
  }
}
