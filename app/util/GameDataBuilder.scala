package util

import services.GameStateInstance
import model.GameData

object GameDataBuilder {
  def build(sessionId: String, gameState: GameStateInstance): GameData = {
    val grid = gameState.getGrid
    val universalGridPreview = gameState.getUniversalGridPreviewGenerator.getUniversalGridPreview
    val elements = gameState.getElements
    val placementHistory = gameState.getPlacementHistory
    
    GameData(elements, universalGridPreview, grid, gameState.getScore,
      gameState.getColorIndex, sessionId, placementHistory)
  }
}
