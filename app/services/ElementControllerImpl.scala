package services

import blockudoku.controllers.GridCollector
import blockudoku.controllers.mediatorImpl.ElementController
import blockudoku.models.Element
import util.ElementGenerator.generateElement
import util.{PCG32Random, ProbabilityList, SeedGenerator}

trait AdvancedElementManager extends ElementController {

  var currentElementCountVar: Int = 3
  val maxElementCount: Int = 9

  var probabilityList: ProbabilityList[Int] = ProbabilityList[Int](List((1, 0.15f), (2, 0.35f),
    (3, 0.35f), (4, 0.15f)))

  /**
   * Regenerates all [[Element]]s.
   */
  def regenerateAll(): Unit = {
    for slot <- 0 until currentElementCountVar do {
      regenerate(slot)
    }
  }
}

class ElementControllerImpl(sessionKeyStore: SessionKeyStore, gridController: GridCollector) extends
                                                                                             AdvancedElementManager {

  private val pcg32 = PCG32Random(SeedGenerator.generateSeed(
    gridController.getGrid,
    sessionKeyStore.getSessionKey))
  /**
   * DONT USE THIS!!!
   */
  override val maxElementLength: Int = 3
  /**
   * DONT USE THIS!!!
   */
  override val elementCount: Int = 3

  /**
   * Generates a new [[Element]] for the given slot.
   *
   * @param slot The slot to generate the [[Element]] for. Must be in the range of 0 to
   *             [[currentElementCountVar]].
   * @return The generated [[Element]].
   */
  override def regenerate(slot: Int): Element = {
    if slot >= maxElementCount then throw new IndexOutOfBoundsException(
      "Slot must be smaller than maximal element count.")
    else if slot < 0 then throw new IndexOutOfBoundsException(
      "Slot must be non-negative.")

    updatePCG32()

    elements = elements.updated(slot, generateElement(slot, pcg32, probabilityList))

    elements(slot)
  }

  /**
   * Selects the given [[Element]].
   *
   * @param element [[Element]] to select.
   */
  override def selectElement(element: Element): Unit = selectedElement = Some(element)

  /**
   * The currently selectable [[Element]]s.
   */
  var elements: List[Element] = List.tabulate(maxElementCount)(slot => {
    generateElement(slot, pcg32, probabilityList)
  })

  override def getElements: List[Element] = {
    elements.take(currentElementCountVar)
  }

  private def updatePCG32(): Unit = {
    pcg32.setSeed(SeedGenerator.generateSeed(gridController.getGrid, sessionKeyStore
      .getSessionKey))
  }
}
